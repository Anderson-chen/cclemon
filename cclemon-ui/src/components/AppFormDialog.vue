<template>
  <q-dialog
    v-model="isOpen"
    persistent
    :position="$q.screen.lt.md ? 'bottom' : 'standard'"
  >
    <q-card :style="$q.screen.lt.md ? 'width: 100vw; border-radius: 16px 16px 0 0;' : `width: 95vw; max-width: ${maxWidth}`">
      <!-- 統一 Header/Toolbar (手機與桌機皆包含主要操作) -->
      <q-toolbar class="bg-teal-8 text-white dialog-top-bar">
        <q-btn flat round dense icon="close" @click="isOpen = false" class="cursor-pointer">
          <q-tooltip v-if="$q.screen.gt.xs">關閉取消</q-tooltip>
        </q-btn>
        
        <q-toolbar-title :class="$q.screen.lt.sm ? 'text-subtitle1' : 'text-h6'">
          <q-icon :name="icon" class="q-mr-sm" size="sm" />
          {{ title }}
        </q-toolbar-title>

        <div class="row items-center q-gutter-x-sm">
          <q-btn
            v-if="$q.screen.gt.xs"
            flat
            label="取消"
            @click="isOpen = false"
            class="q-px-md"
          />
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

      <!-- 表單內容（響應式高度） -->
      <div
        class="q-pa-md"
        :style="
          $q.screen.lt.md
            ? 'max-height: 75dvh; overflow-y: auto;'
            : 'max-height: 75vh; overflow-y: auto;'
        "
      >
        <slot />
      </div>

      <!-- 桌機：底部按鈕 (保留作為備選或提示) -->
      <q-card-actions v-if="$q.screen.gt.sm" align="right" class="q-px-md q-pb-md bg-grey-1">
        <div class="text-caption text-grey-6 q-mr-md">提示：您也可以直接點擊右上角「{{ computedSubmitLabel }}」完成操作</div>
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
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useQuasar } from 'quasar';

const props = defineProps<{
  modelValue: boolean;
  title: string;
  icon: string;
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

const isOpen = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const computedSubmitLabel = computed(
  () => props.submitLabel ?? (props.isEdit ? '儲存' : '建立')
);
</script>

<style scoped>
.dialog-top-bar {
  z-index: 10;
  box-shadow: 0 1px 5px rgba(0,0,0,0.1);
}
.dialog-header {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}
</style>
