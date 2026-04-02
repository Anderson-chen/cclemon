import type { Meal, FoodItem } from '@/types/models'
import { apiGet, apiPost, apiDelete } from './client'

export const dietApi = {
  getMeals: (date: string) => apiGet<Meal[]>(`/meals?date=${date}`),
  addMeal: (meal: Omit<Meal, 'id' | 'createdAt'>) => apiPost<Meal>('/meals', meal),
  deleteMeal: (id: string) => apiDelete(`/meals/${id}`),
  copyYesterday: () => apiPost<Meal[]>('/meals/copy-yesterday', {}),
  getFoodLibrary: () => apiGet<FoodItem[]>('/food-library'),
  addFood: (food: Omit<FoodItem, 'id'>) => apiPost<FoodItem>('/food-library', food),
}
