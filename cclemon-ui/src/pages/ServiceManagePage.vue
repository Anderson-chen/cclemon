<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center justify-between q-mb-lg">
      <div class="row items-center q-gutter-sm">
        <div class="title-icon-wrap">
          <q-icon name="tune" color="white" size="sm" />
        </div>
        <div>
          <h5 class="q-mt-none q-mb-none text-weight-bold text-grey-9">服務設定</h5>
          <div class="text-caption text-grey-5">Shoes Reborn · 服務項目</div>
        </div>
      </div>
      <q-btn
        unelevated
        color="teal-8"
        icon="playlist_add"
        @click="openAddDialog"
        class="cursor-pointer"
      />
    </div>

    <!-- 服務項目清單 -->
    <q-card class="section-card">

      <!-- 列表主體 -->
      <div class="list-body">
        <!-- 無資料（非載入中） -->
        <div v-if="!loading && services.length === 0" class="list-body-center">
          <q-icon name="list_alt" size="3em" color="grey-4" />
          <div class="text-subtitle1 text-grey-5 q-mt-sm">尚無服務項目</div>
          <div class="text-caption text-grey-4">點擊「新增服務項目」開始建立</div>
        </div>

        <!-- 列表 -->
        <q-list v-if="pagedServices.length > 0" separator>
          <q-item
            v-for="svc in pagedServices"
            :key="svc.code"
            :class="['svc-item', svc.isActive ? 'svc-item--active' : 'svc-item--inactive']"
            class="order-list-item cursor-pointer"
            clickable
            @click="openEditDialog(svc)"
          >
            <q-item-section>
              <q-item-label class="text-weight-bold text-grey-9 text-body2">
                {{ svc.name }}
              </q-item-label>
              <q-item-label caption class="text-grey-6">
                {{ svc.isActive ? '啟用中' : '已停用' }}
              </q-item-label>
            </q-item-section>

            <q-item-section side>
              <q-btn
                flat round
                :icon="svc.isActive ? 'toggle_on' : 'toggle_off'"
                :color="svc.isActive ? 'teal-8' : 'grey-6'"
                size="lg"
                class="cursor-pointer"
                @click.stop="toggleActive(svc)"
              >
                <q-tooltip>{{ svc.isActive ? '停用' : '啟用' }}</q-tooltip>
              </q-btn>
            </q-item-section>
          </q-item>
        </q-list>

        <!-- Loading 遮罩 -->
        <div v-if="loading" class="list-loading-overlay">
          <q-spinner color="teal-8" size="2.5em" />
        </div>
      </div>

      <!-- 分頁 (統一風格) -->
      <div v-if="pagination.totalPages > 1" class="row justify-center q-py-md">
        <q-pagination
          v-model="pagination.page"
          :max="pagination.totalPages"
          :max-pages="5"
          color="teal-8"
          active-color="teal-8"
          boundary-links
          direction-links
          dense
        />
      </div>
    </q-card>

    <!-- 全域急件費率 -->
    <q-card class="section-card q-mt-lg">
      <q-card-section class="bg-teal-1">
        <div class="row items-center">
          <q-icon name="speed" color="teal-8" size="sm" class="q-mr-sm" />
          <span class="text-h6">全域急件費率設定</span>
        </div>
      </q-card-section>
      <q-card-section class="q-pa-lg">
        <div class="row items-center q-gutter-lg">
          <div class="col-12 col-sm-auto text-center">
            <div class="text-h4 text-weight-bolder text-teal-8">
              {{ globalRateInput }}%
            </div>
            <div class="text-caption text-grey-6">預設急件加收</div>
          </div>
          
          <div class="col">
            <q-slider
              v-model="globalRateInput"
              :min="0"
              :max="200"
              :step="5"
              label
              color="teal-8"
              class="q-px-md"
            />
          </div>

          <div class="col-12 col-sm-auto">
            <q-btn
              unelevated
              rounded
              color="teal-8"
              label="儲存設定"
              icon="save"
              padding="8px 32px"
              class="text-weight-bold"
              :loading="savingRate"
              @click="saveGlobalRate"
            />
          </div>
        </div>
        
        <div class="q-mt-md bg-teal-0 q-pa-sm border-radius-8">
          <q-icon name="info" color="teal-4" size="16px" class="q-mr-xs" />
          <span class="text-caption text-grey-7">
            提示：此費率會套用到所有未設定「專屬費率」的項目。50% 代表總金額加收一半。
          </span>
        </div>
      </q-card-section>
    </q-card>

    <!-- 新增 / 編輯 Dialog -->
    <AppFormDialog
      v-model="formDialog.open"
      :title="formDialog.isEdit ? '編輯服務項目' : '新增服務項目'"
      :icon="formDialog.isEdit ? 'edit' : 'playlist_add'"
      :is-edit="formDialog.isEdit"
      :submitting="formDialog.submitting"
      max-width="480px"
      @submit="submitForm"
    >
      <q-form ref="formRef" class="q-gutter-md">

        <q-input
          v-model="form.name"
          label="服務名稱 *"
          outlined
          dense
          :rules="[(v) => !!v || '服務名稱為必填']"
        />
        <q-input
          v-model.number="form.defaultPrice"
          label="售價 (NT$) *"
          type="number"
          outlined
          dense
          :rules="[(v) => v > 0 || '售價須大於 0']"
        />
        <q-input
          v-model="form.urgentFeeRateInput"
          label="急件費率 (%) — 選填"
          type="number"
          outlined
          dense
          hint="留空表示使用全域費率。例：50 代表 50%"
        />
      </q-form>
    </AppFormDialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import AppFormDialog from '../components/AppFormDialog.vue';
