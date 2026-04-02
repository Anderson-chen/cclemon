<script setup lang="ts">
import { computed } from 'vue'
import type { DayScore } from '@/types/models'
import { formatDateShort } from '@/utils/dateUtils'

const props = defineProps<{
  data: DayScore[]
  metric?: 'score' | 'diet' | 'exercise' | 'water'
  color?: string
  label?: string
}>()

const WIDTH = 340
const HEIGHT = 160
const PAD = { top: 20, right: 12, bottom: 36, left: 40 }
const CHART_W = WIDTH - PAD.left - PAD.right
const CHART_H = HEIGHT - PAD.top - PAD.bottom

const activeColor = computed(() => props.color || '#0A9488')
const activeLabel = computed(() => props.label || '分數')

const values = computed(() =>
  props.data.map(d => {
    const m = props.metric || 'score'
    if (m === 'score') return d.score
    return d.breakdown[m as keyof typeof d.breakdown]
  })
)

const minVal = computed(() => Math.max(0, Math.min(...values.value) - 5))
const maxVal = computed(() => Math.min(100, Math.max(...values.value) + 5))
const valRange = computed(() => maxVal.value - minVal.value || 1)

function xPos(i: number): number {
  return PAD.left + (i / Math.max(values.value.length - 1, 1)) * CHART_W
}

function yPos(v: number): number {
  return PAD.top + CHART_H - ((v - minVal.value) / valRange.value) * CHART_H
}

const points = computed(() =>
  values.value.map((v, i) => ({ x: xPos(i), y: yPos(v), v, d: props.data[i]! }))
)

const linePath = computed(() => {
  if (points.value.length < 2) return ''
  const pts = points.value
  const first = pts[0]!
  let d = `M ${first.x} ${first.y}`
  for (let i = 1; i < pts.length; i++) {
    const prev = pts[i - 1]!
    const curr = pts[i]!
    const cpx = (prev.x + curr.x) / 2
    d += ` C ${cpx} ${prev.y}, ${cpx} ${curr.y}, ${curr.x} ${curr.y}`
  }
  return d
})

const areaPath = computed(() => {
  if (points.value.length < 2) return ''
  const pts = points.value
  const first = pts[0]!
  const last = pts[pts.length - 1]!
  const bottom = PAD.top + CHART_H
  let d = `M ${first.x} ${bottom} L ${first.x} ${first.y}`
  for (let i = 1; i < pts.length; i++) {
    const prev = pts[i - 1]!
    const curr = pts[i]!
    const cpx = (prev.x + curr.x) / 2
    d += ` C ${cpx} ${prev.y}, ${cpx} ${curr.y}, ${curr.x} ${curr.y}`
  }
  d += ` L ${last.x} ${bottom} Z`
  return d
})

const xLabels = computed(() =>
  points.value.filter((_, i) => i % Math.max(1, Math.floor(points.value.length / 5)) === 0)
)

const gradId = `trendGrad_${Math.random().toString(36).slice(2, 8)}`
</script>

<template>
  <div class="trend-chart">
    <div class="chart-header">
      <span class="chart-label">{{ activeLabel }} 趨勢</span>
    </div>
    <svg :viewBox="`0 0 ${WIDTH} ${HEIGHT}`" preserveAspectRatio="xMidYMid meet" class="chart-svg">
      <defs>
        <linearGradient :id="gradId" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" :stop-color="activeColor" stop-opacity="0.28"/>
          <stop offset="100%" :stop-color="activeColor" stop-opacity="0.01"/>
        </linearGradient>
      </defs>

      <!-- Grid -->
      <line
        v-for="pct in [25, 50, 75, 100]"
        :key="pct"
        :x1="PAD.left"
        :y1="yPos(pct)"
        :x2="PAD.left + CHART_W"
        :y2="yPos(pct)"
        stroke="rgba(10,148,136,0.07)"
        stroke-width="1"
      />

      <!-- Y labels -->
      <text
        v-for="pct in [0, 50, 100]"
        :key="pct"
        :x="PAD.left - 5"
        :y="yPos(pct) + 4"
        text-anchor="end"
        font-size="9"
        fill="#0A7870"
      >{{ pct }}</text>

      <!-- Area -->
      <path :d="areaPath" :fill="`url(#${gradId})`" />

      <!-- Line -->
      <path :d="linePath" fill="none" :stroke="activeColor" stroke-width="2.5" stroke-linecap="round"/>

      <!-- Dots -->
      <circle
        v-for="(pt, i) in points"
        :key="i"
        :cx="pt.x" :cy="pt.y"
        r="3"
        fill="white"
        :stroke="activeColor"
        stroke-width="1.8"
      />

      <!-- X labels -->
      <text
        v-for="pt in xLabels"
        :key="pt.d.date"
        :x="pt.x"
        :y="HEIGHT - 4"
        text-anchor="middle"
        font-size="9"
        fill="#0A7870"
      >{{ formatDateShort(pt.d.date) }}</text>
    </svg>
  </div>
</template>

<style scoped>
.trend-chart {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-label {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
}

.chart-svg {
  width: 100%;
  height: auto;
}
</style>
