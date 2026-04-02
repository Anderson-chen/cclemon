<script setup lang="ts">
import { watch, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  modelValue: boolean
  title?: string
  height?: 'auto' | 'half' | 'full'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

function close() {
  emit('update:modelValue', false)
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') close()
}

onMounted(() => document.addEventListener('keydown', handleKeydown))
onUnmounted(() => document.removeEventListener('keydown', handleKeydown))

watch(() => props.modelValue, (val) => {
  document.body.style.overflow = val ? 'hidden' : ''
})
</script>

<template>
  <Teleport to="body">
    <Transition name="sheet">
      <div v-if="modelValue" class="sheet-overlay" @click.self="close">
        <div class="sheet-panel" :class="height ?? 'auto'" role="dialog" aria-modal="true">
          <div class="sheet-handle" @click="close">
            <div class="handle-bar" />
          </div>
          <div v-if="title" class="sheet-header">
            <h3 class="sheet-title">{{ title }}</h3>
            <button class="close-btn" @click="close" aria-label="關閉">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="sheet-content">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.sheet-overlay {
  position: fixed;
  inset: 0;
  background: rgba(8, 56, 48, 0.4);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  backdrop-filter: blur(4px);
}

.sheet-panel {
  width: 100%;
  background: #F0FDFA;
  border-radius: 24px 24px 0 0;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  box-shadow: 0 -8px 40px rgba(10, 148, 136, 0.18);
  max-height: 92vh;
  display: flex;
  flex-direction: column;
}

.sheet-panel.half {
  max-height: 55vh;
}

.sheet-panel.full {
  max-height: 92vh;
}

.sheet-handle {
  padding: 12px;
  display: flex;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
}

.handle-bar {
  width: 40px;
  height: 4px;
  background: rgba(10, 148, 136, 0.25);
  border-radius: 2px;
}

.sheet-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px 16px;
  flex-shrink: 0;
}

.sheet-title {
  font-size: 18px;
  font-weight: 600;
  color: #083830;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(10, 148, 136, 0.1);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0A9488;
  transition: background 200ms;
}

.close-btn:hover {
  background: rgba(10, 148, 136, 0.2);
}

.sheet-content {
  overflow-y: auto;
  padding: 0 20px;
  flex: 1;
}

/* Transitions */
.sheet-enter-active,
.sheet-leave-active {
  transition: opacity 300ms ease;
}

.sheet-enter-from,
.sheet-leave-to {
  opacity: 0;
}

.sheet-enter-active .sheet-panel,
.sheet-leave-active .sheet-panel {
  transition: transform 300ms cubic-bezier(0.32, 0.72, 0, 1);
}

.sheet-enter-from .sheet-panel,
.sheet-leave-to .sheet-panel {
  transform: translateY(100%);
}
</style>
