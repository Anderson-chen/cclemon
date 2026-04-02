import type { IfThenPlan } from '@/types/models'
import { apiGet, apiPost, apiPatch, apiDelete } from './client'

export const ifThenApi = {
  getPlans: () => apiGet<IfThenPlan[]>('/if-then'),
  createPlan: (p: Omit<IfThenPlan, 'id'>) => apiPost<IfThenPlan>('/if-then', p),
  updatePlan: (id: string, data: Partial<IfThenPlan>) => apiPatch<IfThenPlan>(`/if-then/${id}`, data),
  deletePlan: (id: string) => apiDelete(`/if-then/${id}`),
}
