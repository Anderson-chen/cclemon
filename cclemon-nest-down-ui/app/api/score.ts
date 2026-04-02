import type { DayScore } from '@/types/models'
import { apiGet } from './client'

export const scoreApi = {
  getToday: () => apiGet<DayScore>('/score/today'),
  getHistory: (days = 30) => apiGet<DayScore[]>(`/score/history?days=${days}`),
}
