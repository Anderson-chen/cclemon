import { computed } from 'vue'
import { useGoalStore } from '@/stores/goalStore'
import { useDietStore } from '@/stores/dietStore'
import { useExerciseStore } from '@/stores/exerciseStore'
import { useWaterStore } from '@/stores/waterStore'

export function useGoals() {
  const goals = useGoalStore()
  const diet = useDietStore()
  const exercise = useExerciseStore()
  const water = useWaterStore()

  const calorieProgress = computed(() => {
    const target = goals.goals.dailyCalories || 2000
    return Math.min(1, diet.totalCalories / target)
  })

  const caloriePercent = computed(() => Math.round(calorieProgress.value * 100))

  const waterProgress = computed(() => {
    const target = goals.goals.dailyWaterMl || 2000
    return Math.min(1, water.todayTotal / target)
  })

  const waterPercent = computed(() => Math.round(waterProgress.value * 100))

  const exerciseProgress = computed(() => {
    const target = goals.goals.weeklyExerciseDays || 3
    // Count workouts this week (simplified: today's workout counts as 1)
    return Math.min(1, exercise.hasWorkoutToday ? 1 / target : 0)
  })

  const isCalorieGoalMet = computed(() => {
    const target = goals.goals.dailyCalories || 2000
    const ratio = diet.totalCalories / target
    return ratio >= 0.9 && ratio <= 1.1
  })

  const isWaterGoalMet = computed(() => waterProgress.value >= 1)
  const isExerciseGoalMet = computed(() => exercise.hasWorkoutToday)

  return {
    goals: computed(() => goals.goals),
    calorieProgress,
    caloriePercent,
    waterProgress,
    waterPercent,
    exerciseProgress,
    isCalorieGoalMet,
    isWaterGoalMet,
    isExerciseGoalMet,
    totalCalories: computed(() => diet.totalCalories),
    totalWater: computed(() => water.todayTotal),
    hasWorkout: computed(() => exercise.hasWorkoutToday)
  }
}
