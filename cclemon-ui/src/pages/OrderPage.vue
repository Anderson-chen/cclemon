<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="receipt_long" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">
            訂單管理
          </h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 訂單列表</div>
        </div>
      </div>
      <q-btn
        unelevated
        color="teal-8"
        icon="post_add"
        @click="openCreateDialog"
        class="cursor-pointer"
      />
    </div>

    <!-- 搜尋列 -->
    <q-card class="q-mb-sm">
      <q-card-section>
        <div class="row q-gutter-md wrap items-center">
          <q-input
            v-model="keyword"
            label="姓名 / 電話搜尋"
            outlined
            dense
            clearable
            style="flex: 1; min-width: 200px"
            @keyup.enter="search"
          />
          <q-btn
            unelevated
            :color="activeFilterCount > 0 ? 'teal-8' : 'grey-3'"
            :text-color="activeFilterCount > 0 ? 'white' : 'grey-7'"
            icon="tune"
            round
            class="cursor-pointer filter-tune-btn"
            @click="openFilterSheet"
          >
            <q-badge
              v-if="activeFilterCount > 0"
              color="red-5"
              floating
              rounded
              :label="activeFilterCount"
            />
          </q-btn>
        </div>
      </q-card-section>
    </q-card>

    <!-- 篩選 Chips（永遠顯示，啟用/停用狀態） -->
    <div class="filter-chips-row q-mb-sm">
      <!-- 狀態 chip：有值才顯示，toggle applied -->
      <q-chip
        v-if="statusValue"
        clickable
        dense
        :color="statusApplied ? 'teal-8' : 'grey-4'"
        :text-color="statusApplied ? 'white' : 'grey-6'"
        icon="label"
        class="filter-chip cursor-pointer"
        @click="
          statusApplied = !statusApplied;
          search();
        "
        >{{ statusLabelMap[statusValue] }}</q-chip
      >

      <!-- 急件 chip：曾設定才顯示，toggle applied -->
      <q-chip
        v-if="urgentChipShown"
        clickable
        dense
        :color="isUrgentApplied ? 'red-6' : 'grey-4'"
        :text-color="isUrgentApplied ? 'white' : 'grey-6'"
        icon="bolt"
        class="filter-chip cursor-pointer"
        @click="
          isUrgentApplied = !isUrgentApplied;
          search();
        "
        >急件</q-chip
      >

      <!-- 取件日 chip：有值才顯示，toggle applied -->
      <q-chip
        v-if="dateValue"
        clickable
        dense
        :color="dateApplied ? 'blue-7' : 'grey-4'"
        :text-color="dateApplied ? 'white' : 'grey-6'"
        icon="event"
        class="filter-chip cursor-pointer"
        @click="
          dateApplied = !dateApplied;
          search();
        "
        >{{ dateValue }}</q-chip
      >

      <!-- 排序 chip：永遠顯示，非預設時彩色 -->
      <q-chip
        clickable
        dense
        :color="sortKey !== 'estimatedPickupDate,asc' ? 'purple-7' : 'grey-4'"
        :text-color="sortKey !== 'estimatedPickupDate,asc' ? 'white' : 'grey-6'"
        icon="sort"
        class="filter-chip cursor-pointer"
        @click="openFilterSheet"
        >{{
          sortKeyOptions.find((o) => o.value === sortKey)?.shortLabel
        }}</q-chip
      >
    </div>

    <!-- 篩選底部抽屜 -->
    <q-dialog v-model="filterSheetOpen" position="bottom">
      <q-card class="filter-sheet-card">
        <q-toolbar class="bg-teal-8 text-white filter-sheet-toolbar">
          <q-icon name="tune" class="q-mr-sm" />
          <q-toolbar-title class="text-subtitle1 text-weight-medium"
            >篩選 &amp; 排序</q-toolbar-title
          >
          <q-btn
            flat
            round
            dense
            icon="close"
            v-close-popup
            class="cursor-pointer"
          />
        </q-toolbar>

        <div class="filter-sheet-body">
          <!-- 訂單狀態 -->
          <div class="filter-section">
            <div class="filter-section-label">訂單狀態</div>
            <div class="row q-gutter-sm">
              <q-btn
                v-for="opt in statusOptions"
                :key="opt.value"
                unelevated
                no-caps
                :color="draft.status === opt.value ? 'teal-8' : 'grey-2'"
                :text-color="draft.status === opt.value ? 'white' : 'grey-8'"
                :label="opt.label"
                class="filter-chip-btn cursor-pointer"
                @click="
                  draft.status = draft.status === opt.value ? null : opt.value
                "
              />
            </div>
          </div>

          <!-- 急件 -->
          <div class="filter-section">
            <div class="filter-section-label">急件篩選</div>
            <q-btn
              unelevated
              no-caps
              :color="draft.isUrgent ? 'red-1' : 'grey-2'"
              :text-color="draft.isUrgent ? 'red-7' : 'grey-8'"
              icon="bolt"
              label="只顯示急件"
              class="filter-chip-btn cursor-pointer"
              :class="{ 'urgent-on': draft.isUrgent }"
              @click="draft.isUrgent = !draft.isUrgent"
            />
          </div>

          <!-- 取件日 -->
          <div class="filter-section">
            <div class="filter-section-label">預計取件日</div>
            <div class="row items-center no-wrap" style="max-width: 220px">
              <AppDateInput
                v-model="draft.date"
                label="選擇日期"
                prepend-icon="event"
                class="col"
              />
              <q-btn
                v-if="draft.date"
                flat
                round
                dense
                size="xs"
                icon="close"
                color="grey-5"
                class="cursor-pointer q-ml-xs"
                @click="draft.date = ''"
              />
            </div>
          </div>

          <!-- 排序 -->
          <div class="filter-section">
            <div class="filter-section-label">排序方式</div>
            <div class="row q-gutter-sm">
              <q-btn
                v-for="opt in sortKeyOptions"
                :key="opt.value"
                unelevated
                no-caps
                :color="draft.sortKey === opt.value ? 'teal-8' : 'grey-2'"
                :text-color="draft.sortKey === opt.value ? 'white' : 'grey-8'"
                :label="opt.label"
                class="filter-chip-btn cursor-pointer"
                @click="draft.sortKey = opt.value"
              />
            </div>
          </div>
        </div>

        <!-- 底部操作 -->
        <div class="filter-sheet-actions row items-center">
          <q-btn
            flat
            no-caps
            label="清除篩選"
            color="grey-6"
            class="cursor-pointer"
            @click="clearDraft"
          />
          <q-space />
          <q-btn
            unelevated
            rounded
            no-caps
            color="teal-8"
            label="套用"
            class="cursor-pointer q-px-xl"
            @click="applyFilter"
          />
        </div>
      </q-card>
    </q-dialog>

    <!-- 訂單列表 -->
    <q-card class="section-card">
      <!-- 列表主體：固定最低高度避免換頁跳動 -->
      <div class="list-body">
        <!-- 無資料（非載入中） -->
        <div v-if="!loading && orders.length === 0" class="list-body-center">
          <q-icon name="inbox" size="4em" color="grey-4" />
          <div class="text-subtitle1 text-grey-5 q-mt-sm">尚無訂單資料</div>
          <div class="text-caption text-grey-4">點擊「新增訂單」開始開單</div>
        </div>

        <!-- 訂單列表 -->
        <q-list v-if="orders.length > 0" separator>
          <q-item
            v-for="order in orders"
            :key="order.id"
            class="order-list-item cursor-pointer"
            :class="orderItemClass(order)"
            clickable
            @click="openDetailDialog(order)"
          >
            <!-- 姓名 + 電話 -->
            <q-item-section>
              <q-item-label
                class="text-weight-bold text-grey-9 text-body2 row items-center q-gutter-xs"
              >
                <span>{{ order.customerName }}</span>
                <q-badge
                  v-if="order.isUrgent"
                  color="red-6"
                  label="急件"
                  dense
                  style="font-size: 0.68rem"
                />
              </q-item-label>
              <q-item-label caption class="text-grey-6 q-mt-xs">{{
                order.customerPhone
              }}</q-item-label>
            </q-item-section>

            <!-- 取件日 -->
            <q-item-section side class="items-end q-mr-sm">
              <div class="pickup-date" :class="pickupDateClass(order.status)">
                {{ order.estimatedPickupDate || '–' }}
              </div>
              <div class="pickup-label text-grey-5">預計取件</div>
            </q-item-section>

            <!-- 狀態 + 操作 -->
            <q-item-section side class="items-end">
              <q-badge
                :color="statusColor(order.status)"
                :label="statusLabel(order.status)"
                outline
                class="q-mb-xs"
                style="font-size: 0.72rem"
              />
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
          @update:model-value="loadOrders"
          boundary-links
          direction-links
          dense
        />
      </div>
    </q-card>

    <AppFormDialog
      v-model="formDialog.open"
      :title="formDialog.isEdit ? '編輯訂單' : '建立新訂單'"
      :is-edit="formDialog.isEdit"
      max-width="800px"
    >
      <q-form ref="formRef" @submit.prevent class="order-form-redesign">
        <q-stepper
          v-model="currentStep"
          ref="stepperRef"
          color="teal-8"
          animated
          flat
          header-nav
          :contracted="$q.screen.lt.sm"
          class="modern-stepper"
        >
          <!-- 步驟 1: 選擇顧客 -->
          <q-step
            :name="1"
            title="選擇顧客"
            icon="person"
            :done="currentStep > 1"
            :error="!form.customerId && currentStep > 1"
          >
            <div class="text-caption text-teal-8 q-mb-sm">
              <q-icon name="info" size="12px" class="q-mr-xs" />可點擊上方步驟圖示快速切換
            </div>
            <div class="form-section q-mt-md">
              <div class="row items-center q-mb-md">
                <div class="modern-section-dot" />
                <div class="column">
                  <div class="text-subtitle1 text-weight-bold text-grey-9">
                    顧客資訊
                  </div>
                </div>
              </div>

              <div class="row q-col-gutter-md">
                <div class="col-12">
                  <q-select
                    v-model="selectedCustomer"
                    use-input
                    fill-input
                    hide-selected
                    clearable
                    :options="customerOptions"
                    option-value="id"
                    :option-label="
                      (opt) => (opt ? `${opt.name} (${opt.phone})` : '')
                    "
                    label="搜尋現有會員（姓名或電話）"
                    outlined
                    dense
                    bg-color="grey-1"
                    @filter="filterCustomers"
                    @update:model-value="onCustomerSelected"
                    @input-value="(val) => (customerSearchText = val)"
                    :disable="formDialog.isEdit"
                    :loading="lookingUp"
                    hide-dropdown-icon
                    behavior="menu"
                    debounce="1500"
                    class="modern-select"
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
                        <q-item-section
                          class="text-grey-7 row items-center justify-between no-wrap"
                          horizontal
                        >
                          <q-icon name="info" size="xs" class="q-mr-xs" />
                          <div>查無此會員，請嘗試其他關鍵字或</div>
                          <q-btn
                            flat
                            dense
                            color="teal-8"
                            label="建立新會員"
                            @click="customerFormOpen = true"
                            class="q-ml-sm text-weight-bold"
                          />
                        </q-item-section>
                      </q-item>
                    </template>
                    <template v-slot:option="scope">
                      <q-item
                        v-bind="scope.itemProps"
                        class="search-result-item"
                      >
                        <q-item-section avatar>
                          <q-avatar
                            color="teal-1"
                            text-color="teal-8"
                            icon="person"
                            size="32px"
                          />
                        </q-item-section>
                        <q-item-section>
                          <q-item-label class="text-weight-bold grey-9">{{
                            scope.opt.name
                          }}</q-item-label>
                          <q-item-label caption class="text-grey-6">{{
                            scope.opt.phone
                          }}</q-item-label>
                        </q-item-section>
                        <q-item-section
                          side
                          v-if="scope.opt.id === form.customerId"
                        >
                          <q-icon name="check_circle" color="teal-8" />
                        </q-item-section>
                      </q-item>
                    </template>

                    <template v-slot:selected-item="scope">
                      <div class="selected-customer-pill" v-if="scope.opt">
                        <q-icon name="person" size="xs" class="q-mr-xs" />
                        <span>{{ scope.opt.name }}</span>
                      </div>
                    </template>
                  </q-select>
                </div>
              </div>

              <div
                v-if="selectedCustomer"
                class="customer-profile-card q-mt-lg animate-fade-in"
              >
                <div class="row items-center no-wrap">
                  <q-avatar
                    color="teal-8"
                    text-color="white"
                    icon="person"
                    size="56px"
                    class="profile-avatar shadow-2 q-mr-md"
                  />
                  <div class="column flex-1">
                    <div
                      class="row items-center justify-between no-wrap q-mb-sm"
                    >
                      <span class="text-h6 text-weight-bolder text-grey-9">{{
                        form.customerName
                      }}</span>
                      <q-badge
                        color="teal-1"
                        text-color="teal-9"
                        label="已選會員"
                        class="q-px-sm"
                      />
                    </div>
                    <div class="row items-center text-grey-6">
                      <q-icon name="phone" size="xs" class="q-mr-xs" />
                      <span class="text-body2 text-weight-medium text-mono">{{
                        form.customerPhone
                      }}</span>
                    </div>
                  </div>
                  <q-btn
                    flat
                    round
                    color="grey-4"
                    icon="close"
                    @click="
                      selectedCustomer = null;
                      onCustomerSelected(null);
                    "
                    class="q-ml-sm"
                  >
                    <q-tooltip>重新選擇</q-tooltip>
                  </q-btn>
                </div>
              </div>
            </div>
          </q-step>

          <!-- 步驟 2: 服務內容 -->
          <q-step
            :name="2"
            title="服務明細"
            icon="cleaning_services"
            :done="currentStep > 2"
          >
            <div class="form-section q-mt-md">
              <div class="row items-center q-mb-md">
                <div class="modern-section-dot" />
                <div class="text-subtitle1 text-weight-bold text-grey-9">
                  服務內容
                </div>
                <q-space />
                <q-btn
                  flat
                  rounded
                  dense
                  icon="add_circle_outline"
                  color="teal-8"
                  label="新增項目"
                  @click="addServiceItem"
                  :disable="formDialog.isEdit"
                  class="q-px-sm cursor-pointer text-weight-bold"
                />
              </div>

              <div class="service-items-grid">
                <div
                  v-for="(item, idx) in form.items"
                  :key="idx"
                  class="service-card shadow-1 q-mb-md"
                  :class="{ 'is-collapsed': !item.isExpanded }"
                >
                  <!-- 服務卡片標題 (極簡風格) -->
                  <div
                    class="modern-card-header row items-center justify-between cursor-pointer"
                    @click="item.isExpanded = !item.isExpanded"
                  >
                    <div class="row items-center">
                      <div class="item-badge q-mr-md">{{ idx + 1 }}</div>
                      <div class="column">
                        <span
                          v-if="item.serviceCode"
                          class="text-weight-bold grey-9"
                        >
                          {{
                            serviceMap[item.serviceCode]?.name ||
                            item.serviceCode
                          }}
                        </span>
                        <span v-else class="text-weight-bold text-grey-6 italic"
                          >尚未選擇服務</span
                        >

                        <div
                          v-show="!item.isExpanded && item.serviceCode"
                          class="text-caption text-grey-6"
                        >
                          NT$ {{ item.unitPrice.toLocaleString() }} x
                          {{ item.quantity }}
                        </div>
                      </div>
                    </div>
                    <div class="row items-center">
                      <q-icon
                        :name="
                          item.isExpanded
                            ? 'keyboard_arrow_up'
                            : 'keyboard_arrow_down'
                        "
                        size="sm"
                        color="grey-5"
                        class="q-mr-sm"
                      />
                      <q-btn
                        flat
                        round
                        dense
                        icon="remove_circle_outline"
                        color="grey-4"
                        @click.stop="removeServiceItem(idx)"
                        :disable="form.items.length === 1 || formDialog.isEdit"
                        class="hover-red-5 cursor-pointer"
                      />
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
                              @update:model-value="
                                (val) => onServiceChange(idx, val as string)
                              "
                            >
                              <template v-slot:option="scope">
                                <q-item
                                  v-bind="
                                    scope.itemProps as Record<string, unknown>
                                  "
                                  class="search-result-item"
                                >
                                  <q-item-section avatar>
                                    <q-avatar
                                      color="teal-1"
                                      text-color="teal-8"
                                      icon="cleaning_services"
                                      size="32px"
                                    />
                                  </q-item-section>
                                  <q-item-section>
                                    <q-item-label class="text-weight-bold">{{
                                      (scope.opt as any).label.split('（')[0]
                                    }}</q-item-label>
                                    <q-item-label caption class="text-teal-7">{{
                                      (scope.opt as any).label
                                        .split('（')[1]
                                        .replace('）', '')
                                    }}</q-item-label>
                                  </q-item-section>
                                </q-item>
                              </template>
                              <template
                                v-slot:mobile-option="{ opt, isSelected }"
                              >
                                <div class="column items-center full-width">
                                  <span
                                    :class="
                                      isSelected
                                        ? 'text-teal-8 text-weight-bold'
                                        : 'text-grey-8'
                                    "
                                    >{{ opt.label.split('（')[0] }}</span
                                  >
                                  <span class="text-caption text-grey-6">{{
                                    opt.label.split('（')[1]?.replace('）', '')
                                  }}</span>
                                </div>
                              </template>
                            </AppSelect>
                          </div>
                          <!-- 單價 -->
                          <div class="col-12 col-sm-3">
                            <q-input
                              v-model.number="item.unitPrice"
                              label="單價"
                              outlined
                              dense
                              type="number"
                              prefix="NT$"
                              :disable="formDialog.isEdit"
                              :rules="[(val) => val >= 0 || '不得為負']"
                            >
                              <template v-slot:prepend>
                                <q-icon name="payments" size="xs" />
                              </template>
                            </q-input>
                          </div>
                          <!-- 數量 -->
                          <div class="col-12 col-sm-3">
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
                            <q-input
                              v-model="item.productName"
                              label="品牌/型號"
                              outlined
                              dense
                              :disable="formDialog.isEdit"
                            >
                              <template v-slot:prepend
                                ><q-icon name="branding_watermark" size="xs"
                              /></template>
                            </q-input>
                          </div>
                          <div class="col-12 col-sm-4">
                            <q-input
                              v-model="item.itemStorageLocation"
                              label="存放位置"
                              outlined
                              dense
                              :disable="formDialog.isEdit"
                            >
                              <template v-slot:prepend
                                ><q-icon name="place" size="xs"
                              /></template>
                            </q-input>
                          </div>
                          <div class="col-12 col-sm-4">
                            <q-input
                              v-model="item.itemNote"
                              label="物件備註"
                              outlined
                              dense
                              :disable="formDialog.isEdit"
                            >
                              <template v-slot:prepend
                                ><q-icon name="sticky_note_2" size="xs"
                              /></template>
                            </q-input>
                          </div>

                          <!-- 照片 -->
                          <div class="col-12 q-mt-sm">
                            <div class="text-caption text-grey-7 q-mb-xs">
                              商品照片
                            </div>
                            <div class="row q-gutter-sm items-center">
                              <div
                                v-for="(img, imgIdx) in item.imageUrls"
                                :key="imgIdx"
                                class="photo-preview-wrap relative-position"
                              >
                                <q-img
                                  :src="img"
                                  style="
                                    height: 64px;
                                    width: 64px;
                                    border-radius: 8px;
                                  "
                                  class="bg-grey-2 shadow-1"
                                />
                                <q-btn
                                  round
                                  dense
                                  size="xs"
                                  color="red-5"
                                  icon="close"
                                  class="absolute-top-right btn-remove-photo"
                                  @click="removeImage(idx, imgIdx)"
                                />
                              </div>
                              <div
                                class="photo-upload-container relative-position"
                              >
                                <q-btn
                                  unelevated
                                  class="photo-upload-mini"
                                  :loading="uploadingMap[idx]"
                                  @click="filePicker[idx].pickFiles()"
                                >
                                  <div
                                    class="column items-center justify-center"
                                  >
                                    <q-icon name="add_a_photo" size="sm" />
                                    <div
                                      class="text-caption"
                                      style="font-size: 10px"
                                    >
                                      上傳
                                    </div>
                                  </div>
                                </q-btn>
                                <q-file
                                  :ref="
                                    (el: any) => {
                                      if (el) filePicker[idx] = el;
                                    }
                                  "
                                  v-model="tempFiles[idx]"
                                  style="display: none"
                                  multiple
                                  accept="image/*"
                                  @update:model-value="
                                    (files) =>
                                      onFilesPicked(idx, files as any as File[])
                                  "
                                />
                              </div>
                            </div>
                          </div>
                        </div>

                        <!-- 單項小計 -->
                        <div
                          class="item-subtotal row items-center justify-end q-mt-md q-px-sm"
                        >
                          <span class="text-grey-7 q-mr-sm">單項小計</span>
                          <span
                            class="text-subtitle1 text-weight-bold text-teal-8"
                            >NT$
                            {{
                              (item.unitPrice * item.quantity).toLocaleString()
                            }}</span
                          >
                        </div>
                      </div>
                    </div>
                  </q-slide-transition>
                </div>
              </div>
            </div>
          </q-step>

          <q-step
            :name="3"
            title="結帳設定"
            icon="receipt"
            :done="currentStep === 3"
          >
            <div class="form-section q-mt-md">
              <div class="row items-center q-mb-md">
                <div class="modern-section-dot" />
                <div class="text-subtitle1 text-weight-bold text-grey-9">
                  結帳與交期
                </div>
              </div>

              <div class="row q-col-gutter-lg">
                <div class="col-12 col-sm-6">
                  <!-- 交期 -->
                  <AppDateInput
                    v-model="form.estimatedPickupDate"
                    label="預計取件日期"
                    prepend-icon="event_available"
                    class="q-mb-md"
                    :rules="[
                      (val: string | null) => !!val || '請選擇預計取件日期',
                    ]"
                  />
                  <!-- 急件開關 -->
                  <div
                    class="urgent-toggle-box q-pa-sm rounded-borders row items-center"
                    :class="form.isUrgent ? 'bg-red-1' : 'bg-grey-1'"
                  >
                    <q-toggle
                      v-model="form.isUrgent"
                      label="加急處理"
                      color="red-6"
                      icon="bolt"
                      class="text-weight-bold"
                    />
                    <q-space />
                    <div
                      v-if="form.isUrgent"
                      class="text-red-7 text-caption text-weight-bold"
                    >
                      +50% 加急費
                    </div>
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

              <!-- 價格摘要卡片 (現代風格) -->
              <div class="modern-summary-card q-mt-xl q-pa-lg">
                <div class="row justify-between items-center q-mb-md">
                  <span class="text-grey-7">服務小計</span>
                  <span class="text-weight-medium"
                    >NT$ {{ serviceSubtotal.toLocaleString() }}</span
                  >
                </div>
                <div
                  v-if="form.isUrgent"
                  class="row justify-between items-center q-mb-md text-red-7"
                >
                  <span class="row items-center">
                    <q-icon name="bolt" size="xs" class="q-mr-xs" />
                    加急處理費 (50%)
                  </span>
                  <span class="text-weight-medium"
                    >+ NT$ {{ urgentFeeAmount.toLocaleString() }}</span
                  >
                </div>
                <q-separator color="grey-3" class="q-my-md" />
                <div class="row justify-between items-center">
                  <div class="column">
                    <span class="text-weight-bold text-grey-9 text-h6"
                      >應收總計</span
                    >
                    <span class="text-caption text-grey-5"
                      >共 {{ form.items.length }} 個服務項目</span
                    >
                  </div>
                  <div class="text-h4 text-weight-bolder text-teal-8">
                    <small class="text-subtitle2">NT$</small>
                    {{ (serviceSubtotal + urgentFeeAmount).toLocaleString() }}
                  </div>
                </div>
              </div>
            </div>
          </q-step>
        </q-stepper>
      </q-form>
      <template #footer>
        <div class="row full-width justify-between items-center">
          <!-- 左側：步驟進度或金額 -->
          <div class="row items-center q-gutter-xs">
            <template v-if="currentStep < 3">
              <div
                v-for="n in 3"
                :key="n"
                class="step-dot"
                :class="n === currentStep ? 'step-dot--active' : n < currentStep ? 'step-dot--done' : ''"
              />
            </template>
            <span v-else class="text-subtitle2 text-teal-8 text-weight-bold">
              <q-icon name="receipt" size="16px" class="q-mr-xs" />
              NT$ {{ (serviceSubtotal + urgentFeeAmount).toLocaleString() }}
            </span>
          </div>

          <!-- 右側：導航按鈕 -->
          <div class="row q-gutter-sm">
            <q-btn
              v-if="currentStep > 1"
              outline
              rounded
              color="teal-8"
              icon="chevron_left"
              label="上一步"
              @click="stepperRef?.previous()"
              class="cursor-pointer"
              padding="6px 16px"
            />
            <q-btn
              unelevated
              rounded
              color="teal-8"
              :icon-right="currentStep === 3 ? 'check' : 'chevron_right'"
              :label="
                currentStep === 3
                  ? formDialog.isEdit
                    ? '更新訂單'
                    : '確認開單'
                  : '下一步'
              "
              @click="currentStep === 3 ? submitForm() : stepperRef?.next()"
              padding="6px 20px"
              class="cursor-pointer"
              :loading="submitting"
              :disable="currentStep === 1 && !form.customerId"
            />
          </div>
        </div>
      </template>
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
import { ref, computed, reactive, onMounted } from 'vue';
import { useQuasar, date as quasarDate, QStepper, QFile } from 'quasar';
import AppFormDialog from '../components/AppFormDialog.vue';
import AppDateInput from '../components/AppDateInput.vue';
import AppSelect from '../components/AppSelect.vue';
import CustomerFormDialog from '../components/CustomerFormDialog.vue';
import OrderDetailDialog from '../components/OrderDetailDialog.vue';
import { useOrderNavStore } from '../stores/orderNavStore';
import { listOrders, createOrder, updateOrder } from '../api/order/order';
import { searchCustomers } from '../api/customer/customer';
import { listServices } from '../api/service/service';
import type { OrderResult, OrderStatus } from '../api/order/types';
import type { CustomerResult } from '../api/customer/types';
import type { ServiceTypeResult } from '../api/service/types';
import { uploadImage } from '../api/upload';

