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
    </div>

    <!-- 載入中覆蓋 -->
    <div v-if="loading" class="text-center q-py-xl">
      <q-spinner color="teal-8" size="3em" />
      <div class="text-caption text-grey-5 q-mt-sm">載入資料中...</div>
    </div>

    <template v-else>
      <!-- KPI 卡片列 -->
      <div class="kpi-grid q-mb-md">
        <div v-for="kpi in kpiCards" :key="kpi.label" class="kpi-card" :style="`--accent: ${kpi.accentColor}`">
          <div class="kpi-inner">
            <div class="kpi-icon-wrap" :style="`background: ${kpi.iconBg}`">
              <q-icon :name="kpi.icon" size="1.5em" :style="`color: ${kpi.accentColor}`" />
            </div>
            <div class="kpi-value">{{ kpi.value }}</div>
            <div class="kpi-label">{{ kpi.label }}</div>
          </div>
        </div>
      </div>



      <!-- 中段：急件待辦 + 待取件清單 -->
      <div class="row q-gutter-md q-mb-lg">
        <!-- 急件待辦 -->
        <div class="col-12 col-md-6">
          <q-card class="section-card full-height">
            <q-card-section class="section-header">
              <q-icon name="bolt" size="sm" color="red-6" class="q-mr-xs" />
              <span class="text-subtitle1 text-weight-medium">急件待辦</span>
              <q-badge
                v-if="urgentActiveOrders.length > 0"
                color="red-6"
                :label="urgentActiveOrders.length"
                class="q-ml-xs"
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
                @click="goToOrder(order.id)"
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

        <!-- 待取件清單 -->
        <div class="col-12 col-md-6">
          <q-card class="section-card full-height">
            <q-card-section class="section-header row items-center justify-between">
              <div class="row items-center">
                <q-icon name="inventory_2" size="sm" color="teal-8" class="q-mr-xs" />
                <span class="text-subtitle1 text-weight-medium">待取件清單</span>
                <q-badge v-if="readyOrders.length > 0" color="teal-8" :label="readyOrders.length" class="q-ml-xs" />
              </div>
            </q-card-section>

            <q-card-section v-if="readyOrders.length === 0" class="text-center q-py-lg">
              <q-icon name="check_circle_outline" size="3em" color="teal-3" />
              <div class="text-body2 text-grey-5 q-mt-sm">目前無待取件</div>
            </q-card-section>

            <q-list v-else dense separator>
              <q-item
                v-for="order in readyOrders.slice(0, 6)"
                :key="order.id"
                class="pickup-item cursor-pointer"
                clickable
                @click="goToOrder(order.id)"
              >
                <q-item-section avatar>
                  <q-avatar color="teal-6" text-color="white" size="36px" icon="check_circle" />
                </q-item-section>
                <q-item-section>
                  <q-item-label class="text-weight-medium row items-center q-gutter-xs">
                    <span>{{ order.orderNo }}</span>
                    <q-badge v-if="order.isUrgent" color="red-6" label="急件" size="xs" />
                  </q-item-label>
                  <q-item-label caption>
                    {{ order.customerName }} · {{ order.items.map((i) => i.serviceName).join('、') }}
                  </q-item-label>
                </q-item-section>
                <q-item-section side>
                  <div class="text-caption text-teal-8 text-weight-medium">{{ order.estimatedPickupDate ?? '–' }}</div>
                  <div class="text-caption text-grey-5">預計取件</div>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card>
        </div>
      </div>

      <!-- 即將到期訂單 -->
      <q-card class="section-card">
        <q-card-section class="section-header row items-center justify-between">
          <div class="row items-center">
            <q-icon name="event_busy" size="sm" color="orange-8" class="q-mr-xs" />
            <span class="text-subtitle1 text-weight-medium">即將到期訂單</span>
            <q-badge v-if="tomorrowPendingOrders.length > 0" color="orange-8" :label="tomorrowPendingOrders.length" class="q-ml-xs" />
          </div>
        </q-card-section>

        <q-list separator>
          <q-item
            v-for="order in tomorrowPendingOrders"
            :key="order.id"
            class="recent-item cursor-pointer"
            clickable
            @click="openDetail(order)"
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
              <div class="text-weight-bold text-orange-8 text-body2">
                {{ order.estimatedPickupDate }}
              </div>
              <div class="text-caption text-grey-5">預計取件日</div>
            </q-item-section>
          </q-item>
        </q-list>

        <q-card-section v-if="tomorrowPendingOrders.length === 0" class="text-center q-py-lg">
          <q-icon name="event_available" size="3em" color="orange-3" />
          <div class="text-body2 text-grey-4 q-mt-sm">明日無待處理訂單</div>
        </q-card-section>
      </q-card>
    </template>

    <!-- 訂單明細 Dialog -->
    <q-dialog v-model="detailOpen">
      <q-card style="min-width: 340px; max-width: 520px; width: 100%">
        <q-card-section class="row items-center justify-between q-pb-sm">
          <div class="row items-center q-gutter-xs">
            <span class="text-weight-bold text-body1 font-mono">{{ selectedOrder?.orderNo }}</span>
            <q-badge v-if="selectedOrder?.isUrgent" color="red-6" label="急件" />
            <q-badge
              :color="statusColorMap[selectedOrder?.status ?? '']"
              :label="statusLabelMap[selectedOrder?.status ?? '']"
              outline
            />
          </div>
          <q-btn flat round dense icon="close" v-close-popup class="cursor-pointer" />
        </q-card-section>

        <q-separator />

        <q-card-section v-if="selectedOrder" class="q-pt-md">
          <!-- 顧客資訊 -->
          <div class="row items-center q-gutter-xs q-mb-md">
            <q-icon name="person" color="grey-6" size="sm" />
            <span class="text-weight-medium">{{ selectedOrder.customerName }}</span>
            <span class="text-caption text-grey-5">{{ selectedOrder.customerPhone }}</span>
          </div>

          <!-- 服務項目 -->
          <div class="q-mb-md">
            <div class="text-caption text-grey-5 q-mb-xs">服務項目</div>
            <div
              v-for="(item, idx) in selectedOrder.items"
              :key="idx"
              class="row items-center justify-between q-py-xs"
              style="border-bottom: 1px solid #f1f5f9"
            >
              <span class="text-body2">{{ item.serviceName }}</span>
              <span class="text-body2 text-grey-7">NT$ {{ item.unitPrice.toLocaleString() }} × {{ item.quantity }}</span>
            </div>
          </div>

          <!-- 金額 & 日期 -->
          <div class="row justify-between items-end">
            <div>
              <div class="text-caption text-grey-5">預計取件日</div>
              <div class="text-body2 text-weight-medium text-teal-8">{{ selectedOrder.estimatedPickupDate || '尚未安排' }}</div>
              <div v-if="selectedOrder.isUrgent && selectedOrder.urgentDeadline" class="text-caption text-red-6 q-mt-xs">
                <q-icon name="bolt" size="xs" /> 急件截止：{{ selectedOrder.urgentDeadline }}
              </div>
            </div>
            <div class="text-right">
              <div class="text-caption text-grey-5">總金額</div>
              <div class="text-h6 text-weight-bold text-teal-8">NT$ {{ selectedOrder.totalAmount.toLocaleString() }}</div>
              <div v-if="selectedOrder.urgentFee > 0" class="text-caption text-red-6">含急件費 NT$ {{ selectedOrder.urgentFee.toLocaleString() }}</div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { listOrders } from '../api/order/order';
