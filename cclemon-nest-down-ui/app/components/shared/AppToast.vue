<script setup lang="ts">
import { useUiStore } from '@/stores/uiStore'

const ui = useUiStore()

function iconForType(type: string) {
  if (type === 'success') return 'M20 6L9 17l-5-5'
  if (type === 'error') return 'M18 6L6 18M6 6l12 12'
  return 'M12 8v4m0 4h.01'
}
</script>

<template>
  <Teleport to="body">
    <div class="toast-container" aria-live="polite">
      <TransitionGroup name="toast">
        <div
          v-for="toast in ui.toasts"
          :key="toast.id"
          class="toast"
          :class="toast.type"
          role="alert"
        >
          <div class="toast-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path :d="iconForType(toast.type)" />
            </svg>
          </div>
          <p class="toast-message">{{ toast.message }}</p>
          <button class="toast-close" @click="ui.removeToast(toast.id)" aria-label="關閉">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
  width: calc(100% - 40px);
  max-width: 420px;
}

.toast {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 14px;
  backdrop-filter: blur(16px);
  pointer-events: all;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.toast.success {
  background: rgba(10, 148, 136, 0.92);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.toast.error {
  background: rgba(229, 62, 62, 0.92);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast.info {
  background: rgba(8, 56, 48, 0.88);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.toast-message {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
}

.toast-close {
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  transition: color 150ms;
}

.toast-close:hover {
  color: white;
}

/* Transitions */
.toast-enter-active,
.toast-leave-active {
  transition: all 300ms ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-16px) scale(0.95);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}
</style>
