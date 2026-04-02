<script setup lang="ts">
import { useRouter } from 'vue-router'
import OrangeButton from '@/components/shared/OrangeButton.vue'
import GlassButton from '@/components/shared/GlassButton.vue'

const router = useRouter()

const actions = [
  { id: 'diet', label: '+ 飲食', path: '/diet/add', primary: true },
  { id: 'exercise', label: '+ 運動', path: '/exercise/add', primary: false },
  { id: 'weight', label: '+ 體重', path: '/weight', primary: false },
  { id: 'water', label: '+ 水', path: '/home', primary: false, scroll: 'water' },
]
</script>

<template>
  <div class="quick-bar-wrap">
    <div class="quick-bar">
      <template v-for="action in actions" :key="action.id">
        <OrangeButton
          v-if="action.primary"
          size="sm"
          @click="router.push(action.path)"
        >
          {{ action.label }}
        </OrangeButton>
        <GlassButton
          v-else
          size="sm"
          @click="router.push(action.path)"
        >
          {{ action.label }}
        </GlassButton>
      </template>
    </div>
  </div>
</template>

<style scoped>
.quick-bar-wrap {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  padding: 4px 0;
}

.quick-bar-wrap::-webkit-scrollbar {
  display: none;
}

.quick-bar {
  display: flex;
  gap: 10px;
  min-width: max-content;
  padding: 0 2px;
}
</style>
