<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
})
</script>

<template>
  <div class="page">
    <header class="page__head">
      <div>
        <h1 class="page__title">{{ title }}</h1>
        <p v-if="subtitle" class="page__subtitle">{{ subtitle }}</p>
      </div>
      <div v-if="$slots.actions" class="page__actions">
        <slot name="actions" />
      </div>
    </header>

    <div class="page__body">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}
.page__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--sp-4);
}
.page__title {
  font-size: var(--fs-display);
  letter-spacing: -0.01em;
}
.page__subtitle {
  margin-top: var(--sp-2);
  color: var(--c-text-2);
  font-size: var(--fs-md);
}
.page__actions {
  display: flex;
  gap: var(--sp-2);
  flex-shrink: 0;
}
.page__body {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}
</style>
