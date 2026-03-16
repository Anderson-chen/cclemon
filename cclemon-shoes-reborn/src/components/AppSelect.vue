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
    
    <!-- Forward option slot -->
    <template v-if="$slots['option']" #option="scope">
      <slot name="option" v-bind="scope" />
    </template>
    <!-- Forward no-option slot -->
    <template v-if="$slots['no-option']" #no-option="scope">
      <slot name="no-option" v-bind="scope" />
    </template>
  </q-select>

  <!-- 手機：Action Sheet（iOS 風格） -->
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

    <!-- Action Sheet Dialog -->
    <q-dialog v-model="drawerOpen" position="bottom" class="app-select-dialog">
      <div
        class="action-sheet-container"
        :style="dragY > 0 ? `transform: translateY(${dragY}px); transition: none` : ''"
        @touchstart="onTouchStart"
        @touchmove.prevent="onTouchMove"
        @touchend="onTouchEnd"
      >
        <!-- 選項卡片 -->
        <q-card class="action-sheet-card">
          <!-- 標題 -->
          <div class="action-sheet-title">{{ label }}</div>

          <q-separator />

          <!-- 不限（clearable） -->
          <template v-if="clearable">
            <button class="action-sheet-item" @click="select(null)">
              <span :class="proxyValue == null ? 'text-teal-8 text-weight-bold' : 'text-grey-8'">不限</span>
            </button>
            <q-separator />
          </template>

          <template v-for="(opt, idx) in normalizedOptions" :key="String(opt.value)">
            <button class="action-sheet-item" @click="select(opt.value)">
              <!-- Mobile Action Sheet item customization slot -->
              <slot name="mobile-option" :opt="opt" :is-selected="proxyValue === opt.value">
                <span :class="proxyValue === opt.value ? 'text-teal-8 text-weight-bold' : 'text-grey-8'">
                  {{ opt.label }}
                </span>
              </slot>
            </button>
            <q-separator v-if="idx < normalizedOptions.length - 1" />
          </template>
        </q-card>

        <!-- 取消按鈕（獨立卡片） -->
        <q-card class="action-sheet-cancel">
          <button class="action-sheet-item action-sheet-cancel-btn" @click="drawerOpen = false">
            取消
          </button>
        </q-card>
      </div>
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
const isMobile = computed(() => $q.screen.lt.md);
const drawerOpen = ref(false);
const dragY = ref(0);
let dragStartY = 0;

function onTouchStart(e: TouchEvent) {
  dragStartY = e.touches[0]!.clientY;
  dragY.value = 0;
}

function onTouchMove(e: TouchEvent) {
  const delta = e.touches[0]!.clientY - dragStartY;
  if (delta > 0) dragY.value = delta;
}

function onTouchEnd() {
  if (dragY.value > 80) {
    drawerOpen.value = false;
  }
  dragY.value = 0;
}

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

/* 強制 AppSelect dialog 水平置中（:global 穿透 teleport） */
:global(.app-select-dialog .q-dialog__inner) {
  justify-content: center !important;
  align-items: flex-end !important;
}

/* Action Sheet 容器：左右有間距，底部有安全區域 */
.action-sheet-container {
  width: calc(100% - 16px);
  max-width: 480px;
  padding: 0 0 calc(8px + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: transparent;
  box-shadow: none;
}

/* 選項卡片 */
.action-sheet-card {
  border-radius: 14px !important;
  overflow: hidden;
}

/* 標題列 */
.action-sheet-title {
  padding: 12px 16px 10px;
  font-size: 13px;
  color: #8e8e93;
  text-align: center;
  letter-spacing: 0.01em;
}

/* 每一個選項行 */
.action-sheet-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 52px;
  padding: 0 20px;
  background: white;
  border: none;
  cursor: pointer;
  font-size: 17px;
  text-align: center;
  transition: background 0.15s;
}

.action-sheet-item:active {
  background: #f2f2f7;
}

/* 取消按鈕卡片 */
.action-sheet-cancel {
  border-radius: 14px !important;
  overflow: hidden;
}

.action-sheet-cancel-btn {
  justify-content: center;
  color: #007aff;
  font-weight: 600;
}
</style>
