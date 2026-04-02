<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import { formatDate } from '@/utils/dateUtils'

const report = useReportStore()
const weight = useWeightStore()

onMounted(async () => {
  await Promise.all([
    report.fetchReport(),
    weight.fetchEntries(),
  ])
})

function setPeriod(p: 'week' | 'month') {
  report.setPeriod(p)
}

// Convert weight entries to DayScore format for TrendChart
const weightScoreData = computed(() =>
  weight.chartData.map(e => ({
    date: e.date,
    score: e.weight,
    breakdown: { diet: 0, exercise: 0, water: 0, consistency: 0 }
  }))
)
</script>

<template>
  <PageShell>
    <div class="screen-bg" aria-hidden="true">
      <div class="blob blob-1" />
      <div class="blob blob-2" />
    </div>

    <div class="report-content">
      <!-- Header -->
      <header class="page-header">
        <h1 class="page-title">健康報告</h1>
        <div class="period-toggle">
          <button
            class="period-btn"
            :class="{ active: report.period === 'week' }"
            @click="setPeriod('week')"
          >本週</button>
          <button
            class="period-btn"
            :class="{ active: report.period === 'month' }"
            @click="setPeriod('month')"
          >本月</button>
        </div>
      </header>

      <!-- Average Score Hero -->
      <div class="score-hero">
        <div class="score-circle" :style="{ '--score-pct': report.avgScore + '%' }">
          <svg width="120" height="120" viewBox="0 0 120 120">
            <circle cx="60" cy="60" r="50" fill="none" stroke="rgba(10,148,136,0.08)" stroke-width="10"/>
            <circle
              cx="60" cy="60" r="50"
              fill="none"
              stroke="#0A9488"
              stroke-width="10"
              stroke-linecap="round"
              :stroke-dasharray="`${report.avgScore * 3.14} 314`"
              stroke-dashoffset="78.5"
              transform="rotate(-90 60 60)"
              style="transition: stroke-dasharray 600ms ease-out"
            />
          </svg>
          <div class="score-center">
            <span class="score-number">{{ report.avgScore }}</span>
            <span class="score-sub">平均分</span>
          </div>
        </div>
        <div class="score-info">
          <h2 class="score-period">{{ report.period === 'week' ? '本週' : '本月' }}健康狀況</h2>
          <p class="score-desc">
            {{ report.avgScore >= 80 ? '持續保持優秀表現！' : report.avgScore >= 60 ? '穩定進步中，繼續加油！' : '需要加強健康習慣' }}
          </p>
        </div>
      </div>

      <!-- Insights -->
      <GlassCard class="section">
        <InsightNarrative />
      </GlassCard>

      <!-- Score Trend -->
      <GlassCard class="section">
        <TrendChart
          :data="report.currentPeriodScores"
          metric="score"
          color="#0A9488"
          label="健康分數"
        />
      </GlassCard>

      <!-- Weight Trend -->
      <GlassCard class="section">
        <TrendChart
          :data="weightScoreData"
          metric="score"
          color="#6366f1"
          label="體重（kg）"
        />
      </GlassCard>

      <!-- Heatmap -->
      <GlassCard class="section">
        <h3 class="section-title" style="margin-bottom: 14px">活動熱圖</h3>
        <HeatmapCalendar :scores="report.currentPeriodScores" />
      </GlassCard>

      <!-- Top Scores -->
      <GlassCard class="section">
        <h3 class="section-title" style="margin-bottom: 14px">最佳表現</h3>
        <div class="top-scores">
          <div
            v-for="(score, i) in report.topScores"
            :key="score.date"
            class="top-score-item"
          >
            <div class="rank-badge" :class="`rank-${i + 1}`">{{ i + 1 }}</div>
            <div class="score-details">
              <span class="score-date">{{ formatDate(score.date) }}</span>
              <div class="score-badges">
                <BadgeTag color="teal" size="sm">分數 {{ score.score }}</BadgeTag>
              </div>
            </div>
            <div class="score-val">{{ score.score }}</div>
          </div>
        </div>
      </GlassCard>
    </div>
  </PageShell>
</template>

<style scoped>
.report-content {
  position: relative;
  z-index: 1;
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 20px;
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

.period-toggle {
  display: flex;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 10px;
  padding: 3px;
  gap: 2px;
  border: 1px solid rgba(10, 148, 136, 0.12);
}

.period-btn {
  padding: 7px 16px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: rgba(8, 56, 48, 0.5);
  cursor: pointer;
  transition: all 200ms;
}

.period-btn.active {
  background: #0A9488;
  color: white;
  box-shadow: 0 2px 6px rgba(10, 148, 136, 0.25);
}

.score-hero {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 22px;
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

.score-circle {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.score-center {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-number {
  font-family: 'Lora', serif;
  font-size: 30px;
  font-weight: 700;
  color: #0A9488;
  line-height: 1;
}

.score-sub {
  font-size: 11px;
  color: #0A7870;
}

.score-info {
  flex: 1;
}

.score-period {
  font-size: 18px;
  color: #083830;
  margin-bottom: 6px;
}

.score-desc {
  font-size: 14px;
  color: #0A7870;
  line-height: 1.5;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-family: 'Lora', serif;
  font-size: 15px;
  font-weight: 600;
  color: #083830;
}

.top-scores {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.top-score-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 10px;
}

.rank-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 800;
  flex-shrink: 0;
}

.rank-1 { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: white; }
.rank-2 { background: linear-gradient(135deg, #d1d5db, #9ca3af); color: white; }
.rank-3 { background: linear-gradient(135deg, #cd7f32, #b87333); color: white; }

.score-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.score-date {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
}

.score-badges {
  display: flex;
  gap: 6px;
}

.score-val {
  font-family: 'Lora', serif;
  font-size: 20px;
  font-weight: 700;
  color: #0A9488;
}
</style>
