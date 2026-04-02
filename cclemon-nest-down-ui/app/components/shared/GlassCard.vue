<script setup lang="ts">
defineProps<{
  padding?: string
  organic?: boolean
  shadow?: 'sm' | 'md' | 'lg'
}>()
</script>

<template>
  <div class="glass-card" :class="[{ organic }, shadow ? `shadow-${shadow}` : '']">
    <slot />
  </div>
</template>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.32);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 22px;
  padding: 20px;
  position: relative;
  box-shadow: 0 4px 12px rgba(10, 148, 136, 0.12);
}

.glass-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 12px;
  right: 12px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.9), transparent);
  border-radius: 22px 22px 0 0;
  pointer-events: none;
}

.glass-card.organic {
  border-radius: 22px 8px 22px 8px;
}

.glass-card.shadow-sm {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.glass-card.shadow-md {
  box-shadow: 0 4px 12px rgba(10, 148, 136, 0.12);
}

.glass-card.shadow-lg {
  box-shadow: 0 8px 32px rgba(10, 148, 136, 0.18);
}
</style>
