/**
 * POST /auth/logout
 * Server-side：revoke refresh token → 清除 session → redirect 到 OIDC end_session
 */
import { fetchOidcConfig, revokeToken, buildLogoutUrl } from '../../utils/oidc'
import { getAppSession } from '../../utils/session'

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)
  const session = await getAppSession(event)

  const { refreshToken, idToken } = session.data

  const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl)

  if (refreshToken) {
    await revokeToken(oidcConfig, refreshToken)
  }

  await session.clear()

  const logoutUrl = buildLogoutUrl(oidcConfig, {
    idTokenHint: idToken,
    postLogoutRedirectUri: config.oidcPostLogoutRedirectUri,
  })

  return sendRedirect(event, logoutUrl)
})
