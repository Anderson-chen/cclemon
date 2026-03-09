<template>
  <q-page padding class="dashboard-page">
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="dashboard" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">訂單總覽</h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 即時資料</div>
        </div>
      </div>
      <q-btn
        unelevated
        color="teal-8"
        icon="receipt_long"
        label="開單管理"
        to="/orders"
        class="cursor-pointer"
      />
    </div>

    <!-- 載入中覆蓋 -->
    <div v-if="loading" class="text-center q-py-xl">
      <q-spinner color="teal-8" size="3em" />
      <div class="text-caption text-grey-5 q-mt-sm">載入資料中...</div>
    </div>

    <template v-else>
      <!-- KPI 卡片列 -->
      <div class="row q-gutter-md q-mb-lg">
        <div
          v-for="kpi in kpiCards"
          :key="kpi.label"
          class="kpi-card col"
          :style="`--accent: ${kpi.accentColor}`"
        >
          <div class="kpi-inner">
            <div class="kpi-icon-wrap" :style="`background: ${kpi.iconBg}`">
              <q-icon :name="kpi.icon" size="1.4em" :style="`color: ${kpi.accentColor}`" />
            </div>
            <div class="kpi-body">
              <div class="kpi-value">{{ kpi.value }}</div>
              <div class="kpi-label">{{ kpi.label }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 中段：狀態分佈 + 急件警示 -->
      <div class="row q-gutter-md q-mb-lg">
        <!-- 狀態分佈 -->
        <div class="col-12 col-md-5">
          <q-card class="section-card full-height">
            <q-card-section class="section-header">
              <q-icon name="pie_chart" size="sm" color="teal-8" class="q-mr-xs" />
              <span class="text-subtitle1 text-weight-medium">訂單狀態分佈</span>
            </q-card-section>
            <q-card-section>
              <div
                v-for="s in statusBreakdown"
                :key="s.status"
                class="status-row q-mb-sm"
              >
                <div class="row items-center justify-between q-mb-xs">
                  <div class="row items-center q-gutter-xs">
                    <q-icon :name="s.icon" :style="`color: ${s.color}`" size="xs" />
                    <span class="text-body2">{{ s.label }}</span>
                  </div>
                  <span class="text-body2 text-weight-medium" :style="`color: ${s.color}`">
                    {{ s.count }}
                  </span>
                </div>
                <q-linear-progress
                  :value="s.percent"
                  rounded
                  size="8px"
                  color="white"
                  track-color="grey-2"
                  class="status-bar"
                  :style="`--bar-color: ${s.color}`"
                />
              </div>
              <q-separator class="q-my-sm" />
              <div class="row justify-between text-caption text-grey-6">
                <span>共 {{ totalOrders }} 筆訂單</span>
                <span>完成率 {{ completionRate }}%</span>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <!-- 急件警示 -->
        <div class="col">
          <q-card class="section-card full-height">
            <q-card-section class="section-header">
              <q-icon name="bolt" size="sm" color="red-6" class="q-mr-xs" />
              <span class="text-subtitle1 text-weight-medium">急件待辦</span>
              <q-badge
                v-if="urgentActiveOrders.length > 0"
                color="red-6"
                :label="urgentActiveOrders.length"
                class="q-ml-xs"
                floating
              />
            </q-card-section>

            <!-- 無急件 -->
            <q-card-section v-if="urgentActiveOrders.length === 0" class="text-center q-py-lg">
              <q-icon name="check_circle" size="3em" color="teal-4" />
              <div class="text-body2 text-grey-5 q-mt-sm">目前無急件</div>
            </q-card-section>

            <!-- 急件列表 -->
            <q-list v-else dense separator>
              <q-item
                v-for="order in urgentActiveOrders"
                :key="order.id"
                class="urgent-item cursor-pointer"
                clickable
                @click="goToOrders()"
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
                  <q-item-label class="text-weight-medium row items-center q-gutter-xs">
                    <span>{{ order.orderNo }}</span>
                    <q-badge color="red-6" label="急件" size="xs" />
                  </q-item-label>
                  <q-item-label caption>
                    {{ order.customerName }} · {{ statusLabelMap[order.status] }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side>
                  <div class="text-caption text-red-6 text-weight-medium">
                    {{ order.urgentDeadline ?? '–' }}
                  </div>
                  <div class="text-caption text-grey-5">截止日</div>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card>
        </div>
      </div>

      <!-- 最近訂單 -->
      <q-card class="section-card">
        <q-card-section class="section-header row items-center justify-between">
          <div class="row items-center">
            <q-icon name="history" size="sm" color="teal-8" class="q-mr-xs" />
            <span class="text-subtitle1 text-weight-medium">最近訂單</span>
          </div>
          <q-btn
            flat
            dense
            size="sm"
            label="查看全部"
            color="teal-8"
            to="/orders"
            class="cursor-pointer"
          />
        </q-card-section>

        <q-list separator>
          <q-item
            v-for="order in recentOrders"
            :key="order.id"
            class="recent-item cursor-pointer"
            clickable
            @click="goToOrders()"
          >
            <q-item-section avatar>
              <q-avatar
                :color="statusColorMap[order.status]"
                text-color="white"
                size="40px"
                :icon="statusIconMap[order.status]"
              />
            </q-item-section>
            <q-item-section>
              <q-item-label class="row items-center q-gutter-xs">
                <span class="text-weight-medium">{{ order.orderNo }}</span>
                <q-badge v-if="order.isUrgent" color="red-6" label="急" size="xs" />
                <q-badge
                  :color="statusColorMap[order.status]"
                  :label="statusLabelMap[order.status]"
                  outline
                  size="xs"
                />
              </q-item-label>
              <q-item-label caption>
                <q-icon name="person" size="xs" class="q-mr-xs" />{{ order.customerName }}
                <span class="q-ml-sm">
                  <q-icon name="cleaning_services" size="xs" class="q-mr-xs" />
                  {{ order.items.map((i) => i.serviceName).join('、') }}
                </span>
              </q-item-label>
            </q-item-section>
            <q-item-section side>
              <div class="text-weight-bold text-teal-8 text-body2">
                NT$ {{ order.totalAmount.toLocaleString() }}
              </div>
              <div class="text-caption text-grey-5">
                {{ formatDate(order.createTime) }}
              </div>
            </q-item-section>
          </q-item>
        </q-list>

        <q-card-section v-if="recentOrders.length === 0" class="text-center q-py-lg">
          <q-icon name="inbox" size="3em" color="grey-3" />
          <div class="text-body2 text-grey-4 q-mt-sm">尚無訂單資料</div>
        </q-card-section>
      </q-card>
    </template>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { listOrders } from '../api/order/order';
import type { OrderResult, OrderStatus } from '../api/order/types';

const $q = useQuasar();
const router = useRouter();

// ── 資料 ──────────────────────────────────────────────────
const orders = ref<OrderResult[]>([]);
const loading = ref(false);

const loadOrders = async () => {
  try {
    loading.value = true;
    const res = await listOrders({ page: 0, size: 200 });
    orders.value = res.content;
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
const totalOrders = computed(() => orders.value.length);

const pendingOrders = computed(() => orders.value.filter((o) => o.status === 'PENDING'));
const inProgressOrders = computed(() => orders.value.filter((o) => o.status === 'IN_PROGRESS'));
const readyOrders = computed(() => orders.value.filter((o) => o.status === 'READY'));
const pickedUpOrders = computed(() => orders.value.filter((o) => o.status === 'PICKED_UP'));
const urgentActiveOrders = computed(() =>
  orders.value.filter(
    (o) => o.isUrgent && o.status !== 'PICKED_UP' && o.status !== 'CANCELLED'
  )
);

const todayRevenue = computed(() => {
  const today = new Date().toISOString().slice(0, 10);
  return orders.value
    .filter((o) => o.status === 'PICKED_UP' && o.actualPickupDate?.startsWith(today))
    .reduce((sum, o) => sum + o.totalAmount, 0);
});

const completionRate = computed(() => {
  if (totalOrders.value === 0) return 0;
  return Math.round((pickedUpOrders.value.length / totalOrders.value) * 100);
});

const kpiCards = computed(() => [
  {
    label: '全部訂單',
    value: totalOrders.value,
    icon: 'receipt_long',
    accentColor: '#0f766e',
    iconBg: '#ccfbf1',
  },
  {
    label: '待處理',
    value: pendingOrders.value.length,
    icon: 'hourglass_empty',
    accentColor: '#c2410c',
    iconBg: '#ffedd5',
  },
  {
    label: '處理中',
    value: inProgressOrders.value.length,
    icon: 'sync',
    accentColor: '#1d4ed8',
    iconBg: '#dbeafe',
  },
  {
    label: '急件待辦',
    value: urgentActiveOrders.value.length,
    icon: 'bolt',
    accentColor: '#dc2626',
    iconBg: '#fee2e2',
  },
  {
    label: '今日營收',
    value: `NT$ ${todayRevenue.value.toLocaleString()}`,
    icon: 'payments',
    accentColor: '#065f46',
    iconBg: '#d1fae5',
  },
]);

const statusBreakdown = computed(() => {
  const total = totalOrders.value || 1;
  return [
    {
      status: 'PENDING',
      label: '待處理',
      count: pendingOrders.value.length,
      percent: pendingOrders.value.length / total,
      color: '#ea580c',
      icon: 'hourglass_empty',
    },
    {
      status: 'IN_PROGRESS',
      label: '處理中',
      count: inProgressOrders.value.length,
      percent: inProgressOrders.value.length / total,
      color: '#2563eb',
      icon: 'sync',
    },
    {
      status: 'READY',
      label: '待取件',
      count: readyOrders.value.length,
      percent: readyOrders.value.length / total,
      color: '#0d9488',
      icon: 'check_circle',
    },
    {
      status: 'PICKED_UP',
      label: '已取件',
      count: pickedUpOrders.value.length,
      percent: pickedUpOrders.value.length / total,
      color: '#6b7280',
      icon: 'done_all',
    },
  ];
});

const recentOrders = computed(() =>
  [...orders.value]
    .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
    .slice(0, 8)
);

// ── 導航 ──────────────────────────────────────────────────
function goToOrders() {
  router.push('/orders');
}

// ── 格式化 ────────────────────────────────────────────────
function formatDate(dateStr: string): string {
  const d = new Date(dateStr);
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
}

onMounted(loadOrders);
</script>

<style scoped>
.dashboard-page {
  max-width: 1200px;
  margin: 0 auto;
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

/* KPI 卡片 */
.kpi-card {
  min-width: 150px;
  background: white;
  border-radius: 12px;
  border-top: 3px solid var(--accent);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07), 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: default;
}

.kpi-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.kpi-inner {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.kpi-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kpi-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.kpi-label {
  font-size: 0.78rem;
  color: #64748b;
  margin-top: 2px;
}

/* 區塊卡片 */
.section-card {
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07), 0 4px 12px rgba(0, 0, 0, 0.04);
}

.section-header {
  border-bottom: 1px solid rgba(0, 150, 136, 0.1);
  padding-bottom: 12px;
  display: flex;
  align-items: center;
}

/* 狀態進度條 */
.status-bar :deep(.q-linear-progress__track) {
  background: #f1f5f9;
}

.status-bar :deep(.q-linear-progress__model) {
  background: var(--bar-color) !important;
}

/* 急件列表 */
.urgent-item {
  min-height: 60px;
  transition: background 0.2s;
}

.urgent-item:hover {
  background: rgba(220, 38, 38, 0.03);
}

/* 最近訂單列表 */
.recent-item {
  min-height: 64px;
  transition: background 0.2s;
}

.recent-item:hover {
  background: rgba(0, 150, 136, 0.04);
}
</style>
