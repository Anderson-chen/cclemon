<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import type { ExerciseEntry, WorkoutTemplate } from '@/types/models'
import { today } from '@/utils/dateUtils'

const router = useRouter()
const exercise = useExerciseStore()
const ui = useUiStore()

onMounted(async () => {
  await exercise.fetchTemplates()
})

const exercises = ref<ExerciseEntry[]>([])
const notes = ref('')
const totalMinutes = ref(45)
const saveAsTemplate = ref(false)
const templateName = ref('')

const newEx = reactive({
  name: '',
  sets: undefined as number | undefined,
  reps: undefined as number | undefined,
  weight: undefined as number | undefined,
  durationMinutes: undefined as number | undefined,
  type: 'strength' as 'strength' | 'cardio',
})

function addExercise() {
  if (!newEx.name.trim()) return
  exercises.value.push({
    name: newEx.name,
    sets: newEx.type === 'strength' ? newEx.sets : undefined,
    reps: newEx.type === 'strength' ? newEx.reps : undefined,
    weight: newEx.type === 'strength' ? newEx.weight : undefined,
    durationMinutes: newEx.type === 'cardio' ? newEx.durationMinutes : undefined,
  })
  newEx.name = ''
  newEx.sets = undefined
  newEx.reps = undefined
  newEx.weight = undefined
  newEx.durationMinutes = undefined
}

function removeExercise(i: number) {
  exercises.value.splice(i, 1)
}

function applyTemplate(template: WorkoutTemplate) {
  exercises.value = [...template.exercises]
  templateName.value = template.name
}

function estimateCalories(): number {
  return Math.round(totalMinutes.value * 5.5)
}

async function save() {
  if (!exercises.value.length) {
    ui.showToast({ type: 'error', message: '請至少新增一項運動' })
    return
  }

  await exercise.addWorkout({
    userId: '1',
    date: today(),
    exercises: exercises.value,
    notes: notes.value,
    totalMinutes: totalMinutes.value,
    estimatedCaloriesBurned: estimateCalories()
  })

  if (saveAsTemplate.value && templateName.value.trim()) {
    await exercise.saveTemplate({ name: templateName.value, exercises: exercises.value })
  }

  navigateTo('/exercise')
}
</script>

