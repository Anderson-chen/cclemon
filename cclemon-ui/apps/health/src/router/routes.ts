import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  { path: '/login', component: () => import('pages/LoginPage.vue') },
  { path: '/', redirect: '/health' },
  {
    path: '/health',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/HealthPage.vue') }],
  },
  {
    path: '/weight',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/WeightPage.vue') }],
  },
  {
    path: '/weight/chart',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/WeightChartPage.vue') }],
  },
  { path: '/:catchAll(.*)*', component: () => import('pages/ErrorNotFound.vue') },
];

export default routes;
