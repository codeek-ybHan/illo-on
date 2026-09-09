<script setup>
import { useId } from 'vue'

defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: '' },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  hint: { type: String, default: '' },
  error: { type: String, default: '' },
  required: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  autocomplete: { type: String, default: 'off' },
})
defineEmits(['update:modelValue'])

const id = useId()
</script>

<template>
  <div class="field" :class="{ 'field--error': error }">
    <label v-if="label" :for="id" class="field__label">
      {{ label }}
      <span v-if="required" class="field__req" aria-hidden="true">*</span>
    </label>
    <input
      :id="id"
      class="field__input"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :autocomplete="autocomplete"
      :aria-invalid="Boolean(error)"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <p v-if="error" class="field__msg field__msg--error">{{ error }}</p>
    <p v-else-if="hint" class="field__msg">{{ hint }}</p>
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
.field__input {
  height: 40px;
  padding: 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  transition: border-color 0.15s ease;
}
.field__input:focus {
  outline: none;
  border-color: var(--c-primary);
}
.field__input:disabled {
  background: var(--c-surface-alt);
  color: var(--c-text-muted);
}
.field--error .field__input {
  border-color: var(--c-danger);
}
.field__msg {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.field__msg--error {
  color: var(--c-danger);
}
</style>
