<script setup lang="ts">
import { computed } from 'vue'
import { useGoals } from '@/composables/useGoals'

const { calorieProgress, waterProgress, exerciseProgress, totalCalories, totalWater, goals } = useGoals()

const RADIUS = 36
const CIRCUMFERENCE = 2 * Math.PI * RADIUS

function getStrokeDash(progress: number): string {
  const filled = Math.min(progress, 1) * CIRCUMFERENCE
  return `${filled} ${CIRCUMFERENCE}`
}

const rings = computed(() => [
  {
    id: 'diet',
    label: '飲食',
    progress: calorieProgress.value,
    color: '#E86010',
    trackColor: 'rgba(232,96,16,0.12)',
    value: `${totalCalories.value}`,
    unit: 'kcal',
    target: `/ ${goals.value.dailyCalories}`
  },
  {
    id: 'exercise',
    label: '運動',
    progress: exerciseProgress.value,
    color: '#0A9488',
    trackColor: 'rgba(10,148,136,0.12)',
    value: exerciseProgress.value >= 1 ? '達標' : '未運動',
    unit: '',
    target: ''
  },
  {
    id: 'water',
    label: '飲水',
    progress: waterProgress.value,
    color: '#2AACA8',
    trackColor: 'rgba(42,172,168,0.12)',
    value: `${Math.round(totalWater.value / 100) / 10}L`,
    unit: '',
    target: `/ ${(goals.value.dailyWaterMl || 2000) / 1000}L`
  },
])
</script>

<template>
  <div class="rings-container">
    <div v-for="ring in rings" :key="ring.id" class="ring-item">
      <div class="ring-svg-wrap">
        <svg width="88" height="88" viewBox="0 0 88 88">
          <!-- Track -->
          <circle
            cx="44" cy="44" :r="RADIUS"
            fill="none"
            :stroke="ring.trackColor"
            stroke-width="8"
          />
          <!-- Progress -->
          <circle
            cx="44" cy="44" :r="RADIUS"
            fill="none"
            :stroke="ring.color"
            stroke-width="8"
            stroke-linecap="round"
            :stroke-dasharray="getStrokeDash(ring.progress)"
            stroke-dashoffset="56.5"
            transform="rotate(-90 44 44)"
            style="transition: stroke-dasharray 600ms ease-out"
          />
          <!-- Incomplete indicator: show open gap more visually -->
          <circle
            v-if="ring.progress < 1"
            cx="44" cy="44" :r="RADIUS"
            fill="none"
            :stroke="ring.color"
            stroke-width="2"
            stroke-dasharray="4 226"
            transform="rotate(-90 44 44)"
            opacity="0.3"
          />
        </svg>

        <div class="ring-center">
          <span class="ring-value" :style="{ color: ring.color }">{{ ring.value }}</span>
          <span v-if="ring.unit" class="ring-unit">{{ ring.unit }}</span>
        </div>
      </div>

      <p class="ring-label">{{ ring.label }}</p>
      <p v-if="ring.target" class="ring-target">{{ ring.target }}</p>

      <div class="ring-percent" :style="{ color: ring.color }">
        {{ Math.round(ring.progress * 100) }}%
      </div>
    </div>
  </div>
</template>

<style scoped>
.rings-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
}

.ring-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.ring-svg-wrap {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.ring-center {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.ring-value {
  font-family: 'Lora', serif;
  font-size: 13px;
  font-weight: 700;
  line-height: 1;
}

.ring-unit {
  font-size: 9px;
  color: #0A7870;
}

.ring-label {
  font-size: 12px;
  font-weight: 600;
  color: #083830;
}

.ring-target {
  font-size: 10px;
  color: #0A7870;
}

.ring-percent {
  font-size: 11px;
  font-weight: 700;
}
</style>
