<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (v) => ['primary', 'ghost', 'pill', 'soft'].includes(v),
  },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md'].includes(v),
  },
  block: { type: Boolean, default: false },
})
</script>

<template>
  <button
    class="base-btn"
    :class="[`base-btn--${variant}`, `base-btn--${size}`, { 'base-btn--block': block }]"
  >
    <slot name="icon" />
    <span v-if="$slots.default" class="base-btn__label"><slot /></span>
  </button>
</template>

<style scoped>
.base-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--sp-2);
  font-weight: 500;
  border-radius: var(--r-full);
  border: 1px solid transparent;
  transition:
    background-color 0.15s ease,
    border-color 0.15s ease,
    opacity 0.15s ease;
  white-space: nowrap;
}
.base-btn--sm {
  height: 32px;
  padding: 0 var(--sp-3);
  font-size: var(--fs-sm);
}
.base-btn--md {
  height: 40px;
  padding: 0 var(--sp-4);
  font-size: var(--fs-md);
}
.base-btn--block {
  width: 100%;
}

.base-btn--primary {
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.base-btn--primary:hover {
  background: var(--c-primary-hover);
}

.base-btn--ghost {
  background: transparent;
  border-color: var(--c-border-strong);
  color: var(--c-text);
}
.base-btn--ghost:hover {
  background: var(--c-surface-alt);
}

.base-btn--soft {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.base-btn--soft:hover {
  background: var(--c-border);
}

.base-btn--pill {
  background: var(--c-surface);
  border-color: var(--c-border);
  color: var(--c-text-2);
  box-shadow: var(--shadow-card);
}
.base-btn--pill:hover {
  color: var(--c-text);
}
</style>
