/**
 * [業務說明]
 * API Gateway Proxy。所有 /api/* 請求由此轉發至後端 Gateway，
 * server-side 自動注入 Authorization header，client 不持有 token。
 *
 * [技術考量]
 * - 401 時自動嘗試 refresh token，成功後重試原始請求
 * - refresh 失敗則清除 session，回傳 401 要求重新登入
 * - body 以 raw 形式轉發，避免 JSON 二次序列化問題
 */
import { fetchOidcConfig, refreshAccessToken } from '../utils/oidc'
import { getAppSession } from '../utils/session'

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const session = await getAppSession(event)

  if (!session.data.accessToken) {
    throw createError({ statusCode: 401, message: 'Unauthenticated' })
  }

  // /api/xxx → /xxx (strip /api prefix)
  const path = event.path.replace(/^\/api/, '')
  const targetUrl = `${config.apiGatewayUrl}${path}`

  const isBodyMethod = !['GET', 'HEAD'].includes(event.method.toUpperCase())
  const rawBody = isBodyMethod ? await readRawBody(event) : undefined

  async function forward(accessToken: string) {
    return await $fetch(targetUrl, {
      method: event.method as Parameters<typeof $fetch>[1]['method'],
      headers: {
        Authorization: `Bearer ${accessToken}`,
        ...(getHeader(event, 'content-type')
          ? { 'Content-Type': getHeader(event, 'content-type')! }
          : {}),
      },
      body: rawBody,
      // 保留原始回應，不讓 $fetch 自動解析 404/500 為 error
      ignoreResponseError: true,
    })
  }

  try {
    return await forward(session.data.accessToken)
  } catch (err: any) {
    // access token 過期 → 嘗試 refresh
    if (err?.status !== 401 || !session.data.refreshToken) {
      throw createError({ statusCode: err?.status ?? 502, message: err?.message ?? 'Gateway error' })
    }

    const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl).catch(() => null)
    if (!oidcConfig) {
      await session.clear()
      throw createError({ statusCode: 401, message: 'Session expired' })
    }

    const tokens = await refreshAccessToken(oidcConfig, {
      refreshToken: session.data.refreshToken,
      clientId: config.oidcClientId,
    }).catch(() => null)

    if (!tokens) {
      await session.clear()
      throw createError({ statusCode: 401, message: 'Session expired, please login again' })
    }

    await session.update({
      accessToken: tokens.access_token,
      refreshToken: tokens.refresh_token,
      ...(tokens.id_token ? { idToken: tokens.id_token } : {}),
    })

    return await forward(tokens.access_token)
  }
})
