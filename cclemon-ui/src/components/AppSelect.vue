<template>
  <!-- 桌機：標準下拉選單 -->
  <q-select
    v-if="!isMobile"
    v-bind="$attrs"
    v-model="proxyValue"
    :options="normalizedOptions"
    :label="label"
    :clearable="clearable"
    :disable="disable"
    :loading="loading"
    :rules="rules"
    outlined
    dense
    emit-value
    map-options
    behavior="menu"
  >
    <template v-if="prependIcon" #prepend>
      <q-icon :name="prependIcon" />
    </template>
  </q-select>

  <!-- 手機：觸發欄位 + 底部 Drawer -->
  <div v-else v-bind="$attrs" class="app-select-wrapper" @click="openDrawer">
    <q-field
      :model-value="proxyValue"
      :label="label"
      :rules="rules"
      outlined
      dense
      class="cursor-pointer"
      stack-label
    >
      <template #control>
        <div class="self-center full-width ellipsis app-select-display">
          {{ selectedLabel }}
        </div>
      </template>
      <template v-if="prependIcon" #prepend>
        <q-icon :name="prependIcon" />
      </template>
      <template #append>
        <q-icon
          v-if="clearable && proxyValue != null"
          name="cancel"
          class="cursor-pointer"
          @click.stop="clear"
        />
        <q-icon v-else name="expand_more" color="grey-6" />
      </template>
    </q-field>

    <!-- 底部 Drawer 選項 -->
    <q-dialog v-model="drawerOpen" position="bottom">
      <q-card class="app-select-drawer">
        <div class="drawer-handle" />
        <div class="q-px-md q-pt-xs q-pb-sm text-subtitle2 text-grey-7">
          {{ label }}
        </div>
        <q-separator />
        <q-scroll-area
          :style="`height: min(${(normalizedOptions.length + (clearable ? 1 : 0)) * 48 + 24}px, 55vh)`"
        >
          <q-list padding>
            <q-item v-if="clearable" clickable @click="select(null)">
              <q-item-section>
                <q-item-label class="text-grey-5">不限</q-item-label>
              </q-item-section>
              <q-item-section v-if="proxyValue == null" side>
                <q-icon color="teal-8" name="check" size="xs" />
              </q-item-section>
            </q-item>

            <q-item
              v-for="opt in normalizedOptions"
              :key="String(opt.value)"
              clickable
              @click="select(opt.value)"
            >
              <q-item-section>
                <q-item-label
                  :class="
                    proxyValue === opt.value
                      ? 'text-teal-8 text-weight-medium'
                      : ''
                  "
                >
                  {{ opt.label }}
                </q-item-label>
              </q-item-section>
              <q-item-section v-if="proxyValue === opt.value" side>
                <q-icon color="teal-8" name="check" size="xs" />
              </q-item-section>
            </q-item>
          </q-list>
        </q-scroll-area>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useQuasar } from 'quasar';

defineOptions({ inheritAttrs: false });

type OptionValue = string | number | boolean | null;

interface SelectOption {
  label: string;
  value: OptionValue;
}

type Rule = (val: OptionValue) => true | string;

const props = withDefaults(
  defineProps<{
    modelValue: OptionValue;
    options: (SelectOption | string)[];
    label?: string;
    clearable?: boolean;
    disable?: boolean;
    loading?: boolean;
    rules?: Rule[];
    prependIcon?: string;
  }>(),
  {
    clearable: false,
    disable: false,
    loading: false,
  },
);

const emit = defineEmits<{
  'update:modelValue': [value: OptionValue];
}>();

const $q = useQuasar();
const drawerOpen = ref(false);
const isMobile = computed(() => $q.screen.lt.md);

const normalizedOptions = computed<SelectOption[]>(() =>
  props.options.map((opt) =>
    typeof opt === 'string' ? { label: opt, value: opt } : opt,
  ),
);

const proxyValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const selectedLabel = computed(
  () =>
    normalizedOptions.value.find((o) => o.value === props.modelValue)?.label ??
    '',
);

function openDrawer() {
  if (!props.disable) {
    drawerOpen.value = true;
  }
}

function select(value: OptionValue) {
  proxyValue.value = value;
  drawerOpen.value = false;
}

function clear() {
  proxyValue.value = null;
}
</script>

<style scoped>
.app-select-wrapper {
  position: relative;
}

.app-select-display {
  pointer-events: none;
}

.app-select-drawer {
  border-radius: 16px 16px 0 0 !important;
  width: 100%;
  max-width: 100% !important;
}

.drawer-handle {
  width: 40px;
  height: 4px;
  background: rgba(0, 0, 0, 0.12);
  border-radius: 2px;
  margin: 12px auto 8px;
}
</style>
