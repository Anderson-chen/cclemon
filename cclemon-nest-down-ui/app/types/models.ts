export interface UserinfoResponse {
  sub: string
  email?: string
  name?: string
  aud?: string[]
  iss?: string
  exp?: number
  iat?: number
}

export interface User {
  id: string
  name: string
  email: string
  avatarUrl?: string
  birthdate?: string
  height?: number
  startWeight?: number
  goals: UserGoals
}

export interface UserGoals {
  dailyCalories?: number
  weeklyExerciseDays?: number
  dailyWaterMl?: number
  targetWeight?: number
}

export interface Meal {
  id: string
  userId: string
  date: string
  type: 'breakfast' | 'lunch' | 'dinner' | 'snack'
  foods: FoodEntry[]
  createdAt: string
}

export interface FoodEntry {
  foodId?: string
  name: string
  calories: number
  protein: number
  carbs: number
  fat: number
  amount: number
}

export interface Workout {
  id: string
  userId: string
  date: string
  exercises: ExerciseEntry[]
  notes?: string
  totalMinutes: number
  estimatedCaloriesBurned: number
}

export interface ExerciseEntry {
  name: string
  sets?: number
  reps?: number
  weight?: number
  durationMinutes?: number
  isPR?: boolean
}

export interface WeightEntry {
  id: string
  userId: string
  date: string
  weight: number
  measurements?: BodyMeasurements
}

export interface BodyMeasurements {
  waist?: number
  hip?: number
  chest?: number
  arm?: number
  thigh?: number
}

export interface WaterEntry {
  id: string
  userId: string
  date: string
  totalMl: number
  logs: { time: string; ml: number }[]
}

export interface DayScore {
  date: string
  score: number
  breakdown: {
    diet: number
    exercise: number
    water: number
    consistency: number
  }
}

export interface IfThenPlan {
  id: string
  trigger: string
  action: string
  category: 'exercise' | 'diet' | 'general'
  atRiskWindowStart: string
  atRiskWindowEnd: string
  active: boolean
}

export interface BodyPhoto {
  id: string
  userId: string
  date: string
  url: string
  thumbnailUrl: string
  angle?: 'front' | 'side' | 'back'
}

export interface FoodItem {
  id: string
  name: string
  calories: number
  protein: number
  carbs: number
  fat: number
  servingGrams: number
}

export interface WorkoutTemplate {
  id: string
  name: string
  exercises: ExerciseEntry[]
}

export interface PersonalRecord {
  exerciseName: string
  weight?: number
  reps?: number
  date: string
}
