<script setup lang="ts">
import { useIfThen } from '@/composables/useIfThen'
import { useRouter } from 'vue-router'

const { hasActivePlan, currentPlan, dismiss } = useIfThen()
const router = useRouter()

const categoryColors: Record<string, { bg: string; border: string; text: string }> = {
  exercise: { bg: 'rgba(10,148,136,0.08)', border: 'rgba(10,148,136,0.2)', text: '#0A9488' },
  diet: { bg: 'rgba(232,96,16,0.08)', border: 'rgba(232,96,16,0.2)', text: '#E86010' },
  general: { bg: 'rgba(99,102,241,0.08)', border: 'rgba(99,102,241,0.2)', text: '#6366f1' },
}
</script>

<template>
  <Transition name="ifthen">
    <div v-if="hasActivePlan && currentPlan" class="ifthen-card" :style="{
      background: categoryColors[currentPlan.category]?.bg,
      borderColor: categoryColors[currentPlan.category]?.border
    }">
      <div class="ifthen-icon">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"/>
          <path d="M12 8v4m0 4h.01"/>
        </svg>
      </div>

      <div class="ifthen-body">
        <p class="ifthen-trigger">
          <span class="trigger-if">如果</span> {{ currentPlan.trigger }}
        </p>
        <p class="ifthen-action">
          <span class="action-then">那就</span> {{ currentPlan.action }}
        </p>
      </div>

      <div class="ifthen-actions">
        <button class="ifthen-confirm" :style="{ background: categoryColors[currentPlan.category]?.text }" @click="dismiss">
          我會這樣做
        </button>
        <button class="ifthen-view" @click="router.push('/settings')">
          查看計畫
        </button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.ifthen-card {
  border-width: 1px;
  border-style: solid;
  border-radius: 16px 4px 16px 4px;
  padding: 16px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.ifthen-icon {
  color: #0A9488;
  margin-bottom: 10px;
}

.ifthen-body {
  margin-bottom: 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.ifthen-trigger,
.ifthen-action {
  font-size: 14px;
  color: #083830;
  line-height: 1.5;
}

.trigger-if,
.action-then {
  font-weight: 700;
  margin-right: 4px;
}

.trigger-if {
  color: #0A7870;
}

.action-then {
  color: #0A9488;
}

.ifthen-actions {
  display: flex;
  gap: 10px;
}

.ifthen-confirm {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 10px;
  color: white;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 200ms;
  min-height: 40px;
}

.ifthen-confirm:hover {
  opacity: 0.88;
}

.ifthen-view {
  padding: 10px 14px;
  border: 1.5px solid rgba(10, 148, 136, 0.25);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.5);
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #0A9488;
  cursor: pointer;
  transition: all 200ms;
  min-height: 40px;
}

.ifthen-view:hover {
  background: rgba(255, 255, 255, 0.7);
}

/* Transition */
.ifthen-enter-active,
.ifthen-leave-active {
  transition: all 400ms ease;
}

.ifthen-enter-from,
.ifthen-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
