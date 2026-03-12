<template>
  <q-dialog
    v-model="isOpen"
    persistent
    :position="$q.screen.lt.md ? 'bottom' : 'standard'"
  >
    <q-card
      class="dialog-card"
      :style="$q.screen.lt.md
        ? 'width: 100vw; border-radius: 16px 16px 0 0;'
        : `width: 95vw; max-width: ${maxWidth}`"
    >
      <!-- Toolbar -->
      <q-toolbar class="bg-teal-8 text-white dialog-top-bar">
        <q-btn flat round dense icon="close" @click="isOpen = false" class="cursor-pointer">
          <q-tooltip v-if="$q.screen.gt.xs">關閉取消</q-tooltip>
        </q-btn>

        <q-toolbar-title :class="$q.screen.lt.sm ? 'text-subtitle1' : 'text-h6'">
          <q-icon v-if="icon" :name="icon" class="q-mr-sm" size="sm" />
          {{ title }}
        </q-toolbar-title>

        <!-- 無自訂 footer 時才在 toolbar 顯示提交按鈕（手機） -->
        <div v-if="!hasFooterSlot && $q.screen.lt.md" class="row items-center q-gutter-x-sm">
          <q-btn
            unelevated
            color="white"
            text-color="teal-9"
            padding="4px 16px"
            :label="computedSubmitLabel"
            :loading="submitting"
            @click="$emit('submit')"
            class="text-weight-bold"
            rounded
          />
        </div>
      </q-toolbar>

      <!-- 可捲動的表單內容 -->
      <div class="dialog-body q-pa-md">
        <slot />
      </div>

      <!-- Footer：自訂或預設提交列 -->
      <div class="dialog-footer">
        <slot name="footer">
          <q-card-actions align="right" class="q-px-md q-pb-md">
            <q-btn flat label="取消並關閉" color="grey-7" @click="isOpen = false" class="cursor-pointer q-px-md" />
            <q-btn
              unelevated
              rounded
              :label="computedSubmitLabel"
              color="teal-8"
              @click="$emit('submit')"
              :loading="submitting"
              class="cursor-pointer q-px-lg"
            />
          </q-card-actions>
        </slot>
      </div>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue';
import { useQuasar } from 'quasar';

const props = defineProps<{
  modelValue: boolean;
  title: string;
  icon?: string;
  submitting?: boolean;
  isEdit?: boolean;
  maxWidth?: string;
  submitLabel?: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  submit: [];
}>();

const $q = useQuasar();
const slots = useSlots();

const isOpen = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const hasFooterSlot = computed(() => !!slots.footer);

const computedSubmitLabel = computed(
  () => props.submitLabel ?? (props.isEdit ? '儲存' : '建立')
);
</script>

<style scoped>
.dialog-card {
  display: flex;
  flex-direction: column;
  max-height: 90dvh;
}

.dialog-top-bar {
  flex-shrink: 0;
  z-index: 10;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
}

.dialog-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.dialog-footer {
  flex-shrink: 0;
  border-top: 1px solid #edf2f7;
  background: #fafafa;
  z-index: 10;
}
</style>
