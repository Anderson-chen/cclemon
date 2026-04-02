<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import { today } from '@/utils/dateUtils'

const weight = useWeightStore()
const goals = useGoalStore()
const ui = useUiStore()

onMounted(async () => {
  await Promise.all([
    weight.fetchEntries(),
    goals.fetchGoals(),
  ])
})

const activeTab = ref<'weight' | 'measurements' | 'photos'>('weight')
const newWeight = ref<number | string>('')

const tabs = [
  { id: 'weight', label: '體重' },
  { id: 'measurements', label: '體圍' },
  { id: 'photos', label: '體態照片' },
] as const

async function logWeight() {
  const w = Number(newWeight.value)
  if (!w || w < 20 || w > 300) return
  await weight.addEntry({ userId: '1', date: today(), weight: w })
  newWeight.value = ''
}
</script>

<template>
  <PageShell>
    <div class="screen-bg" aria-hidden="true">
      <div class="blob blob-1" />
      <div class="blob blob-2" />
    </div>

    <div class="weight-content">
      <!-- Header -->
      <header class="page-header">
        <h1 class="page-title">體重追蹤</h1>
        <div v-if="weight.latestWeight" class="current-weight">
          <span class="weight-val">{{ weight.latestWeight }}</span>
          <span class="weight-unit">kg</span>
        </div>
      </header>

      <!-- Weight trend summary -->
      <div v-if="weight.weightChange !== 0" class="trend-banner" :class="weight.weightChange < 0 ? 'positive' : 'negative'">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path v-if="weight.weightChange < 0" d="M12 19V5M5 12l7-7 7 7"/>
          <path v-else d="M12 5v14M19 12l-7 7-7-7"/>
        </svg>
        近 14 天 {{ weight.weightChange > 0 ? '+' : '' }}{{ weight.weightChange }} kg
      </div>

      <!-- Tabs -->
      <div class="tab-bar">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          class="tab-btn"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Weight Tab -->
      <div v-if="activeTab === 'weight'">
        <!-- Log weight -->
        <GlassCard class="section">
          <h3 class="card-title">記錄今日體重</h3>
          <div class="weight-input-row">
            <div class="weight-input-wrap">
              <input
                v-model="newWeight"
                type="number"
                step="0.1"
                min="20"
                max="300"
                class="weight-input"
                placeholder="例如：72.5"
              />
              <span class="weight-input-unit">kg</span>
            </div>
            <OrangeButton :loading="ui.isLoading('addWeight')" @click="logWeight">記錄</OrangeButton>
          </div>
          <p v-if="goals.goals.targetWeight" class="target-hint">
            目標體重：{{ goals.goals.targetWeight }} kg
            <span v-if="weight.latestWeight">
              · 距離目標 {{ Math.abs(weight.latestWeight - goals.goals.targetWeight).toFixed(1) }} kg
            </span>
          </p>
        </GlassCard>

        <!-- Chart -->
        <GlassCard class="section">
          <h3 class="card-title">14 天趨勢</h3>
          <WeightChart :entries="weight.chartData" />
        </GlassCard>

        <!-- History -->
        <GlassCard class="section">
          <h3 class="card-title">記錄歷史</h3>
          <div class="history-list">
            <div
              v-for="entry in [...weight.entries].sort((a, b) => b.date.localeCompare(a.date)).slice(0, 10)"
              :key="entry.id"
              class="history-item"
            >
              <span class="history-date">{{ entry.date }}</span>
              <span class="history-weight">{{ entry.weight }} kg</span>
            </div>
          </div>
        </GlassCard>
      </div>

      <!-- Measurements Tab -->
      <div v-else-if="activeTab === 'measurements'">
        <GlassCard class="section">
          <MeasurementForm />
        </GlassCard>
      </div>

      <!-- Photos Tab -->
      <div v-else-if="activeTab === 'photos'">
        <GlassCard class="section">
          <h3 class="card-title">上傳體態照片</h3>
          <BodyPhotoUpload />
        </GlassCard>

        <GlassCard class="section">
          <h3 class="card-title">對比查看</h3>
          <BodyPhotoCompare />
        </GlassCard>
      </div>
    </div>
  </PageShell>
</template>

<style scoped>
.weight-content {
  position: relative;
  z-index: 1;
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-family: 'Lora', serif;
  font-size: 24px;
  font-weight: 700;
  color: #083830;
}

.current-weight {
  display: flex;
  align-items: baseline;
  gap: 3px;
}

.weight-val {
  font-family: 'Lora', serif;
  font-size: 28px;
  font-weight: 700;
  color: #0A9488;
}

.weight-unit {
  font-size: 14px;
  color: #0A7870;
}

.trend-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 14px;
}

.trend-banner.positive {
  background: rgba(10, 148, 136, 0.1);
  color: #0A9488;
}

.trend-banner.negative {
  background: rgba(229, 62, 62, 0.08);
  color: #c53030;
}

.tab-bar {
  display: flex;
  gap: 0;
  margin-bottom: 16px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 12px;
  padding: 3px;
  border: 1px solid rgba(10, 148, 136, 0.12);
}

.tab-btn {
  flex: 1;
  padding: 10px 8px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: rgba(8, 56, 48, 0.5);
  cursor: pointer;
  transition: all 200ms;
}

.tab-btn.active {
  background: #0A9488;
  color: white;
  box-shadow: 0 2px 8px rgba(10, 148, 136, 0.25);
}

.section {
  margin-bottom: 16px;
}

.card-title {
  font-family: 'Lora', serif;
  font-size: 15px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 14px;
}

.weight-input-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

.weight-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 12px;
  overflow: hidden;
}

.weight-input {
  flex: 1;
  padding: 12px 16px;
  background: transparent;
  border: none;
  outline: none;
  font-size: 22px;
  font-family: 'Lora', serif;
  font-weight: 700;
  color: #083830;
  min-height: 52px;
}

.weight-input-unit {
  padding: 0 14px;
  font-size: 14px;
  font-weight: 600;
  color: #0A7870;
}

.target-hint {
  font-size: 12px;
  color: #0A7870;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 12px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 8px;
}

.history-date {
  font-size: 13px;
  color: #0A7870;
}

.history-weight {
  font-size: 14px;
  font-weight: 700;
  color: #083830;
}
</style>
