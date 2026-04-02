<script setup lang="ts">
defineProps<{
  loading?: boolean
  disabled?: boolean
  size?: 'sm' | 'md' | 'lg'
  fullWidth?: boolean
  type?: 'button' | 'submit' | 'reset'
}>()

defineEmits<{
  click: []
}>()
</script>

<template>
  <button
    class="btn-cta"
    :class="[size ?? 'md', { full: fullWidth }]"
    :disabled="loading || disabled"
    :type="type ?? 'button'"
    @click="$emit('click')"
  >
    <span v-if="loading" class="spinner" />
    <slot v-else />
  </button>
</template>

<style scoped>
.btn-cta {
  background: #E86010;
  color: white;
  border: none;
  padding: 13px 24px;
  border-radius: 13px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.9px;
  text-transform: uppercase;
  cursor: pointer;
  min-height: 44px;
  min-width: 44px;
  transition: all 200ms ease;
  box-shadow: 0 4px 16px rgba(232, 96, 16, 0.25);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-cta:hover:not(:disabled) {
  background: #C05010;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(232, 96, 16, 0.35);
}

.btn-cta:active:not(:disabled) {
  transform: translateY(0);
}

.btn-cta:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cta.sm {
  padding: 10px 16px;
  font-size: 11px;
  border-radius: 10px;
}

.btn-cta.lg {
  padding: 16px 32px;
  font-size: 14px;
  border-radius: 16px;
}

.btn-cta.full {
  width: 100%;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
