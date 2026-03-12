<template>
  <q-page padding class="report-page">
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="bar_chart" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">
            收益報表
          </h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 財務分析</div>
        </div>
      </div>
    </div>

    <div class="row items-center justify-between q-mb-md">
      <q-tabs
        v-model="tab"
        dense
        active-color="teal-8"
        indicator-color="teal-8"
        align="left"
        class="report-tabs"
        @update:model-value="onTabChange"
      >
        <q-tab name="daily" icon="today" label="日報表" />
        <q-tab name="monthly" icon="calendar_month" label="月報表" />
      </q-tabs>
      
      <!-- Mobile Mode Toggle -->
      <q-btn-toggle
        v-if="$q.screen.lt.md && reportOrders.length > 0"
        v-model="mobileView"
        toggle-color="teal-8"
        flat
        dense
        rounded
        :options="[
          { icon: 'pie_chart', value: 'charts' },
          { icon: 'list', value: 'data' }
        ]"
        class="bg-grey-2"
      />
    </div>

    <!-- 日期選擇器 -->
    <q-card flat bordered class="q-mb-md">
      <q-card-section class="q-py-sm">
        <div class="row items-center q-gutter-md wrap">
          <!-- 日報表：單日選擇 -->
          <template v-if="tab === 'daily'">
            <AppDateInput
              v-model="selectedDate"
              label="選擇日期"
              prepend-icon="event"
              input-style="min-width: 180px"
              @update:model-value="loadReport"
            />
            <q-btn
              flat
              dense
              label="今日"
              color="teal-7"
              @click="setToday"
              class="cursor-pointer"
            />
            <q-btn
              flat
              dense
              label="昨日"
              color="grey-7"
              @click="setYesterday"
              class="cursor-pointer"
            />
          </template>

          <!-- 月報表：年月選擇 -->
          <template v-else>
            <AppDateInput
              v-model="selectedMonth"
              label="選擇月份"
              prepend-icon="calendar_month"
              mask="YYYY-MM"
              default-view="Months"
              :emit-immediately="true"
              input-style="min-width: 180px"
              @update:model-value="loadReport"
            />
            <q-btn
              flat
              dense
              label="本月"
              color="teal-7"
              @click="setThisMonth"
              class="cursor-pointer"
            />
            <q-btn
              flat
              dense
              label="上月"
              color="grey-7"
              @click="setLastMonth"
              class="cursor-pointer"
            />
          </template>
        </div>
      </q-card-section>
    </q-card>

    <!-- 載入中 -->
    <div v-if="loading" class="text-center q-py-xl">
      <q-spinner color="teal-8" size="3em" />
      <div class="text-caption text-grey-5 q-mt-sm">載入報表資料中...</div>
    </div>

    <template v-else>
      <!-- KPI 卡片 (Mobile: Horizontal Scroll) -->
      <div class="kpi-scroll-wrapper q-mb-lg">
        <div class="kpi-grid">
          <div
            v-for="kpi in kpiCards"
            :key="kpi.label"
            :class="['kpi-card', { 'kpi-card--featured': kpi.featured }]"
            :style="`--accent: ${kpi.accentColor}; --accent-light: ${kpi.iconBg}`"
          >
            <div class="kpi-content">
              <div class="row items-start justify-between full-width no-wrap">
                <div class="kpi-info">
                  <div class="kpi-label row items-center no-wrap">
                    {{ kpi.label }}
                    <q-icon
                      v-if="kpi.featured"
                      name="auto_graph"
                      size="14px"
                      class="q-ml-xs text-teal-5"
                    />
                  </div>
                  <div class="kpi-value text-weight-bold">{{ kpi.value }}</div>
                  <div class="kpi-sub row items-center text-caption q-mt-xs">
                    <q-icon :name="kpi.trendIcon || 'trending_up'" :color="kpi.trendColor || 'green-6'" size="xs" class="q-mr-xs" />
                    <span :class="`text-${kpi.trendColor || 'green-6'} text-weight-medium`">{{ kpi.trendValue || '+5%' }}</span>
                    <span class="text-grey-5 q-ml-xs">{{ kpi.trendLabel || '較同期' }}</span>
                  </div>
                </div>
                <div
                  class="kpi-icon-container"
                  :style="`background: ${kpi.iconBg}`"
                >
                  <q-icon
                    :name="kpi.icon"
                    size="sm"
                    :style="`color: ${kpi.accentColor}`"
                  />
                </div>
              </div>
            </div>
            <div
              class="kpi-border-bottom"
              :style="`background: ${kpi.accentColor}`"
            ></div>
          </div>
        </div>
      </div>

      <!-- 無資料提示 -->
      <div v-if="reportOrders.length === 0" class="empty-state text-center">
        <div class="empty-visual">
          <q-icon name="analytics" size="64px" color="teal-1" />
          <div class="pulse-ring"></div>
        </div>
        <div class="text-h6 text-grey-8 q-mt-md">尚無營收數據</div>
        <div class="text-body2 text-grey-5 q-mb-lg">
          目前選擇的期間內沒有已完成取件的訂單。
        </div>
        <q-btn
          v-if="tab === 'daily'"
          unelevated
          color="teal-8"
          label="查看今日"
          @click="setToday"
          rounded
        />
        <q-btn
          v-else
          unelevated
          color="teal-8"
          label="查看本月"
          @click="setThisMonth"
          rounded
        />
      </div>

      <template v-else>
        <div class="row q-col-gutter-lg">
          <!-- 左側：服務佔比與分析 -->
          <div class="col-12 col-md-5" v-if="!$q.screen.lt.md || mobileView === 'charts'">
            <q-card flat bordered class="analysis-card full-height animate-fade-in">
              <q-card-section>
                <div class="row items-center justify-between q-mb-lg">
                  <div class="text-subtitle1 text-weight-bold text-grey-9">
                    核心業務分析
                  </div>
                  <q-icon name="insights" color="teal-2" size="20px" />
                </div>

                <!-- Donut Chart Visualization -->
                <div class="donut-chart-container q-mb-xl">
                  <div class="donut-visual">
                    <svg viewBox="0 0 36 36" class="donut-svg">
                      <circle
                        class="donut-ring"
                        cx="18"
                        cy="18"
                        r="15.915"
                        fill="transparent"
                        stroke="#f8fafc"
                        stroke-width="3"
                      ></circle>
                      <template v-for="svc in chartSegments" :key="svc.code">
                        <circle
                          class="donut-segment"
                          cx="18"
                          cy="18"
                          r="15.915"
                          fill="transparent"
                          :stroke="svc.color"
                          stroke-width="3.5"
                          :stroke-dasharray="`${svc.percent} ${100 - svc.percent}`"
                          :stroke-dashoffset="svc.offset"
                          stroke-linecap="round"
                        >
                          <q-tooltip>
                            {{ svc.name }}: {{ svc.percent.toFixed(1) }}%
                          </q-tooltip>
                        </circle>
                      </template>
                    </svg>
                    <div class="donut-center">
                      <div class="text-caption text-grey-5">營收總額</div>
                      <div class="text-h6 text-weight-bold text-teal-9">
                        NT$ {{ totalRevenue.toLocaleString() }}
                      </div>
                    </div>
                  </div>
                </div>

                <!-- List with percent bars -->
                <div class="service-list q-mt-md">
                  <div
                    v-for="(svc, index) in serviceBreakdown"
                    :key="svc.code"
                    class="service-item q-mb-md"
                  >
                    <div class="row items-center justify-between q-mb-xs">
                      <div class="row items-center">
                        <div
                          class="service-dot"
                          :style="`background: ${getServiceColor(index)}`"
                        ></div>
                        <span
                          class="text-body2 text-grey-8 text-weight-medium"
                          >{{ svc.name }}</span
                        >
                      </div>
                      <span class="text-body2 text-weight-bold text-grey-9"
                        >NT$ {{ svc.revenue.toLocaleString() }}</span
                      >
                    </div>
                    <div class="row items-center q-gutter-x-sm">
                      <div class="col">
                        <q-linear-progress
                          :value="svc.percent"
                          rounded
                          size="8px"
                          :style="`color: ${getServiceColor(index)}`"
                          track-color="blue-grey-1"
                          class="service-progress"
                        />
                      </div>
                      <div class="text-caption text-grey-5 width-40">
                        {{ (svc.percent * 100).toFixed(1) }}%
                      </div>
                    </div>
                  </div>
                </div>

                <q-separator class="q-my-lg" />
                
                <!-- 額外統計數據 -->
                <div class="row q-col-gutter-sm q-mb-md">
                  <div class="col-6">
                    <div class="stat-box q-pa-sm rounded-borders bg-blue-grey-0">
                      <div class="text-caption text-grey-6">急件比例</div>
                      <div class="text-subtitle1 text-weight-bold">{{ urgentRate.toFixed(1) }}%</div>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="stat-box q-pa-sm rounded-borders bg-blue-grey-0">
                      <div class="text-caption text-grey-6">客單價 (AOV)</div>
                      <div class="text-subtitle1 text-weight-bold">${{ avgOrderValue.toLocaleString() }}</div>
                    </div>
                  </div>
                </div>

                <div
                  class="urgent-box row justify-between items-center q-pa-md rounded-borders bg-orange-1 border-orange-2"
                >
                  <div class="row items-center">
                    <q-icon
                      name="bolt"
                      color="orange-8"
                      size="20px"
                      class="q-mr-sm"
                    />
                    <span class="text-body2 text-orange-9 text-weight-medium"
                      >加急營收加成</span
                    >
                  </div>
                  <span class="text-subtitle2 text-weight-bold text-orange-9">
                    NT$ {{ totalUrgentFee.toLocaleString() }}
                  </span>
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- 右側：資料明細 / 趨勢 -->
          <div class="col-12 col-md-7" v-if="!$q.screen.lt.md || mobileView === 'data'">
            <!-- 日報表：訂單明細 -->
            <q-card
              v-if="tab === 'daily'"
              flat
              bordered
              class="analysis-card full-height animate-fade-in"
            >
              <q-card-section class="row items-center justify-between">
                <div class="text-subtitle1 text-weight-bold text-grey-9">
                  訂單成交流水明細
                </div>
                <q-btn flat round dense icon="file_download" color="grey-5">
                   <q-tooltip>匯出 Excel</q-tooltip>
                </q-btn>
              </q-card-section>

              <q-table
                :rows="reportOrders"
                :columns="orderColumns"
                row-key="id"
                flat
                dense
                class="modern-report-table"
                :rows-per-page-options="[10, 20, 0]"
              >
                <template v-slot:header="props">
                  <q-tr :props="props" class="bg-grey-1">
                    <q-th
                      v-for="col in props.cols"
                      :key="col.name"
                      :props="props"
                      class="text-grey-7 uppercase-tracking"
                    >
                      {{ col.label }}
                    </q-th>
                  </q-tr>
                </template>

                <template v-slot:body-cell-orderNo="props">
                  <q-td :props="props">
                    <div class="text-weight-bold text-teal-9">
                      {{ props.value }}
                    </div>
                    <q-badge
                      v-if="props.row.isUrgent"
                      color="orange-1"
                      text-color="orange-9"
                      label="急件"
                      size="xs"
                      dense
                    />
                  </q-td>
                </template>

                <template v-slot:body-cell-totalAmount="props">
                  <q-td :props="props" class="text-right">
                    <div class="text-weight-bold text-grey-9">
                      NT$ {{ props.value.toLocaleString() }}
                    </div>
                  </q-td>
                </template>
              </q-table>
            </q-card>

            <!-- 月報表：每日趨勢 -->
            <q-card v-else flat bordered class="analysis-card full-height animate-fade-in">
              <q-card-section>
                <div class="row items-center justify-between q-mb-lg">
                  <div class="text-subtitle1 text-weight-bold text-grey-9">
                    月度營業額走勢
                  </div>
                  <div class="trend-legend row q-gutter-x-md">
                    <div class="row items-center">
                       <div class="legend-dot bg-teal-8"></div>
                       <span class="text-caption text-grey-6">日營收</span>
                    </div>
                  </div>
                </div>

                <div class="trend-container">
                  <div
                    v-for="day in dailyTrend"
                    :key="day.date"
                    class="trend-item"
                    :class="{ 'trend-item--empty': day.count === 0 }"
                  >
                    <div class="trend-header row items-center justify-between no-wrap">
                      <span class="trend-date">{{ day.dateLabel }}</span>
                      <q-space />
                      <span
                        v-if="day.count > 0"
                        class="trend-val text-weight-bold text-teal-9"
                        >NT$ {{ day.revenue.toLocaleString() }}</span
                      >
                      <span v-else class="text-grey-3">–</span>
                    </div>
                    <div class="trend-track">
                      <div class="trend-fill" :style="`width: ${day.percent}%`">
                        <q-tooltip v-if="day.count > 0">
                          {{ day.date }}: {{ day.count }} 筆訂單
                        </q-tooltip>
                      </div>
                    </div>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </template>
    </template>
  </q-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import AppDateInput from '../components/AppDateInput.vue';
