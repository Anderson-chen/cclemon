<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import type { FoodEntry } from '@/types/models'
import { today } from '@/utils/dateUtils'

const router = useRouter()
const diet = useDietStore()

const selectedMealType = ref<'breakfast' | 'lunch' | 'dinner' | 'snack'>('breakfast')
const pendingFoods = ref<FoodEntry[]>([])

const mealTypes = [
  { value: 'breakfast', label: '早餐' },
  { value: 'lunch', label: '午餐' },
  { value: 'dinner', label: '晚餐' },
  { value: 'snack', label: '點心' },
] as const

function handleFoodSelect(entry: FoodEntry) {
  pendingFoods.value.push(entry)
}

function removePendingFood(i: number) {
  pendingFoods.value.splice(i, 1)
}

async function save() {
  if (!pendingFoods.value.length) return
  await diet.addMeal({
    userId: '1',
    date: today(),
    type: selectedMealType.value,
    foods: [...pendingFoods.value]
  })
  navigateTo('/diet')
}
</script>

<template>
  <PageShell>
    <div class="add-content">
      <!-- Header -->
      <header class="page-header">
        <GlassButton size="sm" @click="router.back()">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          返回
        </GlassButton>
        <h1 class="page-title">新增飲食</h1>
      </header>

      <!-- Meal type -->
      <div class="section">
        <p class="section-label">餐別</p>
        <div class="meal-type-selector">
          <button
            v-for="mt in mealTypes"
            :key="mt.value"
            class="type-btn"
            :class="{ active: selectedMealType === mt.value }"
            @click="selectedMealType = mt.value"
          >
            {{ mt.label }}
          </button>
        </div>
      </div>

      <!-- Pending foods summary -->
      <div v-if="pendingFoods.length > 0" class="pending-section">
        <div class="pending-header">
          <h4 class="pending-title">已選擇 {{ pendingFoods.length }} 項食物</h4>
          <span class="pending-cal">
            共 {{ pendingFoods.reduce((s, f) => s + f.calories, 0) }} kcal
          </span>
        </div>
        <div class="pending-list">
          <div v-for="(food, i) in pendingFoods" :key="i" class="pending-item">
            <span class="pending-name">{{ food.name }}</span>
            <span class="pending-cal-sm">{{ food.calories }}kcal</span>
            <button class="remove-btn" @click="removePendingFood(i)" aria-label="移除">✕</button>
          </div>
        </div>
        <OrangeButton full-width size="lg" @click="save">
          儲存{{ mealTypes.find(m => m.value === selectedMealType)?.label }}記錄
        </OrangeButton>
      </div>

      <!-- Food search -->
      <div class="section">
        <FoodSearchModal @select="handleFoodSelect" />
      </div>
    </div>
  </PageShell>
</template>

<style scoped>
.add-content {
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-family: 'Lora', serif;
  font-size: 22px;
  font-weight: 700;
  color: #083830;
}

.section {
  margin-bottom: 20px;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 8px;
}

.meal-type-selector {
  display: flex;
  gap: 8px;
  background: rgba(10, 148, 136, 0.06);
  border-radius: 10px;
  padding: 3px;
}

.type-btn {
  flex: 1;
  padding: 10px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: rgba(8, 56, 48, 0.5);
  cursor: pointer;
  transition: all 200ms;
}

.type-btn.active {
  background: white;
  color: #0A9488;
  box-shadow: 0 1px 4px rgba(10, 148, 136, 0.15);
}

.pending-section {
  background: rgba(10, 148, 136, 0.06);
  border: 1px solid rgba(10, 148, 136, 0.15);
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pending-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pending-title {
  font-size: 14px;
  font-weight: 600;
  color: #0A9488;
}

.pending-cal {
  font-size: 14px;
  font-weight: 700;
  color: #E86010;
}

.pending-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 8px;
}

.pending-name {
  flex: 1;
  font-size: 13px;
  color: #083830;
}

.pending-cal-sm {
  font-size: 12px;
  color: #E86010;
  font-weight: 600;
}

.remove-btn {
  background: none;
  border: none;
  color: rgba(8, 56, 48, 0.4);
  cursor: pointer;
  font-size: 12px;
  padding: 4px;
}
</style>
