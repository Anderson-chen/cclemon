import { ref, computed } from 'vue'
import { useGoalStore } from '@/stores/goalStore'

export function useNotifications() {
  const goals = useGoalStore()
  const isSupported = ref('Notification' in window && 'serviceWorker' in navigator)
  const permission = ref<NotificationPermission>(
    'Notification' in window ? Notification.permission : 'denied'
  )

  const isEnabled = computed(() => goals.notificationsEnabled)

  async function requestPermission(): Promise<boolean> {
    if (!isSupported.value) return false
    const result = await Notification.requestPermission()
    permission.value = result
    return result === 'granted'
  }

  async function toggle() {
    if (isEnabled.value) {
      await goals.toggleNotifications(false)
    } else {
      const granted = await requestPermission()
      if (granted) {
        await goals.toggleNotifications(true)
      }
    }
  }

  function sendLocalNotification(title: string, body: string) {
    if (permission.value === 'granted') {
      new Notification(title, {
        body,
        icon: '/icons/icon-192.png',
        badge: '/icons/badge-72.png'
      })
    }
  }

  return {
    isSupported,
    permission,
    isEnabled,
    requestPermission,
    toggle,
    sendLocalNotification
  }
}
