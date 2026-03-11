<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="receipt_long" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">訂單管理</h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 訂單列表</div>
        </div>
      </div>
      <q-btn
        unelevated
        color="teal-8"
        icon="add_circle"
        :label="$q.screen.gt.xs ? '新增訂單' : ''"
        @click="openCreateDialog"
        class="cursor-pointer"
      />
    </div>

    <!-- 搜尋與篩選 -->
    <q-card class="q-mb-md">
      <q-card-section class="card-header-accent">
        <div class="row items-center">
          <q-icon name="search" color="teal-8" size="sm" class="q-mr-sm" />
          <span class="text-h6">搜尋訂單</span>
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row q-gutter-md wrap">
          <q-input
            v-model="filters.keyword"
            label="訂單編號 / 姓名 / 電話"
            outlined
            dense
            clearable
            style="flex: 1; min-width: 200px"
            @keyup.enter="loadOrders"
          >
            <template v-slot:prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <AppSelect
            v-model="filters.status"
            :options="statusOptions"
            label="狀態"
            clearable
            style="min-width: 130px"
          />

          <AppSelect
            v-model="filters.isUrgent"
            :options="urgentOptions"
            label="急件"
            clearable
            style="min-width: 100px"
          />

          <q-input
            :model-value="dateRangeLabel"
            label="預計取件日期"
            outlined
            dense
            readonly
            style="min-width: 300px"
          >
            <template v-slot:prepend>
              <q-icon name="event" />
            </template>
            <template v-slot:append>
              <q-icon
                v-if="dateRangeLabel"
                name="cancel"
                class="cursor-pointer"
                @click.stop="clearDateRange"
              />
              <q-icon name="calendar_today" class="cursor-pointer">
                <q-popup-proxy
                  ref="datePickerRef"
                  cover
                  transition-show="scale"
                  transition-hide="scale"
                  @show="rangeClickCount = 0"
                >
                  <q-date
                    :model-value="pickerValue"
                    range
                    mask="YYYY-MM-DD"
                    color="teal-8"
                    today-btn
                    @update:model-value="onDateRangeSelect"
                  />
                </q-popup-proxy>
              </q-icon>
            </template>
          </q-input>

          <AppSelect
            v-model="sortKey"
            :options="sortOptions"
            label="排序"
            prepend-icon="sort"
            style="min-width: 190px"
            @update:model-value="loadOrders"
          />

          <q-btn
            unelevated
            color="teal-8"
            icon="search"
            label="查詢"
            @click="loadOrders"
            :loading="loading"
            class="cursor-pointer"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 訂單列表 -->
    <q-card>
      <q-card-section class="card-header-accent">
        <div class="row items-center justify-between">
          <div class="row items-center">
            <q-icon name="list" color="teal-8" size="sm" class="q-mr-sm" />
            <span class="text-h6">訂單列表</span>
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
      <q-card-section v-else-if="orders.length === 0" class="text-center q-py-xl">
        <q-icon name="inbox" size="4em" color="grey-4" />
        <div class="text-subtitle1 text-grey-5 q-mt-sm">尚無訂單資料</div>
        <div class="text-caption text-grey-4">點擊右上角「新增訂單」開始開單</div>
      </q-card-section>

      <!-- 列表 (Table) -->
      <q-table
        v-else
        class="order-table no-shadow"
        :rows="orders"
        :columns="columns"
        row-key="id"
        :loading="loading"
        :pagination="tablePagination"
        @request="onRequest"
        :grid="$q.screen.lt.md"
        card-class="bg-grey-1"
        flat
        bordered
        dense
      >
        <!-- 狀態欄位 -->
        <template v-slot:body-cell-status="props">
          <q-td :props="props">
            <q-badge v-if="props.row.isUrgent" color="red-6" label="急件" class="q-mr-xs" style="font-size: 13px; padding: 4px 8px;" />
            <q-badge
              :color="statusColor(props.value)"
              :label="statusLabel(props.value)"
              outline
              style="font-size: 13px; padding: 4px 8px;"
            />
          </q-td>
        </template>

        <!-- 顧客欄位 -->
        <template v-slot:body-cell-customer="props">
          <q-td :props="props">
            <div class="text-weight-medium">{{ props.row.customerName }}</div>
            <div class="text-caption text-grey-6">{{ props.row.customerPhone }}</div>
          </q-td>
        </template>

        <!-- 服務項目欄位 -->
        <template v-slot:body-cell-items="props">
          <q-td :props="props">
            <div class="row q-gutter-xs items-center" style="max-width: 250px; flex-wrap: wrap;">
              <template v-for="(item, index) in props.value" :key="index">
                <q-chip v-if="Number(index) < 2" outline color="teal-6">
                  {{ item.serviceName }}
                </q-chip>
              </template>
              <q-chip v-if="props.value.length > 2" color="grey-3" text-color="grey-8">
                +{{ props.value.length - 2 }}
                <q-tooltip>{{ props.value.map((i: OrderItem) => i.serviceName).join('、') }}</q-tooltip>
              </q-chip>
            </div>
          </q-td>
        </template>

        <!-- 總金額欄位 -->
        <template v-slot:body-cell-amount="props">
          <q-td :props="props" class="text-right">
            <div class="text-weight-bold text-teal-8">
              NT$ {{ props.value.toLocaleString() }}
            </div>
            <div v-if="Number(props.row.urgentFee) > 0" class="text-caption text-red-6">
              含急件費
              <q-tooltip>加急處理費用：NT$ {{ props.row.urgentFee.toLocaleString() }}</q-tooltip>
            </div>
          </q-td>
        </template>

        <!-- 日期欄位 -->
        <template v-slot:body-cell-dates="props">
          <q-td :props="props">
            <div class="row items-center no-wrap">
              <q-icon name="event_available" color="teal-7" size="16px" class="q-mr-xs" />
              <span
                class="text-weight-bold"
                :class="props.row.estimatedPickupDate ? 'text-teal-8' : 'text-grey-4'"
                style="font-size: 0.92rem;"
              >
                {{ props.row.estimatedPickupDate || '尚未安排' }}
              </span>
            </div>
          </q-td>
        </template>

        <!-- 操作欄位 -->
        <template v-slot:body-cell-actions="props">
          <q-td :props="props" class="text-right">
            <q-btn
              flat
              round
              dense
              icon="visibility"
              color="teal-8"
              @click.stop="openDetailDialog(props.row)"
              class="cursor-pointer"
            >
              <q-tooltip>檢視詳情</q-tooltip>
            </q-btn>
            <q-btn
              flat
              round
              dense
              icon="edit"
              color="teal-6"
              @click.stop="openEditDialog(props.row)"
              :disable="props.row.status === 'PICKED_UP' || props.row.status === 'CANCELLED'"
              class="cursor-pointer"
            >
              <q-tooltip>編輯訂單</q-tooltip>
            </q-btn>
          </q-td>
        </template>

        <!-- 手機版 Grid (卡片) 視圖 -->
        <template v-slot:item="props">
          <div class="q-pa-xs col-12 col-sm-6 col-md-4">
            <q-card bordered flat class="cursor-pointer order-grid-card" @click="openDetailDialog(props.row)">
              <q-card-section class="q-pb-xs flex justify-between items-center">
                <div class="text-subtitle2 font-mono">{{ props.row.orderNo }}</div>
                <div>
                  <q-badge v-if="props.row.isUrgent" color="red-6" label="急件" class="q-mr-xs" style="font-size: 13px; padding: 4px 8px;" />
                  <q-badge :color="statusColor(props.row.status)" :label="statusLabel(props.row.status)" outline style="font-size: 13px; padding: 4px 8px;" />
                </div>
              </q-card-section>

              <q-card-section class="q-py-sm">
                <div class="row q-col-gutter-sm">
                  <div class="col-8">
                    <div class="text-weight-medium">{{ props.row.customerName }}</div>
                    <div class="text-caption text-grey-6">{{ props.row.customerPhone }}</div>
                    
                    <div class="row q-gutter-xs items-center q-mt-sm">
                      <q-chip v-for="(item, idx) in props.row.items.slice(0, 2)" :key="idx" outline color="teal-6">
                        {{ item.serviceName }}
                      </q-chip>
                      <span v-if="Number(props.row.items.length) > 2" class="text-caption text-grey-6">+{{ props.row.items.length - 2 }} 項</span>
                    </div>
                  </div>
                  
                  <div class="col-4 text-right">
                    <div class="text-weight-bold text-teal-8">NT$ {{ props.row.totalAmount.toLocaleString() }}</div>
                    <div class="text-caption text-grey-6 q-mt-xs">預計取件：{{ props.row.estimatedPickupDate || '-' }}</div>
                  </div>
                </div>
              </q-card-section>

              <q-separator />
              
              <q-card-actions align="right" class="q-pa-sm">
                <q-btn flat dense icon="edit" color="teal-6" label="編輯" @click.stop="openEditDialog(props.row)" :disable="props.row.status === 'PICKED_UP' || props.row.status === 'CANCELLED'" />
              </q-card-actions>
            </q-card>
          </div>
        </template>

      </q-table>
    </q-card>

    <AppFormDialog
      v-model="formDialog.open"
      :title="formDialog.isEdit ? '編輯訂單' : '新增訂單'"
      :icon="formDialog.isEdit ? 'edit' : 'add_circle'"
      :is-edit="formDialog.isEdit"
      :submitting="submitting"
      :submit-label="formDialog.isEdit ? '儲存' : '建立訂單'"
      max-width="720px"
      @submit="submitForm"
    >
      <q-form ref="formRef" @submit.prevent class="order-form-redesign">
        <!-- 步驟 1: 顧客資訊 -->
        <div class="form-section">
          <div class="section-title row items-center q-mb-md">
            <q-icon name="person_search" color="teal-8" size="20px" class="q-mr-sm" />
            <span>步驟 1: 選擇顧客</span>
          </div>
          
          <div class="row q-col-gutter-sm">
            <div class="col-12">
              <q-select
                v-model="selectedCustomer"
                use-input
                fill-input
                hide-selected
                clearable
                :options="customerOptions"
                option-value="id"
                :option-label="opt => opt ? `${opt.name} (${opt.phone})` : ''"
                label="搜尋現有會員（姓名或電話）"
                outlined
                dense
                @filter="filterCustomers"
                @update:model-value="onCustomerSelected"
                @input-value="val => customerSearchText = val"
                :disable="formDialog.isEdit"
                :loading="lookingUp"
                hide-dropdown-icon
                behavior="menu"
                debounce="1500"
                class="customer-select-field"
                :rules="[(val: unknown) => !!form.customerId || '請搜尋並選擇一個顧客']"
              >
                <template v-slot:prepend>
                  <q-icon name="search" color="teal-8" />
                </template>
                <template v-slot:append>
                  <q-btn
                    unelevated
                    dense
                    rounded
                    icon="person_add"
                    color="teal-8"
                    label="新會員"
                    @click.stop="customerFormOpen = true"
                    :disable="formDialog.isEdit"
                    class="q-px-sm"
                  >
                    <q-tooltip>若查無會員，點此快速建立</q-tooltip>
                  </q-btn>
                </template>
                <template v-slot:no-option>
                  <q-item>
                    <q-item-section class="text-grey-7 row items-center justify-between no-wrap" horizontal>
                      <q-icon name="info" size="xs" class="q-mr-xs" />
                      <div>查無此會員，請嘗試其他關鍵字或</div>
                      <q-btn flat dense color="teal-8" label="建立新會員" @click="customerFormOpen = true" class="q-ml-sm text-weight-bold" />
                    </q-item-section>
                  </q-item>
                </template>
                <template v-slot:selected-item="scope">
                  <div class="selected-customer-chip" v-if="scope.opt">
                    <span>{{ scope.opt.name }}</span>
                    <span class="text-caption q-ml-sm text-grey-7">({{ scope.opt.phone }})</span>
                  </div>
                </template>
              </q-select>
            </div>
          </div>
        </div>

        <q-separator class="q-my-lg" />

        <!-- 步驟 2: 服務項目 -->
        <div class="form-section">
          <div class="section-title row items-center q-mb-md">
            <q-icon name="inventory_2" color="teal-8" size="20px" class="q-mr-sm" />
            <span>步驟 2: 服務內容</span>
            <q-space />
            <q-btn
              unelevated
              rounded
              dense
              icon="add"
              color="teal-1"
              text-color="teal-9"
              label="新增項目"
              @click="addServiceItem"
              :disable="formDialog.isEdit"
              class="q-px-sm cursor-pointer"
            />
          </div>

          <div class="service-items-grid">
            <div
              v-for="(item, idx) in form.items"
              :key="idx"
              class="service-card shadow-1 q-mb-md"
              :class="{ 'is-collapsed': !item.isExpanded }"
            >
              <!-- 服務卡片標題 (可折疊) -->
              <div 
                class="service-card-header row items-center justify-between cursor-pointer"
                @click="item.isExpanded = !item.isExpanded"
              >
                <div class="row items-center">
                  <q-avatar size="24px" color="teal-8" text-color="white" class="q-mr-sm">{{ idx + 1 }}</q-avatar>
                  <div class="column">
                    <span class="text-weight-bold">服務項目 #{{ idx + 1 }}</span>
                    <div v-show="!item.isExpanded && item.serviceCode" class="text-caption text-teal-8">
                      {{ serviceMap[item.serviceCode]?.name || item.serviceCode }} x {{ item.quantity }}
                    </div>
                  </div>
                </div>
                <div class="row items-center">
                  <q-icon 
                    :name="item.isExpanded ? 'expand_less' : 'expand_more'" 
                    size="sm" 
                    color="grey-6" 
                    class="q-mr-sm"
                  />
                  <q-btn
                    flat
                    round
                    dense
                    icon="delete_outline"
                    color="red-4"
                    @click.stop="removeServiceItem(idx)"
                    :disable="form.items.length === 1 || formDialog.isEdit"
                    class="cursor-pointer"
                  >
                    <q-tooltip>移除此項目</q-tooltip>
                  </q-btn>
                </div>
              </div>

              <!-- 服務卡片內容 -->
              <q-slide-transition>
                <div v-show="item.isExpanded">
                  <div class="service-card-body q-pa-sm">
                    <div class="row q-col-gutter-sm">
                      <!-- 服務選擇 -->
                      <div class="col-12 col-sm-6">
                        <AppSelect
                          v-model="item.serviceCode"
                          :options="serviceOptions"
                          label="選擇服務"
                          :disable="formDialog.isEdit"
                          :rules="[(val) => !!val || '請選擇服務']"
                          @update:model-value="(val) => onServiceChange(idx, val as string)"
                        />
                      </div>
                      <!-- 數量 -->
                      <div class="col-12 col-sm-6">
                        <q-input
                          v-model.number="item.quantity"
                          label="數量"
                          outlined
                          dense
                          type="number"
                          :disable="formDialog.isEdit"
                          :rules="[(val) => val > 0 || '至少1']"
                        >
                          <template v-slot:prepend>
                            <q-icon name="tag" size="xs" />
                          </template>
                        </q-input>
                      </div>

                      <!-- 商品詳情 -->
                      <div class="col-12 col-sm-4">
                        <q-input v-model="item.productName" label="品牌/型號" outlined dense :disable="formDialog.isEdit">
                          <template v-slot:prepend><q-icon name="branding_watermark" size="xs" /></template>
                        </q-input>
                      </div>
                      <div class="col-12 col-sm-4">
                        <q-input v-model="item.itemStorageLocation" label="存放位置" outlined dense :disable="formDialog.isEdit">
                          <template v-slot:prepend><q-icon name="place" size="xs" /></template>
                        </q-input>
                      </div>
                      <div class="col-12 col-sm-4">
                        <q-input v-model="item.itemNote" label="物件備註" outlined dense :disable="formDialog.isEdit">
                          <template v-slot:prepend><q-icon name="sticky_note_2" size="xs" /></template>
                        </q-input>
                      </div>

                      <!-- 照片 -->
                      <div class="col-12 q-mt-sm">
                        <div class="text-caption text-grey-7 q-mb-xs">商品照片</div>
                        <div class="row q-gutter-sm items-center">
                          <div v-for="(img, imgIdx) in item.imageUrls" :key="imgIdx" class="photo-preview-wrap relative-position">
                            <q-img :src="img" style="height: 64px; width: 64px; border-radius: 8px" class="bg-grey-2 shadow-1" />
                            <q-btn round dense size="xs" color="red-5" icon="close" class="absolute-top-right btn-remove-photo" @click="removeImage(idx, imgIdx)" />
                          </div>
                          <q-file
                            v-model="tempFiles[idx]"
                            outlined
                            dense
                            multiple
                            append
                            accept="image/*"
                            class="photo-upload-mini"
                            @update:model-value="(files) => onFilesPicked(idx, files)"
                          >
                            <template v-slot:prepend>
                              <div class="column items-center justify-center full-width">
                                <q-icon name="add_a_photo" size="sm" />
                                <div class="text-caption" style="font-size: 10px">上傳</div>
                              </div>
                            </template>
                            <template v-slot:file></template>
                          </q-file>
                        </div>
                      </div>
                    </div>
                    
                    <!-- 單項小計 -->
                    <div class="item-subtotal row items-center justify-end q-mt-md q-px-sm">
                      <span class="text-grey-7 q-mr-sm">單項小計</span>
                      <span class="text-subtitle1 text-weight-bold text-teal-8">NT$ {{ (item.unitPrice * item.quantity).toLocaleString() }}</span>
                    </div>
                  </div>
                </div>
              </q-slide-transition>
            </div>
          </div>
        </div>

        <q-separator class="q-my-lg" />

        <!-- 步驟 3: 結帳與設定 -->
        <div class="form-section">
          <div class="section-title row items-center q-mb-md">
            <q-icon name="payments" color="teal-8" size="20px" class="q-mr-sm" />
            <span>步驟 3: 結帳與交期</span>
          </div>

          <div class="row q-col-gutter-lg">
            <div class="col-12 col-sm-6">
              <!-- 交期 -->
              <AppDateInput
                v-model="form.estimatedPickupDate"
                label="預計取件日期"
                prepend-icon="event_available"
                class="q-mb-md"
                :rules="[(val: string | null) => !!val || '請選擇預計取件日期']"
              />
              <!-- 急件開關 -->
              <div class="urgent-toggle-box q-pa-sm rounded-borders row items-center" :class="form.isUrgent ? 'bg-red-1' : 'bg-grey-1'">
                <q-toggle v-model="form.isUrgent" label="加急處理" color="red-6" icon="bolt" class="text-weight-bold" />
                <q-space />
                <div v-if="form.isUrgent" class="text-red-7 text-caption text-weight-bold">+50% 加急費</div>
              </div>
            </div>

            <div class="col-12 col-sm-6">
              <!-- 總體備註 -->
              <q-input
                v-model="form.note"
                label="全單備註 / 鞋物狀況總覽"
                outlined
                dense
                type="textarea"
                rows="3"
                placeholder="例如：客人的鞋盒要保留、物件有特殊汙損..."
              >
                <template v-slot:prepend>
                  <q-icon name="notes" />
                </template>
              </q-input>
            </div>
          </div>

          <!-- 價格摘要卡片 -->
          <div class="summary-card q-mt-xl q-pa-md shadow-2">
            <div class="row justify-between items-center q-mb-sm">
              <span class="text-grey-7">服務小計</span>
              <span class="text-weight-medium">NT$ {{ serviceSubtotal.toLocaleString() }}</span>
            </div>
            <div v-if="form.isUrgent" class="row justify-between items-center q-mb-sm text-red-7">
              <span class="row items-center">
                <q-icon name="bolt" size="xs" class="q-mr-xs" />
                急件費 (50%)
              </span>
              <span class="text-weight-medium">+ NT$ {{ urgentFeeAmount.toLocaleString() }}</span>
            </div>
            <q-separator class="q-my-sm" />
            <div class="row justify-between items-center">
              <span class="text-h6 text-weight-bold">應收總計</span>
              <div class="column items-end">
                <span class="text-h5 text-weight-bolder text-teal-8">NT$ {{ (serviceSubtotal + urgentFeeAmount).toLocaleString() }}</span>
                <div class="text-caption text-grey-5">共 {{ form.items.length }} 個服務項目</div>
              </div>
            </div>
          </div>
        </div>
      </q-form>
    </AppFormDialog>

    <!-- 新增會員 Dialog（從訂單表單呼叫） -->
    <CustomerFormDialog
      v-model="customerFormOpen"
      @created="onCustomerCreated"
    />

    <!-- 詳情 Dialog -->
    <OrderDetailDialog
      v-model="detailDialog.open"
      :order="detailDialog.order"
      @status-updated="onStatusUpdated"
      @edit-requested="onEditRequested"
    />
  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { useQuasar, date as quasarDate } from 'quasar';
