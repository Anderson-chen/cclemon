<script setup lang="ts">
import { ref, computed } from 'vue'
import type { BodyPhoto } from '@/types/models'
import { useWeightStore } from '@/stores/weightStore'
import { formatDate } from '@/utils/dateUtils'

const weight = useWeightStore()

const filterAngle = ref<BodyPhoto['angle'] | 'all'>('all')

const filtered = computed(() => {
  if (filterAngle.value === 'all') return weight.photos
  return weight.photos.filter(p => p.angle === filterAngle.value)
})

const sorted = computed(() =>
  [...filtered.value].sort((a, b) => a.date.localeCompare(b.date))
)

const leftIdx = ref(0)
const rightIdx = ref(Math.max(0, sorted.value.length - 1))

const leftPhoto = computed(() => sorted.value[leftIdx.value] || null)
const rightPhoto = computed(() => sorted.value[rightIdx.value] || null)

const angles = [
  { value: 'all', label: '全部' },
  { value: 'front', label: '正面' },
  { value: 'side', label: '側面' },
  { value: 'back', label: '背面' },
]
</script>

<template>
  <div class="compare-wrap">
    <!-- Filter -->
    <div class="angle-filter">
      <button
        v-for="a in angles"
        :key="a.value"
        class="filter-btn"
        :class="{ active: filterAngle === a.value }"
        @click="filterAngle = a.value as any"
      >
        {{ a.label }}
      </button>
    </div>

    <!-- Empty state -->
    <div v-if="sorted.length === 0" class="empty-state">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="rgba(10,148,136,0.3)" stroke-width="1.2">
        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
        <circle cx="8.5" cy="8.5" r="1.5"/>
        <polyline points="21 15 16 10 5 21"/>
      </svg>
      <p class="empty-title">尚無體態照片</p>
      <p class="empty-hint">上傳照片後，可在此比較不同時期的體態變化</p>
    </div>

    <!-- Compare view -->
    <div v-else class="compare-view">
      <div class="compare-grid">
        <div class="compare-side">
          <p class="compare-label">之前</p>
          <select v-model="leftIdx" class="photo-select">
            <option v-for="(p, i) in sorted" :key="i" :value="i">
              {{ formatDate(p.date) }} {{ p.angle ? `(${p.angle})` : '' }}
            </option>
          </select>
          <div class="photo-frame">
            <img v-if="leftPhoto" :src="leftPhoto.url" :alt="`${leftPhoto.date} ${leftPhoto.angle}`" class="compare-photo" />
            <div v-else class="photo-placeholder">選擇照片</div>
          </div>
          <div v-if="leftPhoto" class="photo-date">{{ formatDate(leftPhoto.date) }}</div>
        </div>

        <div class="compare-divider">
          <div class="divider-line" />
          <span class="divider-vs">VS</span>
          <div class="divider-line" />
        </div>

        <div class="compare-side">
          <p class="compare-label">之後</p>
          <select v-model="rightIdx" class="photo-select">
            <option v-for="(p, i) in sorted" :key="i" :value="i">
              {{ formatDate(p.date) }} {{ p.angle ? `(${p.angle})` : '' }}
            </option>
          </select>
          <div class="photo-frame">
            <img v-if="rightPhoto" :src="rightPhoto.url" :alt="`${rightPhoto.date} ${rightPhoto.angle}`" class="compare-photo" />
            <div v-else class="photo-placeholder">選擇照片</div>
          </div>
          <div v-if="rightPhoto" class="photo-date">{{ formatDate(rightPhoto.date) }}</div>
        </div>
      </div>
    </div>

    <!-- Gallery -->
    <div v-if="sorted.length > 0" class="gallery">
      <h4 class="gallery-title">所有照片</h4>
      <div class="gallery-grid">
        <div
          v-for="(photo, i) in sorted"
          :key="photo.id"
          class="gallery-item"
          @click="rightIdx = i"
        >
          <img :src="photo.thumbnailUrl" :alt="formatDate(photo.date)" class="gallery-thumb" />
          <span class="gallery-date">{{ formatDate(photo.date) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.compare-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.angle-filter {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.45);
  border: 1.5px solid rgba(10, 148, 136, 0.15);
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #0A7870;
  cursor: pointer;
  transition: all 200ms;
}

.filter-btn.active {
  background: rgba(10, 148, 136, 0.12);
  border-color: #0A9488;
  color: #0A9488;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 40px 20px;
  text-align: center;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #083830;
}

.empty-hint {
  font-size: 13px;
  color: #0A7870;
  line-height: 1.5;
}

.compare-grid {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 8px;
  align-items: start;
}

.compare-side {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.compare-label {
  font-size: 12px;
  font-weight: 600;
  color: #0A7870;
  text-align: center;
}

.photo-select {
  font-family: 'Raleway', sans-serif;
  font-size: 12px;
  color: #083830;
  background: rgba(255, 255, 255, 0.6);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 8px;
  padding: 6px 10px;
  outline: none;
  width: 100%;
}

.photo-frame {
  border-radius: 12px;
  overflow: hidden;
  background: rgba(10, 148, 136, 0.04);
  aspect-ratio: 3/4;
  display: flex;
  align-items: center;
  justify-content: center;
}

.compare-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-placeholder {
  font-size: 12px;
  color: rgba(10, 148, 136, 0.4);
}

.photo-date {
  font-size: 11px;
  color: #0A7870;
  text-align: center;
  font-weight: 600;
}

.compare-divider {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding-top: 60px;
}

.divider-line {
  flex: 1;
  width: 1px;
  background: rgba(10, 148, 136, 0.15);
  min-height: 20px;
}

.divider-vs {
  font-size: 11px;
  font-weight: 800;
  color: rgba(10, 148, 136, 0.4);
  letter-spacing: 1px;
}

.gallery {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.gallery-title {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.gallery-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  cursor: pointer;
}

.gallery-thumb {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid transparent;
  transition: border-color 200ms;
}

.gallery-item:hover .gallery-thumb {
  border-color: #0A9488;
}

.gallery-date {
  font-size: 10px;
  color: #0A7870;
  text-align: center;
}
</style>
