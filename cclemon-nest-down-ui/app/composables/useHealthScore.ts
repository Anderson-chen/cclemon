import { computed } from 'vue'
import { useDietStore } from '@/stores/dietStore'
import { useExerciseStore } from '@/stores/exerciseStore'
import { useWaterStore } from '@/stores/waterStore'
import { useGoalStore } from '@/stores/goalStore'
import { useReportStore } from '@/stores/reportStore'
import {
  calcDayScore,
  getDietScore,
  getExerciseScore,
  getWaterScore,
  getConsistencyScore,
  getScoreColor
} from '@/utils/scoreCalc'

export function useHealthScore() {
  const diet = useDietStore()
  const exercise = useExerciseStore()
  const water = useWaterStore()
  const goals = useGoalStore()
  const report = useReportStore()

  const dietScore = computed(() =>
    getDietScore(diet.totalCalories, goals.goals.dailyCalories || 2000)
  )

  const exerciseScore = computed(() =>
    getExerciseScore(exercise.hasWorkoutToday)
  )

  const waterScore = computed(() =>
    getWaterScore(water.todayTotal, goals.goals.dailyWaterMl || 2000)
  )

  const consistencyScore = computed(() => {
    const last7 = report.last7Scores
    const recorded = last7.filter(s => s.score > 0).length
    return getConsistencyScore(recorded, 7)
  })

  const todayScore = computed(() =>
    calcDayScore(dietScore.value, exerciseScore.value, waterScore.value, consistencyScore.value)
  )

  const scoreColor = computed(() => getScoreColor(todayScore.value))

  const breakdown = computed(() => ({
    diet: dietScore.value,
    exercise: exerciseScore.value,
    water: waterScore.value,
    consistency: consistencyScore.value
  }))

  return {
    todayScore,
    scoreColor,
    dietScore,
    exerciseScore,
    waterScore,
    consistencyScore,
    breakdown
  }
}
