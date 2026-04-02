/**
 * [業務說明]
 * 全域 auth guard，取代舊 Vue Router 的 beforeEach。
 * 未登入的使用者存取受保護頁面時，redirect 到 /auth/login。
 *
 * [技術考量]
 * - Nuxt middleware 在 SSR 和 CSR 都會執行
 * - /auth/* 路徑不需驗證（login page、callback）
 * - isAuthenticated 依據 authStore.userInfo（由 app.vue 初始化）
 */
export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()

  const isPublicPath = to.path.startsWith('/auth/')

  if (!isPublicPath && !authStore.isAuthenticated) {
    return navigateTo('/auth/login')    // pages/auth/login.vue (登入 UI)
  }
})
