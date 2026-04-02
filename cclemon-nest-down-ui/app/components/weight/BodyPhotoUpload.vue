<script setup lang="ts">
import { ref } from 'vue'
import type { BodyPhoto } from '@/types/models'
import { useWeightStore } from '@/stores/weightStore'
import { today } from '@/utils/dateUtils'
import { useUiStore } from '@/stores/uiStore'
import OrangeButton from '@/components/shared/OrangeButton.vue'
import GlassButton from '@/components/shared/GlassButton.vue'
import BadgeTag from '@/components/shared/BadgeTag.vue'

const weight = useWeightStore()
const ui = useUiStore()

const previewUrl = ref<string | null>(null)
const selectedFile = ref<File | null>(null)
const selectedAngle = ref<BodyPhoto['angle']>('front')
const inputRef = ref<HTMLInputElement>()

const angles: { value: BodyPhoto['angle']; label: string }[] = [
  { value: 'front', label: '正面' },
  { value: 'side', label: '側面' },
  { value: 'back', label: '背面' },
]

function triggerFileInput() {
  inputRef.value?.click()
}

function handleFile(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
}

function clearSelection() {
  selectedFile.value = null
  previewUrl.value = null
  if (inputRef.value) inputRef.value.value = ''
}

async function upload() {
  if (!selectedFile.value) return
  await weight.addPhoto({
    userId: '1',
    date: today(),
    url: previewUrl.value!,
    thumbnailUrl: previewUrl.value!,
    angle: selectedAngle.value
  })
  clearSelection()
}
</script>

<template>
  <div class="upload-wrap">
    <input
      ref="inputRef"
      type="file"
      accept="image/*"
      class="hidden-input"
      @change="handleFile"
    />

    <!-- Angle selector -->
    <div class="angle-selector">
      <span class="angle-label">拍攝角度</span>
      <div class="angle-btns">
        <button
          v-for="a in angles"
          :key="a.value"
          class="angle-btn"
          :class="{ active: selectedAngle === a.value }"
          @click="selectedAngle = a.value"
        >
          {{ a.label }}
        </button>
      </div>
    </div>

    <!-- Preview or upload area -->
    <div v-if="previewUrl" class="preview-area">
      <img :src="previewUrl" alt="預覽圖" class="preview-img" />
      <div class="preview-overlay">
        <BadgeTag color="teal" size="sm">{{ angles.find(a => a.value === selectedAngle)?.label }}</BadgeTag>
      </div>
      <div class="preview-actions">
        <GlassButton size="sm" @click="clearSelection">重新選擇</GlassButton>
        <OrangeButton size="sm" :loading="ui.isLoading('uploadPhoto')" @click="upload">上傳照片</OrangeButton>
      </div>
    </div>

    <div v-else class="drop-zone" @click="triggerFileInput">
      <div class="drop-icon">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="17 8 12 3 7 8"/>
          <line x1="12" y1="3" x2="12" y2="15"/>
        </svg>
      </div>
      <p class="drop-title">點擊上傳體態照片</p>
      <p class="drop-hint">支援 JPG、PNG 格式</p>
    </div>
  </div>
</template>

<style scoped>
.hidden-input {
  display: none;
}

.upload-wrap {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.angle-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.angle-label {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
  flex-shrink: 0;
}

.angle-btns {
  display: flex;
  gap: 8px;
}

.angle-btn {
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.45);
  border: 1.5px solid rgba(10, 148, 136, 0.15);
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #0A7870;
  cursor: pointer;
  transition: all 200ms;
}

.angle-btn.active {
  background: rgba(10, 148, 136, 0.12);
  border-color: #0A9488;
  color: #0A9488;
}

.drop-zone {
  border: 2px dashed rgba(10, 148, 136, 0.25);
  border-radius: 16px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 200ms;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.drop-zone:hover {
  border-color: rgba(10, 148, 136, 0.45);
  background: rgba(10, 148, 136, 0.03);
}

.drop-icon {
  color: rgba(10, 148, 136, 0.4);
  margin-bottom: 4px;
}

.drop-title {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.drop-hint {
  font-size: 12px;
  color: #0A7870;
}

.preview-area {
  position: relative;
  border-radius: 14px;
  overflow: hidden;
}

.preview-img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  display: block;
  border-radius: 14px;
}

.preview-overlay {
  position: absolute;
  top: 12px;
  left: 12px;
}

.preview-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
</style>
