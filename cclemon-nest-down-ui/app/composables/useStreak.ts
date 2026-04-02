import { computed } from 'vue'
import { useReportStore } from '@/stores/reportStore'
import { today, yesterday } from '@/utils/dateUtils'

export function useStreak() {
  const report = useReportStore()

  const streakDays = computed(() => {
    const scores = [...report.scoreHistory].sort((a, b) => b.date.localeCompare(a.date))
    let streak = 0
    const todayStr = today()
    let checkDate: string = todayStr

    for (const score of scores) {
      if (score.date === checkDate && score.score > 0) {
        streak++
        const d = new Date(checkDate)
        d.setDate(d.getDate() - 1)
        checkDate = d.toISOString().split('T')[0]!
      } else if (score.date < checkDate) {
        break
      }
    }

    // If today has no score yet, check from yesterday
    if (streak === 0) {
      checkDate = yesterday()
      for (const score of scores) {
        if (score.date === checkDate && score.score > 0) {
          streak++
          const d = new Date(checkDate)
          d.setDate(d.getDate() - 1)
          checkDate = d.toISOString().split('T')[0]!
        } else if (score.date < checkDate) {
          break
        }
      }
    }

    return streak
  })

  const streakMessage = computed(() => {
    const days = streakDays.value
    if (days === 0) return '今天開始記錄，建立你的連續天數！'
    if (days === 1) return '連續 1 天，繼續加油！'
    if (days < 7) return `連續 ${days} 天，不要中斷！`
    if (days < 30) return `連續 ${days} 天，習慣正在養成！`
    return `連續 ${days} 天，你是健康達人！`
  })

  const isAtRisk = computed(() => {
    // User hasn't logged today yet - at risk of breaking streak
    const todayScore = report.scoreHistory.find(s => s.date === today())
    return streakDays.value > 0 && (!todayScore || todayScore.score === 0)
  })

  return { streakDays, streakMessage, isAtRisk }
}
