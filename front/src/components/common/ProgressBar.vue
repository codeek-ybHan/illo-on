<script setup>
defineProps({
  /** 0~100 */
  value: { type: Number, default: 0 },
  /** 토큰 색 키 또는 CSS 색 */
  color: { type: String, default: 'var(--c-primary)' },
  showLabel: { type: Boolean, default: false },
})
</script>

<template>
  <div class="progress">
    <div class="progress__track">
      <div
        class="progress__fill"
        :style="{ width: `${Math.min(100, Math.max(0, value))}%`, background: color }"
      />
    </div>
    <span v-if="showLabel" class="progress__label">{{ Math.round(value) }}%</span>
  </div>
</template>

<style scoped>
.progress {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}
.progress__track {
  flex: 1;
  height: 6px;
  border-radius: var(--r-full);
  background: var(--c-border);
  overflow: hidden;
}
.progress__fill {
  height: 100%;
  border-radius: var(--r-full);
  transition: width 0.3s ease;
}
.progress__label {
  font-size: var(--fs-sm);
  font-weight: 600;
  color: var(--c-text-2);
  min-width: 34px;
  text-align: right;
}
</style>
