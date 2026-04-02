import { c as defineEventHandler, u as useRuntimeConfig, e as createError, r as readRawBody, g as getHeader } from '../../_/nitro.mjs';
import { f as fetchOidcConfig, r as refreshAccessToken } from '../../_/oidc.mjs';
import { g as getAppSession } from '../../_/session.mjs';
import 'node:crypto';
import 'node:http';
import 'node:https';
import 'node:events';
import 'node:buffer';
import 'node:fs';
import 'node:path';
import 'node:url';

const _____ = defineEventHandler(async (event) => {
  var _a, _b;
  const config = useRuntimeConfig(event);
  const session = await getAppSession(event);
  if (!session.data.accessToken) {
    throw createError({ statusCode: 401, message: "Unauthenticated" });
  }
  const path = event.path.replace(/^\/api/, "");
  const targetUrl = `${config.apiGatewayUrl}${path}`;
  const isBodyMethod = !["GET", "HEAD"].includes(event.method.toUpperCase());
  const rawBody = isBodyMethod ? await readRawBody(event) : void 0;
  async function forward(accessToken) {
    return await $fetch(targetUrl, {
      method: event.method,
      headers: {
        Authorization: `Bearer ${accessToken}`,
        ...getHeader(event, "content-type") ? { "Content-Type": getHeader(event, "content-type") } : {}
      },
      body: rawBody,
      // 保留原始回應，不讓 $fetch 自動解析 404/500 為 error
      ignoreResponseError: true
    });
  }
  try {
    return await forward(session.data.accessToken);
  } catch (err) {
    if ((err == null ? void 0 : err.status) !== 401 || !session.data.refreshToken) {
      throw createError({ statusCode: (_a = err == null ? void 0 : err.status) != null ? _a : 502, message: (_b = err == null ? void 0 : err.message) != null ? _b : "Gateway error" });
    }
    const oidcConfig = await fetchOidcConfig(config.oidcDiscoveryUrl).catch(() => null);
    if (!oidcConfig) {
      await session.clear();
      throw createError({ statusCode: 401, message: "Session expired" });
    }
    const tokens = await refreshAccessToken(oidcConfig, {
      refreshToken: session.data.refreshToken,
      clientId: config.oidcClientId
    }).catch(() => null);
    if (!tokens) {
      await session.clear();
      throw createError({ statusCode: 401, message: "Session expired, please login again" });
    }
    await session.update({
      accessToken: tokens.access_token,
      refreshToken: tokens.refresh_token,
      ...tokens.id_token ? { idToken: tokens.id_token } : {}
    });
    return await forward(tokens.access_token);
  }
});

export { _____ as default };
//# sourceMappingURL=_..._.mjs.map
