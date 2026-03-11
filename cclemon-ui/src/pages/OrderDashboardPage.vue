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
          <div class="text-caption text-grey-5">Shoes Reborn · 即時資料</div>
        </div>
      </div>
    </div>

    <!-- 載入中覆蓋 -->
    <div v-if="loading" class="text-center q-py-xl">
      <q-spinner color="teal-8" size="3em" />
      <div class="text-caption text-grey-5 q-mt-sm">載入資料中...</div>
    </div>

    <template v-else>
      <!-- 圓餅圖 -->
      <q-card class="section-card q-mb-md">
        <q-card-section class="q-py-sm q-px-md">
          <div v-if="totalActive === 0" class="text-center q-py-md">
            <q-icon name="donut_large" size="3em" color="grey-3" />
            <div class="text-caption text-grey-5 q-mt-xs">目前無進行中訂單</div>
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
        <q-tabs
          v-model="activeTab"
          dense
          align="left"
          class="dashboard-tabs"
          indicator-color="teal-8"
          active-color="teal-8"
        >
          <q-tab name="ready" class="tab-item">
            <div class="row items-center q-gutter-xs no-wrap">
              <q-icon name="inventory_2" size="xs" />
              <span>待取件</span>
              <q-badge
                v-if="readyOrders.length > 0"
                color="teal-8"
                :label="readyOrders.length"
                class="tab-badge"
              />
            </div>
          </q-tab>
          <q-tab name="expiring" class="tab-item">
            <div class="row items-center q-gutter-xs no-wrap">
              <q-icon name="event_busy" size="xs" />
              <span>即將到期</span>
              <q-badge
                v-if="tomorrowPendingOrders.length > 0"
                color="orange-8"
                :label="tomorrowPendingOrders.length"
                class="tab-badge"
              />
            </div>
          </q-tab>
          <q-tab name="urgent" class="tab-item">
            <div class="row items-center q-gutter-xs no-wrap">
              <q-icon name="bolt" size="xs" />
              <span>待處理急件</span>
              <q-badge
                v-if="urgentActiveOrders.length > 0"
                color="red-6"
                :label="urgentActiveOrders.length"
                class="tab-badge"
              />
            </div>
          </q-tab>
        </q-tabs>

        <q-separator />

        <q-tab-panels v-model="activeTab" animated>
          <!-- 待處理急件 -->
          <q-tab-panel name="urgent" class="q-pa-none">
            <q-card-section
              v-if="urgentActiveOrders.length === 0"
              class="text-center q-py-lg"
            >
              <q-icon name="check_circle" size="3em" color="teal-4" />
              <div class="text-body2 text-grey-5 q-mt-sm">目前無急件</div>
            </q-card-section>
            <q-list v-else dense separator>
              <q-item
                v-for="order in urgentActiveOrders.slice(0, 4)"
                :key="order.id"
                class="list-item cursor-pointer"
                clickable
                @click="openDetail(order)"
              >
                <q-item-section avatar>
                  <q-avatar
                    :color="statusColorMap[order.status]"
                    text-color="white"
                    size="36px"
                    :icon="statusIconMap[order.status]"
                  />
                </q-item-section>
                <q-item-section>
                  <q-item-label
                    class="text-weight-medium row items-center q-gutter-xs"
                  >
                    <span>{{ order.orderNo }}</span>
                    <q-badge
                      :color="statusColorMap[order.status]"
                      :label="statusLabelMap[order.status]"
                      outline
                      size="xs"
                    />
                  </q-item-label>
                  <q-item-label caption
                    >{{ order.customerName }} ·
                    {{ order.customerPhone }}</q-item-label
                  >
                </q-item-section>
                <q-item-section side>
                  <div class="text-caption text-grey-9 text-weight-medium">
                    {{ order.estimatedPickupDate ?? '–' }}
                  </div>
                  <div class="text-caption text-grey-5">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-if="urgentActiveOrders.length > 4" class="show-more-row">
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
              <div class="text-body2 text-grey-5 q-mt-sm">目前無待取件</div>
            </q-card-section>
            <q-list v-else dense separator>
              <q-item
                v-for="order in readyOrders.slice(0, 4)"
                :key="order.id"
                class="list-item cursor-pointer"
                clickable
                @click="openDetail(order)"
              >
                <q-item-section avatar>
                  <q-avatar
                    color="teal-6"
                    text-color="white"
                    size="36px"
                    icon="check_circle"
                  />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-medium">{{
                    order.orderNo
                  }}</q-item-label>
                  <q-item-label caption
                    >{{ order.customerName }} ·
                    {{ order.customerPhone }}</q-item-label
                  >
                </q-item-section>
                <q-item-section side>
                  <div class="text-caption text-grey-9 text-weight-medium">
                    {{ order.estimatedPickupDate ?? '–' }}
                  </div>
                  <div class="text-caption text-grey-5">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-if="readyOrders.length > 4" class="show-more-row">
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
              <div class="text-body2 text-grey-4 q-mt-sm">明日無待處理訂單</div>
            </q-card-section>
            <q-list v-else separator>
              <q-item
                v-for="order in tomorrowPendingOrders.slice(0, 4)"
                :key="order.id"
                class="list-item cursor-pointer"
                clickable
                @click="openDetail(order)"
              >
                <q-item-section avatar>
                  <q-avatar
                    :color="statusColorMap[order.status]"
                    text-color="white"
                    size="36px"
                    :icon="statusIconMap[order.status]"
                  />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="row items-center q-gutter-xs">
                    <span class="text-weight-medium">{{ order.orderNo }}</span>
                    <q-badge
                      :color="statusColorMap[order.status]"
                      :label="statusLabelMap[order.status]"
                      outline
                      size="xs"
                    />
                  </q-item-label>
                  <q-item-label caption
                    >{{ order.customerName }} ·
                    {{ order.customerPhone }}</q-item-label
                  >
                </q-item-section>
                <q-item-section side>
                  <div class="text-caption text-grey-9 text-weight-medium">
                    {{ order.estimatedPickupDate }}
                  </div>
                  <div class="text-caption text-grey-5">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
            <div v-if="tomorrowPendingOrders.length > 4" class="show-more-row">
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

