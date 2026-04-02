import type { Workout, WorkoutTemplate, PersonalRecord } from '@/types/models'
import { apiGet, apiPost, apiDelete } from './client'

export const exerciseApi = {
  getWorkouts: (date: string) => apiGet<Workout[]>(`/workouts?date=${date}`),
  addWorkout: (w: Omit<Workout, 'id'>) => apiPost<Workout>('/workouts', w),
  deleteWorkout: (id: string) => apiDelete(`/workouts/${id}`),
  getTemplates: () => apiGet<WorkoutTemplate[]>('/exercise/templates'),
  saveTemplate: (t: Omit<WorkoutTemplate, 'id'>) => apiPost<WorkoutTemplate>('/exercise/templates', t),
  deleteTemplate: (id: string) => apiDelete(`/exercise/templates/${id}`),
  getPRs: () => apiGet<PersonalRecord[]>('/exercise/prs'),
}
