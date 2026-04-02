import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { IfThenPlan } from '@/types/models'
import { isInTimeWindow } from '@/utils/dateUtils'
import { useUiStore } from './uiStore'
import { ifThenApi } from '@/api/ifThen'

export const useIfThenStore = defineStore('ifThen', () => {
  const ui = useUiStore()

  const plans = ref<IfThenPlan[]>([])
  const dismissedIds = ref<Set<string>>(new Set())

  const activePlans = computed(() =>
    plans.value.filter(p => p.active && !dismissedIds.value.has(p.id))
  )

  const currentWindowPlans = computed(() =>
    activePlans.value.filter(p =>
      isInTimeWindow(p.atRiskWindowStart, p.atRiskWindowEnd)
    )
  )

  const firstActivePlan = computed(() => currentWindowPlans.value[0] || null)

  async function fetchPlans() {
    plans.value = await ifThenApi.getPlans()
  }

  async function addPlan(plan: Omit<IfThenPlan, 'id'>) {
    const p = await ifThenApi.createPlan(plan)
    plans.value.push(p)
    ui.showToast({ type: 'success', message: '備用計畫已新增' })
  }

  async function updatePlan(id: string, data: Partial<IfThenPlan>) {
    const p = await ifThenApi.updatePlan(id, data)
    const idx = plans.value.findIndex(x => x.id === id)
    if (idx >= 0) plans.value[idx] = p
  }

  async function deletePlan(id: string) {
    plans.value = plans.value.filter(p => p.id !== id)
    await ifThenApi.deletePlan(id)
    ui.showToast({ type: 'success', message: '已刪除備用計畫' })
  }

  async function togglePlan(id: string) {
    const plan = plans.value.find(p => p.id === id)
    if (plan) {
      await updatePlan(id, { active: !plan.active })
    }
  }

  function dismissCurrentPlan() {
    const plan = firstActivePlan.value
    if (plan) {
      dismissedIds.value.add(plan.id)
      setTimeout(() => dismissedIds.value.delete(plan.id), 3600000)
    }
  }

  return {
    plans,
    activePlans,
    currentWindowPlans,
    firstActivePlan,
    fetchPlans,
    addPlan,
    updatePlan,
    deletePlan,
    togglePlan,
    dismissCurrentPlan
  }
})