const $q = useQuasar();
const orderNavStore = useOrderNavStore();

// ── 搜尋 ──────────────────────────────────────────────────
const keyword = ref('');

// ── 新增/編輯 步驟控制 ────────────────────────────────────
const currentStep = ref(1);
const stepperRef = ref<QStepper | null>(null);
const filePicker = ref<QFile[]>([]);

// 狀態：value 保留值，applied 是否套用
const statusValue = ref<OrderStatus | null>(null);
const statusApplied = ref(false);

// 取件日：value 保留值，applied 是否套用
const dateValue = ref('');
const dateApplied = ref(false);

// 急件：applied 是否套用，shown 是否曾設定過（chip 可見性）
const isUrgentApplied = ref(false);
const urgentChipShown = ref(false);

const sortKey = ref('estimatedPickupDate,asc');

const sortKeyOptions = [
  {
    label: '取件日↑（預設）',
    shortLabel: '取件日↑',
    value: 'estimatedPickupDate,asc',
  },
  {
    label: '取件日↓',
    shortLabel: '取件日↓',
    value: 'estimatedPickupDate,desc',
  },
  { label: '建立時間↑', shortLabel: '建立↑', value: 'createTime,asc' },
  { label: '建立時間↓', shortLabel: '建立↓', value: 'createTime,desc' },
];

