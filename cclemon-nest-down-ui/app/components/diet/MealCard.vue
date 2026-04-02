<script setup lang="ts">
import { computed } from 'vue'
import type { Meal } from '@/types/models'
import { useDietStore } from '@/stores/dietStore'
import BadgeTag from '@/components/shared/BadgeTag.vue'

const props = defineProps<{ meal: Meal }>()
const emit = defineEmits<{ delete: [id: string] }>()

const diet = useDietStore()

const mealTypeLabels: Record<string, string> = {
  breakfast: '早餐',
  lunch: '午餐',
  dinner: '晚餐',
  snack: '點心',
}

const mealColors: Record<string, 'orange' | 'teal' | 'blue' | 'amber'> = {
  breakfast: 'amber',
  lunch: 'teal',
  dinner: 'blue',
  snack: 'orange',
}

const totalCalories = computed(() =>
  props.meal.foods.reduce((s, f) => s + f.calories, 0)
)
const totalProtein = computed(() =>
  props.meal.foods.reduce((s, f) => s + f.protein, 0)
)
const totalCarbs = computed(() =>
  props.meal.foods.reduce((s, f) => s + f.carbs, 0)
)
const totalFat = computed(() =>
  props.meal.foods.reduce((s, f) => s + f.fat, 0)
)

async function handleDelete() {
  if (confirm('確定要刪除這筆餐點嗎？')) {
    await diet.deleteMeal(props.meal.id)
    emit('delete', props.meal.id)
  }
}
</script>

<template>
  <div class="meal-card">
    <div class="meal-header">
      <BadgeTag :color="mealColors[meal.type] || 'teal'" size="sm">
        {{ mealTypeLabels[meal.type] }}
      </BadgeTag>
      <div class="meal-header-right">
        <span class="meal-cal">{{ totalCalories }} kcal</span>
        <button class="delete-btn" @click="handleDelete" aria-label="刪除餐點">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
            <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="food-list">
      <div v-for="(food, i) in meal.foods" :key="i" class="food-item">
        <div class="food-main">
          <span class="food-name">{{ food.name }}</span>
          <span class="food-amount">{{ food.amount }}g</span>
        </div>
        <div class="food-macros">
          <span class="macro">{{ food.calories }}kcal</span>
          <span class="macro-dot">·</span>
          <span class="macro">蛋白 {{ food.protein }}g</span>
          <span class="macro-dot">·</span>
          <span class="macro">碳水 {{ food.carbs }}g</span>
        </div>
      </div>
    </div>

    <div class="meal-totals">
      <div class="total-item">
        <span class="total-val">{{ totalCalories }}</span>
        <span class="total-lbl">kcal</span>
      </div>
      <div class="total-item">
        <span class="total-val">{{ totalProtein }}g</span>
        <span class="total-lbl">蛋白質</span>
      </div>
      <div class="total-item">
        <span class="total-val">{{ totalCarbs }}g</span>
        <span class="total-lbl">碳水化合物</span>
      </div>
      <div class="total-item">
        <span class="total-val">{{ totalFat }}g</span>
        <span class="total-lbl">脂肪</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.meal-card {
  background: rgba(255, 255, 255, 0.38);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(10, 148, 136, 0.08);
  animation: slideIn 300ms ease-out;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}

.meal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.meal-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meal-cal {
  font-size: 14px;
  font-weight: 700;
  color: #E86010;
}

.delete-btn {
  width: 44px;
  height: 44px;
  border: none;
  background: rgba(229, 62, 62, 0.08);
  border-radius: 10px;
  cursor: pointer;
  color: rgba(229, 62, 62, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms;
  margin: -10px -10px -10px 0;
}

.delete-btn:hover {
  background: rgba(229, 62, 62, 0.14);
  color: #e53e3e;
}

.food-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.food-item {
  padding: 8px 10px;
  background: rgba(10, 148, 136, 0.04);
  border-radius: 8px;
}

.food-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3px;
}

.food-name {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.food-amount {
  font-size: 12px;
  color: #0A7870;
}

.food-macros {
  display: flex;
  align-items: center;
  gap: 4px;
}

.macro {
  font-size: 11px;
  color: rgba(8, 56, 48, 0.55);
}

.macro-dot {
  font-size: 10px;
  color: rgba(8, 56, 48, 0.3);
}

.meal-totals {
  display: flex;
  gap: 12px;
  padding-top: 10px;
  border-top: 1px solid rgba(10, 148, 136, 0.08);
}

.total-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1px;
}

.total-val {
  font-size: 13px;
  font-weight: 700;
  color: #083830;
}

.total-lbl {
  font-size: 10px;
  color: #0A7870;
}
</style>
