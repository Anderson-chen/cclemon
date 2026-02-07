<template>
  <q-page padding>
    <div class="q-mb-md">
      <h5 class="q-mt-none q-mb-md">體重趨勢圖表</h5>

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
      <q-card v-if="chartData" class="q-mb-md">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-3">
              <div class="text-caption text-grey-7">平均體重</div>
              <div class="text-h6 text-primary">
                {{ chartData.avgWeight?.toFixed(1) }} kg
              </div>
            </div>
            <div class="col-12 col-sm-3">
              <div class="text-caption text-grey-7">最高體重</div>
              <div class="text-h6 text-orange">
                {{ chartData.maxWeight?.toFixed(1) }} kg
              </div>
            </div>
            <div class="col-12 col-sm-3">
              <div class="text-caption text-grey-7">最低體重</div>
              <div class="text-h6 text-green">
                {{ chartData.minWeight?.toFixed(1) }} kg
              </div>
            </div>
            <div class="col-12 col-sm-3">
              <div class="text-caption text-grey-7">體重變化</div>
              <div
                class="text-h6"
                :class="
                  chartData.weightChange && chartData.weightChange > 0
                    ? 'text-red'
                    : 'text-green'
                "
              >
                {{
                  chartData.weightChange && chartData.weightChange > 0
                    ? '+'
                    : ''
                }}{{ chartData.weightChange?.toFixed(1) }} kg
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 圖表區域 -->
      <q-card>
        <q-card-section>
          <div class="text-h6">體重趨勢</div>
        </q-card-section>
        <q-card-section v-if="loading" class="text-center">
          <q-spinner color="primary" size="3em" />
        </q-card-section>
        <q-card-section v-else-if="!chartData || chartData.dates.length === 0">
          <div class="text-center text-grey-6">暫無數據</div>
        </q-card-section>
        <q-card-section v-else>
          <!-- 簡易圖表顯示 - 使用 canvas 或 SVG -->
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
import { getWeightChart } from '../api/weight/weight';
import type { WeightChartData } from '../api/weight/types';

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

// 繪製圖表 (簡易版本)
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

  const width = canvas.width;
  const height = canvas.height;
  const padding = 40;
  const chartWidth = width - 2 * padding;
  const chartHeight = height - 2 * padding;

  // 清空畫布
  ctx.clearRect(0, 0, width, height);

  const weights = chartData.value.weights;
  const dates = chartData.value.dates;

  if (weights.length === 0) return;

  // 計算數據範圍
  const minWeight = Math.min(...weights);
  const maxWeight = Math.max(...weights);
  const weightRange = maxWeight - minWeight || 1;

  // 繪製背景網格
  ctx.strokeStyle = '#e0e0e0';
  ctx.lineWidth = 1;
  for (let i = 0; i <= 5; i++) {
    const y = padding + (chartHeight / 5) * i;
    ctx.beginPath();
    ctx.moveTo(padding, y);
    ctx.lineTo(width - padding, y);
    ctx.stroke();

    // Y軸標籤
    const value = maxWeight - (weightRange / 5) * i;
    ctx.fillStyle = '#666';
    ctx.font = '12px sans-serif';
    ctx.textAlign = 'right';
    ctx.fillText(value.toFixed(1), padding - 5, y + 4);
  }

  // 繪製折線
  ctx.strokeStyle = '#1976d2';
  ctx.lineWidth = 2;
  ctx.beginPath();

  weights.forEach((weight, index) => {
    const x = padding + (chartWidth / (weights.length - 1 || 1)) * index;
    const y =
      padding +
      chartHeight -
      ((weight - minWeight) / weightRange) * chartHeight;

    if (index === 0) {
      ctx.moveTo(x, y);
    } else {
      ctx.lineTo(x, y);
    }
  });

  ctx.stroke();

  // 繪製數據點
  ctx.fillStyle = '#1976d2';
  weights.forEach((weight, index) => {
    const x = padding + (chartWidth / (weights.length - 1 || 1)) * index;
    const y =
      padding +
      chartHeight -
      ((weight - minWeight) / weightRange) * chartHeight;

    ctx.beginPath();
    ctx.arc(x, y, 4, 0, 2 * Math.PI);
    ctx.fill();

    // 數值標籤
    ctx.fillStyle = '#666';
    ctx.font = '10px sans-serif';
    ctx.textAlign = 'center';
    ctx.fillText(weight.toFixed(1), x, y - 8);
  });

  // 繪製 X 軸日期標籤
  ctx.fillStyle = '#666';
  ctx.font = '10px sans-serif';
  ctx.textAlign = 'center';
  const step = Math.ceil(dates.length / 10); // 最多顯示 10 個日期標籤
  dates.forEach((date, index) => {
    if (index % step === 0 || index === dates.length - 1) {
      const x = padding + (chartWidth / (dates.length - 1 || 1)) * index;
      const shortDate = date.substring(5); // 只顯示 MM-DD
      ctx.fillText(shortDate, x, height - padding + 15);
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
  max-width: 100%;
  height: auto;
}
</style>
