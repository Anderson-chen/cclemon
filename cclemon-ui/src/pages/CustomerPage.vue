<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="group" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">會員管理中心</h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 會員資料</div>
        </div>
      </div>
      <q-btn
        unelevated
        color="teal-8"
        icon="person_add"
        @click="openCreateDialog"
        class="cursor-pointer"
      />
    </div>

    <!-- 搜尋與篩選 -->
    <q-card class="q-mb-md">
      <q-card-section>
        <div class="row q-gutter-md wrap">
          <q-input
            v-model="filters.keyword"
            label="姓名 / 電話搜尋"
            outlined
            dense
            clearable
            style="flex: 1; min-width: 200px"
            @keyup.enter="search"
          >
          </q-input>

          <q-btn
            unelevated
            color="teal-8"
            label="查詢"
            @click="search"
            :loading="loading"
            class="cursor-pointer"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 會員列表 -->
    <q-card class="section-card">

      <!-- 列表主體 -->
      <div class="list-body">
        <!-- 無資料（非載入中） -->
        <div v-if="!loading && customers.length === 0" class="list-body-center">
          <q-icon name="person_off" size="4em" color="grey-4" />
          <div class="text-subtitle1 text-grey-5 q-mt-sm">尚無會員資料</div>
          <div class="text-caption text-grey-4">點擊右上角「新增會員」開始建立</div>
        </div>

        <!-- 列表 -->
        <q-list v-if="customers.length > 0" separator>
          <q-item
            v-for="customer in customers"
            :key="customer.id"
            class="customer-item customer-item--default cursor-pointer"
            clickable
            @click="openDetailDialog(customer)"
          >
            <q-item-section>
              <q-item-label class="text-weight-bold text-grey-9 text-body2">
                {{ customer.name }}
              </q-item-label>
              <q-item-label caption class="text-grey-6">
                {{ customer.phone }}
              </q-item-label>
            </q-item-section>
          </q-item>
        </q-list>

        <!-- Loading 遮罩 -->
        <div v-if="loading" class="list-loading-overlay">
          <q-spinner color="teal-8" size="2.5em" />
        </div>
      </div>

      <!-- 分頁 -->
      <div v-if="pagination.totalPages > 1" class="row justify-center q-py-sm">
        <q-pagination
          v-model="pagination.page"
          :max="pagination.totalPages"
          :max-pages="5"
          color="teal-8"
          active-color="teal-8"
          @update:model-value="loadCustomers"
          boundary-links
          direction-links
          dense
        />
      </div>
    </q-card>

    <!-- 新增 / 編輯 Dialog -->
    <CustomerFormDialog
      v-model="formDialog.open"
      :edit-customer="formDialog.editCustomer"
      @created="loadCustomers"
      @updated="loadCustomers"
    />

    <!-- 會員詳情：手機版用底部抽屜，桌面版用居中 Dialog -->
    <q-dialog
      v-model="detailDialog.open"
      :position="$q.screen.lt.sm ? 'bottom' : 'standard'"
      :style="$q.screen.lt.sm ? '' : 'width: 700px; max-width: 95vw'"
    >
      <q-card v-if="detailDialog.customer" :class="$q.screen.lt.sm ? 'cust-detail-sheet' : ''">
        <q-toolbar class="bg-teal-8 text-white cust-detail-toolbar">
          <q-icon name="person" size="sm" class="q-mr-sm" />
          <q-toolbar-title>{{ detailDialog.customer.name }} 的會員資料</q-toolbar-title>
          <q-btn flat round dense icon="edit" @click="openEditFromDetail" class="cursor-pointer">
            <q-tooltip>編輯資料</q-tooltip>
          </q-btn>
          <q-btn flat round dense icon="close" v-close-popup class="cursor-pointer" />
        </q-toolbar>

        <div class="cust-detail-body">
          <div class="row q-gutter-md">
            <!-- 基本資料 -->
            <div class="col-12">
              <q-card bordered flat>
                <q-card-section class="card-header-accent">
                  <div class="row items-center">
                    <q-icon name="badge" color="teal-8" size="sm" class="q-mr-sm" />
                    <span class="text-subtitle1 text-weight-medium">基本資料</span>
                  </div>
                </q-card-section>
                <q-card-section>
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
            </div>

            <!-- 歷史訂單 -->
            <div class="col-12">
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
        </div>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import CustomerFormDialog from '../components/CustomerFormDialog.vue';
import {
  listCustomers,
  getCustomerOrders,
} from '../api/customer/customer';
import type {
  CustomerResult,
  CustomerOrderSummary,
} from '../api/customer/types';

const $q = useQuasar();

// ── 篩選 ──────────────────────────────────────────────
const filters = reactive({
  keyword: '',
});

// ── 列表 ──────────────────────────────────────────────
const customers = ref<CustomerResult[]>([]);
const pagination = reactive({ page: 1, size: 5, totalPages: 0, totalElements: 0 });
const loading = ref(false);

const loadCustomers = async () => {
  try {
    loading.value = true;
    const res = await listCustomers({
      keyword: filters.keyword || undefined,
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

// 查詢（重置頁碼）
const search = () => {
  pagination.page = 1;
  loadCustomers();
};

// ── 新增 / 編輯 Dialog ─────────────────────────────────
const formDialog = reactive({
  open: false,
  editCustomer: null as CustomerResult | null,
});

const openCreateDialog = () => {
  formDialog.editCustomer = null;
  formDialog.open = true;
};

const openEditDialog = (customer: CustomerResult) => {
  formDialog.editCustomer = customer;
  formDialog.open = true;
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
.title-icon-wrap {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #0f766e, #0d9488);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-card {
  border-radius: 12px;
  overflow: hidden;
}

.list-body {
  position: relative;
  min-height: 200px;
}

.list-body-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
}

.list-loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

/* 手機版底部抽屜樣式 - 與 OrderPage 篩選抽屜一致 */
.cust-detail-sheet {
  width: 100vw;
  max-height: 85dvh;
  border-radius: 16px 16px 0 0;
  display: flex;
  flex-direction: column;
}

.cust-detail-toolbar {
  position: sticky;
  top: 0;
  z-index: 20;
}

.cust-detail-body {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
}

.customer-item {
  padding: 12px 16px;
  border-left: 3px solid transparent;
  transition: background-color 0.15s;
}

.customer-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.customer-item--default {
  border-left-color: #0f766e;
}

.customer-item--default:hover {
  background-color: rgba(15, 118, 110, 0.04);
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
</style>
