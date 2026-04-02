import { ref, computed, onMounted, onUnmounted } from 'vue'

export function useViewport() {
  const width = ref(typeof window !== 'undefined' ? window.innerWidth : 1280)
  const isMobile = computed(() => width.value < 1024)
  const isTablet = computed(() => width.value >= 768 && width.value < 1024)
  const isDesktop = computed(() => width.value >= 1024)

  function update() {
    width.value = window.innerWidth
  }

  onMounted(() => window.addEventListener('resize', update))
  onUnmounted(() => window.removeEventListener('resize', update))

  return { width, isMobile, isTablet, isDesktop }
}
