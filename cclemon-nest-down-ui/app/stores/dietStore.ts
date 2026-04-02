import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Meal, FoodItem } from '@/types/models'
import { today, yesterday } from '@/utils/dateUtils'
import { useUiStore } from './uiStore'
import { dietApi } from '@/api/diet'

export const useDietStore = defineStore('diet', () => {
  const ui = useUiStore()

  const meals = ref<Meal[]>([])
  const foodLibrary = ref<FoodItem[]>([])

  const todayMeals = computed(() => meals.value.filter(m => m.date === today()))
  const totalCalories = computed(() =>
    todayMeals.value.reduce((s, m) => s + m.foods.reduce((fs, f) => fs + f.calories, 0), 0)
  )
  const totalProtein = computed(() =>
    todayMeals.value.reduce((s, m) => s + m.foods.reduce((fs, f) => fs + f.protein, 0), 0)
  )
  const totalCarbs = computed(() =>
    todayMeals.value.reduce((s, m) => s + m.foods.reduce((fs, f) => fs + f.carbs, 0), 0)
  )
  const totalFat = computed(() =>
    todayMeals.value.reduce((s, m) => s + m.foods.reduce((fs, f) => fs + f.fat, 0), 0)
  )
  const hasYesterdayMeals = computed(() => meals.value.some(m => m.date === yesterday()))
  const hasTodayMeals = computed(() => todayMeals.value.length > 0)

  function getMealsByDate(date: string): Meal[] {
    return meals.value.filter(m => m.date === date)
  }

  async function fetchMeals(date: string) {
    ui.setLoading('fetchMeals', true)
    try {
      const [todayData, yestData] = await Promise.all([
        dietApi.getMeals(date),
        dietApi.getMeals(yesterday()),
      ])
      // Replace data for these dates, keep others
      meals.value = meals.value.filter(m => m.date !== date && m.date !== yesterday())
      meals.value.push(...todayData, ...yestData)
    } finally {
      ui.setLoading('fetchMeals', false)
    }
  }

  async function fetchFoodLibrary() {
    foodLibrary.value = await dietApi.getFoodLibrary()
  }

  async function addMeal(meal: Omit<Meal, 'id' | 'createdAt'>) {
    ui.setLoading('addMeal', true)
    try {
      const newMeal = await dietApi.addMeal(meal)
      meals.value.push(newMeal)
      ui.showToast({ type: 'success', message: '餐點已新增' })
      return newMeal
    } catch {
      ui.showToast({ type: 'error', message: '新增失敗，請稍後再試' })
    } finally {
      ui.setLoading('addMeal', false)
    }
  }

  async function deleteMeal(id: string) {
    meals.value = meals.value.filter(m => m.id !== id) // optimistic
    try {
      await dietApi.deleteMeal(id)
      ui.showToast({ type: 'success', message: '已刪除' })
    } catch {
      await fetchMeals(today()) // revert
      ui.showToast({ type: 'error', message: '刪除失敗' })
    }
  }

  async function copyYesterday() {
    ui.setLoading('copyYesterday', true)
    try {
      const copied = await dietApi.copyYesterday()
      meals.value.push(...copied)
      await fetchMeals(today()) // refresh from server
      ui.showToast({ type: 'success', message: `已複製昨日餐點` })
    } finally {
      ui.setLoading('copyYesterday', false)
    }
  }

  async function addToLibrary(food: Omit<FoodItem, 'id'>) {
    const item = await dietApi.addFood(food)
    foodLibrary.value.push(item)
  }

  return {
    meals,
    foodLibrary,
    todayMeals,
    totalCalories,
    totalProtein,
    totalCarbs,
    totalFat,
    hasYesterdayMeals,
    hasTodayMeals,
    getMealsByDate,
    fetchMeals,
    fetchFoodLibrary,
    addMeal,
    deleteMeal,
    copyYesterday,
    addToLibrary
  }
})
