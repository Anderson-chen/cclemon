<template>
  <q-page padding>
    <div class="q-mb-md">
      <div class="row items-center q-mb-md">
        <q-icon name="trending_up" size="md" color="teal-8" class="q-mr-sm" />
        <h5 class="q-mt-none q-mb-none">體重趨勢圖表</h5>
      </div>

      <!-- 時間範圍選擇器 -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-gutter-md items-center">
            <q-btn-toggle
              v-model="rangeType"
              toggle-color="primary"
              :options="[
                { label: '7天', value: '7d' },
                { label: '30天', value: '30d' },
                { label: '90天', value: '90d' },
                { label: '自訂', value: 'custom' },
              ]"
              @update:model-value="onRangeChange"
            />

            <template v-if="rangeType === 'custom'">
              <q-input
                v-model="customRange.startDate"
                label="開始日期"
                mask="date"
                :rules="['date']"
                outlined
                dense
                style="flex: 1; max-width: 200px"
              >
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy
                      cover
                      transition-show="scale"
                      transition-hide="scale"
                    >
                      <q-date v-model="customRange.startDate">
                        <div class="row items-center justify-end">
                          <q-btn
                            v-close-popup
                            label="關閉"
                            color="primary"
                            flat
                          />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>

              <q-input
                v-model="customRange.endDate"
                label="結束日期"
                mask="date"
                :rules="['date']"
                outlined
                dense
                style="flex: 1; max-width: 200px"
              >
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy
                      cover
                      transition-show="scale"
                      transition-hide="scale"
                    >
                      <q-date v-model="customRange.endDate">
                        <div class="row items-center justify-end">
                          <q-btn
                            v-close-popup
                            label="關閉"
                            color="primary"
                            flat
                          />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>

              <q-btn
                label="查詢"
                color="primary"
                @click="loadChartData"
                :loading="loading"
              />
            </template>
          </div>
        </q-card-section>
      </q-card>

      <!-- 統計資訊 -->
      <div v-if="chartData" class="row q-col-gutter-md q-mb-md">
        <div class="col-6 col-sm-3">
          <q-card class="stat-card">
            <q-card-section class="q-pa-md">
              <div class="row items-center q-mb-xs">
                <q-icon name="straighten" color="teal-6" size="xs" class="q-mr-xs" />
                <span class="text-caption text-grey-6">平均體重</span>
              </div>
              <div class="text-h5 text-teal-8 text-weight-bold">
                {{ chartData.avgWeight?.toFixed(1) }}
                <span class="text-caption text-grey-6">kg</span>
              </div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-6 col-sm-3">
          <q-card class="stat-card">
            <q-card-section class="q-pa-md">
              <div class="row items-center q-mb-xs">
                <q-icon name="arrow_upward" color="orange-6" size="xs" class="q-mr-xs" />
                <span class="text-caption text-grey-6">最高體重</span>
              </div>
              <div class="text-h5 text-orange-8 text-weight-bold">
                {{ chartData.maxWeight?.toFixed(1) }}
                <span class="text-caption text-grey-6">kg</span>
              </div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-6 col-sm-3">
          <q-card class="stat-card">
            <q-card-section class="q-pa-md">
              <div class="row items-center q-mb-xs">
                <q-icon name="arrow_downward" color="green-6" size="xs" class="q-mr-xs" />
                <span class="text-caption text-grey-6">最低體重</span>
              </div>
              <div class="text-h5 text-green-8 text-weight-bold">
                {{ chartData.minWeight?.toFixed(1) }}
                <span class="text-caption text-grey-6">kg</span>
              </div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-6 col-sm-3">
          <q-card class="stat-card">
            <q-card-section class="q-pa-md">
              <div class="row items-center q-mb-xs">
                <q-icon
                  :name="chartData.weightChange && chartData.weightChange > 0 ? 'trending_up' : 'trending_down'"
                  :color="chartData.weightChange && chartData.weightChange > 0 ? 'red-6' : 'green-6'"
                  size="xs"
                  class="q-mr-xs"
                />
                <span class="text-caption text-grey-6">體重變化</span>
              </div>
              <div
                class="text-h5 text-weight-bold"
                :class="chartData.weightChange && chartData.weightChange > 0 ? 'text-red-8' : 'text-green-8'"
              >
                {{ chartData.weightChange && chartData.weightChange > 0 ? '+' : '' }}{{ chartData.weightChange?.toFixed(1) }}
                <span class="text-caption text-grey-6">kg</span>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 圖表區域 -->
      <q-card>
        <q-card-section class="card-header-accent">
          <div class="row items-center">
            <q-icon name="show_chart" color="teal-8" size="sm" class="q-mr-sm" />
            <span class="text-h6">體重趨勢</span>
          </div>
        </q-card-section>
        <q-card-section v-if="loading" class="text-center q-py-xl">
          <q-spinner color="teal-8" size="3em" />
          <div class="text-caption text-grey-6 q-mt-sm">載入中...</div>
        </q-card-section>
        <q-card-section v-else-if="!chartData || chartData.dates.length === 0" class="text-center q-py-xl">
          <q-icon name="bar_chart" size="4em" color="grey-4" />
          <div class="text-subtitle1 text-grey-5 q-mt-sm">暫無數據</div>
        </q-card-section>
        <q-card-section v-else>
          <!-- 簡易圖表顯示 - 使用 canvas -->
          <div class="chart-container">
            <canvas ref="chartCanvas" width="800" height="400"></canvas>
          </div>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, nextTick } from 'vue';
