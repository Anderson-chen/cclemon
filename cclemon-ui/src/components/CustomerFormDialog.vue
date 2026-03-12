<template>
  <AppFormDialog
    v-model="isOpen"
    :title="isEdit ? '編輯會員資料' : '新增會員'"
    :icon="isEdit ? 'edit' : 'person_add'"
    :is-edit="isEdit"
    :submitting="submitting"
    max-width="560px"
    @submit="submitForm"
  >
    <template #footer>
      <q-btn flat color="grey-7" icon="close" label="取消" @click="isOpen = false" class="cursor-pointer" padding="6px 16px" />
      <q-btn unelevated rounded :label="isEdit ? '儲存' : '建立'" color="teal-8" :icon="isEdit ? 'save' : 'check'" @click="submitForm" :loading="submitting" class="cursor-pointer" padding="6px 20px" />
    </template>
    <q-form ref="formRef" class="q-gutter-md">
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
        label="手機號碼 (選填)"
        outlined
        dense
        :rules="[
          (val) => !val || /^[0-9]{8,15}$/.test(val) || '請輸入有效的手機號碼',
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
  </AppFormDialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { useQuasar } from 'quasar';
import AppFormDialog from './AppFormDialog.vue';
import { createCustomer, updateCustomer } from '../api/customer/customer';
import type { CustomerResult } from '../api/customer/types';

const props = defineProps<{
  modelValue: boolean;
  editCustomer?: CustomerResult | null;
  initialPhone?: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  created: [customer: CustomerResult];
  updated: [customer: CustomerResult];
}>();

const $q = useQuasar();
const formRef = ref();
const submitting = ref(false);

const isEdit = computed(() => !!props.editCustomer);

const isOpen = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const form = reactive({ name: '', phone: '', email: '', note: '' });

// 每次開啟時重設表單內容
watch(
  () => props.modelValue,
  (open) => {
    if (!open) return;
    if (props.editCustomer) {
      form.name = props.editCustomer.name;
      form.phone = props.editCustomer.phone ?? '';
      form.email = props.editCustomer.email ?? '';
      form.note = props.editCustomer.note ?? '';
    } else {
      form.name = '';
      form.phone = props.initialPhone ?? '';
      form.email = '';
      form.note = '';
    }
  }
);

const submitForm = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  const payload = {
    name: form.name,
    phone: form.phone || undefined,
    email: form.email || undefined,
    note: form.note || undefined,
  };

  try {
    submitting.value = true;
    if (isEdit.value && props.editCustomer) {
      const updated = await updateCustomer(props.editCustomer.id, payload);
      $q.notify({ type: 'positive', message: '會員資料已更新！' });
      emit('updated', updated);
    } else {
      const created = await createCustomer(payload);
      $q.notify({ type: 'positive', message: `會員「${created.name}」建立成功！` });
      emit('created', created);
    }
    isOpen.value = false;
  } catch {
    $q.notify({
      type: 'negative',
      message: isEdit.value ? '更新失敗，請稍後再試' : '建立失敗，電話號碼可能已存在',
    });
  } finally {
    submitting.value = false;
  }
};
</script>
