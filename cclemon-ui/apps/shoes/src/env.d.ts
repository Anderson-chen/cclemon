/* eslint-disable */

declare namespace NodeJS {
  interface ProcessEnv {
    NODE_ENV: string;
    VUE_APP_AUTH_CONFIG: string;
    VUE_APP_CLIENT_ID: string;
    VUE_APP_REDIRECT_URI: string;
    VUE_APP_POST_LOGOUT_REDIRECT_URI: string;
    VUE_APP_SCOPE: string;
    VUE_ROUTER_MODE: 'hash' | 'history' | 'abstract' | undefined;
    VUE_ROUTER_BASE: string | undefined;
    VUE_APP_SYSTEM_CODE: string;
    VUE_APP_TOKEN_KEY: string;
    VUE_APP_USER_PROFILE_KEY: string;
    VUE_APP_HEALTH_BASE_URL: string;
    VUE_APP_USER_EVENT_BASE_URL: string;
    VUE_APP_VERIFIER: string;
  }
}