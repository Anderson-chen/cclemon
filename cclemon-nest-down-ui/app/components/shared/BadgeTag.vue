<script setup lang="ts">
defineProps<{
  color?: 'teal' | 'orange' | 'amber' | 'red' | 'blue' | 'green'
  size?: 'sm' | 'md'
}>()
</script>

<template>
  <span class="badge" :class="[color ?? 'teal', size ?? 'md']">
    <slot />
  </span>
</template>

<style scoped>
.badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-family: 'Raleway', sans-serif;
  font-weight: 600;
  border-radius: 999px;
  white-space: nowrap;
}

.badge.sm {
  font-size: 11px;
  padding: 3px 10px;
}

.badge.md {
  font-size: 12px;
  padding: 4px 12px;
}

.badge.teal {
  background: rgba(10, 148, 136, 0.12);
  color: #0A9488;
  border: 1px solid rgba(10, 148, 136, 0.2);
}

.badge.orange {
  background: rgba(232, 96, 16, 0.12);
  color: #E86010;
  border: 1px solid rgba(232, 96, 16, 0.2);
}

.badge.amber {
  background: rgba(245, 158, 11, 0.12);
  color: #b45309;
  border: 1px solid rgba(245, 158, 11, 0.25);
}

.badge.red {
  background: rgba(229, 62, 62, 0.1);
  color: #c53030;
  border: 1px solid rgba(229, 62, 62, 0.2);
}

.badge.blue {
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.badge.green {
  background: rgba(34, 197, 94, 0.1);
  color: #15803d;
  border: 1px solid rgba(34, 197, 94, 0.2);
}
</style>
