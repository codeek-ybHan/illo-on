<script setup>
import AppIcon from './AppIcon.vue'
import ProgressBar from './ProgressBar.vue'

defineProps({
  icon: { type: String, default: 'sprint' },
  title: { type: String, required: true },
  actionLabel: { type: String, default: '' },
  metricLabel: { type: String, default: '' },
  metricValue: { type: String, default: '' },
  progress: { type: Number, default: null },
  progressColor: { type: String, default: 'var(--c-accent)' },
})
</script>

<template>
  <article class="stat-card">
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
