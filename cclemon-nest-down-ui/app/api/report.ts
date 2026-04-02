import type { DayScore } from '@/types/models'
import { apiGet } from './client'

export const reportApi = {
  getWeekly: () => apiGet<DayScore[]>('/report/weekly'),
  getMonthly: () => apiGet<DayScore[]>('/report/monthly'),
}
