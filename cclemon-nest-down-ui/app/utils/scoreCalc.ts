import type { DayScore } from '@/types/models'

export function calcDayScore(
  diet: number,
  exercise: number,
  water: number,
  consistency: number
): number {
  return Math.round(diet * 0.35 + exercise * 0.35 + water * 0.20 + consistency * 0.10)
}

export function getDietScore(actualCalories: number, targetCalories: number): number {
  if (!targetCalories) return 0
  const ratio = actualCalories / targetCalories
  if (ratio >= 0.9 && ratio <= 1.1) return 100
  const diff = Math.abs(ratio - 1)
  return Math.max(0, Math.round(100 - diff * 200))
}

export function getExerciseScore(hasWorkout: boolean): number {
  return hasWorkout ? 100 : 0
}

export function getWaterScore(actual: number, target: number): number {
  if (!target) return 0
  return Math.min(100, Math.round((actual / target) * 100))
}

export function getConsistencyScore(recordedDays: number, totalDays: number = 7): number {
  return Math.round((recordedDays / totalDays) * 100)
}

export function getScoreColor(score: number): string {
  if (score >= 80) return '#0A9488'
  if (score >= 60) return '#E86010'
  return '#e53e3e'
}
