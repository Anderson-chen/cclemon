async function generateCodeVerifier() {
  const array = new Uint8Array(32);
  crypto.getRandomValues(array);
  return btoa(String.fromCharCode(...array)).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}
async function generateCodeChallenge(codeVerifier) {
  const buf = new TextEncoder().encode(codeVerifier);
  const hash = await crypto.subtle.digest("SHA-256", buf);
  return btoa(String.fromCharCode(...new Uint8Array(hash))).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}
async function generatePkce() {
  const verifier = await generateCodeVerifier();
  const challenge = await generateCodeChallenge(verifier);
  return { verifier, challenge };
}
function buildStaticOidcConfig(issuer) {
  const base = issuer.replace(/\/$/, "");
  return {
    issuer: base,
    authorization_endpoint: `${base}/oauth2/authorize`,
    token_endpoint: `${base}/oauth2/token`,
    userinfo_endpoint: `${base}/userinfo`,
    end_session_endpoint: `${base}/connect/logout`,
    revocation_endpoint: `${base}/oauth2/revoke`,
    jwks_uri: `${base}/oauth2/jwks`
  };
}
async function fetchOidcConfig(discoveryUrl) {
  try {
    return await $fetch(discoveryUrl);
  } catch {
    const issuer = discoveryUrl.replace("/.well-known/openid-configuration", "").replace("/openid-connect", "");
    return buildStaticOidcConfig(issuer);
  }
}
function buildAuthorizationUrl(config, opts) {
  const params = new URLSearchParams({
    response_type: "code",
    client_id: opts.clientId,
    redirect_uri: opts.redirectUri,
    scope: opts.scope,
    code_challenge: opts.challenge,
    code_challenge_method: "S256"
  });
  return `${config.authorization_endpoint}?${params}`;
}
async function exchangeCode(config, opts) {
  const body = new URLSearchParams({
    grant_type: "authorization_code",
    code: opts.code,
    redirect_uri: opts.redirectUri,
    code_verifier: opts.verifier,
    client_id: opts.clientId
  });
  return await $fetch(config.token_endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: body.toString()
  });
}
async function refreshAccessToken(config, opts) {
  const body = new URLSearchParams({
    grant_type: "refresh_token",
    refresh_token: opts.refreshToken,
    client_id: opts.clientId
  });
  return await $fetch(config.token_endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: body.toString()
  });
}
async function revokeToken(config, token) {
  const body = new URLSearchParams({ token });
  await $fetch(config.revocation_endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: body.toString()
  }).catch(() => {
  });
}
async function fetchUserInfo(config, accessToken) {
  return await $fetch(config.userinfo_endpoint, {
    headers: { Authorization: `Bearer ${accessToken}` }
  });
}
function buildLogoutUrl(config, opts) {
  const params = new URLSearchParams({
    post_logout_redirect_uri: opts.postLogoutRedirectUri
  });
  if (opts.idTokenHint) {
    params.set("id_token_hint", opts.idTokenHint);
  }
  return `${config.end_session_endpoint}?${params}`;
}

export { fetchUserInfo as a, buildAuthorizationUrl as b, revokeToken as c, buildLogoutUrl as d, exchangeCode as e, fetchOidcConfig as f, generatePkce as g, refreshAccessToken as r };
//# sourceMappingURL=oidc.mjs.map
