import type { WeightEntry } from '@/types/models'
import { apiGet, apiPost, apiDelete } from './client'

export const weightApi = {
  getWeightHistory: () => apiGet<WeightEntry[]>('/weight'),
  addWeight: (entry: Omit<WeightEntry, 'id'>) => apiPost<WeightEntry>('/weight', entry),
  deleteWeight: (id: string) => apiDelete(`/weight/${id}`),
}