import AppFormDialog from '../components/AppFormDialog.vue';
import AppDateInput from '../components/AppDateInput.vue';
import AppSelect from '../components/AppSelect.vue';
import CustomerFormDialog from '../components/CustomerFormDialog.vue';
import OrderDetailDialog from '../components/OrderDetailDialog.vue';
import { useOrderNavStore } from '../stores/orderNavStore';
import { listOrders, createOrder, updateOrder } from '../api/order/order';
import { searchCustomers } from '../api/customer/customer';
import { listServices } from '../api/service/service';
import type { OrderResult, OrderStatus, OrderItem } from '../api/order/types';
import type { CustomerResult } from '../api/customer/types';
import type { ServiceTypeResult } from '../api/service/types';

const $q = useQuasar();
const orderNavStore = useOrderNavStore();

// ── 篩選 ──────────────────────────────────────────────────
const filters = reactive({
  keyword: '',
  status: null as OrderStatus | null,
  isUrgent: null as boolean | null,
  dateFrom: '' as string,
  dateTo: '' as string,
  sortField: 'estimatedPickupDate',
  sortDir: 'asc' as 'asc' | 'desc',
});

const sortOptions = [
  { label: '取件日：從早到晚', value: 'estimatedPickupDate,asc' },
  { label: '取件日：從晚到早', value: 'estimatedPickupDate,desc' },
  { label: '建立時間：從早到晚', value: 'createTime,asc' },
  { label: '建立時間：從晚到早', value: 'createTime,desc' },
  { label: '訂單金額：從低到高', value: 'totalAmount,asc' },
  { label: '訂單金額：從高到低', value: 'totalAmount,desc' },
];

