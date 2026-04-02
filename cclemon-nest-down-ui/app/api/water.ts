import type { WaterEntry } from '@/types/models'
import { apiGet, apiPost } from './client'

export const waterApi = {
  getToday: (date: string) => apiGet<WaterEntry | null>(`/water?date=${date}`),
  logWater: (ml: number) => apiPost<WaterEntry>('/water/log', { ml }),
}
