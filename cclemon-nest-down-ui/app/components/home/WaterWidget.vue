<script setup lang="ts">
import { ref, computed } from 'vue'
import { useWaterStore } from '@/stores/waterStore'
import { useGoalStore } from '@/stores/goalStore'
import BottomSheet from '@/components/shared/BottomSheet.vue'
import OrangeButton from '@/components/shared/OrangeButton.vue'
import InputField from '@/components/shared/InputField.vue'

const water = useWaterStore()
const goals = useGoalStore()

const showCustomSheet = ref(false)
const customAmount = ref<number | string>('')

const target = computed(() => goals.goals.dailyWaterMl || 2000)
const progress = computed(() => Math.min(1, water.todayTotal / target.value))
const progressPercent = computed(() => Math.round(progress.value * 100))

const quickAmounts = [200, 250, 500]

// Long press handling
let pressTimer: ReturnType<typeof setTimeout> | null = null

function startLongPress() {
  pressTimer = setTimeout(() => {
    showCustomSheet.value = true
  }, 500)
}

function cancelLongPress() {
  if (pressTimer) {
    clearTimeout(pressTimer)
    pressTimer = null
  }
}

async function quickAdd(ml: number) {
  cancelLongPress()
  await water.addWater(ml)
}

async function addCustom() {
  const ml = Number(customAmount.value)
  if (ml > 0 && ml <= 2000) {
    await water.addWater(ml)
    customAmount.value = ''
    showCustomSheet.value = false
  }
}
</script>

<template>
  <div class="water-widget">
    <!-- Header -->
    <div class="widget-header">
      <div class="widget-title-group">
        <div class="water-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
          </svg>
        </div>
        <h3 class="widget-title">今日飲水</h3>
      </div>
      <div class="water-amount">
        <span class="amount-current">{{ water.todayTotal }}</span>
        <span class="amount-sep">/</span>
        <span class="amount-target">{{ target }}ml</span>
      </div>
    </div>

    <!-- Progress bar -->
    <div class="progress-bar-wrap">
      <div class="progress-bar-bg">
        <div class="progress-bar-fill" :style="{ width: `${progressPercent}%` }" />
        <div v-if="progress >= 1" class="progress-complete-glow" />
      </div>
      <span class="progress-text">{{ progressPercent }}%</span>
    </div>

    <!-- Quick add buttons -->
    <div class="quick-water-row">
      <button
        v-for="ml in quickAmounts"
        :key="ml"
        class="water-btn"
        @click="quickAdd(ml)"
        @mousedown="ml === 500 ? startLongPress() : undefined"
        @mouseup="ml === 500 ? cancelLongPress() : undefined"
        @touchstart.passive="ml === 500 ? startLongPress() : undefined"
        @touchend="ml === 500 ? cancelLongPress() : undefined"
        :title="ml === 500 ? '長按可自訂數量' : undefined"
      >
        +{{ ml }}ml
      </button>
      <button class="water-btn custom-btn" @click="showCustomSheet = true">自訂</button>
    </div>

    <!-- Log history mini -->
    <div v-if="water.todayLogs.length > 0" class="water-log">
      <div v-for="(log, i) in water.todayLogs.slice(-3)" :key="i" class="log-item">
        <span class="log-time">{{ log.time }}</span>
        <span class="log-ml">+{{ log.ml }}ml</span>
      </div>
    </div>
  </div>

  <!-- Custom amount sheet -->
  <BottomSheet v-model="showCustomSheet" title="自訂飲水量">
    <div class="custom-form">
      <InputField
        v-model="customAmount"
        label="飲水量 (ml)"
        type="number"
        placeholder="例如：350"
        hint="輸入 1 到 2000 之間的數值"
      />
      <OrangeButton full-width @click="addCustom">記錄</OrangeButton>
    </div>
  </BottomSheet>
</template>

<style scoped>
.water-widget {
  background: linear-gradient(135deg, rgba(42, 172, 168, 0.12), rgba(10, 148, 136, 0.06));
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border: 1px solid rgba(42, 172, 168, 0.25);
  border-radius: 20px 6px 20px 6px;
  padding: 18px 20px;
  box-shadow: 0 4px 16px rgba(10, 148, 136, 0.1);
}

.widget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.widget-title-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.water-icon {
  color: #2aaca8;
  display: flex;
  align-items: center;
}

.widget-title {
  font-family: 'Lora', serif;
  font-size: 15px;
  font-weight: 600;
  color: #083830;
}

.water-amount {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.amount-current {
  font-family: 'Lora', serif;
  font-size: 18px;
  font-weight: 700;
  color: #0a9488;
}

.amount-sep {
  font-size: 12px;
  color: #0a7870;
  margin: 0 2px;
}

.amount-target {
  font-size: 12px;
  color: #0a7870;
  font-weight: 500;
}

.progress-bar-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.progress-bar-bg {
  flex: 1;
  height: 10px;
  background: rgba(10, 148, 136, 0.12);
  border-radius: 5px;
  overflow: hidden;
  position: relative;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #0a9488, #2aaca8);
  border-radius: 5px;
  transition: width 400ms ease-out;
}

.progress-complete-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3));
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-text {
  font-size: 12px;
  font-weight: 700;
  color: #0a9488;
  min-width: 36px;
  text-align: right;
}

.quick-water-row {
  display: flex;
  gap: 8px;
}

.water-btn {
  flex: 1;
  padding: 9px 6px;
  background: rgba(255, 255, 255, 0.45);
  border: 1px solid rgba(10, 148, 136, 0.2);
  border-radius: 10px;
  font-family: 'Raleway', sans-serif;
  font-size: 12px;
  font-weight: 600;
  color: #0a9488;
  cursor: pointer;
  transition: all 200ms ease;
  min-height: 40px;
}

.water-btn:hover {
  background: rgba(10, 148, 136, 0.12);
  border-color: rgba(10, 148, 136, 0.35);
}

.water-btn:active {
  transform: scale(0.95);
}

.custom-btn {
  color: #e86010;
  border-color: rgba(232, 96, 16, 0.2);
}

.custom-btn:hover {
  background: rgba(232, 96, 16, 0.08);
  border-color: rgba(232, 96, 16, 0.3);
}

.water-log {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.log-time {
  font-size: 10px;
  color: #0a7870;
}

.log-ml {
  font-size: 10px;
  font-weight: 600;
  color: #0a9488;
}

.custom-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 20px;
}
</style>
