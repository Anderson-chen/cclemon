<script setup lang="ts">
import { computed } from 'vue'
import { useDietStore } from '@/stores/dietStore'
import { useGoalStore } from '@/stores/goalStore'

const diet = useDietStore()
const goals = useGoalStore()

const targetCal = computed(() => goals.goals.dailyCalories || 2000)

const bars = computed(() => [
  {
    label: '熱量',
    current: diet.totalCalories,
    target: targetCal.value,
    unit: 'kcal',
    color: '#E86010',
    trackColor: 'rgba(232,96,16,0.1)',
  },
  {
    label: '蛋白質',
    current: Math.round(diet.totalProtein),
    target: Math.round(targetCal.value * 0.25 / 4),
    unit: 'g',
    color: '#0A9488',
    trackColor: 'rgba(10,148,136,0.1)',
  },
  {
    label: '碳水化合物',
    current: Math.round(diet.totalCarbs),
    target: Math.round(targetCal.value * 0.5 / 4),
    unit: 'g',
    color: '#3b82f6',
    trackColor: 'rgba(59,130,246,0.1)',
  },
  {
    label: '脂肪',
    current: Math.round(diet.totalFat),
    target: Math.round(targetCal.value * 0.25 / 9),
    unit: 'g',
    color: '#f59e0b',
    trackColor: 'rgba(245,158,11,0.1)',
  },
])

function getPercent(current: number, target: number): number {
  return Math.min(100, Math.round((current / target) * 100))
}
</script>

<template>
  <div class="nutrition-bars">
    <div v-for="bar in bars" :key="bar.label" class="bar-item">
      <div class="bar-header">
        <span class="bar-label">{{ bar.label }}</span>
        <span class="bar-values">
          <span :style="{ color: bar.color }">{{ bar.current }}{{ bar.unit }}</span>
          <span class="bar-sep">/ {{ bar.target }}{{ bar.unit }}</span>
        </span>
      </div>
      <div class="bar-bg" :style="{ background: bar.trackColor }">
        <div
          class="bar-fill"
          :style="{
            width: `${getPercent(bar.current, bar.target)}%`,
            background: bar.color,
            transition: 'width 500ms ease-out'
          }"
        />
        <!-- Overflow indicator -->
        <div
          v-if="bar.current > bar.target"
          class="bar-overflow"
          :style="{ background: bar.color }"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.nutrition-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.bar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.bar-label {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
}

.bar-values {
  font-size: 12px;
  display: flex;
  gap: 3px;
}

.bar-sep {
  color: #0A7870;
}

.bar-bg {
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
}

.bar-overflow {
  position: absolute;
  top: 0;
  right: 0;
  width: 6px;
  height: 100%;
  animation: pulse 1s ease-in-out infinite alternate;
  opacity: 0.7;
}

@keyframes pulse {
  from { opacity: 0.4; }
  to { opacity: 1; }
}
</style>
