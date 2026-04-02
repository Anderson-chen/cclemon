/**
 * [業務說明]
 * Client-side axios 實例，所有請求打到 Nuxt server /api/* proxy。
 * Token 管理完全由 server-side BFF 負責，client 不持有任何 token。
 *
 * [技術考量]
 * - baseURL 為相對路徑 /api，由 Nuxt server/api/[...].ts 代理至 Gateway
 * - 401 表示 server session 已失效，直接 redirect 到 /auth/login
 * - 不再需要 refresh/localStorage 邏輯（已移至 server/api/[...].ts）
 */
import axios from 'axios'
import type { AxiosInstance } from 'axios'

const client: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// 401 → session 失效，redirect 到登入頁
client.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401 && typeof window !== 'undefined') {
      window.location.href = '/auth/login'
    }
    return Promise.reject(error)
  },
)

export const apiGet = <T>(path: string) =>
  client.get<T>(path).then((r) => r.data)

export const apiPost = <T>(path: string, body: unknown) =>
  client.post<T>(path, body).then((r) => r.data)

export const apiPut = <T>(path: string, body: unknown) =>
  client.put<T>(path, body).then((r) => r.data)

export const apiPatch = <T>(path: string, body: unknown) =>
  client.patch<T>(path, body).then((r) => r.data)

export const apiDelete = (path: string) =>
  client.delete(path).then((r) => r.data)

export default client
