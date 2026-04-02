import { c as defineEventHandler, u as useRuntimeConfig, h as getQuery, e as createError, f as sendRedirect } from '../../_/nitro.mjs';
import { f as fetchOidcConfig, e as exchangeCode, a as fetchUserInfo } from '../../_/oidc.mjs';
import { g as getAppSession } from '../../_/session.mjs';
import 'node:crypto';
import 'node:http';
import 'node:https';
import 'node:events';
import 'node:buffer';
import 'node:fs';
import 'node:path';
import 'node:url';

const callback_get = defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event);
  const { code, error } = getQuery(event);
  if (error || !code) {
    throw createError({ statusCode: 400, message: String(error != null ? error : "Missing authorization code") });
  }
  const session = await getAppSession(event);
  const verifier = session.data.pkceVerifier;
  if (!verifier) {
    throw createError({ statusCode: 400, message: "Missing PKCE verifier, please login again" });
  }
  const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl);
  const tokens = await exchangeCode(oidcConfig, {
    code: String(code),
    verifier,
    clientId: config.oidcClientId,
    redirectUri: config.oidcRedirectUri
  });
  const userInfo = await fetchUserInfo(oidcConfig, tokens.access_token);
  await session.update({
    pkceVerifier: void 0,
    accessToken: tokens.access_token,
    refreshToken: tokens.refresh_token,
    idToken: tokens.id_token,
    userInfo
  });
  return sendRedirect(event, "/");
});

export { callback_get as default };
//# sourceMappingURL=callback.get.mjs.map
