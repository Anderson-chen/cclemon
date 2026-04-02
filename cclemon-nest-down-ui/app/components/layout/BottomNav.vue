<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = computed(() => route.meta.tab as string | undefined)

const tabs = [
  {
    id: 'home',
    label: '首頁',
    path: '/home',
    icon: `<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>`
  },
  {
    id: 'diet',
    label: '飲食',
    path: '/diet',
    icon: `<path d="M12 2a4 4 0 0 0-4 4c0 1.5.6 2.8 1.6 3.8L4 22h16l-5.6-12.2C15.4 8.8 16 7.5 16 6a4 4 0 0 0-4-4z"/><path d="M8 22V12"/><path d="M16 22V12"/>`
  },
  {
    id: 'exercise',
    label: '運動',
    path: '/exercise',
    icon: `<path d="M6 5v14"/><path d="M18 5v14"/><path d="M6 8h12"/><path d="M6 16h12"/><circle cx="3" cy="6.5" r="2"/><circle cx="3" cy="17.5" r="2"/><circle cx="21" cy="6.5" r="2"/><circle cx="21" cy="17.5" r="2"/>`
  },
  {
    id: 'weight',
    label: '體重',
    path: '/weight',
    icon: `<circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>`
  },
  {
    id: 'report',
    label: '報告',
    path: '/report',
    icon: `<path d="M3 3v18h18"/><path d="M7 16l4-8 4 4 4-4"/>`
  },
]
</script>

<template>
  <nav class="bottom-nav">
    <RouterLink
      v-for="tab in tabs"
      :key="tab.id"
      :to="tab.path"
      class="nav-item"
      :class="{ active: activeTab === tab.id }"
      :aria-label="tab.label"
      :aria-current="activeTab === tab.id ? 'page' : undefined"
    >
      <div class="nav-icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="tab.icon" />
      </div>
      <span class="nav-label">{{ tab.label }}</span>
      <div v-if="activeTab === tab.id" class="nav-indicator" />
    </RouterLink>
  </nav>
</template>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: calc(64px + env(safe-area-inset-bottom));
  background: rgba(240, 253, 250, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(10, 148, 136, 0.12);
  display: flex;
  align-items: flex-start;
  padding-top: 8px;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 100;
  box-shadow: 0 -2px 20px rgba(10, 148, 136, 0.08);
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  text-decoration: none;
  color: rgba(8, 56, 48, 0.4);
  transition: color 200ms ease;
  padding: 0 4px;
  position: relative;
  cursor: pointer;
}

.nav-item.active {
  color: #0A9488;
}

.nav-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: background 200ms, transform 200ms;
}

.nav-item.active .nav-icon {
  background: rgba(10, 148, 136, 0.12);
  transform: translateY(-1px);
}

.nav-label {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.3px;
  line-height: 1;
}

.nav-indicator {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background: #0A9488;
  border-radius: 0 0 3px 3px;
}
</style>
