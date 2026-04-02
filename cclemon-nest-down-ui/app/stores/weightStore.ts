import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { WeightEntry, BodyPhoto } from '@/types/models'
import { useUiStore } from './uiStore'
import { weightApi } from '@/api/weight'

export const useWeightStore = defineStore('weight', () => {
  const ui = useUiStore()

  const entries = ref<WeightEntry[]>([])
  const photos = ref<BodyPhoto[]>([])

  const latestEntry = computed(() => {
    return [...entries.value].sort((a, b) => b.date.localeCompare(a.date))[0] || null
  })

  const latestWeight = computed(() => latestEntry.value?.weight || null)

  const weightChange = computed(() => {
    const sorted = [...entries.value].sort((a, b) => b.date.localeCompare(a.date))
    if (sorted.length < 2) return 0
    return parseFloat((sorted[0]!.weight - sorted[sorted.length - 1]!.weight).toFixed(1))
  })

  const chartData = computed(() => {
    return [...entries.value]
      .sort((a, b) => a.date.localeCompare(b.date))
      .slice(-14)
  })

  async function fetchEntries() {
    ui.setLoading('fetchWeight', true)
    try {
      entries.value = await weightApi.getWeightHistory()
    } finally {
      ui.setLoading('fetchWeight', false)
    }
  }

  async function addEntry(entry: Omit<WeightEntry, 'id'>) {
    ui.setLoading('addWeight', true)
    try {
      const saved = await weightApi.addWeight(entry)
      const idx = entries.value.findIndex(e => e.date === entry.date)
      if (idx >= 0) {
        entries.value[idx] = saved
      } else {
        entries.value.push(saved)
      }
      ui.showToast({ type: 'success', message: '體重已記錄' })
    } finally {
      ui.setLoading('addWeight', false)
    }
  }

  async function deleteEntry(id: string) {
    entries.value = entries.value.filter(e => e.id !== id)
    await weightApi.deleteWeight(id)
  }

  async function addPhoto(photo: Omit<BodyPhoto, 'id'>) {
    const p: BodyPhoto = { ...photo, id: Date.now().toString() }
    photos.value.push(p)
    ui.showToast({ type: 'success', message: '照片已上傳' })
    return p
  }

  async function deletePhoto(id: string) {
    photos.value = photos.value.filter(p => p.id !== id)
  }

  return {
    entries,
    photos,
    latestEntry,
    latestWeight,
    weightChange,
    chartData,
    fetchEntries,
    addEntry,
    deleteEntry,
    addPhoto,
    deletePhoto
  }
})
