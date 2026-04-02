import { c as defineEventHandler, u as useRuntimeConfig, f as sendRedirect } from '../../_/nitro.mjs';
import { f as fetchOidcConfig, c as revokeToken, d as buildLogoutUrl } from '../../_/oidc.mjs';
import { g as getAppSession } from '../../_/session.mjs';
import 'node:crypto';
import 'node:http';
import 'node:https';
import 'node:events';
import 'node:buffer';
import 'node:fs';
import 'node:path';
import 'node:url';

const logout_post = defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event);
  const session = await getAppSession(event);
  const { refreshToken, idToken } = session.data;
  const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl);
  if (refreshToken) {
    await revokeToken(oidcConfig, refreshToken);
  }
  await session.clear();
  const logoutUrl = buildLogoutUrl(oidcConfig, {
    idTokenHint: idToken,
    postLogoutRedirectUri: config.oidcPostLogoutRedirectUri
  });
  return sendRedirect(event, logoutUrl);
});

export { logout_post as default };
//# sourceMappingURL=logout.post.mjs.map