const statusOptions: { label: string; value: OrderStatus }[] = [
  { label: '待處理', value: 'PENDING' },
  { label: '處理中', value: 'IN_PROGRESS' },
  { label: '待取件', value: 'READY' },
  { label: '已取件', value: 'PICKED_UP' },
  { label: '已取消', value: 'CANCELLED' },
];

const statusLabelMap: Record<string, string> = {
  PENDING: '待處理',
  IN_PROGRESS: '處理中',
  READY: '待取件',
  PICKED_UP: '已取件',
  CANCELLED: '已取消',
};

// ── 篩選底部抽屜 ──────────────────────────────────────────
const filterSheetOpen = ref(false);

const draft = reactive({
  status: null as OrderStatus | null,
  isUrgent: false,
  date: '',
  sortKey: 'estimatedPickupDate,asc',
});

const activeFilterCount = computed(
  () =>
    (statusApplied.value && statusValue.value ? 1 : 0) +
    (isUrgentApplied.value ? 1 : 0) +
    (dateApplied.value && dateValue.value ? 1 : 0) +
    (sortKey.value !== 'estimatedPickupDate,asc' ? 1 : 0),
);

function openFilterSheet() {
  draft.status = statusValue.value;
  draft.isUrgent = isUrgentApplied.value;
  draft.date = dateValue.value;
  draft.sortKey = sortKey.value;
  filterSheetOpen.value = true;
}