import { useQuasar } from 'quasar';
import { getWeightChart } from 'src/api/weight/weight';
import type { WeightChartData } from 'src/api/weight/types';

const $q = useQuasar();

// 時間範圍
const rangeType = ref<'7d' | '30d' | '90d' | 'custom'>('30d');
const customRange = reactive({
  startDate: '',
  endDate: '',
});

// 圖表數據
const chartData = ref<WeightChartData | null>(null);
const loading = ref(false);
const chartCanvas = ref<HTMLCanvasElement | null>(null);

// 載入圖表數據
const loadChartData = async () => {
  try {
    loading.value = true;
    const params = { range: rangeType.value, startDate: '', endDate: '' };

    if (rangeType.value === 'custom') {
      params.startDate = customRange.startDate?.replace(/\//g, '-');
      params.endDate = customRange.endDate?.replace(/\//g, '-');
    }

    chartData.value = await getWeightChart(params);
    await nextTick();
    drawChart();
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入失敗，請稍後再試',
    });
    console.error('Failed to load chart data:', error);
  } finally {
    loading.value = false;
  }
};

// 範圍變更處理
const onRangeChange = () => {
  if (rangeType.value !== 'custom') {
    loadChartData();
  }
};

// 繪製圖表
const drawChart = () => {
  if (
    !chartCanvas.value ||
    !chartData.value ||
    chartData.value.weights.length === 0
  ) {
    return;
  }

  const canvas = chartCanvas.value;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // 設定高 DPI 畫布
  const dpr = window.devicePixelRatio || 1;
  const displayWidth = canvas.offsetWidth || 800;
  const displayHeight = 320;
  canvas.width = displayWidth * dpr;
  canvas.height = displayHeight * dpr;
  canvas.style.width = displayWidth + 'px';
  canvas.style.height = displayHeight + 'px';
  ctx.scale(dpr, dpr);

  const width = displayWidth;
  const height = displayHeight;
  const paddingLeft = 52;
  const paddingRight = 16;
  const paddingTop = 24;
  const paddingBottom = 36;
  const chartWidth = width - paddingLeft - paddingRight;
  const chartHeight = height - paddingTop - paddingBottom;

  ctx.clearRect(0, 0, width, height);

  const weights = chartData.value.weights;
  const dates = chartData.value.dates;

  if (weights.length === 0) return;

  const minWeight = Math.min(...weights);
  const maxWeight = Math.max(...weights);
  const weightRange = maxWeight - minWeight || 1;
  const paddedMin = minWeight - weightRange * 0.1;
  const paddedMax = maxWeight + weightRange * 0.1;
  const paddedRange = paddedMax - paddedMin;

  const toX = (i: number) => paddingLeft + (chartWidth / (weights.length - 1 || 1)) * i;
  const toY = (w: number) => paddingTop + chartHeight - ((w - paddedMin) / paddedRange) * chartHeight;

  // 背景網格
  ctx.strokeStyle = '#f0f0f0';
  ctx.lineWidth = 1;
  const gridLines = 5;
  for (let i = 0; i <= gridLines; i++) {
    const y = paddingTop + (chartHeight / gridLines) * i;
    ctx.beginPath();
    ctx.moveTo(paddingLeft, y);
    ctx.lineTo(width - paddingRight, y);
    ctx.stroke();

    const value = paddedMax - (paddedRange / gridLines) * i;
    ctx.fillStyle = '#9e9e9e';
    ctx.font = '11px -apple-system, sans-serif';
    ctx.textAlign = 'right';
    ctx.fillText(value.toFixed(1), paddingLeft - 6, y + 4);
  }

  // 面積漸層填充
  const gradient = ctx.createLinearGradient(0, paddingTop, 0, paddingTop + chartHeight);
  gradient.addColorStop(0, 'rgba(0, 121, 107, 0.25)');
  gradient.addColorStop(1, 'rgba(0, 121, 107, 0.02)');

  ctx.beginPath();
  ctx.moveTo(toX(0), toY(weights[0]!));
  weights.forEach((w, i) => {
    if (i === 0) return;
    ctx.lineTo(toX(i), toY(w));
  });
  ctx.lineTo(toX(weights.length - 1), paddingTop + chartHeight);
  ctx.lineTo(toX(0), paddingTop + chartHeight);
  ctx.closePath();
  ctx.fillStyle = gradient;
  ctx.fill();

  // 折線
  ctx.strokeStyle = '#00796b';
  ctx.lineWidth = 2.5;
  ctx.lineJoin = 'round';
  ctx.beginPath();
  weights.forEach((w, i) => {
    i === 0 ? ctx.moveTo(toX(i), toY(w)) : ctx.lineTo(toX(i), toY(w));
  });
  ctx.stroke();

  // 數據點與標籤
  const showLabel = weights.length <= 20;
  weights.forEach((w, i) => {
    const x = toX(i);
    const y = toY(w);

    // 外圈
    ctx.beginPath();
    ctx.arc(x, y, 5, 0, 2 * Math.PI);
    ctx.fillStyle = '#ffffff';
    ctx.fill();
    ctx.strokeStyle = '#00796b';
    ctx.lineWidth = 2;
    ctx.stroke();

    // 標籤
    if (showLabel) {
      ctx.fillStyle = '#546e7a';
      ctx.font = '10px -apple-system, sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText(w.toFixed(1), x, y - 10);
    }
  });

  // X 軸日期標籤
  ctx.fillStyle = '#9e9e9e';
  ctx.font = '10px -apple-system, sans-serif';
  ctx.textAlign = 'center';
  const step = Math.ceil(dates.length / 8);
  dates.forEach((date, i) => {
    if (i % step === 0 || i === dates.length - 1) {
      ctx.fillText(date.substring(5), toX(i), height - paddingBottom + 16);
    }
  });
};

// 格式化日期
function formatDateToString(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
}

// 監聽圖表數據變化
watch(chartData, () => {
  nextTick(() => {
    drawChart();
  });
});

// 初始化
onMounted(() => {
  const today = new Date();
  customRange.endDate = formatDateToString(today);
  customRange.startDate = formatDateToString(
    new Date(today.getTime() - 29 * 24 * 60 * 60 * 1000),
  );
  loadChartData();
});
</script>

<style scoped>
.chart-container {
  width: 100%;
  overflow-x: auto;
}

canvas {
  width: 100%;
  display: block;
}

.stat-card {
  border-top: 3px solid rgba(0, 150, 136, 0.3);
  transition: box-shadow 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header-accent {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}
</style>
