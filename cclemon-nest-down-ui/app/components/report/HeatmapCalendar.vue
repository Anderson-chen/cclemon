<script setup lang="ts">
import { computed } from 'vue'
import type { DayScore } from '@/types/models'
import { getDayOfWeek } from '@/utils/dateUtils'

const props = defineProps<{
  scores: DayScore[]
}>()

const dayLabels = ['日', '一', '二', '三', '四', '五', '六']

// Build a 7-column grid
const grid = computed(() => {
  if (!props.scores.length) return []

  const sorted = [...props.scores].sort((a, b) => a.date.localeCompare(b.date))
  const scoreMap = new Map(sorted.map(s => [s.date, s]))

  const first = new Date(sorted[0]!.date)
  const last = new Date(sorted[sorted.length - 1]!.date)

  // Pad to start from Sunday
  const startDay = first.getDay()
  const cells: { date: string | null; score: number; isEmpty: boolean }[] = []

  // Leading empty cells
  for (let i = 0; i < startDay; i++) {
    cells.push({ date: null, score: 0, isEmpty: true })
  }

  // Fill days
  const cur = new Date(first)
  while (cur <= last) {
    const dateStr = cur.toISOString().split('T')[0] ?? ''
    const s = scoreMap.get(dateStr)
    cells.push({ date: dateStr || null, score: s?.score || 0, isEmpty: false })
    cur.setDate(cur.getDate() + 1)
  }

  // Pad to complete last row
  while (cells.length % 7 !== 0) {
    cells.push({ date: null, score: 0, isEmpty: true })
  }

  // Split into weeks
  const weeks: typeof cells[] = []
  for (let i = 0; i < cells.length; i += 7) {
    weeks.push(cells.slice(i, i + 7))
  }
  return weeks
})

function cellColor(score: number, isEmpty: boolean): string {
  if (isEmpty) return 'transparent'
  if (score === 0) return 'rgba(10,148,136,0.06)'
  if (score >= 80) return `rgba(10,148,136,${0.25 + (score - 80) / 100})`
  if (score >= 60) return `rgba(10,148,136,${0.10 + (score - 60) / 200})`
  return 'rgba(10,148,136,0.07)'
}

function isToday(date: string | null): boolean {
  return date === new Date().toISOString().split('T')[0]
}
</script>

<template>
  <div class="heatmap-wrap">
    <!-- Day headers -->
    <div class="day-headers">
      <div v-for="d in dayLabels" :key="d" class="day-header">{{ d }}</div>
    </div>

    <!-- Grid -->
    <div class="calendar-grid">
      <div
        v-for="(week, wi) in grid"
        :key="wi"
        class="week-row"
      >
        <div
          v-for="(cell, di) in week"
          :key="di"
          class="day-cell"
          :class="{ today: isToday(cell.date), empty: cell.isEmpty }"
          :style="{ background: cellColor(cell.score, cell.isEmpty) }"
          :title="cell.date ? `${cell.date}: ${cell.score}分` : ''"
        >
          <span v-if="!cell.isEmpty" class="cell-score">{{ cell.score || '' }}</span>
        </div>
      </div>
    </div>

    <!-- Legend -->
    <div class="legend">
      <span class="legend-label">低</span>
      <div class="legend-scale">
        <div v-for="i in 5" :key="i" class="legend-dot" :style="{ background: `rgba(10,148,136,${i * 0.18})` }" />
      </div>
      <span class="legend-label">高</span>
    </div>
  </div>
</template>

<style scoped>
.heatmap-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.day-headers {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.day-header {
  text-align: center;
  font-size: 11px;
  font-weight: 600;
  color: #0A7870;
}

.calendar-grid {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.day-cell {
  aspect-ratio: 1;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 9px;
  font-weight: 700;
  color: rgba(8, 56, 48, 0.6);
  transition: transform 150ms;
  cursor: default;
}

.day-cell:not(.empty):hover {
  transform: scale(1.15);
  z-index: 1;
  position: relative;
}

.day-cell.today {
  box-shadow: 0 0 0 2px #0A9488;
}

.day-cell.empty {
  opacity: 0;
  pointer-events: none;
}

.cell-score {
  line-height: 1;
}

.legend {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 4px;
}

.legend-label {
  font-size: 10px;
  color: #0A7870;
}

.legend-scale {
  display: flex;
  gap: 3px;
}

.legend-dot {
  width: 14px;
  height: 14px;
  border-radius: 3px;
}
</style>
