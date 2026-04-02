<script setup lang="ts">
import { useStreak } from '@/composables/useStreak'

const { streakDays, streakMessage, isAtRisk } = useStreak()
</script>

<template>
  <div class="streak-card" :class="{ 'at-risk': isAtRisk }">
    <div class="streak-card-inner">
      <div class="streak-left">
        <div class="streak-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="currentColor" class="flame-icon">
            <path d="M12 2c0 6-8 8-8 14a8 8 0 0 0 16 0c0-6-8-8-8-14z"/>
            <path d="M12 8c0 3-4 4-4 7a4 4 0 0 0 8 0c0-3-4-4-4-7z" fill="rgba(255,255,255,0.5)"/>
          </svg>
        </div>
      </div>

      <div class="streak-center">
        <p class="streak-label">連續記錄</p>
        <div class="streak-count-row">
          <span class="streak-number">{{ streakDays }}</span>
          <span class="streak-days">天</span>
        </div>
      </div>

      <div class="streak-right">
        <p class="streak-message">{{ streakMessage }}</p>
        <div v-if="isAtRisk" class="risk-badge">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2L1 21h22L12 2zm0 3.5l8.5 15h-17L12 5.5zM11 10v5h2v-5h-2zm0 7v2h2v-2h-2z"/>
          </svg>
          今天還沒記錄
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.streak-card {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(251, 191, 36, 0.08));
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 20px 6px 20px 6px;
  padding: 18px 20px;
  box-shadow: 0 4px 18px rgba(245, 158, 11, 0.15);
  position: relative;
  overflow: hidden;
}

.streak-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 12px;
  right: 12px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
  pointer-events: none;
}

.streak-card.at-risk {
  border-color: rgba(232, 96, 16, 0.35);
  background: linear-gradient(135deg, rgba(232, 96, 16, 0.12), rgba(245, 158, 11, 0.08));
}

.streak-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
}

.streak-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.35);
  flex-shrink: 0;
}

.flame-icon {
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.streak-center {
  flex: 1;
}

.streak-label {
  font-size: 12px;
  color: #b45309;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 2px;
}

.streak-count-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.streak-number {
  font-family: 'Lora', serif;
  font-size: 40px;
  font-weight: 700;
  color: #92400e;
  line-height: 1;
}

.streak-days {
  font-size: 16px;
  color: #b45309;
  font-weight: 600;
}

.streak-right {
  flex-shrink: 0;
  max-width: 120px;
}

.streak-message {
  font-size: 12px;
  color: #92400e;
  font-weight: 500;
  line-height: 1.4;
  text-align: right;
}

.risk-badge {
  margin-top: 6px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  font-size: 10px;
  font-weight: 600;
  color: #E86010;
  background: rgba(232, 96, 16, 0.1);
  border-radius: 999px;
  padding: 3px 8px;
}
</style>
