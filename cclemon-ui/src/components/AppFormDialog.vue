<template>
  <q-dialog v-model="isOpen" persistent :maximized="$q.screen.lt.md">
    <q-card :style="$q.screen.gt.sm ? `width: 95vw; max-width: ${maxWidth}` : ''">
      <!-- 手機：toolbar -->
      <q-toolbar v-if="$q.screen.lt.md" class="bg-teal-8 text-white">
        <q-btn flat round dense icon="close" @click="isOpen = false" class="cursor-pointer" />
        <q-toolbar-title class="text-subtitle1">{{ title }}</q-toolbar-title>
        <q-btn
          flat
          :label="computedSubmitLabel"
          @click="$emit('submit')"
          :loading="submitting"
          class="cursor-pointer"
        />
      </q-toolbar>

      <!-- 桌機：card header -->
      <q-card-section v-else class="dialog-header">
        <div class="row items-center">
          <q-icon :name="icon" color="teal-8" size="sm" class="q-mr-sm" />
          <span class="text-h6">{{ title }}</span>
        </div>
      </q-card-section>

      <!-- 表單內容（響應式高度） -->
      <div
        class="q-pa-md"
        :style="
          $q.screen.lt.md
            ? 'height: calc(100dvh - 50px); overflow-y: auto;'
            : 'max-height: 65vh; overflow-y: auto;'
        "
      >
        <slot />
      </div>

      <!-- 桌機：底部按鈕 -->
      <q-card-actions v-if="$q.screen.gt.sm" align="right" class="q-px-md q-pb-md">
        <q-btn flat label="取消" color="grey-7" @click="isOpen = false" class="cursor-pointer" />
        <q-btn
          unelevated
          :label="computedSubmitLabel"
          color="teal-8"
          @click="$emit('submit')"
          :loading="submitting"
          class="cursor-pointer"
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
.dialog-header {
  border-bottom: 1px solid rgba(0, 150, 136, 0.15);
  padding-bottom: 12px;
}
</style>
