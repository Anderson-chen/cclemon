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
        <q-select
          v-model="searchSelectCustomer"
          use-input
          clearable
          :options="searchSelectOptions"
          option-value="id"
          :option-label="(opt) => (opt ? `${opt.name} (${opt.phone})` : '')"
          label="搜尋會員姓名 / 電話"
          outlined
          dense
          bg-color="grey-1"
          @filter="filterSearchCustomers"
          @update:model-value="onSearchCustomerSelected"
          @clear="() => { filters.keyword = ''; search(); }"
          :loading="searchLookingUp"
          hide-dropdown-icon
          behavior="menu"
          debounce="300"
        >
          <template v-slot:prepend>
            <q-icon name="search" color="teal-8" />
          </template>
          <template v-slot:no-option>
            <q-item>
              <q-item-section class="text-grey-5 text-caption">查無符合會員</q-item-section>
            </q-item>
          </template>
          <template v-slot:option="scope">
            <q-item v-bind="(scope.itemProps as Record<string, unknown>)">
              <q-item-section avatar>
                <q-avatar color="teal-1" text-color="teal-8" icon="person" size="32px" />
              </q-item-section>
              <q-item-section>
                <q-item-label class="text-weight-bold text-grey-9">{{ (scope.opt as any).name }}</q-item-label>
                <q-item-label caption class="text-grey-6">{{ (scope.opt as any).phone }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
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
      @updated="formDialog.editCustomer ? onCustomerUpdatedFromDetail($event) : loadCustomers()"
    />

    <!-- 會員詳情 Dialog -->
    <q-dialog
      v-model="detailDialog.open"
      :position="$q.screen.lt.md ? 'bottom' : 'standard'"
    >
      <q-card
        v-if="detailDialog.customer"
        class="cust-detail-card"
        :style="$q.screen.lt.md ? 'width: 100vw; max-height: 90dvh; border-radius: 16px 16px 0 0;' : 'width: 500px; max-width: 95vw;'"
      >
        <!-- Sticky Header -->
        <q-toolbar class="bg-teal-8 text-white cust-detail-toolbar">
          <q-icon name="person" size="sm" class="q-mr-sm" />
          <q-toolbar-title>{{ detailDialog.customer.name }}</q-toolbar-title>
          <q-chip 
            dense 
            color="teal-6" 
            text-color="white" 
            icon="call" 
            class="q-mr-sm cursor-pointer"
            clickable
            tag="a"
            :href="'tel:' + detailDialog.customer.phone"
          >
            {{ detailDialog.customer.phone }}
          </q-chip>
        </q-toolbar>

        <!-- Scrollable Body -->
        <div class="cust-detail-body">
          <q-card-section class="q-pa-md">
            <div class="row q-col-gutter-md">
              <!-- 基本資料 (改為全寬) -->
              <div class="col-12">
                <q-card bordered flat>
                  <q-card-section class="card-header-accent">
                    <div class="row items-center">
                      <q-icon name="badge" color="teal-8" size="sm" class="q-mr-sm" />
                      <span class="text-subtitle1 text-weight-medium">基本資料</span>
                    </div>
                  </q-card-section>
                  <q-card-section class="q-pa-sm">
                    <q-list dense>
                      <q-item>
                        <q-item-section avatar><q-icon name="person" color="teal-7" /></q-item-section>
                        <q-item-section>
                          <q-item-label caption>姓名</q-item-label>
                          <q-item-label>{{ detailDialog.customer.name }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section avatar><q-icon name="phone" color="teal-7" /></q-item-section>
                        <q-item-section>
                          <q-item-label caption>手機號碼</q-item-label>
                          <div class="row items-center no-wrap">
                            <template v-if="detailDialog.customer.phone">
                              <a :href="'tel:' + detailDialog.customer.phone" class="phone-link text-weight-medium text-teal-9">
                                {{ detailDialog.customer.phone }}
                              </a>
                              <div class="q-ml-sm row items-center no-wrap q-gutter-xs">
                                <q-btn flat round dense color="teal-7" icon="call" tag="a" :href="'tel:' + detailDialog.customer.phone" size="sm" class="cursor-pointer">
                                  <q-tooltip>立即撥打</q-tooltip>
                                </q-btn>
                                <q-btn flat round dense color="grey-6" icon="content_copy" size="xs" @click="copyText(detailDialog.customer.phone)" class="cursor-pointer">
                                  <q-tooltip>複製號碼</q-tooltip>
                                </q-btn>
                              </div>
                            </template>
                            <span v-else class="text-grey-5">無電話資訊</span>
                          </div>
                        </q-item-section>
                      </q-item>
                      <q-item v-if="detailDialog.customer.email">
                        <q-item-section avatar><q-icon name="mail" color="teal-7" /></q-item-section>
                        <q-item-section>
                          <q-item-label caption>電子郵件</q-item-label>
                          <q-item-label>{{ detailDialog.customer.email }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item v-if="detailDialog.customer.note">
                        <q-item-section avatar><q-icon name="notes" color="teal-7" /></q-item-section>
                        <q-item-section>
                          <q-item-label caption>備註</q-item-label>
                          <q-item-label>{{ detailDialog.customer.note }}</q-item-label>
                        </q-item-section>
                      </q-item>
                      <q-item>
                        <q-item-section avatar><q-icon name="calendar_today" color="teal-7" /></q-item-section>
                        <q-item-section>
                          <q-item-label caption>建立日期</q-item-label>
                          <q-item-label>{{ formatDate(detailDialog.customer.createTime) }}</q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-list>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>
        </div>

        <div class="cust-detail-footer">
          <q-btn flat color="grey-7" icon="close" label="關閉" v-close-popup class="cursor-pointer" padding="6px 16px" />
          <q-btn unelevated rounded color="teal-8" icon="edit" label="編輯資料" @click="openEditFromDetail" class="cursor-pointer" padding="6px 20px" />
        </div>
      </q-card>
    </q-dialog>


  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { useQuasar, copyToClipboard } from 'quasar';
import CustomerFormDialog from '../components/CustomerFormDialog.vue';
import {
  listCustomers,
  searchCustomers,
} from '../api/customer/customer';
import type {
  CustomerResult,
} from '../api/customer/types';

const $q = useQuasar();

// ── 篩選 ──────────────────────────────────────────────
const filters = reactive({
  keyword: '',
});

// ── 搜尋列 autocomplete ────────────────────────────────
const searchSelectCustomer = ref<CustomerResult | null>(null);
const searchSelectOptions = ref<CustomerResult[]>([]);
const searchLookingUp = ref(false);

const filterSearchCustomers = async (
  val: string,
  update: (callback: () => void) => void,
  abort: () => void,
) => {
  if (val.length < 1) {
    abort();
    return;
  }
  try {
    searchLookingUp.value = true;
    const results = await searchCustomers(val);
    update(() => { searchSelectOptions.value = results; });
  } catch {
    update(() => { searchSelectOptions.value = []; });
  } finally {
    searchLookingUp.value = false;
  }
};

const onSearchCustomerSelected = (opt: CustomerResult | null) => {
  filters.keyword = opt ? opt.name : '';
  search();
};

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
});

const openDetailDialog = (customer: CustomerResult) => {
  detailDialog.customer = customer;
  detailDialog.open = true;
};

const openEditFromDetail = () => {
  if (!detailDialog.customer) return;
  openEditDialog(detailDialog.customer);
};

const onCustomerUpdatedFromDetail = (updated: CustomerResult) => {
  detailDialog.customer = updated;
  loadCustomers();
};



// ── 輔助函式 ───────────────────────────────────────────


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

/* 會員詳情 Dialog */
.cust-detail-card {
  display: flex;
  flex-direction: column;
}

.cust-detail-toolbar {
  flex-shrink: 0;
  z-index: 10;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
}

.cust-detail-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.cust-detail-footer {
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

/* 會員列表項目 */
.customer-item {
  padding: 12px 16px;
  border-left: 3px solid transparent;
  transition: background-color 0.15s;
}

.customer-item--default {
  border-left-color: #0f766e;
}

.phone-link {
  text-decoration: none;
  border-bottom: 1px dashed transparent;
  transition: all 0.2s;
}

.phone-link:hover {
  color: #0f766e;
  border-bottom-color: #0f766e;
}

.customer-item--default:hover {
  background-color: rgba(15, 118, 110, 0.04);
}

.customer-item:hover {
  background-color: rgba(15, 118, 110, 0.04);
}

.card-header-accent {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}
</style>
