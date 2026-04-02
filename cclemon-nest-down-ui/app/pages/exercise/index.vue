<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import { today } from '@/utils/dateUtils'

const router = useRouter()
const exercise = useExerciseStore()
const showTimer = ref(false)

onMounted(async () => {
  await Promise.all([
    exercise.fetchWorkouts(today()),
    exercise.fetchPRs(),
  ])
})
</script>

<template>
  <PageShell>
    <div class="screen-bg" aria-hidden="true">
      <div class="blob blob-1" />
      <div class="blob blob-2" />
    </div>

    <div class="exercise-content">
      <!-- Header -->
      <header class="page-header">
        <div>
          <h1 class="page-title">運動紀錄</h1>
          <p v-if="exercise.hasWorkoutToday" class="page-sub">
            今日已運動 {{ exercise.totalWorkoutMinutes }} 分鐘
          </p>
        </div>
        <RouterLink to="/settings" class="plan-link">備用計畫</RouterLink>
      </header>

      <!-- Summary badges -->
      <div v-if="exercise.hasWorkoutToday" class="summary-row">
        <BadgeTag color="teal">{{ exercise.totalWorkoutMinutes }} 分鐘</BadgeTag>
        <BadgeTag color="orange">消耗 {{ exercise.totalCaloriesBurned }} kcal</BadgeTag>
        <BadgeTag color="green">{{ exercise.todayWorkouts.length }} 項訓練</BadgeTag>
      </div>

      <!-- Today's workouts -->
      <div class="section">
        <div class="section-header">
          <h2 class="section-title">今日訓練</h2>
        </div>

        <div v-if="exercise.todayWorkouts.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="rgba(10,148,136,0.3)" stroke-width="1.2">
              <path d="M6 5v14"/><path d="M18 5v14"/>
              <path d="M6 8h12"/><path d="M6 16h12"/>
            </svg>
          </div>
          <p class="empty-title">今天還沒有運動紀錄</p>
          <p class="empty-hint">開始記錄你的第一項訓練！</p>
        </div>

        <div v-else class="workout-list">
          <WorkoutCard
            v-for="workout in exercise.todayWorkouts"
            :key="workout.id"
            :workout="workout"
          />
        </div>
      </div>

      <!-- Personal Records -->
      <div class="section">
        <GlassCard>
          <h2 class="section-title">個人紀錄 (PR)</h2>
          <div v-if="exercise.prs.length === 0" class="empty-msg">尚無個人紀錄</div>
          <div v-else class="pr-list">
            <div v-for="pr in exercise.prs" :key="pr.exerciseName" class="pr-item">
              <span class="pr-name">{{ pr.exerciseName }}</span>
              <div class="pr-detail">
                <span v-if="pr.weight" class="pr-value">{{ pr.weight }}kg</span>
                <span v-if="pr.reps" class="pr-reps">× {{ pr.reps }}</span>
                <span class="pr-date">{{ pr.date }}</span>
              </div>
            </div>
          </div>
        </GlassCard>
      </div>

      <!-- Rest Timer -->
      <div class="section">
        <button class="timer-toggle" @click="showTimer = !showTimer">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 6v6l4 2"/>
          </svg>
          休息計時器
          <svg
            width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
            :style="{ transform: showTimer ? 'rotate(180deg)' : '', transition: 'transform 200ms' }"
          >
            <path d="M6 9l6 6 6-6"/>
          </svg>
        </button>

        <Transition name="collapse">
          <GlassCard v-if="showTimer" style="margin-top: 10px">
            <ExerciseTimer />
          </GlassCard>
        </Transition>
      </div>
    </div>

    <!-- FAB -->
    <button class="fab" @click="navigateTo('/exercise/add')" aria-label="新增運動">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <path d="M12 5v14M5 12h14"/>
      </svg>
    </button>
  </PageShell>
</template>

<style scoped>
.exercise-content {
  position: relative;
  z-index: 1;
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 20px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.page-title {
  font-family: 'Lora', serif;
  font-size: 24px;
  font-weight: 700;
  color: #083830;
}

.page-sub {
  font-size: 13px;
  color: #0A7870;
  margin-top: 2px;
}

.plan-link {
  font-size: 13px;
  font-weight: 600;
  color: #0A9488;
  text-decoration: none;
  padding: 8px 14px;
  background: rgba(10, 148, 136, 0.08);
  border-radius: 10px;
  margin-top: 4px;
}

.summary-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.section {
  margin-bottom: 20px;
}

.section-header {
  margin-bottom: 12px;
}

.section-title {
  font-family: 'Lora', serif;
  font-size: 16px;
  font-weight: 600;
  color: #083830;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 40px 20px;
  background: rgba(10, 148, 136, 0.03);
  border: 1.5px dashed rgba(10, 148, 136, 0.15);
  border-radius: 16px;
  text-align: center;
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: #083830;
}

.empty-hint {
  font-size: 13px;
  color: #0A7870;
}

.workout-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-msg {
  font-size: 13px;
  color: #0A7870;
  text-align: center;
  padding: 12px;
}

.pr-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pr-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 8px;
}

.pr-name {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.pr-detail {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pr-value {
  font-size: 14px;
  font-weight: 700;
  color: #E86010;
}

.pr-reps {
  font-size: 13px;
  color: #0A7870;
}

.pr-date {
  font-size: 11px;
  color: rgba(8, 56, 48, 0.4);
}

.timer-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(10, 148, 136, 0.15);
  border-radius: 14px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: #083830;
  cursor: pointer;
  transition: all 200ms;
}

.timer-toggle:hover {
  background: rgba(255, 255, 255, 0.6);
}

.fab {
  position: fixed;
  bottom: calc(80px + env(safe-area-inset-bottom));
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: #E86010;
  border: none;
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(232, 96, 16, 0.4);
  transition: all 200ms;
  z-index: 50;
}

.fab:hover { background: #C05010; transform: scale(1.05); }
.fab:active { transform: scale(0.95); }

.collapse-enter-active, .collapse-leave-active {
  transition: all 300ms ease;
  overflow: hidden;
}
.collapse-enter-from, .collapse-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (min-width: 1024px) {
  .fab { bottom: 24px; right: 40px; }
}
</style>
