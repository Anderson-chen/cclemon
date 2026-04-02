<script setup lang="ts">
import { computed } from 'vue'
import { useDietStore } from '@/stores/dietStore'
import { useExerciseStore } from '@/stores/exerciseStore'
import { useWeightStore } from '@/stores/weightStore'
import { useStreak } from '@/composables/useStreak'

const diet = useDietStore()
const exercise = useExerciseStore()
const weight = useWeightStore()
const { streakDays } = useStreak()

const stats = computed(() => [
  {
    id: 'calories',
    label: '熱量',
    value: diet.totalCalories.toString(),
    unit: 'kcal',
    color: '#E86010',
    bg: 'rgba(232,96,16,0.08)',
    shadow: 'rgba(232,96,16,0.15)',
    icon: `<path d="M12 2a4 4 0 0 0-4 4c0 1.5.6 2.8 1.6 3.8L4 22h16l-5.6-12.2C15.4 8.8 16 7.5 16 6a4 4 0 0 0-4-4z"/>`
  },
  {
    id: 'exercise',
    label: '運動',
    value: exercise.totalWorkoutMinutes.toString(),
    unit: 'min',
    color: '#0A9488',
    bg: 'rgba(10,148,136,0.08)',
    shadow: 'rgba(10,148,136,0.15)',
    icon: `<path d="M6 5v14"/><path d="M18 5v14"/><path d="M6 8h12"/><path d="M6 16h12"/>`
  },
  {
    id: 'weight',
    label: '體重',
    value: weight.latestWeight?.toFixed(1) ?? '--',
    unit: 'kg',
    color: '#6366f1',
    bg: 'rgba(99,102,241,0.08)',
    shadow: 'rgba(99,102,241,0.15)',
    icon: `<circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>`
  },
  {
    id: 'streak',
    label: '連續天數',
    value: streakDays.value.toString(),
    unit: '天',
    color: '#f59e0b',
    bg: 'rgba(245,158,11,0.08)',
    shadow: 'rgba(245,158,11,0.15)',
    icon: `<path d="M12 2c0 6-8 8-8 14a8 8 0 0 0 16 0c0-6-8-8-8-14z"/>`
  },
])
</script>

<template>
  <div class="summary-grid">
    <div
      v-for="stat in stats"
      :key="stat.id"
      class="stat-card"
      :style="{
        background: stat.bg,
        boxShadow: `0 4px 14px ${stat.shadow}`
      }"
    >
      <div class="stat-icon" :style="{ color: stat.color }">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="stat.icon" />
      </div>
      <div class="stat-body">
        <div class="stat-value-row">
          <span class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</span>
          <span class="stat-unit">{{ stat.unit }}</span>
        </div>
        <span class="stat-label">{{ stat.label }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.stat-card {
  border-radius: 18px 6px 18px 6px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition: transform 200ms ease;
}

.stat-card:active {
  transform: scale(0.97);
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 3px;
}

.stat-value {
  font-family: 'Lora', serif;
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}

.stat-unit {
  font-size: 11px;
  color: #0A7870;
  font-weight: 600;
}

.stat-label {
  font-size: 11px;
  color: rgba(8, 56, 48, 0.55);
  font-weight: 500;
}
</style>
