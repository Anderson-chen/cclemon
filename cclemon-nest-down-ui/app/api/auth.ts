import type { User } from '@/types/models'
import { apiGet, apiPost } from './client'

export interface LoginResponse { token: string; user: User }

export const authApi = {
  login: (email: string, password: string) =>
    apiPost<LoginResponse>('/auth/login', { email, password }),
  getMe: () => apiGet<User>('/auth/me'),
  logout: () => apiPost<void>('/auth/logout', {}),
}
