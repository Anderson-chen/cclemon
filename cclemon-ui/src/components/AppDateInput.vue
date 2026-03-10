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
    @click="proxyRef?.show()"
  >
    <template v-if="props.prependIcon" v-slot:prepend>
      <q-icon :name="props.prependIcon" />
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
</template>

<script setup lang="ts">
import { ref } from 'vue';

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
