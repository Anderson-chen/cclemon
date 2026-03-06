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
    title: '健康追蹤',
    caption: '健康數據記錄',
    icon: 'favorite_border',
    to: '/health',
  },
  {
    title: '體重記錄',
    caption: '記錄和管理體重',
    icon: 'monitor_weight',
    to: '/weight',
  },
  {
    title: '體重趨勢',
    caption: '查看體重變化圖表',
    icon: 'trending_up',
    to: '/weight/chart',
  },
  {
    title: '會員管理',
    caption: '查詢與管理顧客資料',
    icon: 'group',
    to: '/customers',
  },
];

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
</script>
