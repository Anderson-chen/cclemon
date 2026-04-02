<script setup lang="ts">
import type { WorkoutTemplate } from '@/types/models'
import { useExerciseStore } from '@/stores/exerciseStore'

const emit = defineEmits<{
  select: [template: WorkoutTemplate]
}>()

const exercise = useExerciseStore()
</script>

<template>
  <div class="templates">
    <h4 class="templates-title">訓練模板</h4>
    <div v-if="exercise.templates.length === 0" class="empty-msg">
      尚無模板，完成訓練後可儲存為模板
    </div>
    <div class="template-list">
      <button
        v-for="template in exercise.templates"
        :key="template.id"
        class="template-card"
        @click="emit('select', template)"
      >
        <div class="template-header">
          <span class="template-name">{{ template.name }}</span>
          <div class="template-count">{{ template.exercises.length }} 個動作</div>
        </div>
        <div class="template-exercises">
          <span v-for="(ex, i) in template.exercises.slice(0, 3)" :key="i" class="ex-chip">
            {{ ex.name }}
          </span>
          <span v-if="template.exercises.length > 3" class="ex-chip more">
            +{{ template.exercises.length - 3 }}
          </span>
        </div>
      </button>
    </div>
  </div>
</template>

<style scoped>
.templates {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.templates-title {
  font-size: 14px;
  color: #083830;
  font-weight: 600;
}

.empty-msg {
  font-size: 13px;
  color: #0A7870;
  text-align: center;
  padding: 16px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 10px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-card {
  width: 100%;
  text-align: left;
  padding: 14px;
  background: rgba(255, 255, 255, 0.5);
  border: 1.5px solid rgba(10, 148, 136, 0.15);
  border-radius: 12px;
  cursor: pointer;
  transition: all 200ms;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-card:hover {
  background: rgba(10, 148, 136, 0.06);
  border-color: rgba(10, 148, 136, 0.3);
  transform: translateY(-1px);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-name {
  font-size: 14px;
  font-weight: 700;
  color: #083830;
}

.template-count {
  font-size: 12px;
  color: #0A7870;
}

.template-exercises {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.ex-chip {
  font-size: 11px;
  font-weight: 600;
  color: #0A9488;
  background: rgba(10, 148, 136, 0.08);
  padding: 3px 10px;
  border-radius: 6px;
}

.ex-chip.more {
  color: #0A7870;
  background: rgba(10, 148, 136, 0.04);
}
</style>
