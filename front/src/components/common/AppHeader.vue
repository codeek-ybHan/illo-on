<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import AppIcon from './AppIcon.vue'
import BaseButton from './BaseButton.vue'

const props = defineProps({
  aiPanelCollapsed: { type: Boolean, default: false },
})
const emit = defineEmits(['toggle-ai'])

const route = useRoute()
const crumbs = computed(() => route.meta.breadcrumb || [route.meta.title || '일로ON'])
</script>

<template>
  <header class="header">
    <nav class="header__crumbs" aria-label="breadcrumb">
      <template v-for="(c, i) in crumbs" :key="i">
        <span v-if="i > 0" class="header__sep"><AppIcon name="chevronRight" :size="14" /></span>
        <span class="header__crumb" :class="{ 'is-current': i === crumbs.length - 1 }">{{
          c
        }}</span>
      </template>
    </nav>

    <div class="header__actions">
      <button class="header__icon-btn" type="button" aria-label="알림">
        <AppIcon name="bell" :size="18" />
      </button>
      <button class="header__icon-btn" type="button" aria-label="검색">
        <AppIcon name="search" :size="18" />
      </button>
      <BaseButton variant="primary" size="sm">
        <template #icon><AppIcon name="plus" :size="16" /></template>
        만들기
      </BaseButton>
      <button
        v-if="props.aiPanelCollapsed"
        class="header__icon-btn header__icon-btn--accent"
        type="button"
        aria-label="AI 패널 열기"
        @click="emit('toggle-ai')"
      >
        <AppIcon name="sparkle" :size="18" />
      </button>
    </div>
  </header>
</template>

<style scoped>
.header {
  height: var(--header-h);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-4);
  padding: 0 var(--sp-5);
  border-bottom: 1px solid var(--c-border);
}
.header__crumbs {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.header__sep {
  display: inline-flex;
  color: var(--c-text-muted);
}
.header__crumb.is-current {
  color: var(--c-text);
  font-weight: 600;
}
.header__actions {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}
.header__icon-btn {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: var(--r-full);
  color: var(--c-text-2);
  transition:
    background-color 0.15s ease,
    color 0.15s ease;
}
.header__icon-btn:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.header__icon-btn--accent {
  color: var(--c-accent);
}
</style>
