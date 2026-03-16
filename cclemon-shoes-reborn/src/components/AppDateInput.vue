<template>
  <q-input
    :model-value="props.modelValue"
    :label="props.label"
    outlined
    dense
    readonly
    :rules="props.rules"
    :style="props.inputStyle"
    class="app-date-input cursor-pointer"
    @click="openPicker"
  >
    <template v-if="props.prependIcon" v-slot:prepend>
      <q-icon :name="props.prependIcon" />
    </template>
    <template v-slot:append>
      <q-icon name="calendar_today" class="cursor-pointer" @click.stop="openPicker">
        <!-- 桌機版：標準彈窗 -->
        <q-popup-proxy
          v-if="!$q.screen.lt.md"
          ref="proxyRef"
          cover
          transition-show="scale"
          transition-hide="scale"
        >
          <q-date
            :model-value="props.modelValue"
            :mask="props.mask ?? 'YYYY-MM-DD'"
            :default-view="props.defaultView"
            :emit-immediately="props.emitImmediately"
            color="teal-8"
            today-btn
            @update:model-value="onSelect"
          />
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>

  <!-- 手機版：底部抽屜式選擇器 -->
  <q-dialog
    v-if="$q.screen.lt.md"
    v-model="mobileOpen"
    position="bottom"
    class="mobile-date-dialog"
  >
    <q-card class="mobile-date-card">
      <q-toolbar class="bg-grey-1 text-grey-9">
        <q-toolbar-title class="text-subtitle1 text-weight-bold">{{ props.label || '選擇日期' }}</q-toolbar-title>
        <q-btn flat round dense icon="close" v-close-popup />
      </q-toolbar>
      <q-card-section class="q-pa-none">
        <q-date
          :model-value="props.modelValue"
          :mask="props.mask ?? 'YYYY-MM-DD'"
          :default-view="props.defaultView"
          :emit-immediately="props.emitImmediately"
          color="teal-8"
          flat
          today-btn
          class="full-width"
          @update:model-value="onSelect"
        />
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useQuasar } from 'quasar';

const props = defineProps<{
  modelValue?: string;
  label?: string;
  rules?: ((val: string | null) => true | string)[];
  inputStyle?: string | Record<string, string>;
  prependIcon?: string;
  mask?: string;
  defaultView?: 'Calendar' | 'Months' | 'Years';
  emitImmediately?: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string];
}>();

const $q = useQuasar();
const proxyRef = ref();
const mobileOpen = ref(false);

function openPicker() {
  if ($q.screen.lt.md) {
    mobileOpen.value = true;
  } else {
    proxyRef.value?.show();
  }
}

function onSelect(val: string) {
  emit('update:modelValue', val);
  if ($q.screen.lt.md) {
    mobileOpen.value = false;
  } else {
    proxyRef.value?.hide();
  }
}
</script>

<style scoped>
.app-date-input :deep(.q-field__native) {
  cursor: pointer;
}
.mobile-date-card {
  border-radius: 16px 16px 0 0 !important;
  overflow: hidden;
}
</style>
