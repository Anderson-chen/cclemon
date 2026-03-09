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
        <div class="row items-center q-gutter-md">
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
                  size="sm"
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

    <!-- 新增 Dialog -->
    <q-dialog v-model="addDialog.open" persistent>
      <q-card style="min-width: 380px">
        <q-card-section class="bg-teal-8 text-white">
          <div class="text-h6">
            <q-icon name="add_circle" class="q-mr-sm" />新增服務項目
          </div>
        </q-card-section>

        <q-card-section class="q-pt-md">
          <q-form ref="addFormRef" class="q-gutter-md">
            <q-input
              v-model="addForm.code"
              label="服務代碼 *"
              outlined
              dense
              hint="格式建議：SVC-XXXX，全系統唯一"
              :rules="[(v) => !!v || '服務代碼為必填']"
            />
            <q-input
              v-model="addForm.name"
              label="服務名稱 *"
              outlined
              dense
              :rules="[(v) => !!v || '服務名稱為必填']"
            />
            <q-input
              v-model.number="addForm.defaultPrice"
              label="售價 (NT$) *"
              type="number"
              outlined
              dense
              :rules="[(v) => v > 0 || '售價須大於 0']"
            />
            <q-input
              v-model="addForm.urgentFeeRateInput"
              label="急件費率 (%) — 選填"
              type="number"
              outlined
              dense
              hint="留空表示使用全域費率。例：50 代表 50%"
            />
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="取消" @click="addDialog.open = false" />
          <q-btn
            unelevated
            color="teal-8"
            label="新增"
            :loading="addDialog.submitting"
            @click="submitAdd"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 編輯 Dialog -->
    <q-dialog v-model="editDialog.open" persistent>
      <q-card style="min-width: 380px">
        <q-card-section class="bg-teal-8 text-white">
          <div class="text-h6">
            <q-icon name="edit" class="q-mr-sm" />編輯服務項目
          </div>
        </q-card-section>

        <q-card-section class="q-pt-md">
          <q-form ref="editFormRef" class="q-gutter-md">
            <q-input
              :model-value="editDialog.code"
              label="服務代碼"
              outlined
              dense
              readonly
              disable
            />
            <q-input
              v-model="editForm.name"
              label="服務名稱 *"
              outlined
              dense
              :rules="[(v) => !!v || '服務名稱為必填']"
            />
            <q-input
              v-model.number="editForm.defaultPrice"
              label="售價 (NT$) *"
              type="number"
              outlined
              dense
              :rules="[(v) => v > 0 || '售價須大於 0']"
            />
            <q-input
              v-model="editForm.urgentFeeRateInput"
              label="急件費率 (%) — 選填"
              type="number"
              outlined
              dense
              hint="留空表示使用全域費率。例：50 代表 50%"
            />
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="取消" @click="editDialog.open = false" />
          <q-btn
            unelevated
            color="teal-8"
            label="儲存"
            :loading="editDialog.submitting"
            @click="submitEdit"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
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

// ── 新增 Dialog ───────────────────────────────────────────
const addFormRef = ref();
const addDialog = ref({ open: false, submitting: false });
const addForm = ref({ code: '', name: '', defaultPrice: 0, urgentFeeRateInput: '' });

const openAddDialog = () => {
  addForm.value = { code: '', name: '', defaultPrice: 0, urgentFeeRateInput: '' };
  addDialog.value.open = true;
};

const submitAdd = async () => {
  const valid = await addFormRef.value?.validate();
  if (!valid) return;

  try {
    addDialog.value.submitting = true;
    const rateStr = addForm.value.urgentFeeRateInput;
    const urgentFeeRate = rateStr !== '' && rateStr !== null
      ? Number(rateStr) / 100
      : null;

    await createService({
      code: addForm.value.code,
      name: addForm.value.name,
      defaultPrice: addForm.value.defaultPrice,
      urgentFeeRate,
    });

    $q.notify({ type: 'positive', message: `服務項目「${addForm.value.name}」已新增` });
    addDialog.value.open = false;
    await loadServices();
  } catch {
    $q.notify({ type: 'negative', message: '新增失敗，服務代碼可能重複或請求有誤' });
  } finally {
    addDialog.value.submitting = false;
  }
};

// ── 編輯 Dialog ───────────────────────────────────────────
const editFormRef = ref();
const editDialog = ref({ open: false, submitting: false, code: '' });
const editForm = ref({ name: '', defaultPrice: 0, urgentFeeRateInput: '' });

const openEditDialog = (svc: ServiceTypeResult) => {
  editDialog.value.code = svc.code;
  editForm.value = {
    name: svc.name,
    defaultPrice: svc.defaultPrice,
    urgentFeeRateInput: svc.urgentFeeRate !== null ? String(svc.urgentFeeRate * 100) : '',
  };
  editDialog.value.open = true;
};

const submitEdit = async () => {
  const valid = await editFormRef.value?.validate();
  if (!valid) return;

  try {
    editDialog.value.submitting = true;
    const rateStr = editForm.value.urgentFeeRateInput;
    const urgentFeeRate = rateStr !== '' && rateStr !== null
      ? Number(rateStr) / 100
      : null;

    await updateService(editDialog.value.code, {
      name: editForm.value.name,
      defaultPrice: editForm.value.defaultPrice,
      urgentFeeRate,
    });

    $q.notify({ type: 'positive', message: '服務項目已更新' });
    editDialog.value.open = false;
    await loadServices();
  } catch {
    $q.notify({ type: 'negative', message: '更新失敗，請稍後再試' });
  } finally {
    editDialog.value.submitting = false;
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