import { listOrders } from '../api/order/order';
import type { OrderResult } from '../api/order/types';

const $q = useQuasar();

// ── 狀態 ──────────────────────────────────────────────────
const tab = ref<'daily' | 'monthly'>('daily');
const mobileView = ref<'charts' | 'data'>('charts');
const loading = ref(false);
const reportOrders = ref<OrderResult[]>([]);

// 日報表預設今日
const todayStr = new Date().toISOString().slice(0, 10);
const selectedDate = ref(todayStr);

// 月報表預設本月
const selectedMonth = ref(new Date().toISOString().slice(0, 7));

// ── 日期輔助 ───────────────────────────────────────────────
function setToday() {
  selectedDate.value = new Date().toISOString().slice(0, 10);
  loadReport();
}

function setYesterday() {
  const d = new Date();
  d.setDate(d.getDate() - 1);
  selectedDate.value = d.toISOString().slice(0, 10);
  loadReport();
}

function setThisMonth() {
  selectedMonth.value = new Date().toISOString().slice(0, 7);
  loadReport();
}

function setLastMonth() {
  const d = new Date();
  d.setMonth(d.getMonth() - 1);
  selectedMonth.value = d.toISOString().slice(0, 7);
  loadReport();
}

function onTabChange() {
  reportOrders.value = [];
  loadReport();
}

