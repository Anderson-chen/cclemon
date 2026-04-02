/**
 * GET /auth/login
 * 生成 PKCE → verifier 存 session → redirect 到認證中心
 * Client 只發一個請求到此，不直接接觸認證中心
 */
import { generatePkce, fetchOidcConfig, buildAuthorizationUrl } from '../../utils/oidc'
import { getAppSession } from '../../utils/session'

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event)

  const { verifier, challenge } = await generatePkce()

  const session = await getAppSession(event)
  await session.update({ pkceVerifier: verifier })

  const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl)
  const authUrl = buildAuthorizationUrl(oidcConfig, {
    clientId: config.oidcClientId,
    redirectUri: config.oidcRedirectUri,
    scope: config.oidcScope,
    challenge,
  })

  return sendRedirect(event, authUrl)
})