<template>
  <PageShell>
    <div class="add-content">
      <header class="page-header">
        <GlassButton size="sm" @click="router.back()">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          返回
        </GlassButton>
        <h1 class="page-title">新增運動</h1>
      </header>

      <!-- Templates -->
      <GlassCard class="section">
        <TemplateSelector @select="applyTemplate" />
      </GlassCard>

      <!-- Exercise list -->
      <div class="section">
        <div class="section-header">
          <h2 class="section-title">訓練動作</h2>
          <span class="exercise-count">{{ exercises.length }} 項</span>
        </div>

        <div v-if="exercises.length > 0" class="exercise-list">
          <div v-for="(ex, i) in exercises" :key="i" class="exercise-item">
            <div class="ex-info">
              <span class="ex-name">{{ ex.name }}</span>
              <div class="ex-details">
                <template v-if="ex.sets">
                  <BadgeTag color="teal" size="sm">{{ ex.sets }}×{{ ex.reps }}</BadgeTag>
                  <BadgeTag v-if="ex.weight" color="orange" size="sm">{{ ex.weight }}kg</BadgeTag>
                </template>
                <BadgeTag v-if="ex.durationMinutes" color="blue" size="sm">{{ ex.durationMinutes }}分鐘</BadgeTag>
              </div>
            </div>
            <button class="ex-remove" @click="removeExercise(i)" aria-label="移除">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Add exercise form -->
      <GlassCard class="section">
        <h3 class="card-title">新增動作</h3>

        <div class="ex-type-toggle">
          <button class="type-btn" :class="{ active: newEx.type === 'strength' }" @click="newEx.type = 'strength'">重訓</button>
          <button class="type-btn" :class="{ active: newEx.type === 'cardio' }" @click="newEx.type = 'cardio'">有氧</button>
        </div>

        <div class="add-form">
          <InputField v-model="newEx.name" label="動作名稱" placeholder="例如：深蹲" required />

          <div v-if="newEx.type === 'strength'" class="fields-row">
            <InputField v-model="newEx.sets" type="number" label="組數" placeholder="3" />
            <InputField v-model="newEx.reps" type="number" label="次數" placeholder="12" />
            <InputField v-model="newEx.weight" type="number" label="重量(kg)" placeholder="60" />
          </div>

          <div v-else>
            <InputField v-model="newEx.durationMinutes" type="number" label="時長（分鐘）" placeholder="30" />
          </div>

          <GlassButton full-width @click="addExercise">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            加入動作
          </GlassButton>
        </div>
      </GlassCard>

      <!-- Workout details -->
      <GlassCard class="section">
        <h3 class="card-title">訓練資訊</h3>
        <div class="workout-fields">
          <InputField v-model="totalMinutes" type="number" label="訓練時長（分鐘）" />
          <div class="textarea-wrap">
            <label class="field-label">備註</label>
            <textarea v-model="notes" class="notes-input" placeholder="今天訓練的感受..." rows="3" />
          </div>
        </div>
      </GlassCard>

      <!-- Save as template -->
      <GlassCard class="section">
        <div class="template-toggle">
          <label class="toggle-label">
            <input v-model="saveAsTemplate" type="checkbox" class="toggle-checkbox" />
            <span class="toggle-text">儲存為訓練模板</span>
          </label>
        </div>
        <Transition name="fade">
          <div v-if="saveAsTemplate" class="template-name-input">
            <InputField v-model="templateName" label="模板名稱" placeholder="例如：全身訓練 A" />
          </div>
        </Transition>
      </GlassCard>

      <!-- Save button -->
      <div class="save-section">
        <OrangeButton full-width size="lg" :loading="ui.isLoading('addWorkout')" @click="save">
          儲存運動紀錄
        </OrangeButton>
      </div>
    </div>
  </PageShell>
</template>

<style scoped>
.add-content {
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-family: 'Lora', serif;
  font-size: 22px;
  font-weight: 700;
  color: #083830;
}

.section {
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-title {
  font-family: 'Lora', serif;
  font-size: 16px;
  font-weight: 600;
  color: #083830;
}

.exercise-count {
  font-size: 12px;
  color: #0A7870;
  font-weight: 600;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 14px;
}

.exercise-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.exercise-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.45);
  border: 1px solid rgba(10, 148, 136, 0.12);
  border-radius: 10px;
}

.ex-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.ex-name {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.ex-details {
  display: flex;
  gap: 6px;
}

.ex-remove {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(229, 62, 62, 0.07);
  border-radius: 8px;
  cursor: pointer;
  color: rgba(229, 62, 62, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms;
}

.ex-remove:hover {
  background: rgba(229, 62, 62, 0.14);
  color: #e53e3e;
}

.ex-type-toggle {
  display: flex;
  gap: 8px;
  background: rgba(10, 148, 136, 0.06);
  border-radius: 8px;
  padding: 3px;
  margin-bottom: 14px;
}

.type-btn {
  flex: 1;
  padding: 7px;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: rgba(8, 56, 48, 0.5);
  cursor: pointer;
  transition: all 200ms;
}

.type-btn.active {
  background: white;
  color: #0A9488;
  box-shadow: 0 1px 3px rgba(10, 148, 136, 0.12);
}

.add-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fields-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
}

.workout-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 6px;
}

.notes-input {
  width: 100%;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.55);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 12px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  color: #083830;
  outline: none;
  resize: vertical;
  min-height: 80px;
}

.notes-input:focus {
  border-color: #0A9488;
  box-shadow: 0 0 0 3px rgba(10, 148, 136, 0.1);
}

.template-toggle {
  display: flex;
  align-items: center;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.toggle-checkbox {
  width: 18px;
  height: 18px;
  accent-color: #0A9488;
  cursor: pointer;
}

.toggle-text {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.template-name-input {
  margin-top: 12px;
}

.save-section {
  margin-bottom: 24px;
}

.fade-enter-active, .fade-leave-active {
  transition: all 200ms ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
