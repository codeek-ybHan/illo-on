<script setup>
import AppIcon from '@/components/common/AppIcon.vue'
</script>

<template>
  <div class="auth">
    <div class="auth__card">
      <div class="auth__brand">
        <span class="auth__logo"><AppIcon name="logo" :size="20" /></span>
        <span class="auth__wordmark">일로ON</span>
      </div>
      <RouterView />
    </div>
    <p class="auth__tagline">회의에서 나온 일을, 메신저에서 결정된 일을, 일로ON.</p>
  </div>
</template>

<style scoped>
.auth {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--sp-4);
  padding: var(--sp-6);
  background: var(--c-page);
}
.auth__card {
  width: 100%;
  max-width: 400px;
  padding: var(--sp-6);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-shell);
}
.auth__brand {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  margin-bottom: var(--sp-5);
}
.auth__logo {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: var(--r-sm);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.auth__wordmark {
  font-size: var(--fs-lg);
  font-weight: 700;
}
.auth__tagline {
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
</style>