import { listServices, createService, updateService, updateGlobalUrgentFeeRate } from '../api/service/service';
import type { ServiceTypeResult } from '../api/service/types';

defineOptions({ name: 'ServiceManagePage' });

const $q = useQuasar();

// ── 服務項目清單 ──────────────────────────────────────────
const loading = ref(false);
const services = ref<ServiceTypeResult[]>([]);

// ── 分頁（客戶端） ────────────────────────────────────────
const PAGE_SIZE = 5;
const pagination = reactive({
  page: 1,
  get totalPages() {
    return Math.max(1, Math.ceil(services.value.length / PAGE_SIZE));
  },
});

const pagedServices = computed(() => {
  const start = (pagination.page - 1) * PAGE_SIZE;
  return services.value.slice(start, start + PAGE_SIZE);
});

const loadServices = async () => {
  try {
    loading.value = true;
    services.value = await listServices({ includeInactive: true });
    // 若刪除後當前頁已無資料，退到上一頁
    if (pagination.page > pagination.totalPages) {
      pagination.page = pagination.totalPages;
    }
  } catch {
    $q.notify({ type: 'negative', message: '載入服務項目失敗，請稍後再試' });
  } finally {
    loading.value = false;
  }
};

// ── 全域急件費率 ──────────────────────────────────────────
const globalRateInput = ref<number>(50);
const savingRate = ref(false);

const saveGlobalRate = async () => {
  try {
    savingRate.value = true;
    await updateGlobalUrgentFeeRate({ rate: globalRateInput.value / 100 });
    $q.notify({ type: 'positive', message: '全域急件費率已更新' });
  } catch {
    $q.notify({ type: 'negative', message: '儲存費率失敗，請稍後再試' });
  } finally {
    savingRate.value = false;
  }
};

// ── 新增 / 編輯 Dialog ────────────────────────────────────
const formRef = ref();
const formDialog = ref({ open: false, isEdit: false, submitting: false, editCode: '' });
const form = ref({ code: '', name: '', defaultPrice: 0, urgentFeeRateInput: '' });

const openAddDialog = () => {
  form.value = { code: '', name: '', defaultPrice: 0, urgentFeeRateInput: '' };
  formDialog.value.isEdit = false;
  formDialog.value.editCode = '';
  formDialog.value.open = true;
};

const openEditDialog = (svc: ServiceTypeResult) => {
  form.value = {
    code: svc.code,
    name: svc.name,
    defaultPrice: svc.defaultPrice,
    urgentFeeRateInput: svc.urgentFeeRate !== null ? String(svc.urgentFeeRate * 100) : '',
  };
  formDialog.value.isEdit = true;
  formDialog.value.editCode = svc.code;
  formDialog.value.open = true;
};

const submitForm = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  const rateStr = form.value.urgentFeeRateInput;
  const urgentFeeRate =
    rateStr !== '' && rateStr !== null ? Number(rateStr) / 100 : null;

  try {
    formDialog.value.submitting = true;
    if (formDialog.value.isEdit) {
      const updated = await updateService(formDialog.value.editCode, {
        name: form.value.name,
        defaultPrice: form.value.defaultPrice,
        urgentFeeRate,
      });
      $q.notify({ type: 'positive', message: '服務項目已更新' });
      // Update local state directly
      const idx = services.value.findIndex(s => s.code === updated.code);
      if (idx !== -1) services.value[idx] = updated;
    } else {
      // 自動生成代碼 (SVC-隨機)
      const generatedCode = 'SVC-' + Math.random().toString(36).substring(2, 7).toUpperCase();
      await createService({
        code: generatedCode,
        name: form.value.name,
        defaultPrice: form.value.defaultPrice,
        urgentFeeRate,
      });
      $q.notify({ type: 'positive', message: `服務項目「${form.value.name}」已新增` });
      await loadServices();
    }
    formDialog.value.open = false;
  } catch {
    $q.notify({
      type: 'negative',
      message: formDialog.value.isEdit
        ? '更新失敗，請稍後再試'
        : '新增失敗，服務代碼可能重複或請求有誤',
    });
  } finally {
    formDialog.value.submitting = false;
  }
};

// ── 停用 / 啟用 ───────────────────────────────────────────
const toggleActive = async (svc: ServiceTypeResult) => {
  const nextActive = !svc.isActive;
  const actionLabel = nextActive ? '啟用' : '停用';
  try {
    const updated = await updateService(svc.code, { isActive: nextActive });
    $q.notify({ type: 'positive', message: `服務「${svc.name}」已${actionLabel}` });
    // Update local state directly
    const idx = services.value.findIndex(s => s.code === updated.code);
    if (idx !== -1) services.value[idx] = updated;
  } catch {
    $q.notify({ type: 'negative', message: `${actionLabel}失敗，請稍後再試` });
  }
};

onMounted(loadServices);
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

.svc-item {
  padding: 12px 16px;
  border-left: 3px solid transparent;
  transition: background-color 0.15s;
}

.svc-item--active {
  border-left-color: #0f766e;
}

.svc-item--active:hover {
  background-color: rgba(15, 118, 110, 0.04);
}

.svc-item--inactive {
  border-left-color: #bdbdbd;
}

.svc-item--inactive:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
</style>
