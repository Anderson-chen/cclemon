<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-teal-8">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          <q-icon name="spa" size="sm" class="q-mr-xs" />
          CCLEMON
        </q-toolbar-title>
        <Suspense>
          <UserProfile />
        </Suspense>
      </q-toolbar>
    </q-header>

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
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import EssentialLink, {
  EssentialLinkProps,
} from 'components/EssentialLink.vue';

import UserProfile from 'components/UserProfile.vue';

defineOptions({
  name: 'MainLayout',
});

const linksList: EssentialLinkProps[] = [
  {
    title: '訂單總覽',
    caption: '洗鞋店訂單看板',
    icon: 'dashboard',
    to: '/orders/dashboard',
  },
  {
    title: '開單管理',
    caption: '建立與追蹤洗鞋訂單',
    icon: 'receipt_long',
    to: '/orders',
  },
  {
    title: '會員管理',
    caption: '查詢與管理顧客資料',
    icon: 'group',
    to: '/customers',
  },
  {
    title: '收益報表',
    caption: '日報表、月報表、CSV 匯出',
    icon: 'bar_chart',
    to: '/reports',
  },
  {
    title: '服務設定',
    caption: '服務項目與費率管理',
    icon: 'tune',
    to: '/settings/services',
  },
];

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
</script>
