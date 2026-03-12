<template>
  <q-page padding class="dashboard-page">
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="dashboard" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">
            訂單總覽
          </h5>
          <div class="text-caption text-grey-7">Shoes Reborn · 即時資料</div>
        </div>
      </div>
    </div>

    <!-- 載入中骨架 -->
    <div v-if="loading" class="q-pb-md">
      <q-card class="section-card q-mb-md">
        <q-card-section class="q-py-sm q-px-md">
          <div class="kpi-chart-inner">
            <q-skeleton type="circle" width="120px" height="120px" animation="fade" />
            <div class="kpi-legend" style="flex: 1">
              <q-skeleton type="text" width="80%" animation="fade" />
              <q-skeleton type="text" width="60%" animation="fade" />
              <q-skeleton type="text" width="70%" animation="fade" />
            </div>
          </div>
        </q-card-section>
      </q-card>
      <q-card class="section-card">
        <q-skeleton type="rect" height="48px" animation="fade" />
        <q-separator />
        <q-skeleton type="rect" height="64px" animation="fade" />
        <q-skeleton type="rect" height="64px" animation="fade" class="q-mt-xs" />
        <q-skeleton type="rect" height="64px" animation="fade" class="q-mt-xs" />
      </q-card>
    </div>

    <template v-else>
      <!-- 圓餅圖 -->
      <q-card class="section-card q-mb-md">
        <q-card-section class="q-py-sm q-px-md">
          <div v-if="totalActive === 0" class="text-center q-py-md">
            <q-icon name="donut_large" size="3em" color="grey-3" />
            <div class="text-caption text-grey-6 q-mt-xs">目前無進行中訂單</div>
          </div>
          <div v-else class="kpi-chart-inner">
            <svg viewBox="0 0 120 120" class="kpi-donut-svg">
              <circle
                cx="60"
                cy="60"
                r="48"
                fill="none"
                stroke="#f1f5f9"
                stroke-width="18"
              />
              <circle
                v-for="seg in donutSegments"
                :key="seg.label"
                cx="60"
                cy="60"
                r="48"
                fill="none"
                :stroke="seg.color"
                stroke-width="18"
                stroke-linecap="butt"
                :stroke-dasharray="`${seg.dash} ${seg.gap}`"
                :stroke-dashoffset="seg.offset"
                style="transition: stroke-dasharray 0.6s ease"
              />
              <text
                x="60"
                y="56"
                text-anchor="middle"
                dominant-baseline="middle"
                class="donut-center-num"
              >
                {{ totalActive }}
              </text>
              <text
                x="60"
                y="71"
                text-anchor="middle"
                dominant-baseline="middle"
                class="donut-center-label"
              >
                筆
              </text>
            </svg>
            <div class="kpi-legend">
              <div
                v-for="seg in donutSegments"
                :key="seg.label"
                class="kpi-legend-item"
              >
                <span
                  class="kpi-legend-dot"
                  :style="`background: ${seg.color}`"
                ></span>
                <span class="kpi-legend-text">{{ seg.label }}</span>
                <span class="kpi-legend-val" :style="`color: ${seg.color}`">{{
                  seg.count
                }}</span>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 三合一 Tabs 區塊 -->
      <q-card class="section-card">
        <div class="tab-bar">
          <button
            class="tab-pill"
            :class="{ 'tab-pill--active tab-pill--ready': activeTab === 'ready' }"
            @click="activeTab = 'ready'"
          >
            待取件
            <span v-if="readyOrders.length > 0" class="tab-count tab-count--ready">
              {{ readyOrders.length }}
            </span>
          </button>
          <button
            class="tab-pill"
            :class="{ 'tab-pill--active tab-pill--expiring': activeTab === 'expiring' }"
            @click="activeTab = 'expiring'"
          >
            即將到期
            <span v-if="tomorrowPendingOrders.length > 0" class="tab-count tab-count--expiring">
              {{ tomorrowPendingOrders.length }}
            </span>
          </button>
          <button
            class="tab-pill"
            :class="{ 'tab-pill--active tab-pill--urgent': activeTab === 'urgent' }"
            @click="activeTab = 'urgent'"
          >
            急件
            <span v-if="urgentActiveOrders.length > 0" class="tab-count tab-count--urgent">
              {{ urgentActiveOrders.length }}
            </span>
          </button>
        </div>

        <q-separator />

        <q-tab-panels v-model="activeTab" animated>
          <!-- 待處理急件 -->
          <q-tab-panel name="urgent" class="q-pa-none">
            <q-card-section
              v-if="urgentActiveOrders.length === 0"
              class="text-center q-py-lg"
            >
              <q-icon name="check_circle" size="3em" color="teal-4" />
              <div class="text-body2 text-grey-6 q-mt-sm">目前無急件</div>
            </q-card-section>
            <q-list v-else separator>
              <q-item
                v-for="order in urgentActiveOrders.slice(0, 3)"
                :key="order.id"
                class="order-list-item order-list-item--urgent cursor-pointer"
                clickable
                @click="openDetail(order)"
              >
                <q-item-section>
                  <q-item-label class="text-weight-bold text-grey-9 text-body2">
                    {{ order.customerName }}
                  </q-item-label>
                  <q-item-label caption class="text-grey-6 q-mt-xs">
                    {{ order.customerPhone }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side class="items-end">
                  <div class="pickup-date text-red-7">
                    {{ order.estimatedPickupDate ?? '–' }}
                  </div>
                  <div class="pickup-label text-grey-6">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-if="urgentActiveOrders.length > 3" class="show-more-row">
              <q-btn
                flat
                dense
                size="sm"
                color="red-6"
                icon-right="chevron_right"
                :label="`顯示更多（共 ${urgentActiveOrders.length} 筆）`"
                @click="goToFiltered({ isUrgent: true, status: 'PENDING' })"
                class="cursor-pointer"
              />
            </div>
          </q-tab-panel>

          <!-- 待取件 -->
          <q-tab-panel name="ready" class="q-pa-none">
            <q-card-section
              v-if="readyOrders.length === 0"
              class="text-center q-py-lg"
            >
              <q-icon name="check_circle_outline" size="3em" color="teal-3" />
              <div class="text-body2 text-grey-6 q-mt-sm">目前無待取件</div>
            </q-card-section>
            <q-list v-else separator>
              <q-item
                v-for="order in readyOrders.slice(0, 3)"
                :key="order.id"
                class="order-list-item order-list-item--ready cursor-pointer"
                clickable
                @click="openDetail(order)"
              >
                <q-item-section>
                  <q-item-label class="text-weight-bold text-grey-9 text-body2">
                    {{ order.customerName }}
                  </q-item-label>
                  <q-item-label caption class="text-grey-6 q-mt-xs">
                    {{ order.customerPhone }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side class="items-end">
                  <div class="pickup-date text-teal-8">
                    {{ order.estimatedPickupDate ?? '–' }}
                  </div>
                  <div class="pickup-label text-grey-6">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-if="readyOrders.length > 3" class="show-more-row">
              <q-btn
                flat
                dense
                size="sm"
                color="teal-8"
                icon-right="chevron_right"
                :label="`顯示更多（共 ${readyOrders.length} 筆）`"
                @click="goToFiltered({ status: 'READY' })"
                class="cursor-pointer"
              />
            </div>
          </q-tab-panel>

          <!-- 即將到期 -->
          <q-tab-panel name="expiring" class="q-pa-none">
            <q-card-section
              v-if="tomorrowPendingOrders.length === 0"
              class="text-center q-py-lg"
            >
              <q-icon name="event_available" size="3em" color="orange-3" />
              <div class="text-body2 text-grey-6 q-mt-sm">明日無待處理訂單</div>
            </q-card-section>
            <q-list v-else separator>
              <q-item
                v-for="order in tomorrowPendingOrders.slice(0, 3)"
                :key="order.id"
                class="order-list-item order-list-item--expiring cursor-pointer"
                clickable
                @click="openDetail(order)"
              >
                <q-item-section>
                  <q-item-label class="text-weight-bold text-grey-9 text-body2">
                    {{ order.customerName }}
                  </q-item-label>
                  <q-item-label caption class="text-grey-6 q-mt-xs">
                    {{ order.customerPhone }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side class="items-end">
                  <div class="pickup-date text-orange-8">
                    {{ order.estimatedPickupDate ?? '–' }}
                  </div>
                  <div class="pickup-label text-grey-6">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-if="tomorrowPendingOrders.length > 3" class="show-more-row">
              <q-btn
                flat
                dense
                size="sm"
                color="orange-8"
                icon-right="chevron_right"
                :label="`顯示更多（共 ${tomorrowPendingOrders.length} 筆）`"
                @click="
                  goToFiltered({
                    status: 'PENDING',
                    dateFrom: tomorrowStr(),
                    dateTo: tomorrowStr(),
                  })
                "
                class="cursor-pointer"
              />
            </div>
          </q-tab-panel>
        </q-tab-panels>
      </q-card>
    </template>

    <!-- 訂單詳情 Dialog -->
    <OrderDetailDialog
      v-model="detailDialog.open"
      :order="detailDialog.order"
      :show-edit="false"
      @status-updated="onStatusUpdated"
    />
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { listOrders } from '../api/order/order';
import OrderDetailDialog from '../components/OrderDetailDialog.vue';
import type { OrderResult } from '../api/order/types';
import { useOrderNavStore } from '../stores/orderNavStore';
import type { PendingFilters } from '../stores/orderNavStore';

const $q = useQuasar();
const router = useRouter();
const orderNavStore = useOrderNavStore();

// ── Tabs ───────────────────────────────────────────────────
const activeTab = ref<'urgent' | 'ready' | 'expiring'>('ready');

// ── 資料 ──────────────────────────────────────────────────
const orders = ref<OrderResult[]>([]);
const loading = ref(false);

const loadOrders = async () => {
  try {
    loading.value = true;
    const res = await listOrders({ page: 0, size: 200 });
    orders.value = res.content || [];
  } catch {
    $q.notify({ type: 'negative', message: '載入資料失敗，請稍後再試' });
  } finally {
    loading.value = false;
  }
};

// ── 計算指標 ───────────────────────────────────────────────
const pendingOrders = computed(() =>
  (orders.value || []).filter((o) => o.status === 'PENDING'),
);
const inProgressOrders = computed(() =>
  (orders.value || []).filter((o) => o.status === 'IN_PROGRESS'),
);
const readyOrders = computed(() =>
  (orders.value || []).filter((o) => o.status === 'READY'),
);

const urgentActiveOrders = computed(() =>
  (orders.value || []).filter(
    (o) => o.isUrgent && o.status === 'PENDING',
  ),
);

const totalActive = computed(
  () =>
    pendingOrders.value.length +
    inProgressOrders.value.length +
    readyOrders.value.length,
);

// ── Donut Chart ────────────────────────────────────────────
const CIRC = 2 * Math.PI * 48;

const donutSegments = computed(() => {
  const data = [
    { label: '待處理', count: pendingOrders.value.length, color: '#f97316' },
    { label: '處理中', count: inProgressOrders.value.length, color: '#0ea5e9' },
    { label: '待取件', count: readyOrders.value.length, color: '#0f766e' },
  ];
  const total = totalActive.value || 1;
  let accumulated = 0;
  return data.map((d) => {
    const dash = (d.count / total) * CIRC;
    const gap = CIRC - dash;
    const dashoffset = CIRC * 0.25 - accumulated;
    accumulated += dash;
    return { ...d, dash, gap, offset: dashoffset };
  });
});

const tomorrowPendingOrders = computed(() => {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  const tomorrowStr = d.toISOString().slice(0, 10);
  return (orders.value || []).filter(
    (o) => o.status === 'PENDING' && o.estimatedPickupDate === tomorrowStr,
  );
});

const detailDialog = reactive({
  open: false,
  order: null as OrderResult | null,
});

function openDetail(order: OrderResult) {
  detailDialog.order = order;
  detailDialog.open = true;
}

function onStatusUpdated(updated: OrderResult) {
  // update in-place so the lists refresh without a full reload
  const idx = orders.value.findIndex((o) => o.id === updated.id);
  if (idx !== -1) orders.value[idx] = updated;
}

function goToFiltered(filters: PendingFilters) {
  orderNavStore.setFilters(filters);
  router.push('/orders');
}

function tomorrowStr(): string {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().slice(0, 10);
}

onMounted(loadOrders);
</script>

<style scoped>
.dashboard-page {
  max-width: 1200px;
  margin: 0 auto;
  overflow-x: hidden;
}

/* ── KPI Row ─────────────────────────────────────────────── */
.kpi-row {
  display: grid;
  grid-template-columns: 150px 1fr;
  gap: 12px;
  align-items: stretch;
}

@media (max-width: 599px) {
  .kpi-row {
    grid-template-columns: 1fr;
  }
}

/* 總數卡片 */
.kpi-total-card {
  background: linear-gradient(135deg, #0f766e, #0d9488);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(13, 148, 136, 0.3);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kpi-total-card::before {
  content: '';
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  top: -30px;
  right: -30px;
}

.kpi-total-inner {
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  text-align: center;
  z-index: 1;
}

.kpi-total-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2px;
}

.kpi-total-num {
  font-size: 2rem;
  font-weight: 900;
  color: white;
  line-height: 1;
  letter-spacing: -1px;
}

.kpi-total-label {
  font-size: 0.72rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  letter-spacing: 0.3px;
}

/* 圓餅圖卡片 */
.kpi-chart-card {
  background: white;
  border-radius: 16px;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.06),
    0 4px 16px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 24px;
  min-height: 140px;
}

.kpi-chart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.kpi-chart-inner {
  display: flex;
  align-items: center;
  gap: 28px;
  width: 100%;
}

.kpi-donut-svg {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.donut-center-num {
  font-size: 22px;
  font-weight: 800;
  fill: #0f172a;
}

.donut-center-label {
  font-size: 11px;
  fill: #94a3b8;
  font-weight: 500;
}

/* 圖例 */
.kpi-legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.kpi-legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.kpi-legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.kpi-legend-text {
  font-size: 0.82rem;
  color: #475569;
  flex: 1;
}

.kpi-legend-val {
  font-size: 1rem;
  font-weight: 700;
  min-width: 20px;
  text-align: right;
}

.section-header {
  border-bottom: 1px solid rgba(0, 150, 136, 0.1);
  padding-bottom: 12px;
  display: flex;
  align-items: center;
}

/* 訂單列表項目 */
.order-list-item {
  padding: 12px 16px;
  border-left: 3px solid transparent;
  transition: background 0.18s ease, border-color 0.18s ease;
}

.order-list-item:hover {
  background: rgba(0, 0, 0, 0.025);
}

.order-list-item--urgent {
  border-left-color: #ef4444;
}

.order-list-item--urgent:hover {
  background: rgba(239, 68, 68, 0.04);
}

.order-list-item--ready {
  border-left-color: #0f766e;
}

.order-list-item--ready:hover {
  background: rgba(15, 118, 110, 0.04);
}

.order-list-item--expiring {
  border-left-color: #f97316;
}

.order-list-item--expiring:hover {
  background: rgba(249, 115, 22, 0.04);
}

.pickup-date {
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.pickup-label {
  font-size: 0.72rem;
  margin-top: 2px;
}

/* 顯示更多 */
.show-more-row {
  padding: 6px 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
}

/* 最近訂單列表 */
.recent-item {
  min-height: 64px;
  transition: background 0.2s;
}

.recent-item:hover {
  background: rgba(234, 88, 12, 0.04);
}

/* Tabs */
.tab-bar {
  display: flex;
  gap: 6px;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 12px 12px 0 0;
}

.tab-pill {
  flex: 1;
  justify-content: center;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 13px;
  border-radius: 20px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 0.82rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.18s ease, color 0.18s ease, box-shadow 0.18s ease;
  white-space: nowrap;
}

.tab-pill:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #334155;
}

.tab-pill--active {
  background: white;
  color: #0f172a;
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.tab-count {
  font-size: 0.7rem;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 10px;
  line-height: 1.4;
}

.tab-count--ready {
  background: rgba(15, 118, 110, 0.12);
  color: #0f766e;
}

.tab-count--expiring {
  background: rgba(249, 115, 22, 0.12);
  color: #c2410c;
}

.tab-count--urgent {
  background: rgba(239, 68, 68, 0.12);
  color: #dc2626;
}

/* 停用動畫偏好：關閉甜甜圈圖動畫 */
@media (prefers-reduced-motion: reduce) {
  circle {
    transition: none !important;
  }
}
</style>
