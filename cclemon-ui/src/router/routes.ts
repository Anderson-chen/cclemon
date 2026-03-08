import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('pages/LoginPage.vue'),
  },
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/IndexPage.vue') }],
  },

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
    children: [
      { path: '', component: () => import('pages/WeightChartPage.vue') },
    ],
  },

  {
    path: '/customers',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/CustomerPage.vue') }],
  },

  {
    path: '/orders',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/OrderPage.vue') }],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
