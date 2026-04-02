/**
 * [業務說明]
 * App 啟動時從 server session 還原登入狀態。
 * fetchMe() 失敗（未登入）時 store 保持 null，middleware 會導向登入頁。
 */
export default defineNuxtPlugin(async () => {
  const authStore = useAuthStore()
  await authStore.fetchMe()
})
