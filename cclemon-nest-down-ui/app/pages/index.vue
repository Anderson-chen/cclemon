<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import { formatDate, today } from '@/utils/dateUtils'

const auth = useAuthStore()
const dietStore = useDietStore()
const exerciseStore = useExerciseStore()
const weightStore = useWeightStore()
const waterStore = useWaterStore()
const ifThenStore = useIfThenStore()
const goalStore = useGoalStore()
const todayDate = formatDate(today())

onMounted(async () => {
  await Promise.all([
    dietStore.fetchMeals(today()),
    exerciseStore.fetchWorkouts(today()),
    weightStore.fetchEntries(),
    waterStore.fetchToday(),
    ifThenStore.fetchPlans(),
    goalStore.fetchGoals(),
  ])
})
</script>

<template>
  <PageShell>
    <!-- Blob background -->
    <div class="screen-bg" aria-hidden="true">
      <div class="blob blob-1" />
      <div class="blob blob-2" />
      <div class="blob blob-3" />
    </div>

    <div class="home-content">
      <!-- Header -->
      <header class="home-header">
        <div class="header-greet">
          <p class="greet-sub">{{ todayDate }}</p>
          <h1 class="greet-title">你好，{{ auth.user?.name ?? '用戶' }}！</h1>
        </div>
        <RouterLink to="/settings" class="avatar-btn" aria-label="設定">
          <div class="avatar-circle">
            <img v-if="auth.user?.avatarUrl" :src="auth.user.avatarUrl" :alt="auth.user.name" />
            <span v-else>{{ (auth.user?.name ?? '？').charAt(0) }}</span>
          </div>
        </RouterLink>
      </header>

      <!-- Quick Log Bar -->
      <section class="section">
        <QuickLogBar />
      </section>

      <!-- If-Then Reminder -->
      <section class="section">
        <IfThenReminderCard />
      </section>

      <!-- Health Score -->
      <section class="section">
        <HealthScoreCard />
      </section>

      <!-- Daily Summary Grid -->
      <section class="section">
        <h2 class="section-title">今日總覽</h2>
        <DailySummaryGrid />
      </section>

      <!-- Goal Rings -->
      <section class="section">
        <GlassCard>
          <h2 class="section-title" style="margin-bottom: 16px">目標進度</h2>
          <GoalRings />
        </GlassCard>
      </section>

      <!-- Streak -->
      <section class="section">
        <StreakCard />
      </section>

      <!-- Water Widget -->
      <section class="section">
        <WaterWidget />
      </section>
    </div>
  </PageShell>
</template>

<style scoped>
.home-content {
  position: relative;
  z-index: 1;
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 56px 0 20px;
}

.greet-sub {
  font-size: 13px;
  color: #0A7870;
  font-weight: 500;
  margin-bottom: 2px;
}

.greet-title {
  font-family: 'Lora', serif;
  font-size: 24px;
  font-weight: 700;
  color: #083830;
}

.avatar-btn {
  text-decoration: none;
}

.avatar-circle {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0A9488, #2AACA8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 3px 10px rgba(10, 148, 136, 0.3);
  overflow: hidden;
  border: 2.5px solid rgba(255,255,255,0.6);
}

.avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-family: 'Lora', serif;
  font-size: 16px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 10px;
}
</style>
