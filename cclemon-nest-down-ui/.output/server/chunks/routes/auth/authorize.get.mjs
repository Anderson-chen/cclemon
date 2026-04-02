import { c as defineEventHandler, u as useRuntimeConfig, f as sendRedirect } from '../../_/nitro.mjs';
import { g as generatePkce, f as fetchOidcConfig, b as buildAuthorizationUrl } from '../../_/oidc.mjs';
import { g as getAppSession } from '../../_/session.mjs';
import 'node:crypto';
import 'node:http';
import 'node:https';
import 'node:events';
import 'node:buffer';
import 'node:fs';
import 'node:path';
import 'node:url';

const authorize_get = defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event);
  const { verifier, challenge } = await generatePkce();
  const session = await getAppSession(event);
  await session.update({ pkceVerifier: verifier });
  const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl);
  const authUrl = buildAuthorizationUrl(oidcConfig, {
    clientId: config.oidcClientId,
    redirectUri: config.oidcRedirectUri,
    scope: config.oidcScope,
    challenge
  });
  return sendRedirect(event, authUrl);
});

export { authorize_get as default };
//# sourceMappingURL=authorize.get.mjs.map
