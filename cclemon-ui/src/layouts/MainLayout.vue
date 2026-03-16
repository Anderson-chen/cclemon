<template>
  <q-layout view="lHh Lpr lFf">
    <q-header class="app-header">
      <q-toolbar class="app-toolbar">
        <!-- 桌機才顯示漢堡選單 -->
        <q-btn
          v-if="$q.screen.gt.sm"
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          color="white"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title class="app-brand">
          <q-icon name="spa" size="22px" color="white" />
          <span class="app-brand-name">Shoes Reborn</span>
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <!-- 桌機左側 Drawer -->
    <q-drawer v-model="leftDrawerOpen" show-if-above bordered class="app-drawer">
      <!-- Brand section -->
      <div class="drawer-brand">
        <div class="drawer-brand-icon">
          <q-icon name="spa" size="24px" color="white" />
        </div>
        <div>
          <div class="drawer-brand-title">Shoes Reborn</div>
          <div class="drawer-brand-sub">洗鞋管理系統</div>
        </div>
      </div>

      <q-separator />

      <q-list class="drawer-nav q-mt-sm">
        <q-item-label header class="drawer-nav-label">主選單</q-item-label>
        <EssentialLink
          v-for="link in linksList"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>

    <!-- 手機 / 平板底部導航列 -->
    <q-footer v-if="$q.screen.lt.md" class="bottom-nav">
      <q-tabs
        dense
        active-color="teal-8"
        indicator-color="teal-8"
        align="justify"
        no-caps
      >
        <q-route-tab
          v-for="link in linksList"
          :key="link.to"
          :to="link.to"
          :icon="link.icon"
          :label="link.shortTitle"
          :exact="link.exact"
        />
      </q-tabs>
    </q-footer>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useQuasar } from 'quasar';
import EssentialLink, {
  EssentialLinkProps,
} from 'components/EssentialLink.vue';

defineOptions({
  name: 'MainLayout',
});

interface NavLink extends EssentialLinkProps {
  shortTitle: string;
  exact?: boolean;
}

const linksList: NavLink[] = [
  {
    title: '訂單總覽',
    shortTitle: '總覽',
    caption: '洗鞋店訂單看板',
    icon: 'dashboard',
    to: '/orders/dashboard',
  },
  {
    title: '訂單管理',
    shortTitle: '訂單',
    caption: '建立與追蹤洗鞋訂單',
    icon: 'receipt_long',
    to: '/orders',
    exact: true,
  },
  {
    title: '會員管理',
    shortTitle: '會員',
    caption: '查詢與管理顧客資料',
    icon: 'group',
    to: '/customers',
  },
  {
    title: '收益報表',
    shortTitle: '報表',
    caption: '日報表、月報表、CSV 匯出',
    icon: 'bar_chart',
    to: '/reports',
  },
  {
    title: '服務設定',
    shortTitle: '設定',
    caption: '服務項目與費率管理',
    icon: 'tune',
    to: '/settings/services',
  },
];

const $q = useQuasar();
const route = useRoute();
const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

// 手機版切換路由後關閉 drawer（桌機不自動關閉）
watch(
  () => route.path,
  () => {
    if ($q.screen.lt.md) {
      leftDrawerOpen.value = false;
    }
  },
);
</script>

<style scoped>
/* ── Header ─────────────────────────────────────────────────── */
.app-header {
  background: linear-gradient(135deg, #0f766e 0%, #0d9488 100%);
  box-shadow: 0 1px 0 rgba(255,255,255,0.08), 0 2px 8px rgba(15, 118, 110, 0.25);
}

.app-toolbar {
  min-height: 52px;
  padding: 0 12px;
}

.app-brand {
  display: flex;
  align-items: center;
  gap: 8px;
}

.app-brand-name {
  font-size: 1rem;
  font-weight: 700;
  color: white;
  letter-spacing: 0.01em;
}

/* ── Drawer ──────────────────────────────────────────────────── */
.app-drawer {
  background: #ffffff;
}

.drawer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 16px 16px;
}

.drawer-brand-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0f766e, #0d9488);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.drawer-brand-title {
  font-size: 0.9375rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
}

.drawer-brand-sub {
  font-size: 0.72rem;
  color: #94a3b8;
  margin-top: 1px;
  font-weight: 500;
}

.drawer-nav {
  padding: 0 8px;
}

.drawer-nav-label {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #94a3b8;
  padding: 8px 8px 4px;
}

/* ── Bottom Nav ──────────────────────────────────────────────── */
.bottom-nav {
  background: rgba(255, 255, 255, 0.95);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(8px);
}

.bottom-nav :deep(.q-tab) {
  min-height: 56px;
  padding: 6px 4px;
  color: #94a3b8;
  transition: color 0.15s ease;
}

.bottom-nav :deep(.q-tab--active) {
  color: #0f766e;
}

.bottom-nav :deep(.q-tab__label) {
  font-size: 0.68rem;
  font-weight: 600;
  margin-top: 2px;
  letter-spacing: 0.02em;
}

.bottom-nav :deep(.q-tab__icon) {
  font-size: 22px;
}
</style>
