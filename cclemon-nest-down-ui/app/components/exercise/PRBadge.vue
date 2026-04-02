<script setup lang="ts">
// No props needed - this is a static PR indicator badge
</script>

<template>
  <span class="pr-badge">
    <svg width="11" height="11" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
    </svg>
    PR
  </span>
</template>

<style scoped>
.pr-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: linear-gradient(135deg, #E86010, #f59e0b);
  color: white;
  font-size: 10px;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 999px;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 6px rgba(232, 96, 16, 0.35);
}
</style>
