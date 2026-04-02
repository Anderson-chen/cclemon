
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


export const CopyYesterdayButton: typeof import("../app/components/diet/CopyYesterdayButton.vue")['default']
export const FoodSearchModal: typeof import("../app/components/diet/FoodSearchModal.vue")['default']
export const MealCard: typeof import("../app/components/diet/MealCard.vue")['default']
export const NutritionBar: typeof import("../app/components/diet/NutritionBar.vue")['default']
export const ExerciseTimer: typeof import("../app/components/exercise/ExerciseTimer.vue")['default']
export const PRBadge: typeof import("../app/components/exercise/PRBadge.vue")['default']
export const TemplateSelector: typeof import("../app/components/exercise/TemplateSelector.vue")['default']
export const WorkoutCard: typeof import("../app/components/exercise/WorkoutCard.vue")['default']
export const DailySummaryGrid: typeof import("../app/components/home/DailySummaryGrid.vue")['default']
export const GoalRings: typeof import("../app/components/home/GoalRings.vue")['default']
export const HealthScoreCard: typeof import("../app/components/home/HealthScoreCard.vue")['default']
export const IfThenReminderCard: typeof import("../app/components/home/IfThenReminderCard.vue")['default']
export const QuickLogBar: typeof import("../app/components/home/QuickLogBar.vue")['default']
export const StreakCard: typeof import("../app/components/home/StreakCard.vue")['default']
export const WaterWidget: typeof import("../app/components/home/WaterWidget.vue")['default']
export const BottomNav: typeof import("../app/components/layout/BottomNav.vue")['default']
export const PageShell: typeof import("../app/components/layout/PageShell.vue")['default']
export const SidebarNav: typeof import("../app/components/layout/SidebarNav.vue")['default']
export const HeatmapCalendar: typeof import("../app/components/report/HeatmapCalendar.vue")['default']
export const InsightNarrative: typeof import("../app/components/report/InsightNarrative.vue")['default']
export const TrendChart: typeof import("../app/components/report/TrendChart.vue")['default']
export const AppToast: typeof import("../app/components/shared/AppToast.vue")['default']
export const BadgeTag: typeof import("../app/components/shared/BadgeTag.vue")['default']
export const BottomSheet: typeof import("../app/components/shared/BottomSheet.vue")['default']
export const GlassButton: typeof import("../app/components/shared/GlassButton.vue")['default']
export const GlassCard: typeof import("../app/components/shared/GlassCard.vue")['default']
export const InputField: typeof import("../app/components/shared/InputField.vue")['default']
export const LoadingSkeleton: typeof import("../app/components/shared/LoadingSkeleton.vue")['default']
export const OrangeButton: typeof import("../app/components/shared/OrangeButton.vue")['default']
export const BodyPhotoCompare: typeof import("../app/components/weight/BodyPhotoCompare.vue")['default']
export const BodyPhotoUpload: typeof import("../app/components/weight/BodyPhotoUpload.vue")['default']
export const MeasurementForm: typeof import("../app/components/weight/MeasurementForm.vue")['default']
export const WeightChart: typeof import("../app/components/weight/WeightChart.vue")['default']
export const NuxtWelcome: typeof import("../node_modules/nuxt/dist/app/components/welcome.vue")['default']
export const NuxtLayout: typeof import("../node_modules/nuxt/dist/app/components/nuxt-layout")['default']
export const NuxtErrorBoundary: typeof import("../node_modules/nuxt/dist/app/components/nuxt-error-boundary.vue")['default']
export const ClientOnly: typeof import("../node_modules/nuxt/dist/app/components/client-only")['default']
export const DevOnly: typeof import("../node_modules/nuxt/dist/app/components/dev-only")['default']
export const ServerPlaceholder: typeof import("../node_modules/nuxt/dist/app/components/server-placeholder")['default']
export const NuxtLink: typeof import("../node_modules/nuxt/dist/app/components/nuxt-link")['default']
export const NuxtLoadingIndicator: typeof import("../node_modules/nuxt/dist/app/components/nuxt-loading-indicator")['default']
export const NuxtTime: typeof import("../node_modules/nuxt/dist/app/components/nuxt-time.vue")['default']
export const NuxtRouteAnnouncer: typeof import("../node_modules/nuxt/dist/app/components/nuxt-route-announcer")['default']
export const NuxtAnnouncer: typeof import("../node_modules/nuxt/dist/app/components/nuxt-announcer")['default']
export const NuxtImg: typeof import("../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtImg']
export const NuxtPicture: typeof import("../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtPicture']
export const NuxtPage: typeof import("../node_modules/nuxt/dist/pages/runtime/page")['default']
export const NoScript: typeof import("../node_modules/nuxt/dist/head/runtime/components")['NoScript']
export const Link: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Link']
export const Base: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Base']
export const Title: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Title']
export const Meta: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Meta']
export const Style: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Style']
export const Head: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Head']
export const Html: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Html']
export const Body: typeof import("../node_modules/nuxt/dist/head/runtime/components")['Body']
export const NuxtIsland: typeof import("../node_modules/nuxt/dist/app/components/nuxt-island")['default']
export const LazyCopyYesterdayButton: LazyComponent<typeof import("../app/components/diet/CopyYesterdayButton.vue")['default']>
export const LazyFoodSearchModal: LazyComponent<typeof import("../app/components/diet/FoodSearchModal.vue")['default']>
export const LazyMealCard: LazyComponent<typeof import("../app/components/diet/MealCard.vue")['default']>
export const LazyNutritionBar: LazyComponent<typeof import("../app/components/diet/NutritionBar.vue")['default']>
export const LazyExerciseTimer: LazyComponent<typeof import("../app/components/exercise/ExerciseTimer.vue")['default']>
export const LazyPRBadge: LazyComponent<typeof import("../app/components/exercise/PRBadge.vue")['default']>
export const LazyTemplateSelector: LazyComponent<typeof import("../app/components/exercise/TemplateSelector.vue")['default']>
export const LazyWorkoutCard: LazyComponent<typeof import("../app/components/exercise/WorkoutCard.vue")['default']>
export const LazyDailySummaryGrid: LazyComponent<typeof import("../app/components/home/DailySummaryGrid.vue")['default']>
export const LazyGoalRings: LazyComponent<typeof import("../app/components/home/GoalRings.vue")['default']>
export const LazyHealthScoreCard: LazyComponent<typeof import("../app/components/home/HealthScoreCard.vue")['default']>
export const LazyIfThenReminderCard: LazyComponent<typeof import("../app/components/home/IfThenReminderCard.vue")['default']>
export const LazyQuickLogBar: LazyComponent<typeof import("../app/components/home/QuickLogBar.vue")['default']>
export const LazyStreakCard: LazyComponent<typeof import("../app/components/home/StreakCard.vue")['default']>
export const LazyWaterWidget: LazyComponent<typeof import("../app/components/home/WaterWidget.vue")['default']>
export const LazyBottomNav: LazyComponent<typeof import("../app/components/layout/BottomNav.vue")['default']>
export const LazyPageShell: LazyComponent<typeof import("../app/components/layout/PageShell.vue")['default']>
export const LazySidebarNav: LazyComponent<typeof import("../app/components/layout/SidebarNav.vue")['default']>
export const LazyHeatmapCalendar: LazyComponent<typeof import("../app/components/report/HeatmapCalendar.vue")['default']>
export const LazyInsightNarrative: LazyComponent<typeof import("../app/components/report/InsightNarrative.vue")['default']>
export const LazyTrendChart: LazyComponent<typeof import("../app/components/report/TrendChart.vue")['default']>
export const LazyAppToast: LazyComponent<typeof import("../app/components/shared/AppToast.vue")['default']>
export const LazyBadgeTag: LazyComponent<typeof import("../app/components/shared/BadgeTag.vue")['default']>
export const LazyBottomSheet: LazyComponent<typeof import("../app/components/shared/BottomSheet.vue")['default']>
export const LazyGlassButton: LazyComponent<typeof import("../app/components/shared/GlassButton.vue")['default']>
export const LazyGlassCard: LazyComponent<typeof import("../app/components/shared/GlassCard.vue")['default']>
export const LazyInputField: LazyComponent<typeof import("../app/components/shared/InputField.vue")['default']>
export const LazyLoadingSkeleton: LazyComponent<typeof import("../app/components/shared/LoadingSkeleton.vue")['default']>
export const LazyOrangeButton: LazyComponent<typeof import("../app/components/shared/OrangeButton.vue")['default']>
export const LazyBodyPhotoCompare: LazyComponent<typeof import("../app/components/weight/BodyPhotoCompare.vue")['default']>
export const LazyBodyPhotoUpload: LazyComponent<typeof import("../app/components/weight/BodyPhotoUpload.vue")['default']>
export const LazyMeasurementForm: LazyComponent<typeof import("../app/components/weight/MeasurementForm.vue")['default']>
export const LazyWeightChart: LazyComponent<typeof import("../app/components/weight/WeightChart.vue")['default']>
export const LazyNuxtWelcome: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/welcome.vue")['default']>
export const LazyNuxtLayout: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-layout")['default']>
export const LazyNuxtErrorBoundary: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-error-boundary.vue")['default']>
export const LazyClientOnly: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/client-only")['default']>
export const LazyDevOnly: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/dev-only")['default']>
export const LazyServerPlaceholder: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/server-placeholder")['default']>
export const LazyNuxtLink: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-link")['default']>
export const LazyNuxtLoadingIndicator: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-loading-indicator")['default']>
export const LazyNuxtTime: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-time.vue")['default']>
export const LazyNuxtRouteAnnouncer: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-route-announcer")['default']>
export const LazyNuxtAnnouncer: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-announcer")['default']>
export const LazyNuxtImg: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtImg']>
export const LazyNuxtPicture: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-stubs")['NuxtPicture']>
export const LazyNuxtPage: LazyComponent<typeof import("../node_modules/nuxt/dist/pages/runtime/page")['default']>
export const LazyNoScript: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['NoScript']>
export const LazyLink: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Link']>
export const LazyBase: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Base']>
export const LazyTitle: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Title']>
export const LazyMeta: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Meta']>
export const LazyStyle: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Style']>
export const LazyHead: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Head']>
export const LazyHtml: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Html']>
export const LazyBody: LazyComponent<typeof import("../node_modules/nuxt/dist/head/runtime/components")['Body']>
export const LazyNuxtIsland: LazyComponent<typeof import("../node_modules/nuxt/dist/app/components/nuxt-island")['default']>

export const componentNames: string[]
