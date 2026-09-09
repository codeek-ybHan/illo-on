<script setup>
import AppIcon from './AppIcon.vue'

defineProps({
  tone: {
    type: String,
    default: 'lavender',
    validator: (v) => ['lavender', 'peach', 'cream', 'mint'].includes(v),
  },
  icon: { type: String, default: 'project' },
  count: { type: [Number, String], default: '—' },
  label: { type: String, required: true },
})
</script>

<template>
  <button class="summary-chip" :class="`summary-chip--${tone}`" type="button">
    <span class="summary-chip__icon"><AppIcon :name="icon" :size="16" /></span>
    <span class="summary-chip__text">
      <span class="summary-chip__count">{{ count }}</span>
      <span class="summary-chip__label">{{ label }}</span>
    </span>
    <span class="summary-chip__chevron"><AppIcon name="chevronRight" :size="16" /></span>
  </button>
</template>

<style scoped>
.summary-chip {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  width: 100%;
  padding: var(--sp-3) var(--sp-4);
  border-radius: var(--r-md);
  text-align: left;
  transition: filter 0.15s ease;
}
.summary-chip:hover {
  filter: brightness(0.97);
}
.summary-chip--lavender {
  background: var(--c-lavender);
  color: var(--c-lavender-ink);
}
.summary-chip--peach {
  background: var(--c-peach);
  color: var(--c-peach-ink);
}
.summary-chip--cream {
  background: var(--c-cream);
  color: var(--c-cream-ink);
}
.summary-chip--mint {
  background: var(--c-mint);
  color: var(--c-mint-ink);
}
.summary-chip__icon {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: var(--r-sm);
  background: rgba(255, 255, 255, 0.6);
}
.summary-chip__text {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
}
.summary-chip__count {
  font-size: var(--fs-lg);
  font-weight: 700;
}
.summary-chip__label {
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.summary-chip__chevron {
  margin-left: auto;
  opacity: 0.6;
}
</style>
