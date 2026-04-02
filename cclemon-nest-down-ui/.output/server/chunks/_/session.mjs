import { u as useRuntimeConfig, i as useSession } from './nitro.mjs';

async function getAppSession(event) {
  const config = useRuntimeConfig(event);
  return useSession(event, {
    password: config.sessionSecret,
    name: "nestdown_session",
    maxAge: 60 * 60 * 24,
    cookie: {
      httpOnly: true,
      secure: true,
      sameSite: "lax"
    }
  });
}

export { getAppSession as g };
//# sourceMappingURL=session.mjs.map
