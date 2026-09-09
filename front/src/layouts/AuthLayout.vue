<script setup>
import BrandLogo from '@/components/common/BrandLogo.vue'
</script>

<template>
  <div class="auth">
    <div class="auth__card">
      <BrandLogo class="auth__brand" :size="24" tagline />
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
  margin-bottom: var(--sp-5);
}
.auth__tagline {
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
</style>
