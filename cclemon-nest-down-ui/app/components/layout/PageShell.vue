<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useViewport } from '@/composables/useViewport'
import BottomNav from './BottomNav.vue'
import SidebarNav from './SidebarNav.vue'
import AppToast from '@/components/shared/AppToast.vue'

const { isMobile } = useViewport()
const route = useRoute()
const hideNav = computed(() => route.meta.hideNav as boolean | undefined)

import { computed } from 'vue'
</script>

<template>
  <div class="page-shell" :class="{ desktop: !isMobile }">
    <!-- Desktop Sidebar -->
    <SidebarNav v-if="!isMobile" />

    <!-- Main content area -->
    <main class="page-main" :class="{ 'with-sidebar': !isMobile, 'with-bottom-nav': isMobile && !hideNav }">
      <slot />
    </main>

    <!-- Mobile Bottom Nav -->
    <BottomNav v-if="isMobile && !hideNav" />

    <!-- Toasts -->
    <AppToast />
  </div>
</template>

<style scoped>
.page-shell {
  height: 100vh;
  height: 100dvh;
  background: var(--color-bg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-main {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.page-main.with-bottom-nav {
  padding-bottom: calc(64px + env(safe-area-inset-bottom) + 16px);
}

.page-main.with-sidebar {
  margin-left: 220px;
}
</style>
