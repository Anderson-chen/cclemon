import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { DayScore } from '@/types/models'
import { getLast7Days } from '@/utils/dateUtils'
import { useUiStore } from './uiStore'
import { reportApi } from '@/api/report'

export const useReportStore = defineStore('report', () => {
  const ui = useUiStore()
  const period = ref<'week' | 'month'>('week')
  const scoreHistory = ref<DayScore[]>([])

  const last7Scores = computed(() => {
    const days = getLast7Days()
    return days.map(d => scoreHistory.value.find(s => s.date === d) || {
      date: d,
      score: 0,
      breakdown: { diet: 0, exercise: 0, water: 0, consistency: 0 }
    })
  })

  const last30Scores = computed(() => scoreHistory.value)

  const currentPeriodScores = computed(() =>
    period.value === 'week' ? last7Scores.value : last30Scores.value
  )

  const avgScore = computed(() => {
    const scores = currentPeriodScores.value
    if (!scores.length) return 0
    return Math.round(scores.reduce((s, d) => s + d.score, 0) / scores.length)
  })

  const topScores = computed(() =>
    [...currentPeriodScores.value]
      .sort((a, b) => b.score - a.score)
      .slice(0, 3)
  )

  const insights = computed(() => {
    const msgs: string[] = []
    const scores = currentPeriodScores.value
    const avg = avgScore.value
    const exerciseDays = scores.filter(s => s.breakdown.exercise > 0).length
    const avgWater = scores.length
      ? Math.round(scores.reduce((s, d) => s + d.breakdown.water, 0) / scores.length)
      : 0
    const label = period.value === 'week' ? '週' : '月'

    if (avg >= 80) msgs.push(`本${label}平均分數 ${avg} 分，表現優異！繼續保持！`)
    else if (avg >= 60) msgs.push(`本${label}平均分數 ${avg} 分，還有進步空間。`)
    else msgs.push(`本${label}平均分數 ${avg} 分，讓我們一起加油！`)

    msgs.push(
      exerciseDays >= 3
        ? `本週已運動 ${exerciseDays} 天，達成週目標！`
        : `本週運動 ${exerciseDays} 天，距離目標還需 ${Math.max(0, 3 - exerciseDays)} 天。`
    )

    msgs.push(avgWater >= 80 ? '飲水量保持得很好！' : '飲水量有待加強，建議多補充水分。')

    return msgs
  })

  async function fetchReport() {
    ui.setLoading('fetchReport', true)
    try {
      scoreHistory.value = period.value === 'week'
        ? await reportApi.getWeekly()
        : await reportApi.getMonthly()
    } finally {
      ui.setLoading('fetchReport', false)
    }
  }

  function setPeriod(p: 'week' | 'month') {
    period.value = p
    fetchReport()
  }

  return {
    period,
    scoreHistory,
    last7Scores,
    last30Scores,
    currentPeriodScores,
    avgScore,
    topScores,
    insights,
    fetchReport,
    setPeriod
  }
})