const sortKey = computed({
  get: () => `${filters.sortField},${filters.sortDir}`,
  set: (val: string) => {
    const [field, dir] = val.split(',');
    filters.sortField = field ?? 'estimatedPickupDate';
    filters.sortDir = (dir ?? 'asc') as 'asc' | 'desc';
  },
});

const statusOptions = [
  { label: '待處理', value: 'PENDING' },
  { label: '處理中', value: 'IN_PROGRESS' },
  { label: '待取件', value: 'READY' },
  { label: '已取件', value: 'PICKED_UP' },
  { label: '已取消', value: 'CANCELLED' },
];

const urgentOptions = [
  { label: '急件', value: true },
  { label: '一般', value: false },
];

// ── 列表 ──────────────────────────────────────────────────
const orders = ref<OrderResult[]>([]);
const pagination = reactive({
  page: 1,
  size: 15,
  totalPages: 1,
  totalElements: 0,
});

// q-table 專用的分頁格式
const tablePagination = computed(() => ({
  page: pagination.page,
  rowsPerPage: pagination.size,
  rowsNumber: pagination.totalElements,
}));

// q-table 分頁事件
const onRequest = (props: { pagination: { page: number; rowsPerPage: number } }) => {
  const { page, rowsPerPage } = props.pagination;
  pagination.page = page;
  pagination.size = rowsPerPage;
  loadOrders();
};

