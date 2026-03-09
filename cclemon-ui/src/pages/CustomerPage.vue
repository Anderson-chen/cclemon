<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center q-mb-md">
      <q-icon name="group" size="md" color="teal-8" class="q-mr-sm" />
      <h5 class="q-mt-none q-mb-none">會員管理中心</h5>
      <q-space />
      <q-btn
        unelevated
        color="teal-8"
        icon="person_add"
        :label="$q.screen.gt.xs ? '新增會員' : ''"
        @click="openCreateDialog"
        class="cursor-pointer"
      />
    </div>

    <!-- 搜尋與篩選 -->
    <q-card class="q-mb-md">
      <q-card-section class="card-header-accent">
        <div class="row items-center">
          <q-icon name="search" color="teal-8" size="sm" class="q-mr-sm" />
          <span class="text-h6">搜尋會員</span>
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row q-gutter-md wrap">
          <q-input
            v-model="filters.keyword"
            label="姓名 / 電話搜尋"
            outlined
            dense
            clearable
            style="flex: 1; min-width: 200px"
            @keyup.enter="loadCustomers"
          >
            <template v-slot:prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <q-select
            v-model="filters.tierCode"
            :options="tierOptions"
            label="會員等級"
            outlined
            dense
            clearable
            emit-value
            map-options
            style="min-width: 150px"
          />

          <q-btn
            unelevated
            color="teal-8"
            icon="search"
            label="查詢"
            @click="loadCustomers"
            :loading="loading"
            class="cursor-pointer"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 統計卡片 -->
    <div class="row q-gutter-md q-mb-md">
      <q-card class="stat-card col">
        <q-card-section class="text-center">
          <q-icon name="group" size="2em" color="teal-8" />
          <div class="text-h4 text-weight-bold text-teal-8 q-mt-xs">{{ pagination.totalElements }}</div>
          <div class="text-caption text-grey-6">總會員數</div>
        </q-card-section>
      </q-card>
      <q-card class="stat-card col">
        <q-card-section class="text-center">
          <q-icon name="workspace_premium" size="2em" color="amber-7" />
          <div class="text-h4 text-weight-bold text-amber-7 q-mt-xs">{{ goldCount }}</div>
          <div class="text-caption text-grey-6">金卡會員</div>
        </q-card-section>
      </q-card>
      <q-card class="stat-card col">
        <q-card-section class="text-center">
          <q-icon name="grade" size="2em" color="blue-grey-5" />
          <div class="text-h4 text-weight-bold text-blue-grey-5 q-mt-xs">{{ silverCount }}</div>
          <div class="text-caption text-grey-6">銀卡會員</div>
        </q-card-section>
      </q-card>
    </div>

    <!-- 會員列表 -->
    <q-card>
      <q-card-section class="card-header-accent">
        <div class="row items-center justify-between">
          <div class="row items-center">
            <q-icon name="list" color="teal-8" size="sm" class="q-mr-sm" />
            <span class="text-h6">會員列表</span>
          </div>
          <q-chip
            v-if="pagination.totalElements > 0"
            color="teal-1"
            text-color="teal-9"
            size="sm"
            dense
          >
            共 {{ pagination.totalElements }} 筆
          </q-chip>
        </div>
      </q-card-section>

      <!-- 載入中 -->
      <q-card-section v-if="loading" class="text-center q-py-xl">
        <q-spinner color="teal-8" size="3em" />
        <div class="text-caption text-grey-6 q-mt-sm">載入中...</div>
      </q-card-section>

      <!-- 無資料 -->
      <q-card-section v-else-if="customers.length === 0" class="text-center q-py-xl">
        <q-icon name="person_off" size="4em" color="grey-4" />
        <div class="text-subtitle1 text-grey-5 q-mt-sm">尚無會員資料</div>
        <div class="text-caption text-grey-4">點擊右上角「新增會員」開始建立</div>
      </q-card-section>

      <!-- 列表 -->
      <q-list v-else separator>
        <q-item
          v-for="customer in customers"
          :key="customer.id"
          class="customer-item cursor-pointer"
          clickable
          @click="openDetailDialog(customer)"
        >
          <q-item-section avatar>
            <q-avatar
              :color="tierColor(customer.tierCode)"
              text-color="white"
              size="44px"
            >
              {{ customer.name.charAt(0) }}
            </q-avatar>
          </q-item-section>

          <q-item-section>
            <q-item-label class="text-weight-medium row items-center q-gutter-xs">
              <span>{{ customer.name }}</span>
              <q-badge
                :color="tierColor(customer.tierCode)"
                :label="tierLabel(customer.tierCode)"
                class="q-ml-xs"
              />
            </q-item-label>
            <q-item-label caption>
              <q-icon name="phone" size="xs" class="q-mr-xs" />{{ customer.phone }}
              <span v-if="customer.email" class="q-ml-md">
                <q-icon name="mail" size="xs" class="q-mr-xs" />{{ customer.email }}
              </span>
            </q-item-label>
            <q-item-label caption v-if="customer.note" class="text-grey-6 q-mt-xs">
              <q-icon name="notes" size="xs" class="q-mr-xs" />{{ customer.note }}
            </q-item-label>
          </q-item-section>

          <q-item-section side>
            <div class="text-right">
              <div class="text-weight-bold text-teal-8">
                NT$ {{ customer.totalSpend.toLocaleString() }}
              </div>
              <div class="text-caption text-grey-5">累積消費</div>
              <div class="text-caption text-grey-6 q-mt-xs">
                到店 {{ customer.visitCount }} 次
              </div>
            </div>
          </q-item-section>

          <q-item-section side>
            <q-btn
              flat
              round
              dense
              icon="edit"
              color="teal-6"
              @click.stop="openEditDialog(customer)"
              class="cursor-pointer"
            >
              <q-tooltip>編輯會員資料</q-tooltip>
            </q-btn>
          </q-item-section>
        </q-item>
      </q-list>

      <!-- 分頁 -->
      <q-card-section v-if="pagination.totalPages > 1">
        <div class="row justify-center">
          <q-pagination
            v-model="pagination.page"
            :max="pagination.totalPages"
            @update:model-value="loadCustomers"
            direction-links
            boundary-links
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 新增 / 編輯 Dialog -->
    <q-dialog v-model="formDialog.open" persistent>
      <q-card style="width: 95vw; max-width: 560px">
        <q-card-section class="card-header-accent">
          <div class="row items-center">
            <q-icon
              :name="formDialog.isEdit ? 'edit' : 'person_add'"
              color="teal-8"
              size="sm"
              class="q-mr-sm"
            />
            <span class="text-h6">{{ formDialog.isEdit ? '編輯會員資料' : '新增會員' }}</span>
          </div>
        </q-card-section>
        <q-card-section>
          <q-form ref="formRef" @submit="submitForm" class="q-gutter-md">
            <q-input
              v-model="form.name"
              label="姓名"
              outlined
              dense
              :rules="[(val) => !!val || '請輸入姓名']"
            >
              <template v-slot:prepend>
                <q-icon name="person" />
              </template>
            </q-input>

            <q-input
              v-model="form.phone"
              label="手機號碼"
              outlined
              dense
              :rules="[
                (val) => !!val || '請輸入手機號碼',
                (val) => /^[0-9]{8,15}$/.test(val) || '請輸入有效的手機號碼',
              ]"
            >
              <template v-slot:prepend>
                <q-icon name="phone" />
              </template>
            </q-input>

            <q-input
              v-model="form.email"
              label="電子郵件 (選填)"
              outlined
              dense
              type="email"
              :rules="[
                (val) => !val || /.+@.+\..+/.test(val) || '請輸入有效的電子郵件',
              ]"
            >
              <template v-slot:prepend>
                <q-icon name="mail" />
              </template>
            </q-input>

            <q-input
              v-model="form.note"
              label="特殊偏好 / 注意事項 (選填)"
              outlined
              dense
              type="textarea"
              rows="3"
            >
              <template v-slot:prepend>
                <q-icon name="notes" />
              </template>
            </q-input>
          </q-form>
        </q-card-section>
        <q-card-actions align="right" class="q-px-md q-pb-md">
          <q-btn flat label="取消" color="grey-7" v-close-popup class="cursor-pointer" />
          <q-btn
            unelevated
            :label="formDialog.isEdit ? '儲存' : '建立'"
            color="teal-8"
            @click="submitForm"
            :loading="submitting"
            class="cursor-pointer"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 會員詳情 Dialog -->
    <q-dialog v-model="detailDialog.open" maximized>
      <q-card v-if="detailDialog.customer">
        <q-toolbar class="bg-teal-8 text-white">
          <q-icon name="person" size="sm" class="q-mr-sm" />
          <q-toolbar-title>{{ detailDialog.customer.name }} 的會員資料</q-toolbar-title>
          <q-btn flat round dense icon="edit" @click="openEditFromDetail" class="cursor-pointer">
            <q-tooltip>編輯資料</q-tooltip>
          </q-btn>
          <q-btn flat round dense icon="close" v-close-popup class="cursor-pointer" />
        </q-toolbar>

        <q-card-section class="q-pa-md">
          <div class="row q-gutter-md">
            <!-- 基本資料 -->
            <div class="col-12 col-md-4">
              <q-card bordered flat>
                <q-card-section class="card-header-accent">
                  <div class="row items-center">
                    <q-icon name="badge" color="teal-8" size="sm" class="q-mr-sm" />
                    <span class="text-subtitle1 text-weight-medium">基本資料</span>
                  </div>
                </q-card-section>
                <q-card-section>
                  <!-- 會員等級 -->
                  <div class="text-center q-mb-md">
                    <q-avatar
                      :color="tierColor(detailDialog.customer.tierCode)"
                      text-color="white"
                      size="64px"
                      class="text-h5"
                    >
                      {{ detailDialog.customer.name.charAt(0) }}
                    </q-avatar>
                    <div class="q-mt-sm">
                      <q-badge
                        :color="tierColor(detailDialog.customer.tierCode)"
                        :label="tierLabel(detailDialog.customer.tierCode)"
                        class="text-caption"
                      />
                    </div>
                  </div>

                  <q-list dense>
                    <q-item>
                      <q-item-section avatar>
                        <q-icon name="person" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>姓名</q-item-label>
                        <q-item-label>{{ detailDialog.customer.name }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section avatar>
                        <q-icon name="phone" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>手機號碼</q-item-label>
                        <q-item-label>{{ detailDialog.customer.phone }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="detailDialog.customer.email">
                      <q-item-section avatar>
                        <q-icon name="mail" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>電子郵件</q-item-label>
                        <q-item-label>{{ detailDialog.customer.email }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="detailDialog.customer.note">
                      <q-item-section avatar>
                        <q-icon name="notes" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>備註</q-item-label>
                        <q-item-label>{{ detailDialog.customer.note }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section avatar>
                        <q-icon name="calendar_today" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>建立日期</q-item-label>
                        <q-item-label>{{ formatDate(detailDialog.customer.createTime) }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </q-card-section>
              </q-card>

              <!-- 消費摘要 -->
              <q-card bordered flat class="q-mt-md">
                <q-card-section class="card-header-accent">
                  <div class="row items-center">
                    <q-icon name="payments" color="teal-8" size="sm" class="q-mr-sm" />
                    <span class="text-subtitle1 text-weight-medium">消費摘要</span>
                  </div>
                </q-card-section>
                <q-card-section>
                  <div class="row q-gutter-sm">
                    <div class="col stat-mini-card text-center q-pa-sm">
                      <div class="text-h6 text-weight-bold text-teal-8">
                        NT$ {{ detailDialog.customer.totalSpend.toLocaleString() }}
                      </div>
                      <div class="text-caption text-grey-6">累積消費</div>
                    </div>
                    <div class="col stat-mini-card text-center q-pa-sm">
                      <div class="text-h6 text-weight-bold text-teal-8">
                        {{ detailDialog.customer.visitCount }}
                      </div>
                      <div class="text-caption text-grey-6">到店次數</div>
                    </div>
                  </div>

                  <!-- 升級進度條 -->
                  <div class="q-mt-md" v-if="detailDialog.customer.tierCode !== 'GOLD'">
                    <div class="row items-center justify-between q-mb-xs">
                      <span class="text-caption text-grey-6">升級進度</span>
                      <span class="text-caption text-teal-7">
                        距 {{ nextTierLabel(detailDialog.customer.tierCode) }} 還差
                        NT$ {{ nextTierGap(detailDialog.customer).toLocaleString() }}
                      </span>
                    </div>
                    <q-linear-progress
                      :value="tierProgress(detailDialog.customer)"
                      color="teal-6"
                      track-color="teal-1"
                      rounded
                      size="8px"
                    />
                  </div>
                  <div class="q-mt-md text-center" v-else>
                    <q-icon name="workspace_premium" color="amber-7" size="2em" />
                    <div class="text-caption text-amber-7 text-weight-medium">金卡頂級會員</div>
                  </div>
                </q-card-section>
              </q-card>
            </div>

            <!-- 歷史訂單 -->
            <div class="col">
              <q-card bordered flat>
                <q-card-section class="card-header-accent">
                  <div class="row items-center">
                    <q-icon name="receipt_long" color="teal-8" size="sm" class="q-mr-sm" />
                    <span class="text-subtitle1 text-weight-medium">歷史訂單</span>
                  </div>
                </q-card-section>

                <q-card-section v-if="detailDialog.loadingOrders" class="text-center q-py-lg">
                  <q-spinner color="teal-8" size="2em" />
                </q-card-section>

                <q-card-section
                  v-else-if="detailDialog.orders.length === 0"
                  class="text-center q-py-lg"
                >
                  <q-icon name="inbox" size="3em" color="grey-4" />
                  <div class="text-subtitle2 text-grey-5 q-mt-sm">尚無訂單記錄</div>
                </q-card-section>

                <q-list v-else separator>
                  <q-item
                    v-for="order in detailDialog.orders"
                    :key="order.id"
                    class="order-item"
                  >
                    <q-item-section avatar>
                      <q-avatar
                        :color="statusColor(order.status)"
                        text-color="white"
                        size="36px"
                        icon="directions_run"
                      />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="row items-center q-gutter-xs">
                        <span class="text-weight-medium">{{ order.orderNo }}</span>
                        <q-badge
                          v-if="order.isUrgent"
                          color="red-6"
                          label="急件"
                          class="q-ml-xs"
                        />
                      </q-item-label>
                      <q-item-label caption>
                        <q-badge
                          :color="statusColor(order.status)"
                          :label="statusLabel(order.status)"
                          outline
                        />
                        <span class="q-ml-sm text-grey-5">
                          建立: {{ formatDate(order.createTime) }}
                        </span>
                      </q-item-label>
                      <q-item-label caption class="q-mt-xs">
                        預計取件: {{ order.estimatedPickupDate }}
                        <span v-if="order.actualPickupDate" class="q-ml-sm">
                          / 實際: {{ order.actualPickupDate }}
                        </span>
                      </q-item-label>
                    </q-item-section>
                    <q-item-section side>
                      <div class="text-weight-bold text-teal-8">
                        NT$ {{ order.totalAmount.toLocaleString() }}
                      </div>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-card>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import {
  listCustomers,
  createCustomer,
  updateCustomer,
  getCustomerOrders,
} from '../api/customer/customer';
import type {
  CustomerResult,
  CustomerOrderSummary,
  MemberTierCode,
} from '../api/customer/types';

const $q = useQuasar();

// ── 篩選 ──────────────────────────────────────────────
const filters = reactive({
  keyword: '',
  tierCode: null as MemberTierCode | null,
});

const tierOptions = [
  { label: '一般會員', value: 'STANDARD' },
  { label: '銀卡會員', value: 'SILVER' },
  { label: '金卡會員', value: 'GOLD' },
];

// ── 列表 ──────────────────────────────────────────────
const customers = ref<CustomerResult[]>([]);
const pagination = reactive({ page: 1, size: 20, totalPages: 0, totalElements: 0 });
const loading = ref(false);

const goldCount = computed(
  () => customers.value.filter((c) => c.tierCode === 'GOLD').length
);
const silverCount = computed(
  () => customers.value.filter((c) => c.tierCode === 'SILVER').length
);

const loadCustomers = async () => {
  try {
    loading.value = true;
    const res = await listCustomers({
      keyword: filters.keyword || undefined,
      tierCode: filters.tierCode || undefined,
      page: pagination.page - 1,
      size: pagination.size,
    });
    customers.value = res.content;
    pagination.totalPages = res.totalPages;
    pagination.totalElements = res.totalElements;
  } catch {
    $q.notify({ type: 'negative', message: '載入會員資料失敗，請稍後再試' });
  } finally {
    loading.value = false;
  }
};

// ── 新增 / 編輯 Dialog ─────────────────────────────────
const formRef = ref();
const submitting = ref(false);

const formDialog = reactive({
  open: false,
  isEdit: false,
  editId: null as number | null,
});

const form = reactive({
  name: '',
  phone: '',
  email: '',
  note: '',
});

const resetForm = () => {
  form.name = '';
  form.phone = '';
  form.email = '';
  form.note = '';
};

const openCreateDialog = () => {
  resetForm();
  formDialog.isEdit = false;
  formDialog.editId = null;
  formDialog.open = true;
};

const openEditDialog = (customer: CustomerResult) => {
  form.name = customer.name;
  form.phone = customer.phone;
  form.email = customer.email ?? '';
  form.note = customer.note ?? '';
  formDialog.isEdit = true;
  formDialog.editId = customer.id;
  formDialog.open = true;
};

const submitForm = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  try {
    submitting.value = true;
    const payload = {
      name: form.name,
      phone: form.phone,
      email: form.email || undefined,
      note: form.note || undefined,
    };

    if (formDialog.isEdit && formDialog.editId) {
      await updateCustomer(formDialog.editId, payload);
      $q.notify({ type: 'positive', message: '會員資料已更新！' });
    } else {
      await createCustomer(payload);
      $q.notify({ type: 'positive', message: '會員建立成功！' });
    }

    formDialog.open = false;
    await loadCustomers();
  } catch {
    $q.notify({
      type: 'negative',
      message: formDialog.isEdit ? '更新失敗，請稍後再試' : '建立失敗，電話號碼可能已存在',
    });
  } finally {
    submitting.value = false;
  }
};

// ── 詳情 Dialog ────────────────────────────────────────
const detailDialog = reactive({
  open: false,
  customer: null as CustomerResult | null,
  orders: [] as CustomerOrderSummary[],
  loadingOrders: false,
});

const openDetailDialog = async (customer: CustomerResult) => {
  detailDialog.customer = customer;
  detailDialog.orders = [];
  detailDialog.open = true;
  detailDialog.loadingOrders = true;
  try {
    detailDialog.orders = await getCustomerOrders(customer.id);
  } catch {
    $q.notify({ type: 'negative', message: '載入歷史訂單失敗' });
  } finally {
    detailDialog.loadingOrders = false;
  }
};

const openEditFromDetail = () => {
  if (!detailDialog.customer) return;
  detailDialog.open = false;
  openEditDialog(detailDialog.customer);
};

// ── 輔助函式 ───────────────────────────────────────────
const TIER_THRESHOLDS = { STANDARD: 0, SILVER: 3000, GOLD: 10000 };

function tierColor(tier: MemberTierCode): string {
  return { STANDARD: 'teal-6', SILVER: 'blue-grey-5', GOLD: 'amber-7' }[tier];
}

function tierLabel(tier: MemberTierCode): string {
  return { STANDARD: '一般', SILVER: '銀卡', GOLD: '金卡' }[tier];
}

function nextTierLabel(tier: MemberTierCode): string {
  return tier === 'STANDARD' ? '銀卡' : '金卡';
}

function nextTierGap(customer: CustomerResult): number {
  const next = customer.tierCode === 'STANDARD' ? TIER_THRESHOLDS.SILVER : TIER_THRESHOLDS.GOLD;
  return Math.max(0, next - customer.totalSpend);
}

function tierProgress(customer: CustomerResult): number {
  if (customer.tierCode === 'STANDARD') {
    return Math.min(customer.totalSpend / TIER_THRESHOLDS.SILVER, 1);
  }
  const base = TIER_THRESHOLDS.SILVER;
  const top = TIER_THRESHOLDS.GOLD;
  return Math.min((customer.totalSpend - base) / (top - base), 1);
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

onMounted(loadCustomers);
</script>

<style scoped>
.customer-item {
  min-height: 72px;
  transition: background-color 0.2s;
}

.customer-item:hover {
  background-color: rgba(0, 150, 136, 0.04);
}

.order-item {
  transition: background-color 0.15s;
}

.order-item:hover {
  background-color: rgba(0, 150, 136, 0.03);
}

.card-header-accent {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}

.stat-card {
  min-width: 100px;
  border-top: 3px solid #00695c;
}

.stat-mini-card {
  border: 1px solid rgba(0, 150, 136, 0.2);
  border-radius: 8px;
}
</style>
