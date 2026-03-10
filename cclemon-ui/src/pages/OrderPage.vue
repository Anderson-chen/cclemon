<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center q-mb-md">
      <q-icon name="receipt_long" size="md" color="teal-8" class="q-mr-sm" />
      <h5 class="q-mt-none q-mb-none">開單管理</h5>
      <q-space />
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

          <AppSelect
            v-model="filters.sortField"
            :options="sortOptions"
            label="排序欄位"
            prepend-icon="sort"
            style="min-width: 140px"
            @update:model-value="loadOrders"
          />

          <q-btn
            unelevated
            dense
            outline
            color="teal-8"
            :icon="filters.sortDir === 'desc' ? 'arrow_downward' : 'arrow_upward'"
            class="q-px-sm"
            @click="toggleSortDir"
          >
            <q-tooltip>{{ filters.sortDir === 'desc' ? '降冪排序 (從大到小/從新到舊)' : '升冪排序 (從小到大/從舊到新)' }}</q-tooltip>
          </q-btn>

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
            <div v-if="props.row.isUrgent && props.row.urgentDeadline" class="text-caption text-red-6 q-mt-xs">
              <q-icon name="bolt" size="12px" /> 截止: {{ props.row.urgentDeadline }}
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
                    <div class="text-caption text-grey-6 q-mt-xs">取: {{ props.row.estimatedPickupDate || '-' }}</div>
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

    <!-- 新增 / 編輯 Dialog -->
    <AppFormDialog
      v-model="formDialog.open"
      :title="formDialog.isEdit ? '編輯訂單' : '新增訂單'"
      :icon="formDialog.isEdit ? 'edit' : 'add_circle'"
      :is-edit="formDialog.isEdit"
      :submitting="submitting"
      :submit-label="formDialog.isEdit ? '儲存' : '建立訂單'"
      max-width="680px"
      @submit="submitForm"
    >
      <q-form ref="formRef" @submit.prevent class="q-gutter-md">
        <!-- 顧客查詢 -->
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
              label="搜尋顧客（姓名或電話）"
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
              :rules="[(val: unknown) => !!form.customerId || '請搜尋並選擇顧客']"
            >
              <template v-slot:prepend>
                <q-icon name="search" />
              </template>
              <template v-slot:append>
                <q-btn
                  flat
                  round
                  dense
                  icon="person_add"
                  color="teal-7"
                  @click.stop="customerFormOpen = true"
                  :disable="formDialog.isEdit"
                >
                  <q-tooltip>新增顧客</q-tooltip>
                </q-btn>
              </template>
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="text-grey row flex-center justify-between" horizontal>
                    <div class="q-pl-sm">查無符合條件的會員</div>
                    <q-btn flat dense color="teal-7" icon="person_add" label="立即建立" @click="customerFormOpen = true" class="q-ml-md" />
                  </q-item-section>
                </q-item>
              </template>
              <template v-slot:selected-item="scope">
                <div class="text-subtitle2" v-if="scope.opt">
                  <q-icon name="check_circle" color="teal-6" class="q-mr-xs" size="xs" />
                  {{ scope.opt.name }} ({{ scope.opt.phone }})
                </div>
              </template>
            </q-select>
          </div>
        </div>

        <!-- 服務項目 -->
        <div>
          <div class="row items-center q-mb-sm">
            <span class="text-subtitle2 text-weight-medium">服務項目</span>
            <q-space />
            <q-btn
              flat
              dense
              icon="add"
              color="teal-8"
              label="新增服務"
              size="sm"
              @click="addServiceItem"
              :disable="formDialog.isEdit"
              class="cursor-pointer"
            />
          </div>
          <div
            v-for="(item, idx) in form.items"
            :key="idx"
            class="service-item-block q-mb-sm"
          >
            <div class="row items-center q-gutter-xs">
              <AppSelect
                v-model="item.serviceCode"
                :options="serviceOptions"
                label="服務項目"
                class="col"
                :disable="formDialog.isEdit"
                :rules="[(val) => !!val || '請選擇服務']"
                @update:model-value="(val) => onServiceChange(idx, val as string)"
              />
              <q-btn
                flat
                round
                dense
                icon="remove_circle"
                color="red-5"
                @click="removeServiceItem(idx)"
                :disable="form.items.length === 1 || formDialog.isEdit"
                class="cursor-pointer"
              />
            </div>
            <div v-if="item.serviceCode" class="row items-center q-gutter-sm q-mt-xs q-pl-xs">
              <q-input
                v-model.number="item.quantity"
                label="數量"
                outlined
                dense
                type="number"
                style="width: 90px"
                :disable="formDialog.isEdit"
                :rules="[(val) => val > 0 || '至少1']"
              />
              <div class="col text-teal-8 text-weight-medium text-right">
                小計：NT$ {{ (item.unitPrice * item.quantity).toLocaleString() }}
              </div>
            </div>

            <!-- 照片上傳區 -->
            <div class="q-mt-sm q-pl-xs">
              <div class="row items-center q-mb-xs">
                <span class="text-caption text-grey-7">商品照片 (可多張)</span>
              </div>
              
              <div class="row q-gutter-sm items-start">
                <!-- 預覽圖 -->
                <div 
                  v-for="(img, imgIdx) in item.imageUrls" 
                  :key="imgIdx"
                  class="relative-position"
                >
                  <q-img
                    :src="img"
                    spinner-color="white"
                    style="height: 60px; width: 60px; border-radius: 8px"
                    class="bg-grey-2 shadow-1"
                  />
                  <q-btn
                    round
                    dense
                    size="xs"
                    color="red"
                    icon="close"
                    class="absolute-top-right"
                    style="transform: translate(40%, -40%)"
                    @click="removeImage(idx, imgIdx)"
                  />
                </div>

                <!-- 上傳按鈕 -->
                <q-file
                  v-model="tempFiles[idx]"
                  label="上傳"
                  outlined
                  dense
                  multiple
                  append
                  accept="image/*"
                  style="width: 60px"
                  @update:model-value="(files) => onFilesPicked(idx, files)"
                  class="photo-picker-mini"
                >
                  <template v-slot:prepend>
                    <q-icon name="add_a_photo" size="xs" />
                  </template>
                  <!-- 隱藏預設標籤與內容 -->
                  <template v-slot:file> </template>

                </q-file>
              </div>
            </div>
          </div>
          <div v-if="!formDialog.isEdit && serviceSubtotal > 0" class="text-right text-weight-bold text-teal-8 q-mt-xs">
            服務小計：NT$ {{ serviceSubtotal.toLocaleString() }}
          </div>
        </div>

        <!-- 急件設定 -->
        <div>
          <q-toggle
            v-model="form.isUrgent"
            label="急件"
            color="red-6"
            icon="bolt"
          />
          <AppDateInput
            v-if="form.isUrgent"
            v-model="form.urgentDeadline"
            label="急件截止日"
            prepend-icon="event"
            class="q-mt-sm"
            :rules="[(val: string | null) => !form.isUrgent || !!val || '急件必須填截止日期']"
          />
        </div>

        <!-- 急件費提示 -->
        <q-banner
          v-if="form.isUrgent && !formDialog.isEdit"
          dense
          rounded
          class="bg-red-1 text-red-8"
        >
          <template v-slot:avatar>
            <q-icon name="bolt" color="red-6" />
          </template>
          急件費（50%）：NT$ {{ urgentFeeAmount.toLocaleString() }}
          &nbsp;|&nbsp;
          <strong>總計：NT$ {{ (serviceSubtotal + urgentFeeAmount).toLocaleString() }}</strong>
        </q-banner>

        <!-- 預計取件日 -->
        <AppDateInput
          v-model="form.estimatedPickupDate"
          label="預計取件日期"
          prepend-icon="event_available"
          :rules="[(val: string | null) => !!val || '請填寫預計取件日期']"
        />

        <!-- 存放位置 -->
        <q-input
          v-model="form.storageLocation"
          label="存放位置（如：A-3-2，多個用逗號分隔）"
          outlined
          dense
        >
          <template v-slot:prepend>
            <q-icon name="location_on" />
          </template>
        </q-input>

        <!-- 備註 -->
        <q-input
          v-model="form.note"
          label="備註 / 鞋物狀況"
          outlined
          dense
          type="textarea"
          rows="2"
        >
          <template v-slot:prepend>
            <q-icon name="notes" />
          </template>
        </q-input>
      </q-form>
    </AppFormDialog>

    <!-- 新增會員 Dialog（從訂單表單呼叫） -->
    <CustomerFormDialog
      v-model="customerFormOpen"
      @created="onCustomerCreated"
    />

    <!-- 詳情 Dialog -->
    <q-dialog v-model="detailDialog.open" maximized>
      <q-card v-if="detailDialog.order">
        <q-toolbar class="bg-teal-8 text-white">
          <q-icon name="receipt_long" size="sm" class="q-mr-sm" />
          <q-toolbar-title>{{ detailDialog.order.orderNo }}</q-toolbar-title>
          <q-badge
            v-if="detailDialog.order.isUrgent"
            color="red-4"
            label="急件"
            class="q-mr-sm"
          />
          <q-btn
            flat
            round
            dense
            icon="edit"
            @click="openEditFromDetail"
            :disable="detailDialog.order.status === 'PICKED_UP' || detailDialog.order.status === 'CANCELLED'"
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
                  <!-- 狀態圖示 -->
                  <div class="text-center q-mb-md">
                    <q-avatar
                      :color="statusColor(detailDialog.order.status)"
                      text-color="white"
                      size="56px"
                      :icon="statusIcon(detailDialog.order.status)"
                    />
                    <div class="q-mt-sm">
                      <q-badge
                        :color="statusColor(detailDialog.order.status)"
                        :label="statusLabel(detailDialog.order.status)"
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
                        <q-item-label caption>顧客</q-item-label>
                        <q-item-label>{{ detailDialog.order.customerName }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section avatar>
                        <q-icon name="phone" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>電話</q-item-label>
                        <q-item-label>{{ detailDialog.order.customerPhone }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="detailDialog.order.storageLocations.length > 0">
                      <q-item-section avatar>
                        <q-icon name="location_on" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>存放位置</q-item-label>
                        <q-item-label>{{ detailDialog.order.storageLocations.join('、') }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section avatar>
                        <q-icon name="event_available" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>預計取件日</q-item-label>
                        <q-item-label>{{ detailDialog.order.estimatedPickupDate }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="detailDialog.order.actualPickupDate">
                      <q-item-section avatar>
                        <q-icon name="done_all" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>實際取件日</q-item-label>
                        <q-item-label>{{ detailDialog.order.actualPickupDate }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="detailDialog.order.isUrgent">
                      <q-item-section avatar>
                        <q-icon name="bolt" color="red-6" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>急件截止日</q-item-label>
                        <q-item-label class="text-red-6">{{ detailDialog.order.urgentDeadline }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item v-if="detailDialog.order.note">
                      <q-item-section avatar>
                        <q-icon name="notes" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>備註</q-item-label>
                        <q-item-label>{{ detailDialog.order.note }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item>
                      <q-item-section avatar>
                        <q-icon name="calendar_today" color="teal-7" />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label caption>建立時間</q-item-label>
                        <q-item-label>{{ formatDate(detailDialog.order.createTime) }}</q-item-label>
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
                        NT$ {{ detailDialog.order.totalAmount.toLocaleString() }}
                      </div>
                      <div class="text-caption text-grey-6">訂單總額</div>
                    </div>
                    <div
                      v-if="detailDialog.order.urgentFee > 0"
                      class="col stat-mini-card text-center q-pa-sm"
                    >
                      <div class="text-h6 text-weight-bold text-red-6">
                        NT$ {{ detailDialog.order.urgentFee.toLocaleString() }}
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
                v-if="nextStatusOptions(detailDialog.order.status).length > 0"
              >
                <q-card-section class="card-header-accent">
                  <div class="row items-center">
                    <q-icon name="update" color="teal-8" size="sm" class="q-mr-sm" />
                    <span class="text-subtitle1 text-weight-medium">更新狀態</span>
                  </div>
                </q-card-section>
                <q-card-section class="q-gutter-sm">
                  <q-btn
                    v-for="opt in nextStatusOptions(detailDialog.order.status)"
                    :key="opt.status"
                    unelevated
                    :color="opt.color"
                    :icon="opt.icon"
                    :label="opt.label"
                    class="full-width cursor-pointer"
                    @click="updateStatus(detailDialog.order!, opt.status)"
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
                  <template v-for="item in detailDialog.order.items" :key="item.id">
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
                        <q-item-label class="text-weight-medium">{{ item.serviceName }}</q-item-label>
                        <q-item-label caption>
                          NT$ {{ item.unitPrice.toLocaleString() }} × {{ item.quantity }}
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
                  <q-item v-if="detailDialog.order.urgentFee > 0">
                    <q-item-section avatar>
                      <q-avatar color="red-1" text-color="red-6" size="40px" icon="bolt" />
                    </q-item-section>
                    <q-item-section>
                      <q-item-label class="text-weight-medium text-red-6">急件費</q-item-label>
                      <q-item-label caption>加急處理費用（50%）</q-item-label>
                    </q-item-section>
                    <q-item-section side>
                      <div class="text-weight-bold text-red-6">
                        NT$ {{ detailDialog.order.urgentFee.toLocaleString() }}
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
                      NT$ {{ detailDialog.order.totalAmount.toLocaleString() }}
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
  </q-page>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { useQuasar, date as qDate } from 'quasar';
import AppFormDialog from '../components/AppFormDialog.vue';
import AppDateInput from '../components/AppDateInput.vue';
import AppSelect from '../components/AppSelect.vue';
import CustomerFormDialog from '../components/CustomerFormDialog.vue';
import { useOrderNavStore } from '../stores/orderNavStore';
import { listOrders, createOrder, updateOrder, updateOrderStatus } from '../api/order/order';
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
  sortField: 'estimatedPickupDate',
  sortDir: 'asc' as 'asc' | 'desc',
});

const sortOptions = [
  { label: '建立時間', value: 'createTime' },
  { label: '預計取件日', value: 'estimatedPickupDate' },
  { label: '訂單金額', value: 'totalAmount' },
];

const toggleSortDir = () => {
  filters.sortDir = filters.sortDir === 'desc' ? 'asc' : 'desc';
  loadOrders();
};

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

const loading = ref(false);

const loadOrders = async () => {
  try {
    loading.value = true;
    const res = await listOrders({
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      isUrgent: filters.isUrgent ?? undefined,
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

const imagePreview = reactive({
  open: false,
  url: '',
});

const showFullImage = (url: string) => {
  imagePreview.url = url;
  imagePreview.open = true;
};

type FormItem = { serviceCode: string; quantity: number; unitPrice: number; imageUrls?: string[] };

const form = reactive({
  customerId: null as number | null,
  customerPhone: '',
  customerName: '',
  items: [{ serviceCode: '', quantity: 1, unitPrice: 0, imageUrls: [] }] as FormItem[],
  isUrgent: false,
  urgentDeadline: '',
  estimatedPickupDate: '',
  storageLocation: '',
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
  form.items = [{ serviceCode: '', quantity: 1, unitPrice: 0, imageUrls: [] }];
  form.isUrgent = false;
  form.urgentDeadline = '';
  form.estimatedPickupDate = qDate.formatDate(new Date(Date.now() + 14 * 86400000), 'YYYY/MM/DD');
  form.storageLocation = '';
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
  form.items.push({ serviceCode: '', quantity: 1, unitPrice: 0, imageUrls: [] });
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
  }));
  form.isUrgent = order.isUrgent;
  form.urgentDeadline = order.urgentDeadline ?? '';
  form.estimatedPickupDate = order.estimatedPickupDate;
  form.storageLocation = order.storageLocations.join(', ');
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
    const storageLocations = form.storageLocation
      ? form.storageLocation
          .split(',')
          .map((s) => s.trim())
          .filter(Boolean)
      : [];

    if (formDialog.isEdit && formDialog.editId) {
      await updateOrder(formDialog.editId, {
        isUrgent: form.isUrgent,
        urgentDeadline: form.isUrgent ? form.urgentDeadline : undefined,
        storageLocations,
        estimatedPickupDate: form.estimatedPickupDate,
        note: form.note || undefined,
      });
      $q.notify({ type: 'positive', message: '訂單已更新！' });
    } else {
      await createOrder({
        customerId: form.customerId,
        isUrgent: form.isUrgent,
        urgentDeadline: form.isUrgent ? form.urgentDeadline : undefined,
        items: form.items.map((i) => ({ 
          serviceCode: i.serviceCode, 
          quantity: i.quantity,
          imageUrls: i.imageUrls 
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
const updatingStatus = ref(false);

const detailDialog = reactive({
  open: false,
  order: null as OrderResult | null,
});

const openDetailDialog = (order: OrderResult) => {
  detailDialog.order = order;
  detailDialog.open = true;
};

const openEditFromDetail = async () => {
  if (!detailDialog.order) return;
  const order = detailDialog.order;
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
  form.urgentDeadline = order.urgentDeadline || '';
  form.estimatedPickupDate = order.estimatedPickupDate || '';
  form.storageLocation = order.storageLocations ? order.storageLocations.join(', ') : '';
  form.note = order.note || '';

  form.items = order.items.map((i) => ({
    serviceCode: i.serviceCode,
    quantity: i.quantity,
    unitPrice: 0,
  }));
  form.items.forEach((item) => {
    const svc = serviceMap.value[item.serviceCode];
    if (svc) item.unitPrice = svc.defaultPrice;
  });

  formDialog.open = true;
};

const updateStatus = async (order: OrderResult, status: OrderStatus) => {
  try {
    updatingStatus.value = true;
    const payload: { status: OrderStatus; actualPickupDate?: string } = { status };
    if (status === 'PICKED_UP') {
      payload.actualPickupDate = new Date().toISOString().slice(0, 10);
    }
    const updated = await updateOrderStatus(order.id, payload);
    detailDialog.order = updated;
    $q.notify({ type: 'positive', message: `狀態已更新：${statusLabel(status)}` });
    await loadOrders();
  } catch {
    $q.notify({ type: 'negative', message: '狀態更新失敗' });
  } finally {
    updatingStatus.value = false;
  }
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

onMounted(async () => {
  const pendingId = orderNavStore.consumePendingOrderId();
  await Promise.all([loadServices(), loadOrders()]);
  if (pendingId) {
    const target = orders.value.find((o) => o.id === pendingId);
    if (target) openDetailDialog(target);
  }
});
</script>

<style scoped>
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

.customer-not-found {
  min-height: 40px;
  padding: 4px 8px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.02);
}

.service-item-block {
  border: 1px solid rgba(0, 150, 136, 0.15);
  border-radius: 8px;
  padding: 8px 10px;
  background: rgba(0, 150, 136, 0.02);
}

/* 迷你照片選擇器 */
.photo-picker-mini {
  border: 1px dashed #cbd5e1;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.2s;
}

.photo-picker-mini:hover {
  border-color: #0d9488;
  background-color: #f0fdfa;
}

.photo-picker-mini :deep(.q-field__inner) {
  height: 60px;
}

.photo-picker-mini :deep(.q-field__control) {
  height: 60px;
  min-height: 60px !important;
}

.photo-picker-mini :deep(.q-field__native) {
  display: none !important;
}

.photo-picker-mini :deep(.q-field__prepend) {
  width: 100%;
  justify-content: center;
  padding: 0;
}
</style>