function applyFilter() {
  statusValue.value = draft.status;
  statusApplied.value = draft.status !== null;
  isUrgentApplied.value = draft.isUrgent;
  urgentChipShown.value = draft.isUrgent; // sheet 明確設定時才顯示/隱藏 chip
  dateValue.value = draft.date;
  dateApplied.value = draft.date !== '';
  sortKey.value = draft.sortKey;
  filterSheetOpen.value = false;
  search();
}

function clearDraft() {
  draft.status = null;
  draft.isUrgent = false;
  draft.date = '';
  draft.sortKey = 'estimatedPickupDate,asc';
}

const ACTIVE_STATUSES: OrderStatus[] = ['PENDING', 'IN_PROGRESS', 'READY'];

// ── 列表 ──────────────────────────────────────────────────
const orders = ref<OrderResult[]>([]);
const pagination = reactive({
  page: 1,
  size: 5,
  totalPages: 1,
  totalElements: 0,
});

const loading = ref(false);

const loadOrders = async () => {
  try {
    loading.value = true;
    const res = await listOrders({
      keyword: keyword.value || undefined,
      status:
        statusApplied.value && statusValue.value
          ? statusValue.value
          : undefined,
      statuses:
        statusApplied.value && statusValue.value ? undefined : ACTIVE_STATUSES,
      isUrgent: isUrgentApplied.value || undefined,
      dateFrom:
        dateApplied.value && dateValue.value ? dateValue.value : undefined,
      dateTo:
        dateApplied.value && dateValue.value ? dateValue.value : undefined,
      sort: sortKey.value,
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

// 查詢（重置頁碼）
const search = () => {
  pagination.page = 1;
  loadOrders();
};

// ── 服務項目設定 ───────────────────────────────────────────
const FALLBACK_SERVICES: ServiceTypeResult[] = [
  {
    code: 'SVC-WASH',
    name: '洗鞋',
    defaultPrice: 350,
    urgentFeeRate: null,
    isActive: true,
  },
  {
    code: 'SVC-COATING',
    name: '鍍膜',
    defaultPrice: 500,
    urgentFeeRate: null,
    isActive: true,
  },
  {
    code: 'SVC-BAG',
    name: '洗包',
    defaultPrice: 600,
    urgentFeeRate: null,
    isActive: true,
  },
  {
    code: 'SVC-RECOLOR',
    name: '補色',
    defaultPrice: 800,
    urgentFeeRate: null,
    isActive: true,
  },
];

const activeServices = ref<ServiceTypeResult[]>([]);

const serviceOptions = computed(() =>
  activeServices.value.map((svc) => ({
    label: `${svc.name}（NT$ ${svc.defaultPrice}）`,
    value: svc.code,
  })),
);

const serviceMap = computed(() =>
  Object.fromEntries(activeServices.value.map((svc) => [svc.code, svc])),
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

type FormItem = {
  serviceCode: string;
  quantity: number;
  unitPrice: number;
  imageUrls?: string[];
  productName?: string;
  itemNote?: string;
  itemStorageLocation?: string;
  isExpanded?: boolean;
};

const form = reactive({
  customerId: null as number | null,
  customerPhone: '',
  customerName: '',
  items: [
    {
      serviceCode: '',
      quantity: 1,
      unitPrice: 0,
      imageUrls: [],
      productName: '',
      itemNote: '',
      itemStorageLocation: '',
      isExpanded: true,
    },
  ] as FormItem[],
  isUrgent: false,
  estimatedPickupDate: '',
  note: '',
});

const serviceSubtotal = computed(() =>
  form.items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0),
);

const urgentFeeAmount = computed(() =>
  form.isUrgent ? Math.round(serviceSubtotal.value * 0.5) : 0,
);

const resetForm = () => {
  form.customerId = null;
  form.customerPhone = '';
  form.customerName = '';
  form.items = [
    {
      serviceCode: '',
      quantity: 1,
      unitPrice: 0,
      imageUrls: [],
      productName: '',
      itemNote: '',
      itemStorageLocation: '',
      isExpanded: true,
    },
  ];
  form.isUrgent = false;
  form.estimatedPickupDate = quasarDate.formatDate(
    new Date(Date.now() + 14 * 86400000),
    'YYYY/MM/DD',
  );
  form.note = '';
  customerNotFound.value = false;
  currentStep.value = 1;
  selectedCustomer.value = null;
  tempFiles.value = [null];
};

const selectedCustomer = ref<
  CustomerResult | { id: number; name: string; phone: string } | null
>(null);
const customerOptions = ref<CustomerResult[]>([]);
const customerSearchText = ref('');

const filterCustomers = async (
  val: string,
  update: (callback: () => void) => void,
  abort: () => void,
) => {
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

const onCustomerSelected = (
  opt: CustomerResult | { id: number; name: string; phone: string } | null,
) => {
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

const onCustomerCreated = (customer: {
  id: number;
  name: string;
  phone: string;
}) => {
  form.customerId = customer.id;
  form.customerName = customer.name;
  form.customerPhone = customer.phone;
  selectedCustomer.value = customer;
  customerNotFound.value = false;
};

const addServiceItem = () => {
  // 先縮合其他項目
  form.items.forEach((i) => (i.isExpanded = false));
  form.items.push({
    serviceCode: '',
    quantity: 1,
    unitPrice: 0,
    imageUrls: [],
    productName: '',
    itemNote: '',
    itemStorageLocation: '',
    isExpanded: true,
  });
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
const uploadingMap = reactive<Record<number, boolean>>({});

const onFilesPicked = async (itemIdx: number, files: File[]) => {
  if (!files || files.length === 0) return;

  if (!form.items[itemIdx].imageUrls) {
    form.items[itemIdx].imageUrls = [];
  }

  try {
    uploadingMap[itemIdx] = true;
    for (const file of files) {
      const url = await uploadImage(file);
      form.items[itemIdx].imageUrls?.push(url);
    }
  } catch {
    $q.notify({ type: 'negative', message: '圖片上傳失敗' });
  } finally {
    uploadingMap[itemIdx] = false;
    tempFiles.value[itemIdx] = null;
  }
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

const submitForm = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  if (!form.customerId) {
    $q.notify({
      type: 'warning',
      message: '請先查詢顧客（點擊電話欄右側放大鏡）',
    });
    return;
  }

  try {
    submitting.value = true;
    const storageLocations: string[] = [];

    if (formDialog.isEdit && formDialog.editId) {
      await updateOrder(formDialog.editId, {
        isUrgent: form.isUrgent,
        items: form.items.map((i) => ({
          serviceCode: i.serviceCode,
          quantity: i.quantity,
          unitPrice: i.unitPrice,
          imageUrls: i.imageUrls,
          productName: i.productName || undefined,
          itemNote: i.itemNote || undefined,
          itemStorageLocation: i.itemStorageLocation || undefined,
        })),
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
          unitPrice: i.unitPrice,
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
    if (!formDialog.isEdit) {
      sortKey.value = 'createTime,desc';
    }
    await loadOrders();
  } catch {
    $q.notify({
      type: 'negative',
      message: formDialog.isEdit
        ? '更新失敗，請稍後再試'
        : '建立失敗，請稍後再試',
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
    const exact = custs.find((c) => c.id === order.customerId);
    selectedCustomer.value = exact || {
      id: order.customerId,
      name: order.customerName,
      phone: order.customerPhone,
    };
  } catch {
    selectedCustomer.value = {
      id: order.customerId,
      name: order.customerName,
      phone: order.customerPhone,
    };
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
    unitPrice: i.unitPrice,
    imageUrls: [...(i.imageUrls || [])],
    productName: i.productName || '',
    itemNote: i.itemNote || '',
    itemStorageLocation: i.itemStorageLocation || '',
    isExpanded: false,
  }));
  if (form.items.length > 0) form.items[0].isExpanded = true;

  currentStep.value = 1;
  formDialog.open = true;
};

// ── 輔助函式 ──────────────────────────────────────────────
function orderItemClass(order: OrderResult): string {
  if (order.isUrgent && order.status === 'PENDING') return 'order-item--urgent';
  if (order.status === 'READY') return 'order-item--ready';
  if (order.status === 'IN_PROGRESS') return 'order-item--in-progress';
  return '';
}

function pickupDateClass(status: string): string {
  if (status === 'READY') return 'text-teal-8';
  if (status === 'PENDING') return 'text-orange-8';
  return 'text-grey-8';
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

onMounted(async () => {
  const pendingId = orderNavStore.consumePendingOrderId();
  const pendingFilters = orderNavStore.consumeFilters();

  // 套用來自總覽的篩選條件
  if (pendingFilters) {
    if (pendingFilters.status !== undefined) {
      statusValue.value = pendingFilters.status;
      statusApplied.value = pendingFilters.status !== null;
    }
    if (pendingFilters.isUrgent) {
      isUrgentApplied.value = true;
      urgentChipShown.value = true;
    }
    if (pendingFilters.dateFrom) {
      dateValue.value = pendingFilters.dateFrom;
      dateApplied.value = true;
    }
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

/* 篩選 chips 列 */
.filter-chips-row {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  gap: 6px;
  scrollbar-width: none;
}

.filter-chips-row::-webkit-scrollbar {
  display: none;
}

.filter-chip {
  flex-shrink: 0;
  font-size: 0.8rem;
  transition:
    background-color 0.15s,
    color 0.15s;
}

/* 篩選底部抽屜 */
.filter-sheet-card {
  width: 100vw;
  max-height: 85dvh;
  border-radius: 16px 16px 0 0;
  overflow-y: auto;
}

.filter-sheet-toolbar {
  position: sticky;
  top: 0;
  z-index: 10;
}

.filter-sheet-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-section {
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.filter-section:last-child {
  border-bottom: none;
}

.filter-section-label {
  font-size: 0.75rem;
  color: #9e9e9e;
  font-weight: 500;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  margin-bottom: 10px;
}

.filter-chip-btn {
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 0.875rem;
  min-height: 36px;
}

.filter-chip-btn.urgent-on {
  border: 1px solid #ef9a9a;
}

.filter-sheet-actions {
  padding: 12px 16px 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  background: #fafafa;
  position: sticky;
  bottom: 0;
}

/* 列表 */
.section-card {
  border-radius: 12px;
  box-shadow:
    0 1px 4px rgba(0, 0, 0, 0.07),
    0 4px 12px rgba(0, 0, 0, 0.04);
}

.list-header {
  border-bottom: none;
}

/* 現代化表單組件 */
.step-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e0;
  transition: all 0.2s;
}

.step-dot--active {
  width: 24px;
  border-radius: 4px;
  background: #0f766e;
}

.step-dot--done {
  background: #0d9488;
}

.modern-section-dot {
  width: 8px;
  height: 8px;
  background: #0f766e;
  border-radius: 50%;
  margin-right: 12px;
}

.modern-select :deep(.q-field__control) {
  border-radius: 12px;
}

.modern-card-header {
  padding: 16px;
  background: white;
  transition: all 0.2s;
}

.item-badge {
  width: 28px;
  height: 28px;
  background: #f0fdfa;
  color: #0d9488;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 0.8rem;
  border: 1px solid #ccfbf1;
}

.service-card {
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  overflow: hidden;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03) !important;
  transition: all 0.3s ease;
}

.service-card.is-collapsed {
  background: #fcfcfc;
  border-color: #f1f5f9;
}

.service-card:hover {
  border-color: #0f766e40;
}

.hover-red-5:hover {
  color: #ef5350 !important;
  background-color: #ffebee;
}

.modern-summary-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
}

.modern-summary-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #0d9488, #5eead4);
}

.list-body {
  min-height: 310px;
  position: relative;
}

.list-body-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 310px;
}

.list-loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.65);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 0 12px 12px;
}

.order-list-item {
  padding: 12px 16px;
  border-left: 3px solid transparent;
  transition: background 0.18s ease;
}

.order-list-item:hover {
  background: rgba(0, 0, 0, 0.025);
}

.order-item--urgent {
  border-left-color: #ef4444;
}

.order-item--urgent:hover {
  background: rgba(239, 68, 68, 0.04);
}

.order-item--ready {
  border-left-color: #0f766e;
}

.order-item--ready:hover {
  background: rgba(15, 118, 110, 0.04);
}

.order-item--in-progress {
  border-left-color: #0ea5e9;
}

.order-item--in-progress:hover {
  background: rgba(14, 165, 233, 0.04);
}

.pickup-date {
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.pickup-label {
  font-size: 0.72rem;
  margin-top: 2px;
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

.selected-customer-pill {
  display: flex;
  align-items: center;
  background: #f0fdfa;
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 600;
  color: #0d9488;
  border: 1px solid #ccfbf1;
}

.customer-profile-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.customer-profile-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #0d9488;
}

.profile-avatar {
  background: linear-gradient(135deg, #0d9488, #14b8a6) !important;
}

.search-result-item {
  padding: 10px 16px;
  transition: all 0.2s;
}

.search-result-item:hover {
  background: #f0fdfa !important;
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
  border: 1px dashed #cbd5e1;
  border-radius: 8px;
  background: #f8fafc;
  color: #64748b;
  transition: all 0.2s;
  cursor: pointer;
}

.photo-upload-mini:hover {
  border-color: #0d9488;
  background-color: #f0fdfa;
  color: #0d9488;
}

.photo-upload-container {
  display: inline-block;
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