const columns = [
  { name: 'orderNo', label: '訂單編號', field: 'orderNo', align: 'left' as const, style: 'font-family: monospace; font-weight: 500;' },
  { name: 'status', label: '狀態', field: 'status', align: 'left' as const },
  { name: 'customer', label: '顧客', field: 'customerName', align: 'left' as const },
  { name: 'items', label: '服務項目', field: 'items', align: 'left' as const },
  { name: 'amount', label: '總金額', field: 'totalAmount', align: 'right' as const },
  { name: 'dates', label: '預計取件日', field: 'estimatedPickupDate', align: 'left' as const },
  { name: 'actions', label: '操作', field: 'actions', align: 'right' as const }
];

const datePickerRef = ref();
const rangeClickCount = ref(0);
const pickerValue = ref<{ from: string; to: string } | null>(null);

const dateRangeLabel = computed(() => {
  if (filters.dateFrom && filters.dateTo) return `${filters.dateFrom} ~ ${filters.dateTo}`;
  return '';
});

function onDateRangeSelect(val: { from: string; to: string } | string | null) {
  if (!val) return;
  rangeClickCount.value++;
  if (rangeClickCount.value < 2) {
    // 第一次點擊：不更新 pickerValue，讓 q-date 自行維護內部起始狀態
    return;
  }
  // 第二次點擊：提交並關閉
  const from = typeof val === 'string' ? val : val.from;
  const to = typeof val === 'string' ? val : val.to;
  pickerValue.value = { from, to };
  filters.dateFrom = from;
  filters.dateTo = to;
  rangeClickCount.value = 0;
  datePickerRef.value?.hide();
}