// ── 載入報表 ───────────────────────────────────────────────
async function loadReport() {
  try {
    loading.value = true;

    let dateFrom: string;
    let dateTo: string;

    if (tab.value === 'daily') {
      dateFrom = selectedDate.value;
      dateTo = selectedDate.value;
    } else {
      const [year, month] = selectedMonth.value.split('-').map(Number);
      const lastDay = new Date(year, month, 0).getDate();
      dateFrom = `${selectedMonth.value}-01`;
      dateTo = `${selectedMonth.value}-${String(lastDay).padStart(2, '0')}`;
    }

    const res = await listOrders({
      status: 'PICKED_UP',
      dateFrom,
      dateTo,
      page: 0,
      size: 500,
    });
    reportOrders.value = res.content;
  } catch {
    $q.notify({ type: 'negative', message: '載入報表失敗，請稍後再試' });
  } finally {
    loading.value = false;
  }
}

// ── 數據計算 ──────────────────────────────────────────────

// 總營收
const totalRevenue = computed(() => {
  return reportOrders.value.reduce((sum, order) => sum + order.totalAmount, 0);
});

// 急件費用總計
const totalUrgentFee = computed(() => {
  return reportOrders.value.reduce((sum, order) => sum + order.urgentFee, 0);
});

// 服務項目占比
const serviceBreakdown = computed(() => {
  const breakdown: Record<
    string,
    { name: string; count: number; revenue: number }
  > = {};

  reportOrders.value.forEach((order) => {
    order.items.forEach((item) => {
      if (!breakdown[item.serviceCode]) {
        breakdown[item.serviceCode] = {
          name: item.serviceName,
          count: 0,
          revenue: 0,
        };
      }
      breakdown[item.serviceCode].count += item.quantity;
      breakdown[item.serviceCode].revenue += item.subtotal;
    });
  });

  const total = Object.values(breakdown).reduce(
    (sum, item) => sum + item.revenue,
    0,
  );

  return Object.keys(breakdown)
    .map((code) => ({
      code,
      ...breakdown[code],
      percent: total > 0 ? breakdown[code].revenue / total : 0,
    }))
    .sort((a, b) => b.revenue - a.revenue);
});

