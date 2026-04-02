import type { Router } from 'vue-router'

let _router: Router | null = null

export function initApiError(router: Router) {
  _router = router
}

export function parseApiError(err: unknown): string {
  const e = err as {
    response?: { status: number; data?: { message?: string } }
    message?: string
  }
  if (!e.response) return '網路連線失敗'
  const { status, data } = e.response
  if (status === 401) {
    _router?.push('/auth/login')
    return 'Token 已過期，請重新登入'
  }
  if (status === 403) return '權限不足'
  if (status === 404) return '找不到資料'
  if (status === 422) return data?.message || '資料驗證失敗'
  if (status >= 500) return '伺服器錯誤，請稍後再試'
  return '發生未知錯誤'
}