function clearDateRange() {
  filters.dateFrom = '';
  filters.dateTo = '';
  pickerValue.value = null;
  rangeClickCount.value = 0;
}

const loading = ref(false);

const loadOrders = async () => {
  try {
    loading.value = true;
    const res = await listOrders({
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      isUrgent: filters.isUrgent ?? undefined,
      dateFrom: filters.dateFrom || undefined,
      dateTo: filters.dateTo || undefined,
      sort: `${filters.sortField},${filters.sortDir}`,
      page: pagination.page - 1,
      size: pagination.size,
    });
    orders.value = res.content;
    pagination.totalPages = res.totalPages;
    pagination.totalElements = res.totalElements;
  } catch {
    $q.notify({ type: 'negative', message: '載入訂單資料失敗，請稍後再試' });
  } finally {
    loading.value = false;
  }
};

// ── 服務項目設定 ───────────────────────────────────────────
const FALLBACK_SERVICES: ServiceTypeResult[] = [
  { code: 'SVC-WASH',    name: '洗鞋', defaultPrice: 350, urgentFeeRate: null, isActive: true },
  { code: 'SVC-COATING', name: '鍍膜', defaultPrice: 500, urgentFeeRate: null, isActive: true },
  { code: 'SVC-BAG',     name: '洗包', defaultPrice: 600, urgentFeeRate: null, isActive: true },
  { code: 'SVC-RECOLOR', name: '補色', defaultPrice: 800, urgentFeeRate: null, isActive: true },
];