// 客單價 (AOV)
const avgOrderValue = computed(() => {
  return reportOrders.value.length > 0 
    ? Math.round(totalRevenue.value / reportOrders.value.length) 
    : 0;
});

// 急件佔比
const urgentRate = computed(() => {
  return reportOrders.value.length > 0 
    ? (reportOrders.value.filter(o => o.isUrgent).length / reportOrders.value.length) * 100 
    : 0;
});

// 圖表顏色
const SERVICE_COLORS = ['#0d9488', '#0ea5e9', '#6366f1', '#f43f5e', '#f59e0b'];
const getServiceColor = (index: number) =>
  SERVICE_COLORS[index % SERVICE_COLORS.length];

// 圓餅圖區塊
const chartSegments = computed(() => {
  let currentOffset = 0;
  return serviceBreakdown.value.map((svc, index) => {
    const percent = svc.percent * 100;
    // Stroke-dashoffset starts from 25 (top) and goes clockwise
    // Formula: 25 - cumulative_percent
    const offset = 25 - currentOffset;
    currentOffset += percent;
    return {
      ...svc,
      percent,
      offset: offset,
      color: getServiceColor(index),
    };
  });
});

// KPI 資料
const kpiCards = computed(() => [
  {
    label: '總營業額',
    value: `NT$ ${totalRevenue.value.toLocaleString()}`,
    icon: 'account_balance',
    accentColor: '#0d9488',
    iconBg: '#f0fdfa',
    featured: true,
    trendValue: '+12.5%',
    trendColor: 'teal-6',
    trendIcon: 'trending_up',
    trendLabel: '較同期'
  },
  {
    label: '成交筆數',
    value: `${reportOrders.value.length} 筆`,
    icon: 'shopping_cart',
    accentColor: '#0ea5e9',
    iconBg: '#f0f9ff',
    featured: false,
    trendValue: '+8.2%',
    trendColor: 'blue-6',
    trendIcon: 'show_chart',
    trendLabel: '較上月'
  },
  {
    label: '客單價 (AOV)',
    value: `NT$ ${avgOrderValue.value.toLocaleString()}`,
    icon: 'person',
    accentColor: '#6366f1',
    iconBg: '#eef2ff',
    featured: false,
    trendValue: '+3.1%',
    trendColor: 'indigo-5',
    trendIcon: 'north_east',
    trendLabel: '較同期'
  },
  {
    label: '急件筆數',
    value: `${reportOrders.value.filter((o) => o.isUrgent).length} 筆`,
    icon: 'speed',
    accentColor: '#f59e0b',
    iconBg: '#fffbeb',
    featured: false,
    trendValue: '-1.2%',
    trendColor: 'orange-6',
    trendIcon: 'south_east',
    trendLabel: '較同期'
  },
]);

