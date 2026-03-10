<template>
  <q-page padding class="report-page">
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="bar_chart" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">收益報表</h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 財務分析</div>
        </div>
      </div>
      <q-btn
        unelevated
        color="teal-8"
        icon="download"
        :label="$q.screen.gt.xs ? '匯出 CSV' : ''"
        :loading="exporting"
        :disable="loading || reportOrders.length === 0"
        @click="exportCsv"
        class="cursor-pointer"
      />
    </div>

    <!-- 報表類型切換 -->
    <q-tabs
      v-model="tab"
      dense
      active-color="teal-8"
      indicator-color="teal-8"
      align="left"
      class="q-mb-md"
      @update:model-value="onTabChange"
    >
      <q-tab name="daily" icon="today" label="日報表" />
      <q-tab name="monthly" icon="calendar_month" label="月報表" />
    </q-tabs>

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
            <q-btn flat dense label="今日" color="teal-7" @click="setToday" class="cursor-pointer" />
            <q-btn flat dense label="昨日" color="grey-7" @click="setYesterday" class="cursor-pointer" />
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
            <q-btn flat dense label="本月" color="teal-7" @click="setThisMonth" class="cursor-pointer" />
            <q-btn flat dense label="上月" color="grey-7" @click="setLastMonth" class="cursor-pointer" />
          </template>

          <q-btn
            unelevated
            color="teal-8"
            icon="search"
            label="查詢"
            :loading="loading"
            @click="loadReport"
            class="cursor-pointer"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 載入中 -->
    <div v-if="loading" class="text-center q-py-xl">
      <q-spinner color="teal-8" size="3em" />
      <div class="text-caption text-grey-5 q-mt-sm">載入報表資料中...</div>
    </div>

    <template v-else>
      <!-- KPI 卡片 -->
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

      <!-- 無資料提示 -->
      <div v-if="reportOrders.length === 0" class="text-center q-py-xl">
        <q-icon name="inbox" size="4em" color="grey-3" />
        <div class="text-subtitle1 text-grey-5 q-mt-sm">本期間無已取件訂單</div>
        <div class="text-caption text-grey-4">報表以「實際取件日」為入帳基準，已取消訂單不計入</div>
      </div>

      <template v-else>
        <!-- 服務項目收益分析 -->
        <div class="row q-gutter-md q-mb-md">
          <div class="col-12 col-md-5">
            <q-card class="section-card full-height">
              <q-card-section class="section-header">
                <q-icon name="cleaning_services" size="sm" color="teal-8" class="q-mr-xs" />
                <span class="text-subtitle1 text-weight-medium">服務項目收益分析</span>
              </q-card-section>
              <q-card-section>
                <div
                  v-for="svc in serviceBreakdown"
                  :key="svc.code"
                  class="q-mb-md"
                >
                  <div class="row items-center justify-between q-mb-xs">
                    <div class="text-body2 text-weight-medium">{{ svc.name }}</div>
                    <div class="row items-center q-gutter-sm">
                      <q-chip dense size="sm" color="teal-1" text-color="teal-8">
                        {{ svc.count }} 件
                      </q-chip>
                      <span class="text-body2 text-weight-bold text-teal-8">
                        NT$ {{ svc.revenue.toLocaleString() }}
                      </span>
                    </div>
                  </div>
                  <q-linear-progress
                    :value="svc.percent"
                    rounded
                    size="10px"
                    color="teal-6"
                    track-color="grey-2"
                  />
                  <div class="text-caption text-grey-5 q-mt-xs">
                    佔比 {{ (svc.percent * 100).toFixed(1) }}%
                  </div>
                </div>

                <q-separator class="q-my-sm" />
                <div class="row justify-between items-center">
                  <span class="text-caption text-grey-6">急件加收費用</span>
                  <span class="text-body2 text-weight-bold text-orange-7">
                    NT$ {{ totalUrgentFee.toLocaleString() }}
                  </span>
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- 訂單明細表 (日報表) / 每日趨勢表 (月報表) -->
          <div class="col">
            <!-- 日報表：訂單明細 -->
            <q-card v-if="tab === 'daily'" class="section-card full-height">
              <q-card-section class="section-header row items-center justify-between">
                <div class="row items-center">
                  <q-icon name="list_alt" size="sm" color="teal-8" class="q-mr-xs" />
                  <span class="text-subtitle1 text-weight-medium">訂單明細</span>
                </div>
                <q-chip dense color="teal-1" text-color="teal-8" size="sm">
                  共 {{ reportOrders.length }} 筆
                </q-chip>
              </q-card-section>
              <div style="overflow-x: auto">
              <q-table
                :rows="reportOrders"
                :columns="orderColumns"
                row-key="id"
                flat
                dense
                hide-bottom
                :rows-per-page-options="[0]"
                class="report-table"
              >
                <template v-slot:body-cell-orderNo="props">
                  <q-td :props="props">
                    <span class="text-weight-medium text-mono">{{ props.value }}</span>
                    <q-badge v-if="props.row.isUrgent" color="red-5" label="急" size="xs" class="q-ml-xs" />
                  </q-td>
                </template>
                <template v-slot:body-cell-services="props">
                  <q-td :props="props">
                    <span class="text-caption">{{ props.value }}</span>
                  </q-td>
                </template>
                <template v-slot:body-cell-totalAmount="props">
                  <q-td :props="props" class="text-right">
                    <span class="text-weight-bold text-teal-8">
                      NT$ {{ props.value.toLocaleString() }}
                    </span>
                  </q-td>
                </template>
                <template v-slot:body-cell-urgentFee="props">
                  <q-td :props="props" class="text-right">
                    <span v-if="props.value > 0" class="text-orange-7 text-caption">
                      +{{ props.value.toLocaleString() }}
                    </span>
                    <span v-else class="text-grey-4">–</span>
                  </q-td>
                </template>
              </q-table>
              </div>
            </q-card>

            <!-- 月報表：每日趨勢 -->
            <q-card v-else class="section-card full-height">
              <q-card-section class="section-header row items-center justify-between">
                <div class="row items-center">
                  <q-icon name="trending_up" size="sm" color="teal-8" class="q-mr-xs" />
                  <span class="text-subtitle1 text-weight-medium">每日營收趨勢</span>
                </div>
              </q-card-section>
              <q-card-section class="q-pa-none">
                <div class="trend-scroll">
                  <div
                    v-for="day in dailyTrend"
                    :key="day.date"
                    class="trend-row"
                    :class="{ 'trend-row--active': day.count > 0 }"
                  >
                    <div class="trend-date text-caption text-grey-6">{{ day.dateLabel }}</div>
                    <div class="trend-bar-wrap">
                      <div
                        class="trend-bar"
                        :style="`width: ${day.percent}%`"
                      />
                    </div>
                    <div class="trend-amount text-caption text-weight-medium" :class="day.count > 0 ? 'text-teal-8' : 'text-grey-4'">
                      {{ day.count > 0 ? `NT$ ${day.revenue.toLocaleString()}` : '–' }}
                    </div>
                    <div class="trend-count text-caption text-grey-5">
                      {{ day.count > 0 ? `${day.count} 筆` : '' }}
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
const loading = ref(false);
const exporting = ref(false);
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

    // PRD 8.5: 以 actualPickupDate 為入帳日，只計算 PICKED_UP 狀態
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

