import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Workout, WorkoutTemplate, PersonalRecord } from '@/types/models'
import { today } from '@/utils/dateUtils'
import { useUiStore } from './uiStore'
import { exerciseApi } from '@/api/exercise'

export const useExerciseStore = defineStore('exercise', () => {
  const ui = useUiStore()

  const workouts = ref<Workout[]>([])
  const templates = ref<WorkoutTemplate[]>([])
  const prs = ref<PersonalRecord[]>([])

  const todayWorkouts = computed(() => workouts.value.filter(w => w.date === today()))
  const totalCaloriesBurned = computed(() =>
    todayWorkouts.value.reduce((s, w) => s + w.estimatedCaloriesBurned, 0)
  )
  const totalWorkoutMinutes = computed(() =>
    todayWorkouts.value.reduce((s, w) => s + w.totalMinutes, 0)
  )
  const hasWorkoutToday = computed(() => todayWorkouts.value.length > 0)

  async function fetchWorkouts(date: string) {
    ui.setLoading('fetchWorkouts', true)
    try {
      workouts.value = await exerciseApi.getWorkouts(date)
    } finally {
      ui.setLoading('fetchWorkouts', false)
    }
  }

  async function fetchTemplates() {
    templates.value = await exerciseApi.getTemplates()
  }

  async function fetchPRs() {
    prs.value = await exerciseApi.getPRs()
  }

  async function addWorkout(workout: Omit<Workout, 'id'>) {
    ui.setLoading('addWorkout', true)
    try {
      const w = await exerciseApi.addWorkout(workout)
      workouts.value.push(w)
      ui.showToast({ type: 'success', message: '運動紀錄已新增' })
      return w
    } finally {
      ui.setLoading('addWorkout', false)
    }
  }

  async function deleteWorkout(id: string) {
    workouts.value = workouts.value.filter(w => w.id !== id)
    try {
      await exerciseApi.deleteWorkout(id)
    } catch {
      await fetchWorkouts(today())
    }
  }

  async function saveTemplate(template: Omit<WorkoutTemplate, 'id'>) {
    const t = await exerciseApi.saveTemplate(template)
    templates.value.push(t)
    ui.showToast({ type: 'success', message: '訓練模板已儲存' })
  }

  async function deleteTemplate(id: string) {
    templates.value = templates.value.filter(t => t.id !== id)
    await exerciseApi.deleteTemplate(id)
  }

  function checkAndUpdatePR(exerciseName: string, weight: number, reps: number): boolean {
    const existing = prs.value.find(p => p.exerciseName === exerciseName)
    if (!existing || (weight >= (existing.weight || 0) && reps >= (existing.reps || 0))) {
      const newPR: PersonalRecord = { exerciseName, weight, reps, date: today() }
      const idx = prs.value.findIndex(p => p.exerciseName === exerciseName)
      if (idx >= 0) {
        prs.value[idx] = newPR
      } else {
        prs.value.push(newPR)
      }
      return true
    }
    return false
  }

  return {
    workouts,
    templates,
    prs,
    todayWorkouts,
    totalCaloriesBurned,
    totalWorkoutMinutes,
    hasWorkoutToday,
    fetchWorkouts,
    fetchTemplates,
    fetchPRs,
    addWorkout,
    deleteWorkout,
    saveTemplate,
    deleteTemplate,
    checkAndUpdatePR
  }
})
