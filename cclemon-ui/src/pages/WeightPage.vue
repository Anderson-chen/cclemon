<template>
  <q-page padding>
    <div class="q-mb-md">
      <h5 class="q-mt-none q-mb-md">體重記錄</h5>

      <!-- 體重記錄表單 -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="text-h6">新增記錄</div>
        </q-card-section>
        <q-card-section>
          <q-form @submit="onSubmit" class="q-gutter-md">
            <q-input
              v-model="form.measureDate"
              label="測量日期"
              mask="date"
              :rules="['date']"
              outlined
              dense
            >
              <template v-slot:prepend>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy
                    cover
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    <q-date v-model="form.measureDate">
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="關閉" color="primary" flat />
                      </div>
                    </q-date>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>

            <q-input
              v-model.number="form.weightKg"
              type="number"
              label="體重 (公斤)"
              :rules="[
                (val) => !!val || '請輸入體重',
                (val) => val >= 20 || '體重必須大於等於 20 公斤',
                (val) => val <= 300 || '體重必須小於等於 300 公斤',
              ]"
              outlined
              dense
              step="0.1"
              min="20"
              max="300"
            />

            <q-input
              v-model="form.measureTime"
              label="測量時間 (選填)"
              mask="time"
              :rules="['time']"
              outlined
              dense
            >
              <template v-slot:prepend>
                <q-icon name="access_time" class="cursor-pointer">
                  <q-popup-proxy
                    cover
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    <q-time v-model="form.measureTime" format24h>
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="關閉" color="primary" flat />
                      </div>
                    </q-time>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>

            <q-input
              v-model="form.note"
              label="備註 (選填)"
              outlined
              dense
              type="textarea"
              rows="2"
            />

            <q-btn
              type="submit"
              label="提交"
              color="primary"
              class="full-width"
              :loading="submitting"
            />
          </q-form>
        </q-card-section>
      </q-card>

      <!-- 日期範圍選擇器 -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="row q-gutter-md">
            <q-input
              v-model="filters.startDate"
              label="開始日期"
              mask="date"
              :rules="['date']"
              outlined
              dense
              style="flex: 1"
            >
              <template v-slot:prepend>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy
                    cover
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    <q-date v-model="filters.startDate">
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="關閉" color="primary" flat />
                      </div>
                    </q-date>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>

            <q-input
              v-model="filters.endDate"
              label="結束日期"
              mask="date"
              :rules="['date']"
              outlined
              dense
              style="flex: 1"
            >
              <template v-slot:prepend>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy
                    cover
                    transition-show="scale"
                    transition-hide="scale"
                  >
                    <q-date v-model="filters.endDate">
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="關閉" color="primary" flat />
                      </div>
                    </q-date>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>

            <q-btn
              label="查詢"
              color="primary"
              @click="loadWeights"
              :loading="loading"
            />
          </div>
        </q-card-section>
      </q-card>

      <!-- 體重記錄列表 -->
      <q-card>
        <q-card-section>
          <div class="text-h6">歷史記錄</div>
        </q-card-section>
        <q-card-section v-if="loading" class="text-center">
          <q-spinner color="primary" size="3em" />
        </q-card-section>
        <q-card-section v-else-if="weights.length === 0">
          <div class="text-center text-grey-6">暫無記錄</div>
        </q-card-section>
        <q-list v-else separator>
          <q-item v-for="weight in weights" :key="weight.id">
            <q-item-section>
              <q-item-label>{{ formatDate(weight.measureDate) }}</q-item-label>
              <q-item-label caption v-if="weight.measureTime">
                {{ weight.measureTime }}
              </q-item-label>
            </q-item-section>
            <q-item-section>
              <q-item-label class="text-h6 text-primary">
                {{ weight.weightKg }} kg
              </q-item-label>
            </q-item-section>
            <q-item-section v-if="weight.note" side>
              <q-item-label caption>{{ weight.note }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-btn
                flat
                round
                dense
                icon="delete"
                color="negative"
                @click="confirmDelete(weight.id)"
              />
            </q-item-section>
          </q-item>
        </q-list>
        <q-card-section v-if="pagination.totalPages > 1">
          <div class="row justify-center">
            <q-pagination
              v-model="pagination.page"
              :max="pagination.totalPages"
              @update:model-value="loadWeights"
              direction-links
              boundary-links
            />
          </div>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import {
  upsertWeight,
  listWeights,
  deleteWeight,
} from '../api/weight/weight';
import type { WeightRecordResult, WeightUpsertRequest } from '../api/weight/types';

const $q = useQuasar();

// 表單數據
const form = reactive<WeightUpsertRequest>({
  measureDate: formatDateToString(new Date()),
  weightKg: 0,
  measureTime: '',
  note: '',
});

// 篩選條件
const filters = reactive({
  startDate: formatDateToString(new Date(Date.now() - 29 * 24 * 60 * 60 * 1000)),
  endDate: formatDateToString(new Date()),
});

// 列表數據
const weights = ref<WeightRecordResult[]>([]);
const pagination = reactive({
  page: 1,
  size: 30,
  totalPages: 0,
  totalElements: 0,
});

// 載入狀態
const loading = ref(false);
const submitting = ref(false);

// 提交表單
const onSubmit = async () => {
  try {
    submitting.value = true;
    await upsertWeight({
      ...form,
      measureDate: form.measureDate?.replace(/\//g, '-'),
      measureTime: form.measureTime || undefined,
      note: form.note || undefined,
    });

    $q.notify({
      type: 'positive',
      message: '記錄成功！',
    });

    // 重置表單
    form.weightKg = 0;
    form.measureTime = '';
    form.note = '';

    // 重新載入列表
    await loadWeights();
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '記錄失敗，請稍後再試',
    });
    console.error('Failed to submit weight:', error);
  } finally {
    submitting.value = false;
  }
};

// 載入體重記錄
const loadWeights = async () => {
  try {
    loading.value = true;
    const response = await listWeights({
      startDate: filters.startDate?.replace(/\//g, '-'),
      endDate: filters.endDate?.replace(/\//g, '-'),
      page: pagination.page - 1, // API 使用 0-based index
      size: pagination.size,
    });

    weights.value = response.content;
    pagination.totalPages = response.totalPages;
    pagination.totalElements = response.totalElements;
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: '載入失敗，請稍後再試',
    });
    console.error('Failed to load weights:', error);
  } finally {
    loading.value = false;
  }
};

// 確認刪除
const confirmDelete = (id: number) => {
  $q.dialog({
    title: '確認刪除',
    message: '確定要刪除這筆記錄嗎？',
    cancel: true,
    persistent: true,
  }).onOk(async () => {
    try {
      await deleteWeight(id);
      $q.notify({
        type: 'positive',
        message: '刪除成功！',
      });
      await loadWeights();
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: '刪除失敗，請稍後再試',
      });
      console.error('Failed to delete weight:', error);
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

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return `${date.getFullYear()}/${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`;
}

// 初始化
onMounted(() => {
  loadWeights();
});
</script>

<style scoped>
.q-item {
  min-height: 64px;
}
</style>