const activeServices = ref<ServiceTypeResult[]>([]);

const serviceOptions = computed(() =>
  activeServices.value.map((svc) => ({
    label: `${svc.name}（NT$ ${svc.defaultPrice}）`,
    value: svc.code,
  }))
);

const serviceMap = computed(() =>
  Object.fromEntries(activeServices.value.map((svc) => [svc.code, svc]))
);

const loadServices = async () => {
  try {
    const result = await listServices({ includeInactive: false });
    activeServices.value = result.length > 0 ? result : FALLBACK_SERVICES;
  } catch {
    activeServices.value = FALLBACK_SERVICES;
  }
};

// ── 新增 / 編輯 Dialog ────────────────────────────────────
const formRef = ref();
const submitting = ref(false);
const lookingUp = ref(false);
const customerNotFound = ref(false);
const customerFormOpen = ref(false);

const formDialog = reactive({
  open: false,
  isEdit: false,
  editId: null as number | null,
});



type FormItem = { serviceCode: string; quantity: number; unitPrice: number; imageUrls?: string[]; productName?: string; itemNote?: string; itemStorageLocation?: string; isExpanded?: boolean };

const form = reactive({
  customerId: null as number | null,
  customerPhone: '',
  customerName: '',
  items: [{ serviceCode: '', quantity: 1, unitPrice: 0, imageUrls: [], productName: '', itemNote: '', itemStorageLocation: '', isExpanded: true }] as FormItem[],
  isUrgent: false,
  estimatedPickupDate: '',
  note: '',
});

const serviceSubtotal = computed(() =>
  form.items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0)
);

const urgentFeeAmount = computed(() =>
  form.isUrgent ? Math.round(serviceSubtotal.value * 0.5) : 0
);

const resetForm = () => {
  form.customerId = null;
  form.customerPhone = '';
  form.customerName = '';
  form.items = [{ serviceCode: '', quantity: 1, unitPrice: 0, imageUrls: [], productName: '', itemNote: '', itemStorageLocation: '', isExpanded: true }];
  form.isUrgent = false;
  form.estimatedPickupDate = quasarDate.formatDate(new Date(Date.now() + 14 * 86400000), 'YYYY/MM/DD');
  form.note = '';
  customerNotFound.value = false;
  selectedCustomer.value = null;
  tempFiles.value = [null];
};

