/**
 * GET /auth/callback?code=xxx
 * 認證中心 redirect 回此端點，server-side 換 token，存入 session
 * Client 永遠不會看到 access_token / refresh_token
 */
import { fetchOidcConfig, exchangeCode, fetchUserInfo } from '../../utils/oidc'
import { getAppSession } from '../../utils/session'

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const { code, error } = getQuery(event)

  if (error || !code) {
    throw createError({ statusCode: 400, message: String(error ?? 'Missing authorization code') })
  }

  const session = await getAppSession(event)
  const verifier = session.data.pkceVerifier

  if (!verifier) {
    throw createError({ statusCode: 400, message: 'Missing PKCE verifier, please login again' })
  }

  const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl)

  const tokens = await exchangeCode(oidcConfig, {
    code: String(code),
    verifier,
    clientId: config.oidcClientId,
    redirectUri: config.oidcRedirectUri,
  })

  const userInfo = await fetchUserInfo(oidcConfig, tokens.access_token)

  // token 存 server session，清除暫存的 pkceVerifier
  await session.update({
    pkceVerifier: undefined,
    accessToken: tokens.access_token,
    refreshToken: tokens.refresh_token,
    idToken: tokens.id_token,
    userInfo,
  })

  return sendRedirect(event, '/')
})