// ── 計算指標 ───────────────────────────────────────────────
const totalRevenue = computed(() =>
  reportOrders.value.reduce((sum, o) => sum + o.totalAmount, 0)
);

const totalUrgentFee = computed(() =>
  reportOrders.value.reduce((sum, o) => sum + o.urgentFee, 0)
);

const avgOrderAmount = computed(() =>
  reportOrders.value.length === 0
    ? 0
    : Math.round(totalRevenue.value / reportOrders.value.length)
);

const urgentCount = computed(
  () => reportOrders.value.filter((o) => o.isUrgent).length
);

const kpiCards = computed(() => [
  {
    label: '訂單數',
    value: reportOrders.value.length,
    icon: 'receipt_long',
    accentColor: '#0f766e',
    iconBg: '#ccfbf1',
  },
  {
    label: '總營收',
    value: `NT$ ${totalRevenue.value.toLocaleString()}`,
    icon: 'payments',
    accentColor: '#065f46',
    iconBg: '#d1fae5',
  },
  {
    label: '急件費收入',
    value: `NT$ ${totalUrgentFee.value.toLocaleString()}`,
    icon: 'bolt',
    accentColor: '#c2410c',
    iconBg: '#ffedd5',
  },
  {
    label: '平均訂單金額',
    value: `NT$ ${avgOrderAmount.value.toLocaleString()}`,
    icon: 'calculate',
    accentColor: '#1d4ed8',
    iconBg: '#dbeafe',
  },
  {
    label: '急件筆數',
    value: urgentCount.value,
    icon: 'priority_high',
    accentColor: '#7c3aed',
    iconBg: '#ede9fe',
  },
]);

// ── 服務項目分析 ───────────────────────────────────────────
const SERVICE_NAMES: Record<string, string> = {
  'SVC-WASH': '洗鞋',
  'SVC-COATING': '鍍膜',
  'SVC-BAG': '洗包',
  'SVC-RECOLOR': '補色',
};

const serviceBreakdown = computed(() => {
  const map: Record<string, { name: string; count: number; revenue: number }> = {};

  for (const order of reportOrders.value) {
    for (const item of order.items) {
      if (!map[item.serviceCode]) {
        map[item.serviceCode] = {
          name: SERVICE_NAMES[item.serviceCode] ?? item.serviceName,
          count: 0,
          revenue: 0,
        };
      }
      map[item.serviceCode].count += item.quantity;
      map[item.serviceCode].revenue += item.subtotal;
    }
  }

  const entries = Object.entries(map).map(([code, v]) => ({ code, ...v }));
  const maxRevenue = Math.max(...entries.map((e) => e.revenue), 1);

  return entries
    .sort((a, b) => b.revenue - a.revenue)
    .map((e) => ({ ...e, percent: e.revenue / maxRevenue }));
});

