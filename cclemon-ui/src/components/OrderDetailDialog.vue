<template>
  <q-dialog
    :model-value="modelValue"
    maximized
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card v-if="localOrder">
      <q-toolbar class="bg-teal-8 text-white">
        <q-icon name="receipt_long" size="sm" class="q-mr-sm" />
        <q-toolbar-title>{{ localOrder.orderNo }}</q-toolbar-title>
        <q-badge v-if="localOrder.isUrgent" color="red-4" label="急件" class="q-mr-sm" />
        <q-btn
          v-if="showEdit"
          flat round dense icon="edit"
          @click="emit('edit-requested', localOrder)"
          :disable="localOrder.status === 'PICKED_UP' || localOrder.status === 'CANCELLED'"
          class="cursor-pointer"
        >
          <q-tooltip>編輯訂單</q-tooltip>
        </q-btn>
        <q-btn flat round dense icon="close" v-close-popup class="cursor-pointer" />
      </q-toolbar>

      <q-card-section class="q-pa-md">
        <div class="row q-gutter-md">
          <!-- 訂單資訊 + 狀態操作 -->
          <div class="col-12 col-md-4">
            <q-card bordered flat>
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon name="info" color="teal-8" size="sm" class="q-mr-sm" />
                  <span class="text-subtitle1 text-weight-medium">訂單資訊</span>
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
                    <q-item-section avatar><q-icon name="person" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>顧客</q-item-label>
                      <q-item-label>{{ localOrder.customerName }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section avatar><q-icon name="phone" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>電話</q-item-label>
                      <q-item-label>{{ localOrder.customerPhone }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item v-if="localOrder.storageLocations.length > 0">
                    <q-item-section avatar><q-icon name="location_on" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>存放位置</q-item-label>
                      <q-item-label>{{ localOrder.storageLocations.join('、') }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section avatar><q-icon name="event_available" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>預計取件日</q-item-label>
                      <q-item-label>{{ localOrder.estimatedPickupDate }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item v-if="localOrder.actualPickupDate">
                    <q-item-section avatar><q-icon name="done_all" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>實際取件日</q-item-label>
                      <q-item-label>{{ localOrder.actualPickupDate }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item v-if="localOrder.note">
                    <q-item-section avatar><q-icon name="notes" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>備註</q-item-label>
                      <q-item-label>{{ localOrder.note }}</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section avatar><q-icon name="calendar_today" color="teal-7" /></q-item-section>
                    <q-item-section>
                      <q-item-label caption>建立時間</q-item-label>
                      <q-item-label>{{ formatDate(localOrder.createTime) }}</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-card-section>
            </q-card>

            <!-- 金額摘要 -->
            <q-card bordered flat class="q-mt-md">
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon name="payments" color="teal-8" size="sm" class="q-mr-sm" />
                  <span class="text-subtitle1 text-weight-medium">金額摘要</span>
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
                  <div v-if="localOrder.urgentFee > 0" class="col stat-mini-card text-center q-pa-sm">
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
              bordered flat class="q-mt-md"
              v-if="nextStatusOptions(localOrder.status).length > 0"
            >
              <q-card-section class="card-header-accent">
                <div class="row items-center">
                  <q-icon name="update" color="teal-8" size="sm" class="q-mr-sm" />
                  <span class="text-subtitle1 text-weight-medium">更新狀態</span>
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
                  <q-icon name="cleaning_services" color="teal-8" size="sm" class="q-mr-sm" />
                  <span class="text-subtitle1 text-weight-medium">服務明細</span>
                </div>
              </q-card-section>
              <q-list separator>
                <template v-for="item in localOrder.items" :key="item.id">
                  <q-item>
                    <q-item-section avatar>
                      <q-avatar color="teal-1" text-color="teal-8" size="40px" icon="cleaning_services" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-medium">
                        {{ item.serviceName }}
                        <span v-if="item.productName" class="text-caption text-grey-6 q-ml-xs">（{{ item.productName }}）</span>
                      </q-item-label>
                      <q-item-label caption>
                        NT$ {{ item.unitPrice.toLocaleString() }} × {{ item.quantity }}
                        <span v-if="item.itemStorageLocation" class="q-ml-sm">
                          <q-icon name="location_on" size="xs" />{{ item.itemStorageLocation }}
                        </span>
                      </q-item-label>
                      <q-item-label v-if="item.itemNote" caption class="text-grey-6">
                        <q-icon name="notes" size="xs" />{{ item.itemNote }}
                      </q-item-label>
                    </q-item-section>
                    <q-item-section side>
                      <div class="text-weight-bold text-teal-8">
                        NT$ {{ item.subtotal.toLocaleString() }}
                      </div>
                    </q-item-section>
                  </q-item>

                  <!-- 服務項目照片 -->
                  <div v-if="item.imageUrls && item.imageUrls.length > 0" class="q-px-md q-pb-md row q-gutter-sm">
                    <div
                      v-for="(img, idx) in item.imageUrls"
                      :key="idx"
                      class="cursor-pointer"
                      @click="showFullImage(img)"
                    >
                      <q-img
                        :src="img"
                        style="height: 80px; width: 80px; border-radius: 8px"
                        class="bg-grey-2 shadow-1"
                      >
                        <template v-slot:loading>
                          <q-spinner-oval color="white" />
                        </template>
                      </q-img>
                    </div>
                  </div>
                </template>

                <!-- 急件費行 -->
                <q-item v-if="localOrder.urgentFee > 0">
                  <q-item-section avatar>
                    <q-avatar color="red-1" text-color="red-6" size="40px" icon="bolt" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="text-weight-medium text-red-6">急件費</q-item-label>
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
    </q-card>
  </q-dialog>

  <!-- 全螢幕照片預覽 -->
  <q-dialog v-model="imagePreview.open" transition-show="scale" transition-hide="scale">
    <q-card style="max-width: 90vw; background: transparent;" flat>
      <q-img :src="imagePreview.url" style="max-height: 80vh; object-fit: contain;">
        <div class="absolute-top-right">
          <q-btn round dense color="black" icon="close" v-close-popup />
        </div>
      </q-img>
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
const imagePreview = reactive({ open: false, url: '' });

watch(() => props.order, (o) => {
  localOrder.value = o ? { ...o } : null;
}, { immediate: true });

function showFullImage(url: string) {
  imagePreview.url = url;
  imagePreview.open = true;
}

async function updateStatus(status: OrderStatus) {
  if (!localOrder.value) return;
  try {
    updatingStatus.value = true;
    const payload: { status: OrderStatus; actualPickupDate?: string } = { status };
    if (status === 'PICKED_UP') {
      payload.actualPickupDate = new Date().toISOString().slice(0, 10);
    }
    const updated = await updateOrderStatus(localOrder.value.id, payload);
    localOrder.value = updated;
    emit('status-updated', updated);
    $q.notify({ type: 'positive', message: `狀態已更新：${statusLabel(status)}` });
    emit('update:modelValue', false);
  } catch {
    $q.notify({ type: 'negative', message: '狀態更新失敗' });
  } finally {
    updatingStatus.value = false;
  }
}

function statusColor(status: string): string {
  return ({ PENDING: 'orange-6', IN_PROGRESS: 'blue-6', READY: 'teal-6', PICKED_UP: 'grey-5', CANCELLED: 'red-4' }[status] ?? 'grey-5');
}

function statusIcon(status: string): string {
  return ({ PENDING: 'hourglass_empty', IN_PROGRESS: 'sync', READY: 'check_circle', PICKED_UP: 'done_all', CANCELLED: 'cancel' }[status] ?? 'help');
}

function statusLabel(status: string): string {
  return ({ PENDING: '待處理', IN_PROGRESS: '處理中', READY: '待取件', PICKED_UP: '已取件', CANCELLED: '已取消' }[status] ?? status);
}

type StatusOption = { status: OrderStatus; label: string; color: string; icon: string };

function nextStatusOptions(status: string): StatusOption[] {
  const map: Record<string, StatusOption[]> = {
    PENDING: [
      { status: 'IN_PROGRESS', label: '開始處理', color: 'blue-6', icon: 'sync' },
      { status: 'CANCELLED', label: '取消訂單', color: 'red-4', icon: 'cancel' },
    ],
    IN_PROGRESS: [
      { status: 'READY', label: '標記完成（待取件）', color: 'teal-6', icon: 'check_circle' },
      { status: 'CANCELLED', label: '取消訂單', color: 'red-4', icon: 'cancel' },
    ],
    READY: [
      { status: 'PICKED_UP', label: '完成取件', color: 'grey-7', icon: 'done_all' },
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
.card-header-accent {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}

.stat-mini-card {
  border: 1px solid rgba(0, 150, 136, 0.2);
  border-radius: 8px;
}
</style>
