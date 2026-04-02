<script setup lang="ts">
import { computed } from 'vue'
import type { WeightEntry } from '@/types/models'
import { formatDateShort } from '@/utils/dateUtils'

const props = defineProps<{
  entries: WeightEntry[]
}>()

const WIDTH = 340
const HEIGHT = 180
const PAD = { top: 20, right: 16, bottom: 40, left: 44 }
const CHART_W = WIDTH - PAD.left - PAD.right
const CHART_H = HEIGHT - PAD.top - PAD.bottom

const sorted = computed(() =>
  [...props.entries].sort((a, b) => a.date.localeCompare(b.date)).slice(-14)
)

const minWeight = computed(() => Math.min(...sorted.value.map(e => e.weight)) - 0.5)
const maxWeight = computed(() => Math.max(...sorted.value.map(e => e.weight)) + 0.5)
const weightRange = computed(() => maxWeight.value - minWeight.value || 1)

function xPos(i: number): number {
  return PAD.left + (i / Math.max(sorted.value.length - 1, 1)) * CHART_W
}

function yPos(w: number): number {
  return PAD.top + CHART_H - ((w - minWeight.value) / weightRange.value) * CHART_H
}

const points = computed(() =>
  sorted.value.map((e, i) => ({ x: xPos(i), y: yPos(e.weight), entry: e, i }))
)

// Build smooth SVG path using cubic bezier
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

// Area fill path
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

// Y-axis labels
const yLabels = computed(() => {
  const steps = 4
  return Array.from({ length: steps + 1 }, (_, i) => {
    const w = minWeight.value + (weightRange.value * i) / steps
    return { y: yPos(w), label: w.toFixed(1) }
  })
})

// X-axis: show every 3rd date label to avoid crowding
const xLabels = computed(() =>
  points.value.filter((_, i) => i % 3 === 0 || i === points.value.length - 1)
)

const latestPoint = computed(() => points.value[points.value.length - 1])
</script>

<template>
  <div class="chart-wrap">
    <svg :viewBox="`0 0 ${WIDTH} ${HEIGHT}`" preserveAspectRatio="xMidYMid meet" class="chart-svg">
      <defs>
        <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stop-color="#0A9488" stop-opacity="0.22"/>
          <stop offset="100%" stop-color="#0A9488" stop-opacity="0.01"/>
        </linearGradient>
      </defs>

      <!-- Grid lines -->
      <g class="grid">
        <line
          v-for="label in yLabels"
          :key="label.label"
          :x1="PAD.left"
          :y1="label.y"
          :x2="PAD.left + CHART_W"
          :y2="label.y"
          stroke="rgba(10,148,136,0.08)"
          stroke-width="1"
        />
      </g>

      <!-- Y axis labels -->
      <g>
        <text
          v-for="label in yLabels"
          :key="label.label"
          :x="PAD.left - 6"
          :y="label.y + 4"
          text-anchor="end"
          font-size="9"
          fill="#0A7870"
        >
          {{ label.label }}
        </text>
      </g>

      <!-- Area fill -->
      <path :d="areaPath" fill="url(#areaGrad)" />

      <!-- Line -->
      <path
        :d="linePath"
        fill="none"
        stroke="#0A9488"
        stroke-width="2.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      />

      <!-- Data dots -->
      <g>
        <circle
          v-for="pt in points"
          :key="pt.i"
          :cx="pt.x"
          :cy="pt.y"
          r="3.5"
          fill="white"
          stroke="#0A9488"
          stroke-width="2"
        />
      </g>

      <!-- Latest point highlight -->
      <circle
        v-if="latestPoint"
        :cx="latestPoint.x"
        :cy="latestPoint.y"
        r="5.5"
        fill="#E86010"
        stroke="white"
        stroke-width="2"
      />

      <!-- X axis labels -->
      <g>
        <text
          v-for="pt in xLabels"
          :key="pt.i"
          :x="pt.x"
          :y="HEIGHT - 6"
          text-anchor="middle"
          font-size="9"
          fill="#0A7870"
        >
          {{ formatDateShort(pt.entry.date) }}
        </text>
      </g>
    </svg>

    <!-- Latest value display -->
    <div v-if="latestPoint" class="chart-latest">
      <span class="latest-label">最新</span>
      <span class="latest-value">{{ latestPoint.entry.weight }} kg</span>
    </div>
  </div>
</template>

<style scoped>
.chart-wrap {
  position: relative;
}

.chart-svg {
  width: 100%;
  height: auto;
  overflow: visible;
}

.chart-latest {
  position: absolute;
  top: 8px;
  right: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.latest-label {
  font-size: 11px;
  color: #0A7870;
}

.latest-value {
  font-family: 'Lora', serif;
  font-size: 18px;
  font-weight: 700;
  color: #E86010;
}
</style>
