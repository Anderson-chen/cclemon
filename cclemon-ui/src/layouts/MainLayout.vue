<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-teal-8">
      <q-toolbar>
        <!-- 桌機才顯示漢堡選單 -->
        <q-btn
          v-if="$q.screen.gt.sm"
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          <q-icon name="spa" size="sm" class="q-mr-xs" />
        </q-toolbar-title>
        <Suspense> </Suspense>
      </q-toolbar>
    </q-header>

    <!-- 桌機左側 Drawer -->
    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header class="text-weight-bold text-grey-7">
          主選單
        </q-item-label>

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
.bottom-nav {
  background: white;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

.bottom-nav :deep(.q-tab) {
  min-height: 56px;
  padding: 6px 4px;
  color: #9e9e9e;
}

.bottom-nav :deep(.q-tab--active) {
  color: #0f766e;
}

.bottom-nav :deep(.q-tab__label) {
  font-size: 0.72rem;
  font-weight: 500;
  margin-top: 2px;
}
</style>
