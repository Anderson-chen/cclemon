import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('pages/LoginPage.vue'),
  },
  {
    path: '/',
    redirect: '/orders/dashboard',
  },

  {
    path: '/customers',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/CustomerPage.vue') }],
  },

  {
    path: '/orders/dashboard',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/OrderDashboardPage.vue') }],
  },

  {
    path: '/orders',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/OrderPage.vue') }],
  },

  {
    path: '/reports',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/ReportPage.vue') }],
  },

  {
    path: '/settings/services',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/ServiceManagePage.vue') }],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
