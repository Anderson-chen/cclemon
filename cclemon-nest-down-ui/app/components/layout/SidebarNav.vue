<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
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
    label: '飲食記錄',
    path: '/diet',
    icon: `<path d="M12 2a4 4 0 0 0-4 4c0 1.5.6 2.8 1.6 3.8L4 22h16l-5.6-12.2C15.4 8.8 16 7.5 16 6a4 4 0 0 0-4-4z"/>`
  },
  {
    id: 'exercise',
    label: '運動紀錄',
    path: '/exercise',
    icon: `<path d="M6 5v14"/><path d="M18 5v14"/><path d="M6 8h12"/><path d="M6 16h12"/><circle cx="3" cy="6.5" r="2"/><circle cx="3" cy="17.5" r="2"/><circle cx="21" cy="6.5" r="2"/><circle cx="21" cy="17.5" r="2"/>`
  },
  {
    id: 'weight',
    label: '體重追蹤',
    path: '/weight',
    icon: `<circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>`
  },
  {
    id: 'report',
    label: '健康報告',
    path: '/report',
    icon: `<path d="M3 3v18h18"/><path d="M7 16l4-8 4 4 4-4"/>`
  },
]

function handleLogout() {
  auth.logout()
  router.push('/auth/login')
}
</script>

<template>
  <aside class="sidebar">
    <!-- Logo -->
    <div class="sidebar-logo">
      <div class="logo-mark">N</div>
      <div class="logo-text">
        <span class="logo-name">NestDown</span>
        <span class="logo-tagline">健康追蹤</span>
      </div>
    </div>

    <!-- User -->
    <div v-if="auth.user" class="sidebar-user">
      <div class="user-avatar">
        <img v-if="auth.user.avatarUrl" :src="auth.user.avatarUrl" :alt="auth.user.name" />
        <span v-else>{{ auth.user.name.charAt(0) }}</span>
      </div>
      <div class="user-info">
        <p class="user-name">{{ auth.user.name }}</p>
        <p class="user-email">{{ auth.user.email }}</p>
      </div>
    </div>

    <!-- Nav -->
    <nav class="sidebar-nav">
      <RouterLink
        v-for="tab in tabs"
        :key="tab.id"
        :to="tab.path"
        class="sidebar-item"
        :class="{ active: activeTab === tab.id }"
        :aria-current="activeTab === tab.id ? 'page' : undefined"
      >
        <div class="item-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="tab.icon" />
        </div>
        <span class="item-label">{{ tab.label }}</span>
      </RouterLink>
    </nav>

    <!-- Settings + Logout at bottom -->
    <div class="sidebar-footer">
      <RouterLink to="/settings" class="sidebar-item" :class="{ active: route.path === '/settings' }">
        <div class="item-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
          </svg>
        </div>
        <span class="item-label">設定</span>
      </RouterLink>

      <button class="sidebar-item logout-btn" @click="handleLogout">
        <div class="item-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </div>
        <span class="item-label">登出</span>
      </button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 220px;
  height: 100vh;
  background: rgba(240, 253, 250, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(10, 148, 136, 0.12);
  display: flex;
  flex-direction: column;
  padding: 24px 12px;
  gap: 8px;
  z-index: 100;
  box-shadow: 2px 0 20px rgba(10, 148, 136, 0.06);
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px 20px;
}

.logo-mark {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #0A9488, #2AACA8);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Lora', serif;
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-name {
  font-family: 'Lora', serif;
  font-size: 16px;
  font-weight: 700;
  color: #083830;
  line-height: 1.2;
}

.logo-tagline {
  font-size: 11px;
  color: #0A7870;
  font-weight: 500;
}

.sidebar-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: rgba(10, 148, 136, 0.06);
  border-radius: 14px;
  margin-bottom: 8px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0A9488, #2AACA8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  overflow: hidden;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  font-size: 11px;
  color: #0A7870;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  text-decoration: none;
  color: rgba(8, 56, 48, 0.5);
  font-size: 14px;
  font-weight: 500;
  transition: all 200ms ease;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.sidebar-item:hover {
  background: rgba(10, 148, 136, 0.06);
  color: #0A9488;
}

.sidebar-item.active {
  background: rgba(10, 148, 136, 0.12);
  color: #0A9488;
  font-weight: 600;
}

.item-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-label {
  white-space: nowrap;
}

.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 8px;
  border-top: 1px solid rgba(10, 148, 136, 0.1);
}

.logout-btn {
  color: rgba(229, 62, 62, 0.7);
}

.logout-btn:hover {
  background: rgba(229, 62, 62, 0.06);
  color: #e53e3e;
}
</style>
