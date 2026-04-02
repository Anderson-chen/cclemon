<script setup lang="ts">
import { useReportStore } from '@/stores/reportStore'

const report = useReportStore()

// Extract numbers from text for highlighting
function parseInsight(text: string): { type: 'text' | 'number'; content: string }[] {
  const parts: { type: 'text' | 'number'; content: string }[] = []
  const regex = /(\d+(?:\.\d+)?(?:\s*(?:分|天|次|kcal|ml|g|%|週|月))?)/g
  let lastIndex = 0
  let match

  while ((match = regex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      parts.push({ type: 'text', content: text.slice(lastIndex, match.index) })
    }
    parts.push({ type: 'number', content: match[0] })
    lastIndex = match.index + match[0].length
  }

  if (lastIndex < text.length) {
    parts.push({ type: 'text', content: text.slice(lastIndex) })
  }

  return parts
}
</script>

<template>
  <div class="narrative-wrap">
    <div class="narrative-header">
      <div class="narrative-icon">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M9 11l3 3L22 4"/>
          <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
        </svg>
      </div>
      <h3 class="narrative-title">健康洞察</h3>
    </div>

    <div class="insights-list">
      <div
        v-for="(insight, i) in report.insights"
        :key="i"
        class="insight-item"
        :style="{ animationDelay: `${i * 80}ms` }"
      >
        <div class="insight-dot" />
        <p class="insight-text">
          <template v-for="part in parseInsight(insight)" :key="part.content">
            <strong v-if="part.type === 'number'" class="insight-number">{{ part.content }}</strong>
            <span v-else>{{ part.content }}</span>
          </template>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.narrative-wrap {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.narrative-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.narrative-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(10, 148, 136, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0A9488;
}

.narrative-title {
  font-size: 16px;
  color: #083830;
}

.insights-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.insight-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  animation: fadeUp 400ms ease-out both;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.insight-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #0A9488;
  flex-shrink: 0;
  margin-top: 8px;
}

.insight-text {
  font-size: 14px;
  color: #083830;
  line-height: 1.6;
}

.insight-number {
  color: #0A9488;
  font-weight: 700;
}
</style>
