import { c as defineEventHandler, e as createError } from '../../_/nitro.mjs';
import { g as getAppSession } from '../../_/session.mjs';
import 'node:crypto';
import 'node:http';
import 'node:https';
import 'node:events';
import 'node:buffer';
import 'node:fs';
import 'node:path';
import 'node:url';

const me_get = defineEventHandler(async (event) => {
  var _a;
  const session = await getAppSession(event);
  if (!session.data.accessToken) {
    throw createError({ statusCode: 401, message: "Unauthenticated" });
  }
  return (_a = session.data.userInfo) != null ? _a : null;
});

export { me_get as default };
//# sourceMappingURL=me.get.mjs.map
