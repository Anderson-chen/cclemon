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
        <div class="row q-col-gutter-lg" :class="{ 'column': $q.screen.lt.md }">
          <!-- 訂單資訊 + 狀態操作 -->
          <div class="col-12 col-md-4">
            <q-card bordered flat>
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon
                    name="info"
                    color="teal-8"
                    size="sm"
                    class="q-mr-sm"
                  />
                  <span class="text-subtitle1 text-weight-medium"
                    >訂單資訊</span
                  >
                </div>
              </q-card-section>
              <q-card-section>
                <div class="text-center q-mb-md">
                  <q-avatar
                    :color="statusColor(localOrder.status)"
                    text-color="white"
                    size="56px"
                    :icon="statusIcon(localOrder.status)"
                  />
                  <div class="q-mt-sm">
                    <q-badge
                      :color="statusColor(localOrder.status)"
                      :label="statusLabel(localOrder.status)"
                      class="text-caption"
                    />
                  </div>
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
                        <q-item-label>{{ localOrder.customerPhone }}</q-item-label>
                        <q-btn v-if="$q.screen.lt.md" flat round dense color="teal-7" icon="phone" :href="'tel:' + localOrder.customerPhone" class="q-ml-sm" />
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
                  <q-item v-if="localOrder.note">
                    <q-item-section avatar
                      ><q-icon name="notes" color="teal-7"
                    /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>備註</q-item-label>
                      <q-item-label>{{ localOrder.note }}</q-item-label>
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
            </q-card>

            <!-- 金額摘要 -->
            <q-card bordered flat class="q-mt-md">
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon
                    name="payments"
                    color="teal-8"
                    size="sm"
                    class="q-mr-sm"
                  />
                  <span class="text-subtitle1 text-weight-medium"
                    >金額摘要</span
                  >
                </div>
              </q-card-section>
              <q-card-section>
                <div class="row q-gutter-sm">
                  <div class="col stat-mini-card text-center q-pa-sm">
                    <div class="text-h6 text-weight-bold text-teal-8">
                      NT$ {{ localOrder.totalAmount.toLocaleString() }}
                    </div>
                    <div class="text-caption text-grey-6">訂單總額</div>
                  </div>
                  <div
                    v-if="localOrder.urgentFee > 0"
                    class="col stat-mini-card text-center q-pa-sm"
                  >
                    <div class="text-h6 text-weight-bold text-red-6">
                      NT$ {{ localOrder.urgentFee.toLocaleString() }}
                    </div>
                    <div class="text-caption text-grey-6">急件費</div>
                  </div>
                </div>
              </q-card-section>
            </q-card>

            <!-- 狀態操作 -->
            <q-card
              bordered
              flat
              class="q-mt-md"
              v-if="nextStatusOptions(localOrder.status).length > 0"
            >
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon
                    name="update"
                    color="teal-8"
                    size="sm"
                    class="q-mr-sm"
                  />
                  <span class="text-subtitle1 text-weight-medium"
                    >更新狀態</span
                  >
                </div>
              </q-card-section>
              <q-card-section class="q-gutter-sm">
                <q-btn
                  v-for="opt in nextStatusOptions(localOrder.status)"
                  :key="opt.status"
                  unelevated
                  :color="opt.color"
                  :icon="opt.icon"
                  :label="opt.label"
                  class="full-width cursor-pointer"
                  @click="updateStatus(opt.status)"
                  :loading="updatingStatus"
                />
              </q-card-section>
            </q-card>
          </div>

          <!-- 服務明細 -->
          <div class="col">
            <q-card bordered flat>
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon
                    name="cleaning_services"
                    color="teal-8"
                    size="sm"
                    class="q-mr-sm"
                  />
                  <span class="text-subtitle1 text-weight-medium"
                    >服務明細</span
                  >
                </div>
              </q-card-section>
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
            </q-card>
          </div>
        </div>
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
import { useQuasar } from 'quasar';
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
const updatingStatus = ref(false);
const localOrder = ref<OrderResult | null>(null);

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

async function updateStatus(status: OrderStatus) {
  if (!localOrder.value) return;
  try {
    updatingStatus.value = true;
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
    updatingStatus.value = false;
  }
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

function statusIcon(status: string): string {
  return (
    {
      PENDING: 'hourglass_empty',
      IN_PROGRESS: 'sync',
      READY: 'check_circle',
      PICKED_UP: 'done_all',
      CANCELLED: 'cancel',
    }[status] ?? 'help'
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

type StatusOption = {
  status: OrderStatus;
  label: string;
  color: string;
  icon: string;
};

function nextStatusOptions(status: string): StatusOption[] {
  const map: Record<string, StatusOption[]> = {
    PENDING: [
      {
        status: 'IN_PROGRESS',
        label: '開始處理',
        color: 'blue-6',
        icon: 'sync',
      },
      {
        status: 'CANCELLED',
        label: '取消訂單',
        color: 'red-4',
        icon: 'cancel',
      },
    ],
    IN_PROGRESS: [
      {
        status: 'READY',
        label: '標記完成（待取件）',
        color: 'teal-6',
        icon: 'check_circle',
      },
      {
        status: 'CANCELLED',
        label: '取消訂單',
        color: 'red-4',
        icon: 'cancel',
      },
    ],
    READY: [
      {
        status: 'PICKED_UP',
        label: '完成取件',
        color: 'grey-7',
        icon: 'done_all',
      },
    ],
    PICKED_UP: [],
    CANCELLED: [],
  };
  return map[status] ?? [];
}

function formatDate(dateStr: string): string {
  const d = new Date(dateStr);
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}/${String(d.getDate()).padStart(2, '0')}`;
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
</style>
