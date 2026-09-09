<script setup>
import { ref } from 'vue'
import AppIcon from './AppIcon.vue'
import ProgressBar from './ProgressBar.vue'

const rootEl = ref(null)
function onKey() {
  rootEl.value?.click()
}

defineProps({
  icon: { type: String, default: 'sprint' },
  title: { type: String, required: true },
  actionLabel: { type: String, default: '' },
  metricLabel: { type: String, default: '' },
  metricValue: { type: String, default: '' },
  progress: { type: Number, default: null },
  progressColor: { type: String, default: 'var(--c-accent)' },
  clickable: { type: Boolean, default: false },
})
</script>

<template>
  <article
    ref="rootEl"
    class="stat-card"
    :class="{ 'is-clickable': clickable }"
    :role="clickable ? 'button' : null"
    :tabindex="clickable ? 0 : null"
    @keydown.enter="clickable && onKey()"
    @keydown.space.prevent="clickable && onKey()"
  >
    <header class="stat-card__top">
      <span class="stat-card__title">
        <span class="stat-card__icon"><AppIcon :name="icon" :size="16" /></span>
        {{ title }}
      </span>
      <span v-if="actionLabel" class="stat-card__action">{{ actionLabel }}</span>
    </header>

    <div class="stat-card__metric">
      <span class="stat-card__metric-label">{{ metricLabel }}</span>
      <span class="stat-card__metric-value">{{ metricValue }}</span>
    </div>

    <ProgressBar v-if="progress !== null" :value="progress" :color="progressColor" />
  </article>
</template>

<style scoped>
.stat-card {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
  padding: var(--sp-4);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
}
.stat-card.is-clickable {
  cursor: pointer;
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease;
}
.stat-card.is-clickable:hover {
  border-color: var(--c-border-strong);
  box-shadow: var(--shadow-shell);
}
.stat-card.is-clickable:focus-visible {
  outline: 2px solid var(--c-primary);
  outline-offset: 2px;
}
.stat-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-2);
}
.stat-card__title {
  display: inline-flex;
  align-items: center;
  gap: var(--sp-2);
  font-weight: 600;
  font-size: var(--fs-md);
}
.stat-card__icon {
  display: grid;
  place-items: center;
  width: 24px;
  height: 24px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
.stat-card__action {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.stat-card__metric {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--sp-2);
}
.stat-card__metric-label {
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.stat-card__metric-value {
  font-size: var(--fs-lg);
  font-weight: 700;
}
</style>