// ── 輔助對應表 ─────────────────────────────────────────────
const statusColorMap: Record<string, string> = {
  PENDING: 'orange-6',
  IN_PROGRESS: 'blue-6',
  READY: 'teal-6',
  PICKED_UP: 'grey-5',
  CANCELLED: 'red-4',
};

const statusIconMap: Record<string, string> = {
  PENDING: 'hourglass_empty',
  IN_PROGRESS: 'sync',
  READY: 'check_circle',
  PICKED_UP: 'done_all',
  CANCELLED: 'cancel',
};

const statusLabelMap: Record<string, string> = {
  PENDING: '待處理',
  IN_PROGRESS: '處理中',
  READY: '待取件',
  PICKED_UP: '已取件',
  CANCELLED: '已取消',
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

/* 標題圖示 */
.title-icon-wrap {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #0f766e, #0d9488);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(15, 118, 110, 0.3);
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

/* 區塊卡片 */
.section-card {
  border-radius: 12px;
  box-shadow:
    0 1px 4px rgba(0, 0, 0, 0.07),
    0 4px 12px rgba(0, 0, 0, 0.04);
}

.section-header {
  border-bottom: 1px solid rgba(0, 150, 136, 0.1);
  padding-bottom: 12px;
  display: flex;
  align-items: center;
}

/* 急件列表 */
.urgent-item {
  min-height: 60px;
  transition: background 0.2s;
}

.urgent-item:hover {
  background: rgba(220, 38, 38, 0.03);
}

/* 取件清單列表 */
.pickup-item {
  min-height: 60px;
  transition: background 0.2s;
}

.pickup-item:hover {
  background: rgba(13, 148, 136, 0.04);
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
.dashboard-tabs {
  background: #f8fafc;
  border-radius: 12px 12px 0 0;
}

.tab-item {
  min-width: 0;
  padding: 0 12px;
}

.tab-badge {
  font-size: 0.65rem;
  min-width: 18px;
  height: 16px;
  line-height: 16px;
  padding: 0 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.list-item {
  min-height: 60px;
  transition: background 0.2s;
}

.list-item:hover {
  background: rgba(0, 0, 0, 0.03);
}
</style>
