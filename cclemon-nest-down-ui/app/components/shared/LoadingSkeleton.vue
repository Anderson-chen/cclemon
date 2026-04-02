<script setup lang="ts">
defineProps<{
  width?: string
  height?: string
  borderRadius?: string
  lines?: number
}>()
</script>

<template>
  <div v-if="lines && lines > 1" class="skeleton-group">
    <div
      v-for="i in lines"
      :key="i"
      class="skeleton"
      :style="{
        width: i === lines ? '70%' : '100%',
        height: height ?? '16px',
        borderRadius: borderRadius ?? '8px'
      }"
    />
  </div>
  <div
    v-else
    class="skeleton"
    :style="{
      width: width ?? '100%',
      height: height ?? '16px',
      borderRadius: borderRadius ?? '8px'
    }"
  />
</template>

<style scoped>
.skeleton {
  background: linear-gradient(
    90deg,
    rgba(10, 148, 136, 0.08) 0%,
    rgba(10, 148, 136, 0.18) 50%,
    rgba(10, 148, 136, 0.08) 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

.skeleton-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
