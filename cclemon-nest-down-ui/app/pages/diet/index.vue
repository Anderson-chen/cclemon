<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import type { FoodEntry } from '@/types/models'
import { today, formatDate } from '@/utils/dateUtils'

const diet = useDietStore()

onMounted(async () => {
  await Promise.all([
    diet.fetchMeals(today()),
    diet.fetchFoodLibrary(),
  ])
})

const selectedDate = ref(today())
const showAddSheet = ref(false)
const selectedMealType = ref<'breakfast' | 'lunch' | 'dinner' | 'snack'>('breakfast')
const pendingFoods = ref<FoodEntry[]>([])

const mealTypes = [
  { value: 'breakfast', label: '早餐' },
  { value: 'lunch', label: '午餐' },
  { value: 'dinner', label: '晚餐' },
  { value: 'snack', label: '點心' },
] as const

function mealsByType(type: string) {
  return diet.meals.filter(m => m.date === selectedDate.value && m.type === type)
}

function handleFoodSelect(entry: FoodEntry) {
  pendingFoods.value.push(entry)
}

function removePendingFood(i: number) {
  pendingFoods.value.splice(i, 1)
}

async function saveMeal() {
  if (!pendingFoods.value.length) return
  await diet.addMeal({
    userId: '1',
    date: selectedDate.value,
    type: selectedMealType.value,
    foods: [...pendingFoods.value]
  })
  pendingFoods.value = []
  showAddSheet.value = false
}
</script>

<template>
  <PageShell>
    <div class="screen-bg" aria-hidden="true">
      <div class="blob blob-1" />
      <div class="blob blob-2" />
    </div>

    <div class="diet-content">
      <!-- Header -->
      <header class="page-header">
        <h1 class="page-title">飲食記錄</h1>
        <div class="date-selector">
          <input
            v-model="selectedDate"
            type="date"
            class="date-input"
            :max="today()"
          />
        </div>
      </header>

      <!-- Copy Yesterday -->
      <div class="section">
        <CopyYesterdayButton />
      </div>

      <!-- Nutrition Summary -->
      <GlassCard class="section">
        <h2 class="section-title">今日營養</h2>
        <NutritionBar />
      </GlassCard>

      <!-- Meal sections -->
      <div v-for="mt in mealTypes" :key="mt.value" class="meal-section">
        <div class="meal-section-header">
          <h3 class="meal-section-title">{{ mt.label }}</h3>
          <button
            class="add-meal-btn"
            @click="() => { selectedMealType = mt.value; showAddSheet = true }"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            新增
          </button>
        </div>

        <div v-if="mealsByType(mt.value).length === 0" class="empty-meal">
          尚未記錄{{ mt.label }}
        </div>
        <div v-else class="meal-list">
          <MealCard
            v-for="meal in mealsByType(mt.value)"
            :key="meal.id"
            :meal="meal"
          />
        </div>
      </div>
    </div>

    <!-- FAB -->
    <button class="fab" @click="showAddSheet = true" aria-label="新增餐點">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <path d="M12 5v14M5 12h14"/>
      </svg>
    </button>

    <!-- Add Meal Sheet -->
    <BottomSheet v-model="showAddSheet" title="新增餐點" height="full">
      <div class="add-sheet-content">
        <!-- Meal type selector -->
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

        <!-- Pending foods -->
        <div v-if="pendingFoods.length > 0" class="pending-foods">
          <h4 class="pending-title">已選擇</h4>
          <div v-for="(food, i) in pendingFoods" :key="i" class="pending-item">
            <span class="pending-name">{{ food.name }}</span>
            <span class="pending-cal">{{ food.calories }} kcal</span>
            <button class="remove-btn" @click="removePendingFood(i)">✕</button>
          </div>
          <OrangeButton full-width @click="saveMeal">
            儲存 {{ mealTypes.find(m => m.value === selectedMealType)?.label }}
          </OrangeButton>
        </div>

        <FoodSearchModal @select="handleFoodSelect" />
      </div>
    </BottomSheet>
  </PageShell>
</template>

<style scoped>
.diet-content {
  position: relative;
  z-index: 1;
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-family: 'Lora', serif;
  font-size: 24px;
  font-weight: 700;
  color: #083830;
}

.date-input {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.5);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 10px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  color: #083830;
  outline: none;
}

.date-input:focus {
  border-color: #0A9488;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-family: 'Lora', serif;
  font-size: 15px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 14px;
}

.meal-section {
  margin-bottom: 20px;
}

.meal-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.meal-section-title {
  font-size: 16px;
  font-weight: 600;
  color: #083830;
}

.add-meal-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(10, 148, 136, 0.08);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #0A9488;
  cursor: pointer;
  transition: all 200ms;
}

.add-meal-btn:hover {
  background: rgba(10, 148, 136, 0.14);
}

.empty-meal {
  font-size: 13px;
  color: rgba(8, 56, 48, 0.4);
  padding: 16px;
  text-align: center;
  background: rgba(10, 148, 136, 0.03);
  border-radius: 10px;
  border: 1px dashed rgba(10, 148, 136, 0.15);
}

.meal-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.fab {
  position: fixed;
  bottom: calc(80px + env(safe-area-inset-bottom));
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: #E86010;
  border: none;
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(232, 96, 16, 0.4);
  transition: all 200ms;
  z-index: 50;
}

.fab:hover {
  background: #C05010;
  transform: scale(1.05);
}

.fab:active {
  transform: scale(0.95);
}

.add-sheet-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 24px;
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
  padding: 8px;
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

.pending-foods {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
  background: rgba(10, 148, 136, 0.06);
  border-radius: 12px;
  border: 1px solid rgba(10, 148, 136, 0.15);
}

.pending-title {
  font-size: 13px;
  font-weight: 700;
  color: #0A9488;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pending-name {
  flex: 1;
  font-size: 13px;
  color: #083830;
  font-weight: 500;
}

.pending-cal {
  font-size: 12px;
  font-weight: 700;
  color: #E86010;
}

.remove-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(8, 56, 48, 0.4);
  font-size: 13px;
  padding: 4px;
}

@media (min-width: 1024px) {
  .fab {
    bottom: 24px;
    right: 40px;
  }
}
</style>