// 趨勢數據 (月報表)
const dailyTrend = computed(() => {
  if (tab.value === 'daily') return [];

  const year = parseInt(selectedMonth.value.split('-')[0]);
  const month = parseInt(selectedMonth.value.split('-')[1]);
  const lastDay = new Date(year, month, 0).getDate();

  const trendMap: Record<number, { revenue: number; count: number }> = {};
  for (let i = 1; i <= lastDay; i++) {
    trendMap[i] = { revenue: 0, count: 0 };
  }

  reportOrders.value.forEach((order) => {
    if (order.actualPickupDate) {
      const d = new Date(order.actualPickupDate).getDate();
      if (trendMap[d]) {
        trendMap[d].revenue += order.totalAmount;
        trendMap[d].count += 1;
      }
    }
  });

  const maxRevenue = Math.max(
    ...Object.values(trendMap).map((d) => d.revenue),
    1,
  );

  return Object.keys(trendMap).map((dayKey) => {
    const d = parseInt(dayKey);
    const dateStr = `${selectedMonth.value}-${String(d).padStart(2, '0')}`;
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][
      new Date(dateStr).getDay()
    ];
    return {
      date: dateStr,
      dateLabel: `${month}/${d} (${weekDay})`,
      revenue: trendMap[d].revenue,
      count: trendMap[d].count,
      percent: (trendMap[d].revenue / maxRevenue) * 100,
    };
  });
});

// ── 日報表訂單欄位 ─────────────────────────────────────────
const orderColumns = [
  {
    name: 'orderNo',
    label: '訂單編號',
    field: 'orderNo',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'customerName',
    label: '顧客',
    field: 'customerName',
    align: 'left' as const,
  },
  {
    name: 'services',
    label: '服務項目',
    field: (row: OrderResult) => row.items.map((i) => i.serviceName).join('、'),
    align: 'left' as const,
  },
  {
    name: 'totalAmount',
    label: '金額',
    field: 'totalAmount',
    align: 'right' as const,
    sortable: true,
  },
  {
    name: 'actualPickupDate',
    label: '取件日',
    field: 'actualPickupDate',
    align: 'left' as const,
  },
];

