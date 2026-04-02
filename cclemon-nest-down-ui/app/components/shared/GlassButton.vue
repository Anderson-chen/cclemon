<script setup lang="ts">
defineProps<{
  loading?: boolean
  disabled?: boolean
  size?: 'sm' | 'md' | 'lg'
  fullWidth?: boolean
  type?: 'button' | 'submit' | 'reset'
  active?: boolean
}>()

defineEmits<{
  click: []
}>()
</script>

<template>
  <button
    class="btn-glass"
    :class="[size ?? 'md', { full: fullWidth, active }]"
    :disabled="loading || disabled"
    :type="type ?? 'button'"
    @click="$emit('click')"
  >
    <span v-if="loading" class="spinner" />
    <slot v-else />
  </button>
</template>

<style scoped>
.btn-glass {
  background: rgba(255, 255, 255, 0.32);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #0A9488;
  border: 1px solid rgba(255, 255, 255, 0.6);
  padding: 13px 22px;
  border-radius: 13px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  min-height: 44px;
  min-width: 44px;
  transition: all 200ms ease;
  box-shadow: 0 2px 8px rgba(10, 148, 136, 0.10);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-glass:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.5);
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(10, 148, 136, 0.18);
}

.btn-glass:active:not(:disabled) {
  transform: translateY(0);
}

.btn-glass.active {
  background: rgba(10, 148, 136, 0.15);
  border-color: rgba(10, 148, 136, 0.4);
  color: #087870;
}

.btn-glass:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-glass.sm {
  padding: 8px 14px;
  font-size: 11px;
  border-radius: 10px;
  min-height: 36px;
}

.btn-glass.lg {
  padding: 16px 28px;
  font-size: 14px;
  border-radius: 16px;
}

.btn-glass.full {
  width: 100%;
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(10, 148, 136, 0.3);
  border-top-color: #0A9488;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
