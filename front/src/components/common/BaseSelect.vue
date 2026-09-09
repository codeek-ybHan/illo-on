<script setup>
import { useId } from 'vue'

defineProps({
  modelValue: { type: [String, Number, null], default: '' },
  label: { type: String, default: '' },
  /** [{ label, value }] 또는 원시값 배열 */
  options: { type: Array, default: () => [] },
  placeholder: { type: String, default: '선택' },
  error: { type: String, default: '' },
  required: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
})
defineEmits(['update:modelValue'])

const id = useId()
const normalize = (opt) =>
  typeof opt === 'object' && opt !== null ? opt : { label: String(opt), value: opt }
</script>

<template>
  <div class="field" :class="{ 'field--error': error }">
    <label v-if="label" :for="id" class="field__label">
      {{ label }}
      <span v-if="required" class="field__req" aria-hidden="true">*</span>
    </label>
    <div class="field__wrap">
      <select
        :id="id"
        class="field__select"
        :value="modelValue"
        :disabled="disabled"
        :aria-invalid="Boolean(error)"
        @change="$emit('update:modelValue', $event.target.value)"
      >
        <option value="" disabled>{{ placeholder }}</option>
        <option v-for="opt in options.map(normalize)" :key="String(opt.value)" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>
      <span class="field__caret" aria-hidden="true">▾</span>
    </div>
    <p v-if="error" class="field__msg field__msg--error">{{ error }}</p>
  </div>
</template>

<style scoped>
.field {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.field__label {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.field__req {
  color: var(--c-danger);
}
.field__wrap {
  position: relative;
}
.field__select {
  width: 100%;
  height: 40px;
  padding: 0 var(--sp-6) 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  appearance: none;
  transition: border-color 0.15s ease;
}
.field__select:focus {
  outline: none;
  border-color: var(--c-primary);
}
.field__select:disabled {
  background: var(--c-surface-alt);
  color: var(--c-text-muted);
}
.field--error .field__select {
  border-color: var(--c-danger);
}
.field__caret {
  position: absolute;
  right: var(--sp-3);
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: var(--c-text-muted);
  font-size: var(--fs-xs);
}
.field__msg {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.field__msg--error {
  color: var(--c-danger);
}
</style>
