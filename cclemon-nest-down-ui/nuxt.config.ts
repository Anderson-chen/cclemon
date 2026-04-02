// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-11-15',

  // SPA 模式開發，server routes（BFF auth）不受此設定影響，仍在 server-side 執行
  ssr: false,

  // Nuxt 4 標準：srcDir 預設為 app/，與根目錄的 server/ 分離

  modules: ['@pinia/nuxt'],

  components: [
    { path: '~/components', pathPrefix: false },
  ],

  // 全部 OIDC / Gateway 設定為 server-only，client 完全不可見
  runtimeConfig: {
    oidcDiscoveryUrl: '',
    oidcClientId: '',
    oidcRedirectUri: '',
    oidcScope: 'openid profile email offline_access',
    oidcPostLogoutRedirectUri: '',
    apiGatewayUrl: '',
    sessionSecret: '',
  },

  css: ['~/assets/main.css'],

  devtools: { enabled: true },

  devServer: {
    host: '127.0.0.1',
    port: 3000,
  },
})
