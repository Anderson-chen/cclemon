<script setup lang="ts">
import { ref } from 'vue'
import { useDietStore } from '@/stores/dietStore'
import { useUiStore } from '@/stores/uiStore'
import BottomSheet from '@/components/shared/BottomSheet.vue'
import OrangeButton from '@/components/shared/OrangeButton.vue'
import GlassButton from '@/components/shared/GlassButton.vue'

const diet = useDietStore()
const ui = useUiStore()
const showConfirm = ref(false)

async function confirmCopy() {
  showConfirm.value = false
  await diet.copyYesterday()
}
</script>

<template>
  <div v-if="!diet.hasTodayMeals && diet.hasYesterdayMeals">
    <button class="copy-btn" @click="showConfirm = true">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
        <path d="M8 17.929H6c-1.105 0-2-.912-2-2.036V5.036C4 3.91 4.895 3 6 3h8c1.105 0 2 .911 2 2.036v1.866"/>
        <path d="M18 21H10a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2z"/>
      </svg>
      複製昨日餐點
    </button>

    <BottomSheet v-model="showConfirm" title="複製昨日餐點">
      <div class="confirm-content">
        <div class="confirm-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#0A9488" stroke-width="1.5">
            <path d="M8 17.929H6c-1.105 0-2-.912-2-2.036V5.036C4 3.91 4.895 3 6 3h8c1.105 0 2 .911 2 2.036v1.866"/>
            <path d="M18 21H10a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2z"/>
          </svg>
        </div>
        <h3 class="confirm-title">複製昨日餐點？</h3>
        <p class="confirm-desc">將昨天記錄的所有餐點複製到今天，你可以再做調整。</p>
        <div class="confirm-actions">
          <GlassButton full-width @click="showConfirm = false">取消</GlassButton>
          <OrangeButton full-width :loading="ui.isLoading('copyYesterday')" @click="confirmCopy">確認複製</OrangeButton>
        </div>
      </div>
    </BottomSheet>
  </div>
</template>

<style scoped>
.copy-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(10, 148, 136, 0.08);
  border: 1.5px dashed rgba(10, 148, 136, 0.3);
  border-radius: 12px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #0A9488;
  cursor: pointer;
  transition: all 200ms;
  width: 100%;
  justify-content: center;
}

.copy-btn:hover {
  background: rgba(10, 148, 136, 0.12);
  border-color: rgba(10, 148, 136, 0.5);
}

.confirm-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 8px 0 24px;
  text-align: center;
}

.confirm-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(10, 148, 136, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-title {
  font-size: 18px;
  color: #083830;
}

.confirm-desc {
  font-size: 14px;
  color: #0A7870;
  line-height: 1.6;
}

.confirm-actions {
  display: flex;
  gap: 10px;
  width: 100%;
}
</style>
