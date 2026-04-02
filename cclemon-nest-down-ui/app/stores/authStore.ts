/**
 * [業務說明]
 * Client-side auth 狀態管理。token 完全由 server session 持有，
 * 此 store 只負責快取 userInfo 與提供 login/logout 操作介面。
 *
 * [技術考量]
 * - 所有 auth 操作轉發至 server routes（/auth/login、/auth/logout）
 * - isAuthenticated 依據 userInfo 是否存在判斷（非 token）
 * - fetchMe() 在 app 初始化時呼叫，從 server session 取得登入狀態
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserinfoResponse } from '~/types/models'

export const useAuthStore = defineStore('auth', () => {
  const userInfo = ref<UserinfoResponse | null>(null)

  const isAuthenticated = computed(() => !!userInfo.value)

  /** 初始化：從 server session 取得目前登入狀態 */
  async function fetchMe(): Promise<void> {
    try {
      userInfo.value = await $fetch<UserinfoResponse>('/auth/me')
    } catch {
      userInfo.value = null
    }
  }

  /** 觸發登入：導向 server /auth/authorize，server 接手 PKCE 並 redirect 到認證中心 */
  function login(): void {
    window.location.href = '/auth/authorize'
  }

  /** 登出：呼叫 server /auth/logout，server 撤銷 token、清除 session 後 redirect */
  async function logout(): Promise<void> {
    userInfo.value = null
    await $fetch('/auth/logout', { method: 'POST' })
  }

  return {
    userInfo,
    isAuthenticated,
    fetchMe,
    login,
    logout,
  }
})