// ── 日報表訂單欄位 ─────────────────────────────────────────
const orderColumns = [
  { name: 'orderNo', label: '訂單編號', field: 'orderNo', align: 'left' as const, sortable: true },
  { name: 'customerName', label: '顧客', field: 'customerName', align: 'left' as const },
  {
    name: 'services',
    label: '服務項目',
    field: (row: OrderResult) => row.items.map((i) => i.serviceName).join('、'),
    align: 'left' as const,
  },
  {
    name: 'urgentFee',
    label: '急件費',
    field: 'urgentFee',
    align: 'right' as const,
  },
  { name: 'totalAmount', label: '金額', field: 'totalAmount', align: 'right' as const, sortable: true },
  { name: 'actualPickupDate', label: '取件日', field: 'actualPickupDate', align: 'left' as const },
];

// ── 月報表每日趨勢 ─────────────────────────────────────────
const dailyTrend = computed(() => {
  if (tab.value !== 'monthly') return [];

  const [year, month] = selectedMonth.value.split('-').map(Number);
  const lastDay = new Date(year, month, 0).getDate();

  // 按 actualPickupDate 彙整
  const dayMap: Record<string, { count: number; revenue: number }> = {};
  for (const order of reportOrders.value) {
    const key = order.actualPickupDate?.slice(0, 10) ?? '';
    if (!key) continue;
    if (!dayMap[key]) dayMap[key] = { count: 0, revenue: 0 };
    dayMap[key].count += 1;
    dayMap[key].revenue += order.totalAmount;
  }

  const maxRevenue = Math.max(...Object.values(dayMap).map((d) => d.revenue), 1);

  const result = [];
  for (let d = 1; d <= lastDay; d++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    const data = dayMap[dateStr] ?? { count: 0, revenue: 0 };
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][new Date(dateStr).getDay()];
    result.push({
      date: dateStr,
      dateLabel: `${month}/${d}（${weekDay}）`,
      count: data.count,
      revenue: data.revenue,
      percent: (data.revenue / maxRevenue) * 100,
    });
  }
  return result;
});

// ── CSV 匯出 ───────────────────────────────────────────────
// PRD 8.5: 匯出欄位：訂單編號、顧客姓名、服務項目、金額、取件日
function exportCsv() {
  try {
    exporting.value = true;

    const headers = ['訂單編號', '顧客姓名', '顧客電話', '服務項目', '急件費', '訂單金額', '取件日'];
    const rows = reportOrders.value.map((o) => [
      o.orderNo,
      o.customerName,
      o.customerPhone,
      o.items.map((i) => `${i.serviceName}×${i.quantity}`).join('; '),
      o.urgentFee,
      o.totalAmount,
      o.actualPickupDate ?? '',
    ]);

    const csvContent = [headers, ...rows]
      .map((row) =>
        row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(',')
      )
      .join('\n');

    const bom = '\uFEFF'; // UTF-8 BOM for Excel
    const blob = new Blob([bom + csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');

    const filename =
      tab.value === 'daily'
        ? `ShoesReborn_日報表_${selectedDate.value}.csv`
        : `ShoesReborn_月報表_${selectedMonth.value}.csv`;

    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);

    $q.notify({ type: 'positive', message: `已匯出 ${filename}` });
  } catch {
    $q.notify({ type: 'negative', message: '匯出失敗，請稍後再試' });
  } finally {
    exporting.value = false;
  }
}

onMounted(loadReport);
</script>

<style scoped>
.report-page {
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
  min-width: 130px;
  background: white;
  border-radius: 12px;
  border-top: 3px solid var(--accent);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07), 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s, transform 0.2s;
}

.kpi-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}


.kpi-inner {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.kpi-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kpi-value {
  font-size: 1.35rem;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.kpi-label {
  font-size: 0.75rem;
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

/* 報表表格 */
.report-table :deep(th) {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 600;
}

.text-mono {
  font-family: 'Courier New', monospace;
  font-size: 0.85rem;
}

/* 月報趨勢 */
.trend-scroll {
  max-height: 420px;
  overflow-y: auto;
  padding: 8px 16px;
}

.trend-row {
  display: grid;
  grid-template-columns: 90px 1fr 120px 48px;
  align-items: center;
  gap: 8px;
  padding: 5px 0;
  border-bottom: 1px solid #f1f5f9;
}

.trend-row:last-child {
  border-bottom: none;
}

.trend-bar-wrap {
  background: #f1f5f9;
  border-radius: 4px;
  height: 8px;
  overflow: hidden;
}

.trend-bar {
  height: 100%;
  background: linear-gradient(90deg, #0d9488, #14b8a6);
  border-radius: 4px;
  transition: width 0.4s ease;
  min-width: 0;
}

.trend-row--active .trend-bar-wrap {
  background: #e0f2f1;
}

.trend-amount {
  text-align: right;
}

.trend-count {
  text-align: right;
}
</style>
