<script setup lang="ts">
import { ref, computed } from 'vue'
import type { FoodItem, FoodEntry } from '@/types/models'
import { useDietStore } from '@/stores/dietStore'
import InputField from '@/components/shared/InputField.vue'
import OrangeButton from '@/components/shared/OrangeButton.vue'
import GlassButton from '@/components/shared/GlassButton.vue'

const emit = defineEmits<{
  select: [entry: FoodEntry]
}>()

const diet = useDietStore()
const searchQuery = ref('')
const mode = ref<'search' | 'manual'>('search')
const selectedFood = ref<FoodItem | null>(null)
const amount = ref(100)

// Manual entry
const manualEntry = ref({
  name: '',
  calories: 0,
  protein: 0,
  carbs: 0,
  fat: 0,
  amount: 100
})

const filteredFoods = computed(() => {
  if (!searchQuery.value.trim()) return diet.foodLibrary
  const q = searchQuery.value.toLowerCase()
  return diet.foodLibrary.filter(f => f.name.toLowerCase().includes(q))
})

function selectFood(food: FoodItem) {
  selectedFood.value = food
  amount.value = food.servingGrams
}

function addSelected() {
  if (!selectedFood.value) return
  const ratio = amount.value / selectedFood.value.servingGrams
  emit('select', {
    foodId: selectedFood.value.id,
    name: selectedFood.value.name,
    calories: Math.round(selectedFood.value.calories * ratio),
    protein: Math.round(selectedFood.value.protein * ratio * 10) / 10,
    carbs: Math.round(selectedFood.value.carbs * ratio * 10) / 10,
    fat: Math.round(selectedFood.value.fat * ratio * 10) / 10,
    amount: amount.value
  })
  selectedFood.value = null
  amount.value = 100
}

function addManual() {
  if (!manualEntry.value.name) return
  emit('select', { ...manualEntry.value })
  manualEntry.value = { name: '', calories: 0, protein: 0, carbs: 0, fat: 0, amount: 100 }
}
</script>

<template>
  <div class="food-modal">
    <!-- Mode toggle -->
    <div class="mode-toggle">
      <button class="mode-btn" :class="{ active: mode === 'search' }" @click="mode = 'search'">搜尋食物庫</button>
      <button class="mode-btn" :class="{ active: mode === 'manual' }" @click="mode = 'manual'">手動輸入</button>
    </div>

    <!-- Search mode -->
    <div v-if="mode === 'search'" class="search-mode">
      <div class="search-wrap">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
        </svg>
        <input
          v-model="searchQuery"
          class="search-input"
          type="text"
          placeholder="搜尋食物..."
        />
      </div>

      <!-- Selected food amount input -->
      <div v-if="selectedFood" class="selected-food-panel">
        <div class="selected-header">
          <span class="selected-name">{{ selectedFood.name }}</span>
          <button class="deselect-btn" @click="selectedFood = null">✕</button>
        </div>
        <div class="amount-row">
          <InputField
            v-model="amount"
            type="number"
            label="份量 (g)"
            :hint="`每 ${selectedFood.servingGrams}g = ${selectedFood.calories} kcal`"
          />
        </div>
        <OrangeButton full-width @click="addSelected">加入此食物</OrangeButton>
      </div>

      <!-- Food list -->
      <div v-else class="food-list">
        <p v-if="filteredFoods.length === 0" class="empty-msg">找不到符合的食物</p>
        <button
          v-for="food in filteredFoods"
          :key="food.id"
          class="food-row"
          @click="selectFood(food)"
        >
          <div class="food-info">
            <span class="food-name">{{ food.name }}</span>
            <span class="food-meta">每 {{ food.servingGrams }}g</span>
          </div>
          <div class="food-cal-badge">{{ food.calories }} kcal</div>
        </button>
      </div>
    </div>

    <!-- Manual mode -->
    <div v-else class="manual-mode">
      <div class="manual-fields">
        <InputField v-model="manualEntry.name" label="食物名稱" required placeholder="例如：水煮雞胸肉" />
        <div class="fields-grid">
          <InputField v-model="manualEntry.amount" type="number" label="份量 (g)" />
          <InputField v-model="manualEntry.calories" type="number" label="熱量 (kcal)" />
          <InputField v-model="manualEntry.protein" type="number" label="蛋白質 (g)" />
          <InputField v-model="manualEntry.carbs" type="number" label="碳水化合物 (g)" />
          <InputField v-model="manualEntry.fat" type="number" label="脂肪 (g)" />
        </div>
      </div>
      <OrangeButton full-width @click="addManual">加入食物</OrangeButton>
    </div>
  </div>
</template>

<style scoped>
.food-modal {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.mode-toggle {
  display: flex;
  background: rgba(10, 148, 136, 0.06);
  border-radius: 10px;
  padding: 3px;
  gap: 2px;
}

.mode-btn {
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

.mode-btn.active {
  background: white;
  color: #0A9488;
  box-shadow: 0 1px 4px rgba(10, 148, 136, 0.15);
}

.search-wrap {
  position: relative;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #0A7870;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 38px;
  background: rgba(255, 255, 255, 0.6);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 12px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  color: #083830;
  outline: none;
}

.search-input:focus {
  border-color: #0A9488;
  box-shadow: 0 0 0 3px rgba(10, 148, 136, 0.1);
}

.food-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 280px;
  overflow-y: auto;
}

.food-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.45);
  border: 1px solid rgba(10, 148, 136, 0.1);
  border-radius: 10px;
  cursor: pointer;
  text-align: left;
  width: 100%;
  transition: all 200ms;
}

.food-row:hover {
  background: rgba(10, 148, 136, 0.06);
  border-color: rgba(10, 148, 136, 0.2);
}

.food-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.food-name {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.food-meta {
  font-size: 11px;
  color: #0A7870;
}

.food-cal-badge {
  font-size: 12px;
  font-weight: 700;
  color: #E86010;
  background: rgba(232, 96, 16, 0.08);
  padding: 3px 10px;
  border-radius: 8px;
}

.selected-food-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px;
  background: rgba(10, 148, 136, 0.06);
  border-radius: 12px;
  border: 1px solid rgba(10, 148, 136, 0.15);
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-name {
  font-weight: 600;
  color: #083830;
}

.deselect-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #0A7870;
  font-size: 14px;
}

.empty-msg {
  text-align: center;
  color: #0A7870;
  font-size: 14px;
  padding: 24px;
}

.manual-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.amount-row {
  width: 100%;
}
</style>
