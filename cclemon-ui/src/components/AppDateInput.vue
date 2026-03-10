<template>
  <q-input
    :model-value="modelValue"
    :label="label"
    outlined
    dense
    readonly
    :rules="rules"
    :style="inputStyle"
    class="app-date-input cursor-pointer"
    @click="proxyRef?.show()"
  >
    <template v-if="prependIcon" v-slot:prepend>
      <q-icon :name="prependIcon" />
    </template>
    <template v-slot:append>
      <q-icon name="calendar_today" class="cursor-pointer" @click.stop="proxyRef?.show()">
        <q-popup-proxy
          ref="proxyRef"
          cover
          transition-show="scale"
          transition-hide="scale"
        >
          <q-date
            :model-value="modelValue"
            :mask="mask ?? 'YYYY-MM-DD'"
            :default-view="defaultView"
            :emit-immediately="emitImmediately"
            color="teal-8"
            today-btn
            @update:model-value="onSelect"
          />
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  modelValue: string;
  label: string;
  prependIcon?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  rules?: Array<(val: any) => boolean | string>;
  mask?: string;
  defaultView?: 'Calendar' | 'Months' | 'Years';
  emitImmediately?: boolean;
  inputStyle?: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string];
}>();

const proxyRef = ref();

function onSelect(val: string) {
  emit('update:modelValue', val);
  proxyRef.value?.hide();
}
</script>

<style scoped>
.app-date-input :deep(.q-field__native) {
  cursor: pointer;
}
</style>
