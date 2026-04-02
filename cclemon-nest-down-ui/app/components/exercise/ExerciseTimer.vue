<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { useNotifications } from '@/composables/useNotifications'

const presets = [60, 90, 120, 180]
const { sendLocalNotification } = useNotifications()

const selected = ref(90)
const customSeconds = ref('')
const timeLeft = ref(0)
const running = ref(false)
const finished = ref(false)
let interval: ReturnType<typeof setInterval> | null = null

const displayTime = computed(() => {
  const m = Math.floor(timeLeft.value / 60)
  const s = timeLeft.value % 60
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
})

const progress = computed(() => {
  if (!selected.value) return 0
  return 1 - timeLeft.value / selected.value
})

const circumference = 2 * Math.PI * 54

function selectPreset(s: number) {
  if (running.value) return
  selected.value = s
  timeLeft.value = s
  finished.value = false
}

function applyCustom() {
  const n = parseInt(customSeconds.value)
  if (n > 0 && n <= 600) {
    selected.value = n
    timeLeft.value = n
    finished.value = false
  }
}

function start() {
  if (timeLeft.value === 0) timeLeft.value = selected.value
  running.value = true
  finished.value = false
  interval = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--
    } else {
      stop()
      finished.value = true
      sendLocalNotification('休息完成！', '準備好開始下一組訓練了嗎？')
    }
  }, 1000)
}

function pause() {
  running.value = false
  if (interval) clearInterval(interval)
}

function stop() {
  pause()
  timeLeft.value = selected.value
}

function reset() {
  stop()
  finished.value = false
}

onUnmounted(() => {
  if (interval) clearInterval(interval)
})
</script>

<template>
  <div class="timer-wrap">
    <!-- Preset buttons -->
    <div class="preset-row">
      <button
        v-for="s in presets"
        :key="s"
        class="preset-btn"
        :class="{ active: selected === s }"
        :disabled="running"
        @click="selectPreset(s)"
      >
        {{ s }}s
      </button>
      <div class="custom-input-wrap">
        <input
          v-model="customSeconds"
          class="custom-input"
          type="number"
          placeholder="自訂"
          min="1"
          max="600"
          :disabled="running"
        />
        <button class="preset-btn apply-btn" :disabled="running" @click="applyCustom">✓</button>
      </div>
    </div>

    <!-- Timer circle -->
    <div class="timer-display" :class="{ finished }">
      <svg width="128" height="128" viewBox="0 0 128 128">
        <circle cx="64" cy="64" r="54" fill="none" stroke="rgba(10,148,136,0.1)" stroke-width="10"/>
        <circle
          cx="64" cy="64" r="54"
          fill="none"
          :stroke="finished ? '#E86010' : '#0A9488'"
          stroke-width="10"
          stroke-linecap="round"
          :stroke-dasharray="`${progress * circumference} ${circumference}`"
          stroke-dashoffset="84.8"
          transform="rotate(-90 64 64)"
          style="transition: stroke-dasharray 900ms linear, stroke 300ms"
        />
      </svg>
      <div class="timer-text">
        <span class="timer-time" :class="{ finished }">{{ displayTime }}</span>
        <span v-if="finished" class="timer-done-label">完成！</span>
        <span v-else class="timer-label">{{ running ? '計時中' : '準備' }}</span>
      </div>
    </div>

    <!-- Controls -->
    <div class="timer-controls">
      <button v-if="!running" class="ctrl-btn start" @click="start">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M5 3l14 9-14 9V3z"/>
        </svg>
        開始
      </button>
      <button v-else class="ctrl-btn pause" @click="pause">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <rect x="6" y="4" width="4" height="16"/><rect x="14" y="4" width="4" height="16"/>
        </svg>
        暫停
      </button>
      <button class="ctrl-btn reset" @click="reset">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
          <path d="M3 3v5h5"/>
        </svg>
        重置
      </button>
    </div>
  </div>
</template>

<style scoped>
.timer-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.preset-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.preset-btn {
  padding: 7px 14px;
  background: rgba(255, 255, 255, 0.45);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 8px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #0A7870;
  cursor: pointer;
  transition: all 200ms;
  min-height: 36px;
}

.preset-btn.active {
  background: rgba(10, 148, 136, 0.12);
  border-color: #0A9488;
  color: #0A9488;
}

.preset-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.preset-btn:hover:not(:disabled):not(.active) {
  background: rgba(10, 148, 136, 0.07);
}

.custom-input-wrap {
  display: flex;
  gap: 4px;
}

.custom-input {
  width: 64px;
  padding: 7px 10px;
  background: rgba(255, 255, 255, 0.55);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 8px;
  font-size: 13px;
  color: #083830;
  outline: none;
}

.custom-input:focus {
  border-color: #0A9488;
}

.apply-btn {
  padding: 7px 10px;
}

.timer-display {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 200ms;
}

.timer-display.finished {
  animation: bounce 400ms ease-out;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.06); }
}

.timer-text {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.timer-time {
  font-family: 'Lora', serif;
  font-size: 28px;
  font-weight: 700;
  color: #083830;
  line-height: 1;
}

.timer-time.finished {
  color: #E86010;
}

.timer-label, .timer-done-label {
  font-size: 11px;
  font-weight: 600;
  color: #0A7870;
}

.timer-done-label {
  color: #E86010;
}

.timer-controls {
  display: flex;
  gap: 10px;
}

.ctrl-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border: none;
  border-radius: 12px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 200ms;
  min-height: 44px;
}

.ctrl-btn.start {
  background: #0A9488;
  color: white;
  box-shadow: 0 4px 14px rgba(10, 148, 136, 0.3);
}

.ctrl-btn.start:hover {
  background: #087870;
}

.ctrl-btn.pause {
  background: #E86010;
  color: white;
  box-shadow: 0 4px 14px rgba(232, 96, 16, 0.25);
}

.ctrl-btn.pause:hover {
  background: #C05010;
}

.ctrl-btn.reset {
  background: rgba(255, 255, 255, 0.5);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  color: #0A7870;
}

.ctrl-btn.reset:hover {
  background: rgba(255, 255, 255, 0.7);
}
</style>
