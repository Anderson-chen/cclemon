<template>
  <q-dialog
    :model-value="modelValue"
    :position="$q.screen.lt.md ? 'bottom' : 'standard'"
    :maximized="!$q.screen.lt.md"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card
      v-if="localOrder"
      class="detail-card"
      :style="
        $q.screen.lt.md
          ? 'width: 100vw; max-height: 90dvh; border-radius: 16px 16px 0 0;'
          : 'height: 100%;'
      "
    >
      <!-- Sticky Header -->
      <q-toolbar class="bg-teal-8 text-white detail-toolbar">
        <q-icon name="receipt_long" size="sm" class="q-mr-sm" />
        <q-toolbar-title>{{ localOrder.orderNo }}</q-toolbar-title>
        <q-badge
          v-if="localOrder.isUrgent"
          color="red-4"
          label="急件"
          class="q-mr-sm"
        />
        <q-badge
          :color="statusColor(localOrder.status)"
          :label="statusLabel(localOrder.status)"
        />
      </q-toolbar>

      <!-- Scrollable Body -->
      <div class="detail-body">
      <q-card-section class="q-pa-md">
        <q-card bordered flat>
          <!-- Tabs -->
          <q-tabs
            v-model="activeTab"
            dense
            align="left"
            active-color="teal-8"
            indicator-color="teal-8"
            class="text-grey-7 tab-bar"
          >
            <q-tab name="info" icon="info" label="訂單資訊" />
            <q-tab name="items" icon="cleaning_services" label="服務明細" />
          </q-tabs>
          <q-separator />

          <q-tab-panels v-model="activeTab" animated>
            <!-- Tab: 訂單資訊 -->
            <q-tab-panel name="info" class="q-pa-none">
              <q-card-section>
                <!-- 整合後的狀態軌道 (Status Rail) -->
                <div class="text-caption text-grey-6 text-center q-mb-xs">點擊圖示可更新狀態</div>
                <div class="status-rail q-mb-md">
                  <template v-for="(step, idx) in STATUS_STEPS" :key="step.status">
                    <div v-if="idx > 0"
                      class="status-rail__line"
                      :class="{ 'status-rail__line--done': statusIndex(localOrder.status) >= idx }"
                    />
                    <div
                      class="status-node"
                      :class="[
                        `status-node--${step.status.toLowerCase()}`,
                        {
                          'status-node--done': statusIndex(localOrder.status) > idx,
                          'status-node--active': localOrder.status === step.status,
                          'status-node--future': statusIndex(localOrder.status) < idx,
                          'status-node--clickable': canUpdateTo(step.status),
                        }
                      ]"
                      @click="canUpdateTo(step.status) && updateStatus(step.status)"
                    >
                      <div class="status-node__ring">
                        <q-spinner v-if="pendingStatus === step.status" size="18px" color="white" />
                        <q-icon v-else :name="step.icon" size="18px" />
                      </div>
                      <div class="status-node__label">{{ step.label }}</div>
                    </div>
                  </template>
                </div>

                <!-- 取消訂單按鈕 (僅在未取件且未取消時顯示) -->
                <div v-if="localOrder.status !== 'PICKED_UP' && localOrder.status !== 'CANCELLED'" class="row justify-center q-mb-md">
                  <q-btn
                    flat
                    color="red-4"
                    icon="cancel"
                    label="取消訂單"
                    dense
                    size="sm"
                    padding="2px 12px"
                    class="cursor-pointer"
                    :loading="pendingStatus === 'CANCELLED'"
                    @click="confirmCancel"
                  />
                </div>

                <!-- 若已取消則顯示取消標誌 -->
                <div v-if="localOrder.status === 'CANCELLED'" class="text-center q-mb-md">
                  <q-chip color="red-1" text-color="red-6" icon="cancel" label="訂單已取消" />
                </div>

                <q-list dense>
                  <q-item>
                    <q-item-section avatar
                      ><q-icon name="person" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>顧客</q-item-label>
                      <q-item-label>{{ localOrder.customerName }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section avatar
                      ><q-icon name="phone" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>電話</q-item-label>
                      <div class="row items-center no-wrap">
                        <template v-if="localOrder.customerPhone">
                          <a 
                            :href="'tel:' + localOrder.customerPhone" 
                            class="phone-link text-weight-medium text-teal-9"
                          >
                            {{ localOrder.customerPhone }}
                          </a>
                          <div class="q-ml-sm row items-center no-wrap q-gutter-xs">
                            <q-btn flat round dense color="teal-7" icon="call" tag="a" :href="'tel:' + localOrder.customerPhone" size="sm" class="cursor-pointer">
                              <q-tooltip>立即撥打</q-tooltip>
                            </q-btn>
                            <q-btn flat round dense color="grey-6" icon="content_copy" size="xs" @click="copyText(localOrder.customerPhone)" class="cursor-pointer">
                              <q-tooltip>複製號碼</q-tooltip>
                            </q-btn>
                          </div>
                        </template>
                        <span v-else class="text-grey-5">無電話資訊</span>
                      </div>
                    </q-item-section>
                  </q-item>
                  <q-item v-if="localOrder.storageLocations.length > 0">
                    <q-item-section avatar
                      ><q-icon name="location_on" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>存放位置</q-item-label>
                      <q-item-label>{{
                        localOrder.storageLocations.join('、')
                      }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section avatar
                      ><q-icon name="event_available" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>預計取件日</q-item-label>
                      <q-item-label>{{
                        localOrder.estimatedPickupDate
                      }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item v-if="localOrder.actualPickupDate">
                    <q-item-section avatar
                      ><q-icon name="done_all" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>實際取件日</q-item-label>
                      <q-item-label>{{
                        localOrder.actualPickupDate
                      }}</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item>
                    <q-item-section avatar
                      ><q-icon name="calendar_today" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>建立時間</q-item-label>
                      <q-item-label>{{
                        formatDate(localOrder.createTime)
                      }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-card-section>
            </q-tab-panel>

            <!-- Tab: 服務明細 -->
            <q-tab-panel name="items" class="q-pa-none">
              <q-list separator>
                <template v-for="item in localOrder.items" :key="item.id">
                  <q-item>
                    <q-item-section avatar>
                      <q-avatar
                        color="teal-1"
                        text-color="teal-8"
                        size="40px"
                        icon="cleaning_services"
                      />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-medium">
                        {{ item.serviceName }}
                        <span
                          v-if="item.productName"
                          class="text-caption text-grey-6 q-ml-xs"
                          >（{{ item.productName }}）</span
                        >
                      </q-item-label>
                      <q-item-label caption>
                        NT$ {{ item.unitPrice.toLocaleString() }} ×
                        {{ item.quantity }}
                        <span v-if="item.itemStorageLocation" class="q-ml-sm">
                          <q-icon name="location_on" size="xs" />{{
                            item.itemStorageLocation
                          }}
                        </span>
                      </q-item-label>
                      <q-item-label
                        v-if="item.itemNote"
                        caption
                        class="text-grey-6"
                      >
                        <q-icon name="notes" size="xs" />{{ item.itemNote }}
                      </q-item-label>
                    </q-item-section>
                    <q-item-section side>
                      <div class="text-weight-bold text-teal-8">
                        NT$ {{ item.subtotal.toLocaleString() }}
                      </div>
                    </q-item-section>
                  </q-item>

                  <!-- 服務項目照片 (點擊開啟全螢幕藝廊) -->
                  <div
                    v-if="item.imageUrls && item.imageUrls.length > 0"
                    class="q-px-md q-pb-md row q-gutter-sm"
                  >
                    <div
                      v-for="(img, idx) in item.imageUrls"
                      :key="idx"
                      class="cursor-pointer image-thumb-container"
                      @click="showFullImage(item.imageUrls || [], idx)"
                    >
                      <q-img
                        :src="img"
                        style="height: 100px; width: 100px; border-radius: 12px"
                        class="bg-grey-2 shadow-2 thumb-hover"
                      >
                        <template v-slot:loading>
                          <q-spinner-oval color="teal-8" />
                        </template>
                        <div
                          class="absolute-bottom text-center text-caption q-pa-xs thumb-overlay"
                        >
                          檢視
                        </div>
                      </q-img>
                    </div>
                  </div>
                </template>

                <!-- 急件費行 -->
                <q-item v-if="localOrder.urgentFee > 0">
                  <q-item-section avatar>
                    <q-avatar
                      color="red-1"
                      text-color="red-6"
                      size="40px"
                      icon="bolt"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-weight-medium text-red-6"
                      >急件費</q-item-label
                    >
                    <q-item-label caption>加急處理費用（50%）</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <div class="text-weight-bold text-red-6">
                      NT$ {{ localOrder.urgentFee.toLocaleString() }}
                    </div>
                  </q-item-section>
                </q-item>
              </q-list>

              <!-- 合計列 -->
              <q-card-section class="q-pt-none">
                <q-separator class="q-mb-sm" />
                <div class="row justify-end items-center q-gutter-sm">
                  <span class="text-subtitle2 text-grey-7">訂單總額</span>
                  <span class="text-h6 text-weight-bold text-teal-8">
                    NT$ {{ localOrder.totalAmount.toLocaleString() }}
                  </span>
                </div>
              </q-card-section>
            </q-tab-panel>
          </q-tab-panels>
        </q-card>
      </q-card-section>
      </div>

      <!-- Sticky Footer -->
      <div class="detail-footer">
        <q-btn
          flat
          color="grey-7"
          icon="close"
          label="關閉"
          v-close-popup
          class="cursor-pointer"
          padding="6px 16px"
        />
        <q-btn
          v-if="showEdit"
          unelevated
          rounded
          color="teal-8"
          icon="edit"
          label="編輯訂單"
          @click="emit('edit-requested', localOrder)"
          :disable="
            localOrder.status === 'PICKED_UP' ||
            localOrder.status === 'CANCELLED'
          "
          class="cursor-pointer"
          padding="6px 20px"
        />
      </div>
    </q-card>
  </q-dialog>

  <!-- 全螢幕照片藝廊 (支援多張切換) -->
  <q-dialog
    v-model="imagePreview.open"
    maximized
    transition-show="fade"
    transition-hide="fade"
  >
    <q-card class="bg-black text-white">
      <q-toolbar class="absolute-top transparent z-top">
        <q-btn
          flat
          round
          dense
          icon="arrow_back"
          v-close-popup
          class="q-ml-sm"
          color="white"
        />
        <q-toolbar-title class="text-center text-subtitle1">
          照片檢視 ({{ imagePreview.currentIndex + 1 }} /
          {{ imagePreview.urls.length }})
        </q-toolbar-title>
        <q-btn
          flat
          round
          dense
          icon="close"
          v-close-popup
          class="q-mr-sm"
          color="white"
        />
      </q-toolbar>

      <q-carousel
        v-model="imagePreview.currentIndex"
        animated
        swipeable
        arrows
        thumbnails
        infinite
        class="full-height bg-transparent"
        control-color="white"
      >
        <q-carousel-slide
          v-for="(url, idx) in imagePreview.urls"
          :key="idx"
          :name="idx"
          :img-src="url"
        />
      </q-carousel>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { useQuasar, copyToClipboard } from 'quasar';
import { updateOrderStatus } from '../api/order/order';
import type { OrderResult, OrderStatus } from '../api/order/types';

interface Props {
  modelValue: boolean;
  order: OrderResult | null;
  showEdit?: boolean;
}

const props = withDefaults(defineProps<Props>(), { showEdit: true });

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  'status-updated': [order: OrderResult];
  'edit-requested': [order: OrderResult];
}>();

const $q = useQuasar();
const pendingStatus = ref<OrderStatus | null>(null);
const localOrder = ref<OrderResult | null>(null);
const activeTab = ref<'info' | 'items'>('info');

const STATUS_STEPS: { status: OrderStatus; label: string; icon: string }[] = [
  { status: 'PENDING', label: '待處理', icon: 'hourglass_empty' },
  { status: 'IN_PROGRESS', label: '處理中', icon: 'sync' },
  { status: 'READY', label: '待取件', icon: 'check_circle' },
  { status: 'PICKED_UP', label: '已取件', icon: 'done_all' },
];

// 藝廊狀態
const imagePreview = reactive({
  open: false,
  urls: [] as string[],
  currentIndex: 0,
});

watch(
  () => props.order,
  (o) => {
    localOrder.value = o ? { ...o } : null;
  },
  { immediate: true },
);

function showFullImage(urls: string[], index: number) {
  imagePreview.urls = urls;
  imagePreview.currentIndex = index;
  imagePreview.open = true;
}

function statusIndex(status: string): number {
  return STATUS_STEPS.findIndex((s) => s.status === status);
}

function canUpdateTo(status: OrderStatus): boolean {
  if (!localOrder.value) return false;
  const cur = localOrder.value.status;
  return cur !== status && cur !== 'CANCELLED' && cur !== 'PICKED_UP';
}

async function updateStatus(status: OrderStatus) {
  if (!localOrder.value) return;
  try {
    pendingStatus.value = status;
    const payload: { status: OrderStatus; actualPickupDate?: string } = {
      status,
    };
    if (status === 'PICKED_UP') {
      payload.actualPickupDate = new Date().toISOString().slice(0, 10);
    }
    const updated = await updateOrderStatus(localOrder.value.id, payload);
    localOrder.value = updated;
    emit('status-updated', updated);
    $q.notify({
      type: 'positive',
      message: `狀態已更新：${statusLabel(status)}`,
    });
    emit('update:modelValue', false);
  } catch {
    $q.notify({ type: 'negative', message: '狀態更新失敗' });
  } finally {
    pendingStatus.value = null;
  }
}

function confirmCancel() {
  $q.dialog({
    title: '確認取消',
    message: '確定要取消這筆訂單嗎？取消後將無法回復狀態。',
    cancel: {
      flat: true,
      color: 'grey-7',
      label: '返回',
    },
    ok: {
      unelevated: true,
      color: 'red-6',
      label: '確定取消',
      rounded: true,
    },
    class: 'modern-dialog',
  }).onOk(() => {
    updateStatus('CANCELLED');
  });
}

function statusColor(status: string): string {
  return (
    {
      PENDING: 'orange-6',
      IN_PROGRESS: 'blue-6',
      READY: 'teal-6',
      PICKED_UP: 'grey-5',
      CANCELLED: 'red-4',
    }[status] ?? 'grey-5'
  );
}



function statusLabel(status: string): string {
  return (
    {
      PENDING: '待處理',
      IN_PROGRESS: '處理中',
      READY: '待取件',
      PICKED_UP: '已取件',
      CANCELLED: '已取消',
    }[status] ?? status
  );
}


function formatDate(dateStr: string): string {
  const d = new Date(dateStr);
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}/${String(d.getDate()).padStart(2, '0')}`;
}

async function copyText(text: string) {
  try {
    await copyToClipboard(text);
    $q.notify({ type: 'positive', message: '已複製號碼', timeout: 1000, position: 'top' });
  } catch {
    $q.notify({ type: 'negative', message: '複製失敗' });
  }
}
</script>

<style scoped>
.detail-card {
  display: flex;
  flex-direction: column;
}

.detail-toolbar {
  flex-shrink: 0;
  z-index: 10;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
}

.detail-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.detail-footer {
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-top: 1px solid #edf2f7;
  background: #fafafa;
  z-index: 10;
}

.card-header-accent {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}

.stat-mini-card {
  border: 1px solid rgba(0, 150, 136, 0.2);
  border-radius: 8px;
}

.image-thumb-container {
  position: relative;
  transition: transform 0.2s ease;
}

.image-thumb-container:hover {
  transform: scale(1.05);
  z-index: 1;
}

.thumb-hover {
  border: 2px solid transparent;
  transition: border-color 0.3s ease;
}

.image-thumb-container:hover .thumb-hover {
  border-color: var(--q-primary);
}

.thumb-overlay {
  background: rgba(0, 121, 107, 0.7);
  backdrop-filter: blur(2px);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 0 0 12px 12px;
}

.image-thumb-container:hover .thumb-overlay {
  opacity: 1;
}

:deep(.q-carousel__thumbnails) {
  padding: 8px;
  background: rgba(0, 0, 0, 0.3);
}

/* Status Rail */
.status-rail {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 8px 4px 4px;
}

.status-rail__line {
  flex: 1;
  height: 3px;
  background: #e0e0e0;
  margin-top: 20px;
  border-radius: 2px;
  transition: background 0.3s;
}

.status-rail__line--done {
  background: #0d9488;
}

.status-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 56px;
  max-width: 70px;
}

.status-node__ring {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e0e0e0;
  background: white;
  transition: all 0.25s;
  flex-shrink: 0;
}

/* 各狀態顏色定義 (正常 & 啟動 & 未啟動) */
.status-node--pending { --status-color: #fb8c00; --status-bg: #fff3e0; }
.status-node--in_progress { --status-color: #1e88e5; --status-bg: #e3f2fd; }
.status-node--ready { --status-color: #0d9488; --status-bg: #f0fdfa; }
.status-node--picked_up { --status-color: #757575; --status-bg: #f5f5f5; }

/* 未啟動 (未來) 狀態: 使用對應顏色的反灰/淺色版本 */
.status-node--future .status-node__ring {
  border-color: var(--status-bg);
  color: var(--status-color);
  opacity: 0.4;
}
.status-node--future.status-node--clickable:hover .status-node__ring {
  opacity: 1;
  border-color: var(--status-color);
  background: var(--status-bg);
}

/* 已完成 (過去) 狀態 */
.status-node--done .status-node__ring {
  background: var(--status-color);
  border-color: var(--status-color);
  color: white;
}

/* 當前 (啟動中) 狀態 */
.status-node--active .status-node__ring {
  background: var(--status-color);
  border-color: var(--status-color);
  color: white;
  box-shadow: 0 0 0 5px rgba(0, 0, 0, 0.05);
  transform: scale(1.1);
}

.status-node--active.status-node--pending .status-node__ring { box-shadow: 0 0 0 5px rgba(251, 140, 0, 0.15); }
.status-node--active.status-node--in_progress .status-node__ring { box-shadow: 0 0 0 5px rgba(30, 136, 229, 0.15); }
.status-node--active.status-node--ready .status-node__ring { box-shadow: 0 0 0 5px rgba(13, 148, 136, 0.15); }

.status-node--clickable {
  cursor: pointer;
}

.status-node__label {
  font-size: 10px;
  color: #212121; /* 使用深黑色，避免反灰 */
  margin-top: 6px;
  text-align: center;
  white-space: nowrap;
  line-height: 1.2;
}

.status-node--active .status-node__label {
  color: var(--status-color);
  font-weight: 700;
}

.status-node--done .status-node__label {
  color: var(--status-color);
  font-weight: 500;
}

.tab-bar {
  background: #f9fafb;
}

.phone-link {
  text-decoration: none;
  border-bottom: 1px dashed transparent;
  transition: all 0.2s;
  font-size: 15px;
}

.phone-link:hover {
  color: #0f766e;
  border-bottom-color: #0f766e;
}
</style>
