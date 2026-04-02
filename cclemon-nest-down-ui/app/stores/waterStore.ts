import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { WaterEntry } from '@/types/models'
import { today } from '@/utils/dateUtils'
import { useUiStore } from './uiStore'
import { waterApi } from '@/api/water'

export const useWaterStore = defineStore('water', () => {
  const ui = useUiStore()

  const entries = ref<WaterEntry[]>([])

  const todayEntry = computed(() => entries.value.find(e => e.date === today()) || null)
  const todayTotal = computed(() => todayEntry.value?.totalMl || 0)
  const todayLogs = computed(() => todayEntry.value?.logs || [])

  async function fetchToday() {
    const entry = await waterApi.getToday(today())
    if (entry) {
      const idx = entries.value.findIndex(e => e.date === today())
      if (idx >= 0) {
        entries.value[idx] = entry
      } else {
        entries.value.push(entry)
      }
    }
  }

  async function addWater(ml: number) {
    // Optimistic update
    const timeStr = new Date().toTimeString().substring(0, 5)
    const existing = entries.value.find(e => e.date === today())
    if (existing) {
      existing.totalMl += ml
      existing.logs.push({ time: timeStr, ml })
    } else {
      entries.value.push({
        id: 'temp', userId: 'u1', date: today(),
        totalMl: ml, logs: [{ time: timeStr, ml }]
      })
    }
    try {
      const updated = await waterApi.logWater(ml)
      const idx = entries.value.findIndex(e => e.date === today())
      if (idx >= 0) entries.value[idx] = updated
      ui.showToast({ type: 'success', message: `+${ml}ml 已記錄` })
    } catch {
      await fetchToday() // revert
      ui.showToast({ type: 'error', message: '記錄失敗' })
    }
  }

  return {
    entries,
    todayEntry,
    todayTotal,
    todayLogs,
    fetchToday,
    addWater
  }
})