const selectedCustomer = ref<CustomerResult | { id: number; name: string; phone: string } | null>(null);
const customerOptions = ref<CustomerResult[]>([]);
const customerSearchText = ref('');

const filterCustomers = async (val: string, update: (callback: () => void) => void, abort: () => void) => {
  if (val.length < 1) {
    abort();
    return;
  }
  try {
    lookingUp.value = true;
    const results = await searchCustomers(val);
    update(() => {
      customerOptions.value = results;
    });
  } catch {
    update(() => {
      customerOptions.value = [];
    });
    $q.notify({ type: 'negative', message: '查詢顧客失敗' });
  } finally {
    lookingUp.value = false;
  }
};

const onCustomerSelected = (opt: CustomerResult | { id: number; name: string; phone: string } | null) => {
  if (opt) {
    form.customerId = opt.id;
    form.customerName = opt.name;
    form.customerPhone = opt.phone;
  } else {
    form.customerId = null;
    form.customerName = '';
    form.customerPhone = '';
  }
};

const onCustomerCreated = (customer: { id: number; name: string; phone: string }) => {
  form.customerId = customer.id;
  form.customerName = customer.name;
  form.customerPhone = customer.phone;
  selectedCustomer.value = customer;
  customerNotFound.value = false;
};

const addServiceItem = () => {
  // 先縮合其他項目
  form.items.forEach(i => i.isExpanded = false);
  form.items.push({ serviceCode: '', quantity: 1, unitPrice: 0, imageUrls: [], productName: '', itemNote: '', itemStorageLocation: '', isExpanded: true });
  tempFiles.value.push(null);
};

const removeServiceItem = (idx: number) => {
  if (form.items.length > 1) {
    form.items.splice(idx, 1);
    tempFiles.value.splice(idx, 1);
  }
};

const onServiceChange = (idx: number, code: string) => {
  const svc = serviceMap.value[code];
  if (svc) form.items[idx].unitPrice = svc.defaultPrice;
};

// 照片處理
const tempFiles = ref<(File | null)[]>([]);
const onFilesPicked = (itemIdx: number, files: File[]) => {
  if (!files || files.length === 0) return;
  
  if (!form.items[itemIdx].imageUrls) {
    form.items[itemIdx].imageUrls = [];
  }

  files.forEach(file => {
    const reader = new FileReader();
    reader.onload = (e) => {
      form.items[itemIdx].imageUrls?.push(e.target?.result as string);
    };
    reader.readAsDataURL(file);
  });
  
  // 清空暫存避免重複觸發或顯示標籤
  tempFiles.value[itemIdx] = null;
};

const removeImage = (itemIdx: number, imgIdx: number) => {
  form.items[itemIdx].imageUrls?.splice(imgIdx, 1);
};

const openCreateDialog = () => {
  resetForm();
  formDialog.isEdit = false;
  formDialog.editId = null;
  formDialog.open = true;
};

const openEditDialog = (order: OrderResult) => {
  form.customerId = order.customerId;
  form.customerPhone = order.customerPhone;
  form.customerName = order.customerName;
  form.items = order.items.map((i) => ({
    serviceCode: i.serviceCode,
    quantity: i.quantity,
    unitPrice: i.unitPrice,
    isExpanded: false, // 編輯時預設收合
  }));
  if (form.items.length > 0) form.items[0].isExpanded = true;
  form.isUrgent = order.isUrgent;
  form.estimatedPickupDate = order.estimatedPickupDate;
  form.note = order.note ?? '';
  formDialog.isEdit = true;
  formDialog.editId = order.id;
  formDialog.open = true;
};

const submitForm = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  if (!form.customerId) {
    $q.notify({ type: 'warning', message: '請先查詢顧客（點擊電話欄右側放大鏡）' });
    return;
  }

  try {
    submitting.value = true;
    const storageLocations: string[] = [];

    if (formDialog.isEdit && formDialog.editId) {
      await updateOrder(formDialog.editId, {
        isUrgent: form.isUrgent,
        storageLocations,
        estimatedPickupDate: form.estimatedPickupDate,
        note: form.note || undefined,
      });
      $q.notify({ type: 'positive', message: '訂單已更新！' });
    } else {
      await createOrder({
        customerId: form.customerId,
        isUrgent: form.isUrgent,
        items: form.items.map((i) => ({
          serviceCode: i.serviceCode,
          quantity: i.quantity,
          imageUrls: i.imageUrls,
          productName: i.productName || undefined,
          itemNote: i.itemNote || undefined,
          itemStorageLocation: i.itemStorageLocation || undefined,
        })),
        storageLocations,
        estimatedPickupDate: form.estimatedPickupDate,
        note: form.note || undefined,
      });
      $q.notify({ type: 'positive', message: '訂單建立成功！' });
    }

    formDialog.open = false;
    await loadOrders();
  } catch {
    $q.notify({
      type: 'negative',
      message: formDialog.isEdit ? '更新失敗，請稍後再試' : '建立失敗，請稍後再試',
    });
  } finally {
    submitting.value = false;
  }
};

