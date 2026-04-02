import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

export interface Toast {
  id: string
  type: 'success' | 'error' | 'info'
  message: string
}

export const useUiStore = defineStore('ui', () => {
  const loadingMap = reactive<Record<string, boolean>>({})
  const toasts = ref<Toast[]>([])
  const activeModal = ref<string | null>(null)

  function setLoading(key: string, val: boolean) {
    loadingMap[key] = val
  }

  function isLoading(key: string) {
    return !!loadingMap[key]
  }

  function showToast(toast: Omit<Toast, 'id'>) {
    const id = Date.now().toString()
    toasts.value.push({ ...toast, id })
    if (toast.type !== 'error') {
      setTimeout(() => removeToast(id), 3000)
    }
  }

  function removeToast(id: string) {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }

  function openModal(name: string) {
    activeModal.value = name
  }

  function closeModal() {
    activeModal.value = null
  }

  return {
    loadingMap,
    toasts,
    activeModal,
    setLoading,
    isLoading,
    showToast,
    removeToast,
    openModal,
    closeModal
  }
})
