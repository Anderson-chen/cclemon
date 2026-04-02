<script setup lang="ts">
defineProps<{
  label?: string
  type?: string
  placeholder?: string
  error?: string
  hint?: string
  disabled?: boolean
  required?: boolean
}>()

const model = defineModel<string | number>()
</script>

<template>
  <div class="field-wrap" :class="{ 'has-error': error }">
    <label v-if="label" class="field-label">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>
    <input
      v-model="model"
      class="field-input"
      :type="type ?? 'text'"
      :placeholder="placeholder"
      :disabled="disabled"
      :required="required"
    />
    <p v-if="error" class="field-error">{{ error }}</p>
    <p v-else-if="hint" class="field-hint">{{ hint }}</p>
  </div>
</template>

<style scoped>
.field-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #083830;
  letter-spacing: 0.3px;
}

.required-star {
  color: #E86010;
  margin-left: 2px;
}

.field-input {
  width: 100%;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.55);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 12px;
  font-family: 'Raleway', sans-serif;
  font-size: 15px;
  color: #083830;
  outline: none;
  transition: border-color 200ms ease, box-shadow 200ms ease;
  min-height: 48px;
}

.field-input::placeholder {
  color: rgba(8, 56, 48, 0.4);
}

.field-input:focus {
  border-color: #0A9488;
  box-shadow: 0 0 0 3px rgba(10, 148, 136, 0.12);
  background: rgba(255, 255, 255, 0.75);
}

.field-input:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.has-error .field-input {
  border-color: #e53e3e;
}

.has-error .field-input:focus {
  box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.12);
}

.field-error {
  font-size: 12px;
  color: #e53e3e;
  font-weight: 500;
}

.field-hint {
  font-size: 12px;
  color: #0A7870;
}
</style>