// ── 詳情 Dialog ───────────────────────────────────────────
const detailDialog = reactive({
  open: false,
  order: null as OrderResult | null,
});

const openDetailDialog = (order: OrderResult) => {
  detailDialog.order = order;
  detailDialog.open = true;
};

const onStatusUpdated = async (updated: OrderResult) => {
  detailDialog.order = updated;
  await loadOrders();
};


const onEditRequested = async (order: OrderResult) => {
  detailDialog.open = false;
  
  formDialog.isEdit = true;
  formDialog.editId = order.id;

  // 嘗試找出預設顧客，以利在 select 顯示
  try {
    const custs = await searchCustomers(order.customerPhone);
    const exact = custs.find(c => c.id === order.customerId);
    selectedCustomer.value = exact || { id: order.customerId, name: order.customerName, phone: order.customerPhone };
  } catch {
    selectedCustomer.value = { id: order.customerId, name: order.customerName, phone: order.customerPhone };
  }

  form.customerId = order.customerId;
  form.customerPhone = order.customerPhone;
  form.customerName = order.customerName;

  form.isUrgent = order.isUrgent;
  form.estimatedPickupDate = order.estimatedPickupDate || '';
  form.note = order.note || '';

  form.items = order.items.map((i) => ({
    serviceCode: i.serviceCode,
    quantity: i.quantity,
    unitPrice: 0,
    isExpanded: false,
  }));
  if (form.items.length > 0) form.items[0].isExpanded = true;
  form.items.forEach((item) => {
    const svc = serviceMap.value[item.serviceCode];
    if (svc) item.unitPrice = svc.defaultPrice;
  });

  formDialog.open = true;
};


// ── 輔助函式 ──────────────────────────────────────────────
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


onMounted(async () => {
  const pendingId = orderNavStore.consumePendingOrderId();
  const pendingFilters = orderNavStore.consumeFilters();

  // 套用來自總覽的篩選條件
  if (pendingFilters) {
    if (pendingFilters.status !== undefined) filters.status = pendingFilters.status;
    if (pendingFilters.isUrgent !== undefined) filters.isUrgent = pendingFilters.isUrgent;
    if (pendingFilters.dateFrom) filters.dateFrom = pendingFilters.dateFrom;
    if (pendingFilters.dateTo) filters.dateTo = pendingFilters.dateTo;
  }

  await Promise.all([loadServices(), loadOrders()]);

  if (pendingId) {
    const target = orders.value.find((o) => o.id === pendingId);
    if (target) openDetailDialog(target);
  }
});
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

.order-table :deep(th) {
  font-weight: 600;
  font-size: 0.85rem;
  color: #455a64;
  background-color: #fcfcfc;
}

.order-table :deep(td) {
  font-size: 0.9rem;
}

.order-table :deep(tr.q-tr) td {
  padding-top: 6px;
  padding-bottom: 6px;
}

.order-grid-card {
  transition: all 0.2s;
}
.order-grid-card:hover {
  border-color: #009688;
  box-shadow: 0 1px 5px rgba(0,0,0,0.1);
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

/* 重新設計的表單樣式 */
.order-form-redesign {
  padding: 4px;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #263238;
}

.selected-customer-chip {
  display: flex;
  align-items: center;
  background: #f0fdfa;
  padding: 4px 12px;
  border-radius: 20px;
  border: 1px solid #99f6e4;
  font-weight: 600;
  color: #0f766e;
}

.service-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.service-card.is-collapsed {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.service-card.is-collapsed:hover {
  border-color: #99f6e4;
  background: #f0fdfa;
}

.service-card:hover {
  border-color: #99f6e4;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.08);
}

.service-card-header {
  background: #f8fafc;
  padding: 8px 12px;
  border-bottom: 1px solid #f1f5f9;
}

.item-subtotal {
  border-top: 1px dashed #e2e8f0;
  padding-top: 12px;
}

.photo-preview-wrap {
  width: 64px;
  height: 64px;
}

.btn-remove-photo {
  transform: translate(30%, -30%);
  z-index: 10;
}

.photo-upload-mini {
  width: 64px;
  height: 64px;
  border: 2px dashed #cbd5e1;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.photo-upload-mini:hover {
  border-color: #0d9488;
  background-color: #f0fdfa;
  color: #0d9488;
}

.photo-upload-mini :deep(.q-field__inner),
.photo-upload-mini :deep(.q-field__control) {
  height: 64px;
  min-height: 64px !important;
}

.photo-upload-mini :deep(.q-field__native),
.photo-upload-mini :deep(.q-field__append) {
  display: none !important;
}

.photo-upload-mini :deep(.q-field__prepend) {
  width: 100%;
  padding: 0;
}

.urgent-toggle-box {
  border: 1px solid transparent;
  transition: all 0.3s;
}

.summary-card {
  background: linear-gradient(to bottom right, #ffffff, #f0fdfa);
  border: 1px solid #ccfbf1;
  border-radius: 16px;
}
</style>
