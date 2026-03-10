<template>
  <q-page padding>
    <!-- 頁面標題 -->
    <div class="row items-center q-mb-md">
      <q-icon name="tune" size="md" color="teal-8" class="q-mr-sm" />
      <h5 class="q-mt-none q-mb-none">服務設定</h5>
    </div>

    <!-- 全域急件費率 -->
    <q-card class="q-mb-lg">
      <q-card-section class="bg-teal-1">
        <div class="row items-center">
          <q-icon name="speed" color="teal-8" size="sm" class="q-mr-sm" />
          <span class="text-h6">全域急件費率</span>
          <q-space />
          <span class="text-caption text-grey-6">未設定個別費率的服務項目將使用此費率</span>
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row items-center q-gutter-md wrap">
          <q-input
            v-model.number="globalRateInput"
            type="number"
            label="全域急件費率 (%)"
            outlined
            dense
            style="max-width: 200px"
            :rules="[(v) => v >= 0 || '費率不得為負數']"
            hint="例：50 代表 50%"
          />
          <q-btn
            unelevated
            color="teal-8"
            label="儲存費率"
            icon="save"
            :loading="savingRate"
            @click="saveGlobalRate"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- 服務項目清單 -->
    <q-card>
      <q-card-section class="bg-teal-1">
        <div class="row items-center">
          <q-icon name="list_alt" color="teal-8" size="sm" class="q-mr-sm" />
          <span class="text-h6">服務項目清單</span>
          <q-space />
          <q-btn
            unelevated
            color="teal-8"
            icon="add_circle"
            label="新增服務"
            @click="openAddDialog"
          />
        </div>
      </q-card-section>

      <q-card-section>
        <div v-if="loading" class="row justify-center q-pa-lg">
          <q-spinner color="teal-8" size="40px" />
        </div>

        <q-list v-else bordered separator>
          <q-item
            v-for="svc in services"
            :key="svc.code"
            :class="svc.isActive ? 'bg-white' : 'bg-grey-2'"
          >
            <q-item-section>
              <q-item-label :class="svc.isActive ? '' : 'text-grey-5'">
                <strong>{{ svc.name }}</strong>
                <q-badge
                  :color="svc.isActive ? 'teal-8' : 'grey-5'"
                  class="q-ml-sm"
                >
                  {{ svc.isActive ? '啟用中' : '已停用' }}
                </q-badge>
              </q-item-label>
              <q-item-label caption :class="svc.isActive ? 'text-grey-7' : 'text-grey-5'">
                代碼：{{ svc.code }} ／ 售價：NT$ {{ svc.defaultPrice }}
                ／ 急件費率：{{ svc.urgentFeeRate !== null ? `${svc.urgentFeeRate * 100}%` : '使用全域' }}
              </q-item-label>
            </q-item-section>

            <q-item-section side>
              <div class="row q-gutter-xs">
                <q-btn
                  flat
                  round
                  icon="edit"
                  color="teal-8"
                  size="sm"
                  @click="openEditDialog(svc)"
                >
                  <q-tooltip>編輯</q-tooltip>
                </q-btn>
                <q-btn
                  flat
                  round
                  :icon="svc.isActive ? 'toggle_on' : 'toggle_off'"
                  :color="svc.isActive ? 'orange-7' : 'grey-6'"
                  size="lg"
                  @click="toggleActive(svc)"
                >
                  <q-tooltip>{{ svc.isActive ? '停用此服務' : '重新啟用' }}</q-tooltip>
                </q-btn>
              </div>
            </q-item-section>
          </q-item>

          <q-item v-if="services.length === 0">
            <q-item-section class="text-center text-grey-5 q-pa-md">
              尚無服務項目，請點擊「新增服務」建立第一筆。
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>
    </q-card>

    <!-- 新增 / 編輯 Dialog -->
    <AppFormDialog
      v-model="formDialog.open"
      :title="formDialog.isEdit ? '編輯服務項目' : '新增服務項目'"
      :icon="formDialog.isEdit ? 'edit' : 'add_circle'"
      :is-edit="formDialog.isEdit"
      :submitting="formDialog.submitting"
      max-width="480px"
      @submit="submitForm"
    >
      <q-form ref="formRef" class="q-gutter-md">
        <q-input
          v-model="form.code"
          label="服務代碼 *"
          outlined
          dense
          :readonly="formDialog.isEdit"
          :disable="formDialog.isEdit"
          hint="格式建議：SVC-XXXX，全系統唯一"
          :rules="[(v) => !!v || '服務代碼為必填']"
        />
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
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import AppFormDialog from '../components/AppFormDialog.vue';
import { listServices, createService, updateService, updateGlobalUrgentFeeRate } from '../api/service/service';
import type { ServiceTypeResult } from '../api/service/types';

defineOptions({ name: 'ServiceManagePage' });

const $q = useQuasar();

// ── 服務項目清單 ──────────────────────────────────────────
const loading = ref(false);
const services = ref<ServiceTypeResult[]>([]);

const loadServices = async () => {
  try {
    loading.value = true;
    services.value = await listServices({ includeInactive: true });
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
      await updateService(formDialog.value.editCode, {
        name: form.value.name,
        defaultPrice: form.value.defaultPrice,
        urgentFeeRate,
      });
      $q.notify({ type: 'positive', message: '服務項目已更新' });
    } else {
      await createService({
        code: form.value.code,
        name: form.value.name,
        defaultPrice: form.value.defaultPrice,
        urgentFeeRate,
      });
      $q.notify({ type: 'positive', message: `服務項目「${form.value.name}」已新增` });
    }
    formDialog.value.open = false;
    await loadServices();
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
    await updateService(svc.code, { isActive: nextActive });
    $q.notify({ type: 'positive', message: `服務「${svc.name}」已${actionLabel}` });
    await loadServices();
  } catch {
    $q.notify({ type: 'negative', message: `${actionLabel}失敗，請稍後再試` });
  }
};

onMounted(loadServices);
</script>
