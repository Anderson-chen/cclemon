/**
 * [業務說明]
 * Server-side OIDC 工具函式。從 src/auth/authFlow.ts 移植至 server，
 * 確保所有認證操作在 server 執行，client 端不直接接觸認證中心。
 *
 * [技術考量]
 * - 使用 Web Crypto API（Node.js 18+ 全域可用），與原始實作相同
 * - 純函式設計，不持有狀態，方便測試
 * - config 由呼叫端傳入，不直接讀取 runtimeConfig
 */

// ─── Types ───────────────────────────────────────────────────────────────────

export type OidcConfig = {
  issuer: string
  authorization_endpoint: string
  token_endpoint: string
  userinfo_endpoint: string
  end_session_endpoint: string
  revocation_endpoint: string
  jwks_uri: string
  device_authorization_endpoint?: string
  token_endpoint_auth_methods_supported?: string[]
  response_types_supported?: string[]
  grant_types_supported?: string[]
  code_challenge_methods_supported?: string[]
  scopes_supported?: string[]
}

export type TokenResponse = {
  access_token: string
  refresh_token: string
  id_token: string
  token_type: string
  expires_in: number
  scope: string
}

export type UserinfoResponse = {
  sub: string
  email?: string
  name?: string
  aud?: string[]
  iss?: string
  exp?: number
  iat?: number
}

// ─── PKCE ────────────────────────────────────────────────────────────────────

async function generateCodeVerifier(): Promise<string> {
  const array = new Uint8Array(32)
  crypto.getRandomValues(array)
  return btoa(String.fromCharCode(...array))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '')
}

async function generateCodeChallenge(codeVerifier: string): Promise<string> {
  const buf = new TextEncoder().encode(codeVerifier)
  const hash = await crypto.subtle.digest('SHA-256', buf)
  return btoa(String.fromCharCode(...new Uint8Array(hash)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '')
}

/** 生成 PKCE verifier + challenge pair，verifier 由呼叫端存入 session */
export async function generatePkce(): Promise<{ verifier: string; challenge: string }> {
  const verifier = await generateCodeVerifier()
  const challenge = await generateCodeChallenge(verifier)
  return { verifier, challenge }
}

// ─── OIDC Discovery ───────────────────────────────────────────────────────────

function buildStaticOidcConfig(issuer: string): OidcConfig {
  const base = issuer.replace(/\/$/, '')
  return {
    issuer: base,
    authorization_endpoint: `${base}/oauth2/authorize`,
    token_endpoint: `${base}/oauth2/token`,
    userinfo_endpoint: `${base}/userinfo`,
    end_session_endpoint: `${base}/connect/logout`,
    revocation_endpoint: `${base}/oauth2/revoke`,
    jwks_uri: `${base}/oauth2/jwks`,
  }
}

/** 取得 OIDC discovery 設定，discovery 端點 403 時改用 issuer 推導 */
export async function fetchOidcConfig(discoveryUrl: string): Promise<OidcConfig> {
  try {
    return await $fetch<OidcConfig>(discoveryUrl)
  } catch {
    const issuer = discoveryUrl
      .replace('/.well-known/openid-configuration', '')
      .replace('/openid-connect', '')
    return buildStaticOidcConfig(issuer)
  }
}

// ─── Authorization ────────────────────────────────────────────────────────────

export type AuthorizationOpts = {
  clientId: string
  redirectUri: string
  scope: string
  challenge: string
}

/** 產生指向認證中心的授權 URL（redirect 由呼叫端執行） */
export function buildAuthorizationUrl(config: OidcConfig, opts: AuthorizationOpts): string {
  const params = new URLSearchParams({
    response_type: 'code',
    client_id: opts.clientId,
    redirect_uri: opts.redirectUri,
    scope: opts.scope,
    code_challenge: opts.challenge,
    code_challenge_method: 'S256',
  })
  return `${config.authorization_endpoint}?${params}`
}

// ─── Token Operations ─────────────────────────────────────────────────────────

export type ExchangeCodeOpts = {
  code: string
  verifier: string
  clientId: string
  redirectUri: string
}

/** Authorization Code + PKCE → token（server-side，client 不可見） */
export async function exchangeCode(
  config: OidcConfig,
  opts: ExchangeCodeOpts,
): Promise<TokenResponse> {
  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    code: opts.code,
    redirect_uri: opts.redirectUri,
    code_verifier: opts.verifier,
    client_id: opts.clientId,
  })
  return await $fetch<TokenResponse>(config.token_endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: body.toString(),
  })
}

export type RefreshOpts = {
  refreshToken: string
  clientId: string
}

/** Refresh token → 新 token pair */
export async function refreshAccessToken(
  config: OidcConfig,
  opts: RefreshOpts,
): Promise<TokenResponse> {
  const body = new URLSearchParams({
    grant_type: 'refresh_token',
    refresh_token: opts.refreshToken,
    client_id: opts.clientId,
  })
  return await $fetch<TokenResponse>(config.token_endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: body.toString(),
  })
}

/** 撤銷 token（logout 時呼叫） */
export async function revokeToken(config: OidcConfig, token: string): Promise<void> {
  const body = new URLSearchParams({ token })
  await $fetch(config.revocation_endpoint, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: body.toString(),
  }).catch(() => {})
}

/** 取得 userInfo */
export async function fetchUserInfo(
  config: OidcConfig,
  accessToken: string,
): Promise<UserinfoResponse> {
  return await $fetch<UserinfoResponse>(config.userinfo_endpoint, {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
}

// ─── Logout ───────────────────────────────────────────────────────────────────

export type LogoutOpts = {
  idTokenHint?: string
  postLogoutRedirectUri: string
}

/** 產生 OIDC end_session URL（redirect 由呼叫端執行） */
export function buildLogoutUrl(config: OidcConfig, opts: LogoutOpts): string {
  const params = new URLSearchParams({
    post_logout_redirect_uri: opts.postLogoutRedirectUri,
  })
  if (opts.idTokenHint) {
    params.set('id_token_hint', opts.idTokenHint)
  }
  return `${config.end_session_endpoint}?${params}`
}