import type { OrderResult } from '../api/order/types';
import { useOrderNavStore } from '../stores/orderNavStore';

const $q = useQuasar();
const router = useRouter();
const orderNavStore = useOrderNavStore();

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


const pendingOrders = computed(() => orders.value.filter((o) => o.status === 'PENDING'));
const inProgressOrders = computed(() => orders.value.filter((o) => o.status === 'IN_PROGRESS'));
const readyOrders = computed(() => orders.value.filter((o) => o.status === 'READY'));

const urgentActiveOrders = computed(() =>
  orders.value.filter(
    (o) => o.isUrgent && o.status !== 'PICKED_UP' && o.status !== 'CANCELLED'
  )
);

const kpiCards = computed(() => [
  {
    label: '未完成訂單',
    value: pendingOrders.value.length + inProgressOrders.value.length + readyOrders.value.length,
    icon: 'pending_actions',
    accentColor: '#7c3aed',
    iconBg: '#f5f3ff',
  },
  {
    label: '待處理',
    value: pendingOrders.value.length,
    icon: 'hourglass_empty',
    accentColor: '#ea580c',
    iconBg: '#fff7ed',
  },
  {
    label: '處理中',
    value: inProgressOrders.value.length,
    icon: 'sync',
    accentColor: '#2563eb',
    iconBg: '#eff6ff',
  },
  {
    label: '待取件',
    value: readyOrders.value.length,
    icon: 'inventory_2',
    accentColor: '#0d9488',
    iconBg: '#f0fdfa',
  },
]);



const tomorrowPendingOrders = computed(() => {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  const tomorrowStr = d.toISOString().slice(0, 10);
  return orders.value.filter(
    (o) => o.status === 'PENDING' && o.estimatedPickupDate === tomorrowStr
  );
});

const selectedOrder = ref<OrderResult | null>(null);
const detailOpen = ref(false);

function openDetail(order: OrderResult) {
  selectedOrder.value = order;
  detailOpen.value = true;
}

// ── 導航 ──────────────────────────────────────────────────

function goToOrder(orderId: number) {
  orderNavStore.setPendingOrderId(orderId);
  router.push('/orders');
}

// ── 格式化 ────────────────────────────────────────────────


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

/* KPI Grid */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

@media (max-width: 1023px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* KPI 卡片 */
.kpi-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 4px 16px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: default;
}

.kpi-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.kpi-inner {
  padding: 24px 16px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  text-align: center;
}

.kpi-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kpi-value {
  font-size: 2rem;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
  letter-spacing: -0.5px;
}

.kpi-label {
  font-size: 0.8rem;
  color: #94a3b8;
  font-weight: 500;
  letter-spacing: 0.3px;
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

/* 最近訂單列表 */
.recent-item {
  min-height: 64px;
  transition: background 0.2s;
}

.recent-item:hover {
  background: rgba(234, 88, 12, 0.04);
}
</style>
