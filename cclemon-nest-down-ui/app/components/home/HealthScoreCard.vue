<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useHealthScore } from '@/composables/useHealthScore'
import GlassCard from '@/components/shared/GlassCard.vue'

const { todayScore, scoreColor, breakdown } = useHealthScore()

const displayScore = ref(0)

onMounted(() => {
  const target = todayScore.value
  const duration = 800
  const start = performance.now()
  const animate = (now: number) => {
    const elapsed = now - start
    const progress = Math.min(elapsed / duration, 1)
    const ease = 1 - Math.pow(1 - progress, 3)
    displayScore.value = Math.round(ease * target)
    if (progress < 1) requestAnimationFrame(animate)
  }
  requestAnimationFrame(animate)
})

const scoreLabel = computed(() => {
  if (todayScore.value >= 90) return '卓越'
  if (todayScore.value >= 80) return '優秀'
  if (todayScore.value >= 70) return '良好'
  if (todayScore.value >= 60) return '普通'
  return '加油'
})

const breakdownItems = computed(() => [
  { label: '飲食', value: breakdown.value.diet, color: '#E86010' },
  { label: '運動', value: breakdown.value.exercise, color: '#0A9488' },
  { label: '飲水', value: breakdown.value.water, color: '#2AACA8' },
  { label: '一致性', value: breakdown.value.consistency, color: '#6366f1' },
])
</script>

<template>
  <div class="score-card">
    <!-- Glass shine top -->
    <div class="score-card-inner">
      <p class="score-title">今日健康分數</p>

      <div class="score-main">
        <div class="score-ring" :style="{ '--color': scoreColor }">
          <svg width="120" height="120" viewBox="0 0 120 120">
            <!-- Background circle -->
            <circle cx="60" cy="60" r="50" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="10"/>
            <!-- Progress arc -->
            <circle
              cx="60" cy="60" r="50"
              fill="none"
              :stroke="scoreColor"
              stroke-width="10"
              stroke-linecap="round"
              :stroke-dasharray="`${todayScore * 3.14} 314`"
              stroke-dashoffset="78.5"
              transform="rotate(-90 60 60)"
              style="transition: stroke-dasharray 800ms ease-out"
            />
          </svg>
          <div class="score-value-wrap">
            <span class="score-number" :style="{ color: scoreColor }">{{ displayScore }}</span>
            <span class="score-max">/100</span>
          </div>
        </div>

        <div class="score-label-badge" :style="{ color: scoreColor, borderColor: scoreColor + '30', background: scoreColor + '12' }">
          {{ scoreLabel }}
        </div>
      </div>

      <div class="breakdown-list">
        <div v-for="item in breakdownItems" :key="item.label" class="breakdown-item">
          <div class="breakdown-header">
            <span class="breakdown-label">{{ item.label }}</span>
            <span class="breakdown-value">{{ item.value }}</span>
          </div>
          <div class="breakdown-bar-bg">
            <div
              class="breakdown-bar-fill"
              :style="{
                width: `${item.value}%`,
                background: item.color,
                transition: 'width 800ms ease-out'
              }"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.score-card {
  background: rgba(255, 255, 255, 0.32);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 24px 8px 24px 8px;
  padding: 24px;
  position: relative;
  box-shadow: 0 8px 32px rgba(10, 148, 136, 0.18);
  overflow: hidden;
}

.score-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 12px;
  right: 12px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.9), transparent);
  pointer-events: none;
}

.score-card::after {
  content: '';
  position: absolute;
  bottom: -40px;
  right: -40px;
  width: 160px;
  height: 160px;
  background: radial-gradient(circle, rgba(10, 148, 136, 0.08) 0%, transparent 70%);
  pointer-events: none;
}

.score-card-inner {
  position: relative;
  z-index: 1;
}

.score-title {
  font-family: 'Lora', serif;
  font-size: 16px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 20px;
  text-align: center;
}

.score-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.score-ring {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.score-value-wrap {
  position: absolute;
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.score-number {
  font-family: 'Lora', serif;
  font-size: 42px;
  font-weight: 700;
  line-height: 1;
}

.score-max {
  font-size: 14px;
  color: #0A7870;
  font-weight: 500;
}

.score-label-badge {
  display: inline-flex;
  padding: 4px 14px;
  border-radius: 999px;
  border: 1.5px solid;
  font-size: 13px;
  font-weight: 600;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.breakdown-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.breakdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breakdown-label {
  font-size: 12px;
  font-weight: 600;
  color: #0A7870;
}

.breakdown-value {
  font-size: 12px;
  font-weight: 700;
  color: #083830;
}

.breakdown-bar-bg {
  height: 6px;
  background: rgba(10, 148, 136, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.breakdown-bar-fill {
  height: 100%;
  border-radius: 3px;
}
</style>
