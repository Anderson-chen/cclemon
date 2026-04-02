
import type { DefineComponent, SlotsType } from 'vue'
type IslandComponent<T> = DefineComponent<{}, {refresh: () => Promise<void>}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, SlotsType<{ fallback: { error: unknown } }>> & T

type HydrationStrategies = {
  hydrateOnVisible?: IntersectionObserverInit | true
  hydrateOnIdle?: number | true
  hydrateOnInteraction?: keyof HTMLElementEventMap | Array<keyof HTMLElementEventMap> | true
  hydrateOnMediaQuery?: string
  hydrateAfter?: number
  hydrateWhen?: boolean
  hydrateNever?: true
}
type LazyComponent<T> = DefineComponent<HydrationStrategies, {}, {}, {}, {}, {}, {}, { hydrated: () => void }> & T

interface _GlobalComponents {
  CopyYesterdayButton: typeof import("../../app/components/diet/CopyYesterdayButton.vue")['default']
  FoodSearchModal: typeof import("../../app/components/diet/FoodSearchModal.vue")['default']
  MealCard: typeof import("../../app/components/diet/MealCard.vue")['default']
  NutritionBar: typeof import("../../app/components/diet/NutritionBar.vue")['default']
  ExerciseTimer: typeof import("../../app/components/exercise/ExerciseTimer.vue")['default']
  PRBadge: typeof import("../../app/components/exercise/PRBadge.vue")['default']
  TemplateSelector: typeof import("../../app/components/exercise/TemplateSelector.vue")['default']
  WorkoutCard: typeof import("../../app/components/exercise/WorkoutCard.vue")['default']
  DailySummaryGrid: typeof import("../../app/components/home/DailySummaryGrid.vue")['default']
  GoalRings: typeof import("../../app/components/home/GoalRings.vue")['default']
  HealthScoreCard: typeof import("../../app/components/home/HealthScoreCard.vue")['default']
  IfThenReminderCard: typeof import("../../app/components/home/IfThenReminderCard.vue")['default']
  QuickLogBar: typeof import("../../app/components/home/QuickLogBar.vue")['default']
  StreakCard: typeof import("../../app/components/home/StreakCard.vue")['default']
  WaterWidget: typeof import("../../app/components/home/WaterWidget.vue")['default']
  BottomNav: typeof import("../../app/components/layout/BottomNav.vue")['default']
  PageShell: typeof import("../../app/components/layout/PageShell.vue")['default']
  SidebarNav: typeof import("../../app/components/layout/SidebarNav.vue")['default']
  HeatmapCalendar: typeof import("../../app/components/report/HeatmapCalendar.vue")['default']
  InsightNarrative: typeof import("../../app/components/report/InsightNarrative.vue")['default']
  TrendChart: typeof import("../../app/components/report/TrendChart.vue")['default']
  AppToast: typeof import("../../app/components/shared/AppToast.vue")['default']
  BadgeTag: typeof import("../../app/components/shared/BadgeTag.vue")['default']
  BottomSheet: typeof import("../../app/components/shared/BottomSheet.vue")['default']
  GlassButton: typeof import("../../app/components/shared/GlassButton.vue")['default']
  GlassCard: typeof import("../../app/components/shared/GlassCard.vue")['default']
  InputField: typeof import("../../app/components/shared/InputField.vue")['default']
  LoadingSkeleton: typeof import("../../app/components/shared/LoadingSkeleton.vue")['default']
  OrangeButton: typeof import("../../app/components/shared/OrangeButton.vue")['default']
  BodyPhotoCompare: typeof import("../../app/components/weight/BodyPhotoCompare.vue")['default']
  BodyPhotoUpload: typeof import("../../app/components/weight/BodyPhotoUpload.vue")['default']
  MeasurementForm: typeof import("../../app/components/weight/MeasurementForm.vue")['default']
  WeightChart: typeof import("../../app/components/weight/WeightChart.vue")['default']
  NuxtWelcome: typeof import("../../node_modules/nuxt/dist/app/components/welcome.vue")['default']
  NuxtLayout: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-layout")['default']
  NuxtErrorBoundary: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-error-boundary.vue")['default']
  ClientOnly: typeof import("../../node_modules/nuxt/dist/app/components/client-only")['default']
  DevOnly: typeof import("../../node_modules/nuxt/dist/app/components/dev-only")['default']
  ServerPlaceholder: typeof import("../../node_modules/nuxt/dist/app/components/server-placeholder")['default']
  NuxtLink: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-link")['default']
  NuxtLoadingIndicator: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-loading-indicator")['default']
  NuxtTime: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-time.vue")['default']
  NuxtRouteAnnouncer: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-route-announcer")['default']
  NuxtAnnouncer: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-announcer")['default']
  NuxtImg: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtImg']
  NuxtPicture: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtPicture']
  NuxtPage: typeof import("../../node_modules/nuxt/dist/pages/runtime/page")['default']
  NoScript: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['NoScript']
  Link: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Link']
  Base: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Base']
  Title: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Title']
  Meta: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Meta']
  Style: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Style']
  Head: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Head']
  Html: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Html']
  Body: typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Body']
  NuxtIsland: typeof import("../../node_modules/nuxt/dist/app/components/nuxt-island")['default']
  LazyCopyYesterdayButton: LazyComponent<typeof import("../../app/components/diet/CopyYesterdayButton.vue")['default']>
  LazyFoodSearchModal: LazyComponent<typeof import("../../app/components/diet/FoodSearchModal.vue")['default']>
  LazyMealCard: LazyComponent<typeof import("../../app/components/diet/MealCard.vue")['default']>
  LazyNutritionBar: LazyComponent<typeof import("../../app/components/diet/NutritionBar.vue")['default']>
  LazyExerciseTimer: LazyComponent<typeof import("../../app/components/exercise/ExerciseTimer.vue")['default']>
  LazyPRBadge: LazyComponent<typeof import("../../app/components/exercise/PRBadge.vue")['default']>
  LazyTemplateSelector: LazyComponent<typeof import("../../app/components/exercise/TemplateSelector.vue")['default']>
  LazyWorkoutCard: LazyComponent<typeof import("../../app/components/exercise/WorkoutCard.vue")['default']>
  LazyDailySummaryGrid: LazyComponent<typeof import("../../app/components/home/DailySummaryGrid.vue")['default']>
  LazyGoalRings: LazyComponent<typeof import("../../app/components/home/GoalRings.vue")['default']>
  LazyHealthScoreCard: LazyComponent<typeof import("../../app/components/home/HealthScoreCard.vue")['default']>
  LazyIfThenReminderCard: LazyComponent<typeof import("../../app/components/home/IfThenReminderCard.vue")['default']>
  LazyQuickLogBar: LazyComponent<typeof import("../../app/components/home/QuickLogBar.vue")['default']>
  LazyStreakCard: LazyComponent<typeof import("../../app/components/home/StreakCard.vue")['default']>
  LazyWaterWidget: LazyComponent<typeof import("../../app/components/home/WaterWidget.vue")['default']>
  LazyBottomNav: LazyComponent<typeof import("../../app/components/layout/BottomNav.vue")['default']>
  LazyPageShell: LazyComponent<typeof import("../../app/components/layout/PageShell.vue")['default']>
  LazySidebarNav: LazyComponent<typeof import("../../app/components/layout/SidebarNav.vue")['default']>
  LazyHeatmapCalendar: LazyComponent<typeof import("../../app/components/report/HeatmapCalendar.vue")['default']>
  LazyInsightNarrative: LazyComponent<typeof import("../../app/components/report/InsightNarrative.vue")['default']>
  LazyTrendChart: LazyComponent<typeof import("../../app/components/report/TrendChart.vue")['default']>
  LazyAppToast: LazyComponent<typeof import("../../app/components/shared/AppToast.vue")['default']>
  LazyBadgeTag: LazyComponent<typeof import("../../app/components/shared/BadgeTag.vue")['default']>
  LazyBottomSheet: LazyComponent<typeof import("../../app/components/shared/BottomSheet.vue")['default']>
  LazyGlassButton: LazyComponent<typeof import("../../app/components/shared/GlassButton.vue")['default']>
  LazyGlassCard: LazyComponent<typeof import("../../app/components/shared/GlassCard.vue")['default']>
  LazyInputField: LazyComponent<typeof import("../../app/components/shared/InputField.vue")['default']>
  LazyLoadingSkeleton: LazyComponent<typeof import("../../app/components/shared/LoadingSkeleton.vue")['default']>
  LazyOrangeButton: LazyComponent<typeof import("../../app/components/shared/OrangeButton.vue")['default']>
  LazyBodyPhotoCompare: LazyComponent<typeof import("../../app/components/weight/BodyPhotoCompare.vue")['default']>
  LazyBodyPhotoUpload: LazyComponent<typeof import("../../app/components/weight/BodyPhotoUpload.vue")['default']>
  LazyMeasurementForm: LazyComponent<typeof import("../../app/components/weight/MeasurementForm.vue")['default']>
  LazyWeightChart: LazyComponent<typeof import("../../app/components/weight/WeightChart.vue")['default']>
  LazyNuxtWelcome: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/welcome.vue")['default']>
  LazyNuxtLayout: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-layout")['default']>
  LazyNuxtErrorBoundary: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-error-boundary.vue")['default']>
  LazyClientOnly: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/client-only")['default']>
  LazyDevOnly: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/dev-only")['default']>
  LazyServerPlaceholder: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/server-placeholder")['default']>
  LazyNuxtLink: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-link")['default']>
  LazyNuxtLoadingIndicator: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-loading-indicator")['default']>
  LazyNuxtTime: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-time.vue")['default']>
  LazyNuxtRouteAnnouncer: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-route-announcer")['default']>
  LazyNuxtAnnouncer: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-announcer")['default']>
  LazyNuxtImg: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtImg']>
  LazyNuxtPicture: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtPicture']>
  LazyNuxtPage: LazyComponent<typeof import("../../node_modules/nuxt/dist/pages/runtime/page")['default']>
  LazyNoScript: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['NoScript']>
  LazyLink: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Link']>
  LazyBase: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Base']>
  LazyTitle: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Title']>
  LazyMeta: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Meta']>
  LazyStyle: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Style']>
  LazyHead: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Head']>
  LazyHtml: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Html']>
  LazyBody: LazyComponent<typeof import("../../node_modules/nuxt/dist/head/runtime/components")['Body']>
  LazyNuxtIsland: LazyComponent<typeof import("../../node_modules/nuxt/dist/app/components/nuxt-island")['default']>
}

declare module 'vue' {
  export interface GlobalComponents extends _GlobalComponents { }
}

export {}
