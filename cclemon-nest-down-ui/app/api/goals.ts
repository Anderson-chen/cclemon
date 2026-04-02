import type { UserGoals } from '@/types/models'
import { apiGet, apiPatch } from './client'

export const goalsApi = {
  getGoals: () => apiGet<UserGoals>('/goals'),
  updateGoals: (goals: Partial<UserGoals>) => apiPatch<UserGoals>('/goals', goals),
}
