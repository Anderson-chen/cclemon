<script setup lang="ts">
import { computed } from 'vue'
import type { Workout } from '@/types/models'
import { useExerciseStore } from '@/stores/exerciseStore'
import BadgeTag from '@/components/shared/BadgeTag.vue'
import PRBadge from './PRBadge.vue'

const props = defineProps<{ workout: Workout }>()
const exercise = useExerciseStore()

const prSet = computed(() => new Set(exercise.prs.map(p => p.exerciseName)))

function isPR(name: string): boolean {
  return prSet.value.has(name)
}

async function handleDelete() {
  if (confirm('確定要刪除這筆運動紀錄嗎？')) {
    await exercise.deleteWorkout(props.workout.id)
  }
}
</script>

<template>
  <div class="workout-card">
    <div class="card-header">
      <div class="header-left">
        <div class="workout-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M6 5v14"/><path d="M18 5v14"/>
            <path d="M6 8h12"/><path d="M6 16h12"/>
          </svg>
        </div>
        <div>
          <p class="workout-date">{{ workout.date }}</p>
          <div class="workout-stats">
            <BadgeTag color="teal" size="sm">{{ workout.totalMinutes }} 分鐘</BadgeTag>
            <BadgeTag color="orange" size="sm">-{{ workout.estimatedCaloriesBurned }} kcal</BadgeTag>
          </div>
        </div>
      </div>
      <button class="delete-btn" @click="handleDelete" aria-label="刪除訓練">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
        </svg>
      </button>
    </div>

    <div class="exercise-list">
      <div v-for="(ex, i) in workout.exercises" :key="i" class="exercise-row">
        <div class="exercise-info">
          <span class="exercise-name">{{ ex.name }}</span>
          <PRBadge v-if="isPR(ex.name)" />
        </div>
        <div class="exercise-detail">
          <template v-if="ex.sets && ex.reps">
            <span>{{ ex.sets }} 組 × {{ ex.reps }} 次</span>
            <span v-if="ex.weight" class="exercise-weight">{{ ex.weight }}kg</span>
          </template>
          <span v-else-if="ex.durationMinutes">{{ ex.durationMinutes }} 分鐘</span>
        </div>
      </div>
    </div>

    <div v-if="workout.notes" class="workout-notes">
      <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
        <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8"/>
      </svg>
      <span>{{ workout.notes }}</span>
    </div>
  </div>
</template>

<style scoped>
.workout-card {
  background: rgba(255, 255, 255, 0.38);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 18px 6px 18px 6px;
  padding: 16px;
  box-shadow: 0 3px 12px rgba(10, 148, 136, 0.10);
  animation: slideIn 300ms ease-out;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.workout-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(10, 148, 136, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0A9488;
  flex-shrink: 0;
}

.workout-date {
  font-size: 12px;
  color: #0A7870;
  margin-bottom: 4px;
}

.workout-stats {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.delete-btn {
  width: 44px;
  height: 44px;
  border: none;
  background: rgba(229, 62, 62, 0.07);
  border-radius: 10px;
  cursor: pointer;
  color: rgba(229, 62, 62, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms;
  margin: -8px -8px 0 0;
}

.delete-btn:hover {
  background: rgba(229, 62, 62, 0.13);
  color: #e53e3e;
}

.exercise-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}

.exercise-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 10px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 8px;
}

.exercise-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.exercise-name {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.exercise-detail {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #0A7870;
  font-weight: 500;
}

.exercise-weight {
  font-weight: 700;
  color: #0A9488;
}

.workout-notes {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 8px 10px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 8px;
  border-left: 3px solid rgba(10, 148, 136, 0.25);
  color: #0A7870;
  font-size: 13px;
  line-height: 1.5;
}
</style>
