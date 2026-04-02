import { onMounted, onUnmounted } from 'vue'

const IDLE_TIMEOUT_MS = 30 * 60 * 1000 // 30 分鐘

const ACTIVITY_EVENTS = ['mousemove', 'keydown', 'click', 'touchstart', 'scroll'] as const

export function useIdleTimer(onIdle: () => void) {
  let timer: ReturnType<typeof setTimeout> | null = null

  function reset() {
    if (timer) clearTimeout(timer)
    timer = setTimeout(onIdle, IDLE_TIMEOUT_MS)
  }

  onMounted(() => {
    ACTIVITY_EVENTS.forEach((e) => window.addEventListener(e, reset, { passive: true }))
    reset()
  })

  onUnmounted(() => {
    ACTIVITY_EVENTS.forEach((e) => window.removeEventListener(e, reset))
    if (timer) clearTimeout(timer)
  })
}
