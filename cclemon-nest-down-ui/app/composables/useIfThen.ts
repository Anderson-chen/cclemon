import { computed } from 'vue'
import { useIfThenStore } from '@/stores/ifThenStore'

export function useIfThen() {
  const store = useIfThenStore()

  const hasActivePlan = computed(() => !!store.firstActivePlan)
  const currentPlan = computed(() => store.firstActivePlan)

  function dismiss() {
    store.dismissCurrentPlan()
  }

  return {
    hasActivePlan,
    currentPlan,
    allPlans: computed(() => store.plans),
    activePlans: computed(() => store.activePlans),
    dismiss
  }
}
