import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserGoals } from '@/types/models'
import { useAuthStore } from './authStore'
import { useUiStore } from './uiStore'
import { goalsApi } from '@/api/goals'

export const useGoalStore = defineStore('goals', () => {
  const auth = useAuthStore()
  const ui = useUiStore()

  const goals = ref<UserGoals>({
    dailyCalories: 2000,
    weeklyExerciseDays: 3,
    dailyWaterMl: 2000,
    targetWeight: 65
  })

  const notificationsEnabled = ref(false)

  async function fetchGoals() {
    goals.value = await goalsApi.getGoals()
  }

  async function updateGoals(newGoals: Partial<UserGoals>) {
    ui.setLoading('updateGoals', true)
    try {
      goals.value = await goalsApi.updateGoals(newGoals)
      if (auth.user) {
        auth.updateUser({ goals: goals.value })
      }
      ui.showToast({ type: 'success', message: '目標已更新' })
    } finally {
      ui.setLoading('updateGoals', false)
    }
  }

  async function toggleNotifications(enabled: boolean) {
    notificationsEnabled.value = enabled
    if (enabled) {
      try {
        const permission = await Notification.requestPermission()
        if (permission !== 'granted') {
          notificationsEnabled.value = false
          ui.showToast({ type: 'error', message: '通知權限未授予' })
          return
        }
      } catch {
        notificationsEnabled.value = false
      }
    }
    ui.showToast({ type: 'success', message: enabled ? '通知已開啟' : '通知已關閉' })
  }

  return {
    goals,
    notificationsEnabled,
    fetchGoals,
    updateGoals,
    toggleNotifications
  }
})