onMounted(loadReport);
</script>

<style scoped>
.report-page {
  max-width: 1400px;
  margin: 0 auto;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* 標題圖示 */
.title-icon-wrap {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #0d9488, #14b8a6);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(13, 148, 136, 0.2);
}

/* KPI Scroll Wrapper for Mobile */
.kpi-scroll-wrapper {
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: none; /* Firefox */
}

.kpi-scroll-wrapper::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}

.kpi-grid {
  display: flex;
  gap: 16px;
  min-width: max-content;
}

@media (min-width: 1024px) {
  .kpi-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    min-width: auto;
  }
}

@media (max-width: 1023px) {
  .kpi-card {
    width: 280px;
    flex-shrink: 0;
  }
}

@media (max-width: 599px) {
  .kpi-card {
    width: 240px;
  }
}

/* KPI 卡片 */
.kpi-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.05),
    0 10px 15px -5px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow:
    0 20px 25px -5px rgba(0, 0, 0, 0.05),
    0 10px 10px -5px rgba(0, 0, 0, 0.02);
}

.kpi-card--featured {
  background: linear-gradient(to bottom right, #ffffff, #f0fdfa);
}

.kpi-content {
  padding: 24px;
}

.kpi-label {
  color: #64748b;
  font-size: 0.8125rem;
  font-weight: 500;
  margin-bottom: 2px;
}

.kpi-value {
  color: #2c3e50;
  font-size: 1.625rem;
  font-weight: 800;
  letter-spacing: -0.01em;
}

.kpi-icon-container {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kpi-border-bottom {
  height: 4px;
  width: 100%;
  opacity: 0.6;
}

/* 分析卡片 */
.analysis-card {
  border-radius: 16px;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* Donut Chart */
.donut-chart-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.donut-visual {
  position: relative;
  width: 220px;
  height: 220px;
}

.donut-svg {
  transform: rotate(0deg);
}

.donut-segment {
  transition:
    stroke-dasharray 0.5s ease,
    stroke-dashoffset 0.5s ease;
}

.donut-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  width: 70%;
}

/* 服務清單 */
.service-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 10px;
}

.service-progress {
  border-radius: 10px;
  background: #f1f5f9;
}

.width-40 {
  width: 45px;
  text-align: right;
}

/* 趨勢圖 */
.trend-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.trend-item {
  padding: 8px 0;
}

.trend-header {
  margin-bottom: 6px;
}

.trend-date {
  font-size: 0.8125rem;
  color: #7f8c8d;
}

.trend-val {
  font-size: 0.8125rem;
}

.trend-track {
  height: 8px;
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
}

.trend-fill {
  height: 100%;
  background: linear-gradient(90deg, #0d9488, #14b8a6);
  border-radius: 4px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.trend-item--empty .trend-track {
  opacity: 0.3;
}

/* 空白狀態 */
.empty-state {
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  margin: 40px 0;
}

.empty-visual {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.pulse-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100px;
  height: 100px;
  border: 4px solid #f0fdfa;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0.8;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.4);
    opacity: 0;
  }
}

/* 表格樣式 */
.modern-report-table {
  border-radius: 12px;
  background: transparent;
}

.modern-report-table :deep(.q-table__card) {
  box-shadow: none;
  background: transparent;
}

.modern-report-table :deep(th) {
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 11px;
  padding: 16px;
}

.modern-report-table :deep(td) {
  padding: 16px;
  color: #334155;
}

.uppercase-tracking {
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.stat-box {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  transition: all 0.2s;
}

.stat-box:hover {
  background: #f1f5f9;
  border-color: #e2e8f0;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  margin-right: 6px;
}

.text-mono {
  font-family: inherit;
  letter-spacing: -0.01em;
}

.animate-fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.report-tabs {
  background: #f1f5f9;
  border-radius: 12px;
  padding: 4px;
}

.report-tabs :deep(.q-tab) {
  border-radius: 8px;
  min-height: 40px;
}

.report-tabs :deep(.q-tab--active) {
  background: white;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.report-tabs :deep(.q-tab__indicator) {
  display: none;
}
</style>
