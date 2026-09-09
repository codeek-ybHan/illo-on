<script setup>
defineProps({
  /** 카드 안쪽 여백 프리셋 */
  padding: {
    type: String,
    default: 'md',
    validator: (v) => ['none', 'sm', 'md', 'lg'].includes(v),
  },
})
</script>

<template>
  <section class="base-card" :class="`base-card--pad-${padding}`">
    <header v-if="$slots.header" class="base-card__header">
      <slot name="header" />
    </header>
    <slot />
  </section>
</template>

<style scoped>
.base-card {
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
}
.base-card--pad-none {
  padding: 0;
}
.base-card--pad-sm {
  padding: var(--sp-3);
}
.base-card--pad-md {
  padding: var(--sp-5);
}
.base-card--pad-lg {
  padding: var(--sp-6);
}
.base-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-3);
  margin-bottom: var(--sp-4);
  font-size: var(--fs-lg);
  font-weight: 600;
}
</style>
