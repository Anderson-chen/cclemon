<script setup lang="ts">
import { ref, computed } from 'vue'
import type { BodyMeasurements } from '@/types/models'
import { useWeightStore } from '@/stores/weightStore'
import { today } from '@/utils/dateUtils'
import InputField from '@/components/shared/InputField.vue'
import OrangeButton from '@/components/shared/OrangeButton.vue'
import { useUiStore } from '@/stores/uiStore'

const weight = useWeightStore()
const ui = useUiStore()

const latest = computed(() => {
  const entry = weight.entries.find(e => e.measurements)
  return entry?.measurements
})

const form = ref<BodyMeasurements>({
  waist: latest.value?.waist,
  hip: latest.value?.hip,
  chest: latest.value?.chest,
  arm: latest.value?.arm,
  thigh: latest.value?.thigh
})

async function save() {
  const entry = weight.entries.find(e => e.date === today())
  if (entry) {
    await weight.addEntry({ ...entry, measurements: { ...form.value } })
  } else {
    await weight.addEntry({
      userId: '1',
      date: today(),
      weight: weight.latestWeight || 0,
      measurements: { ...form.value }
    })
  }
}

const fields = [
  { key: 'waist' as keyof BodyMeasurements, label: '腰圍', icon: '🔵' },
  { key: 'hip' as keyof BodyMeasurements, label: '臀圍', icon: '🟣' },
  { key: 'chest' as keyof BodyMeasurements, label: '胸圍', icon: '🔴' },
  { key: 'arm' as keyof BodyMeasurements, label: '手臂圍', icon: '🟠' },
  { key: 'thigh' as keyof BodyMeasurements, label: '大腿圍', icon: '🟡' },
]
</script>

<template>
  <div class="measurement-form">
    <p class="form-hint">輸入今日身體測量數據（公分）</p>

    <div class="fields-grid">
      <div v-for="field in fields" :key="field.key" class="measure-field">
        <label class="measure-label">{{ field.label }}</label>
        <div class="measure-input-wrap">
          <input
            v-model="form[field.key]"
            class="measure-input"
            type="number"
            step="0.1"
            :placeholder="latest?.[field.key]?.toFixed(1) ?? '--'"
          />
          <span class="measure-unit">cm</span>
        </div>
        <div v-if="latest?.[field.key]" class="measure-prev">
          上次: {{ latest[field.key]?.toFixed(1) }}cm
          <span v-if="form[field.key] && latest[field.key]" :class="(form[field.key]! - latest[field.key]!) < 0 ? 'delta-down' : 'delta-up'">
            {{ ((form[field.key]! - latest[field.key]!) > 0 ? '+' : '') + (form[field.key]! - latest[field.key]!).toFixed(1) }}
          </span>
        </div>
      </div>
    </div>

    <OrangeButton full-width :loading="ui.isLoading('addWeight')" @click="save">
      儲存測量數據
    </OrangeButton>
  </div>
</template>

<style scoped>
.measurement-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-hint {
  font-size: 13px;
  color: #0A7870;
}

.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.measure-field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.measure-label {
  font-size: 13px;
  font-weight: 600;
  color: #083830;
}

.measure-input-wrap {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 10px;
  overflow: hidden;
}

.measure-input {
  flex: 1;
  padding: 10px 12px;
  background: transparent;
  border: none;
  outline: none;
  font-size: 15px;
  color: #083830;
  font-family: 'Raleway', sans-serif;
  min-height: 44px;
}

.measure-unit {
  padding: 0 10px;
  font-size: 12px;
  color: #0A7870;
  font-weight: 600;
}

.measure-prev {
  font-size: 11px;
  color: rgba(8, 56, 48, 0.45);
  display: flex;
  gap: 4px;
  align-items: center;
}

.delta-down {
  color: #0A9488;
  font-weight: 600;
}

.delta-up {
  color: #e53e3e;
  font-weight: 600;
}
</style>
