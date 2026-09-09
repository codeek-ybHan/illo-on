<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { useUiStore } from '@/stores/ui'
import AppIcon from './AppIcon.vue'
import BaseButton from './BaseButton.vue'

const ui = useUiStore()
const open = ref(false)
const root = ref(null)

const items = [
  { key: 'project', icon: 'project', label: '프로젝트' },
  { key: 'meeting', icon: 'meeting', label: '회의' },
  { key: 'task', icon: 'sprint', label: '업무' },
]

function pick(key) {
  open.value = false
  ui.openCreate(key)
}

function onDocClick(e) {
  if (root.value && !root.value.contains(e.target)) open.value = false
}
function onKey(e) {
  if (e.key === 'Escape') open.value = false
}

watch(open, (v) => {
  if (v) {
    setTimeout(() => document.addEventListener('click', onDocClick), 0)
    document.addEventListener('keydown', onKey)
  } else {
    document.removeEventListener('click', onDocClick)
    document.removeEventListener('keydown', onKey)
  }
})
onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick)
  document.removeEventListener('keydown', onKey)
})
</script>

<template>
  <div ref="root" class="cmenu">
    <BaseButton variant="primary" size="sm" @click.stop="open = !open">
      <template #icon><AppIcon name="plus" :size="16" /></template>
      만들기
    </BaseButton>

    <ul v-if="open" class="cmenu__list" role="menu">
      <li v-for="it in items" :key="it.key">
        <button class="cmenu__item" type="button" role="menuitem" @click="pick(it.key)">
          <span class="cmenu__icon"><AppIcon :name="it.icon" :size="16" /></span>
          {{ it.label }}
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.cmenu {
  position: relative;
}
.cmenu__list {
  position: absolute;
  right: 0;
  top: calc(100% + 6px);
  z-index: 40;
  min-width: 160px;
  padding: var(--sp-1);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  box-shadow: var(--shadow-card);
}
.cmenu__item {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  width: 100%;
  padding: var(--sp-2) var(--sp-3);
  border-radius: var(--r-sm);
  font-size: var(--fs-sm);
  color: var(--c-text);
  text-align: left;
}
.cmenu__item:hover {
  background: var(--c-surface-alt);
}
.cmenu__icon {
  display: grid;
  place-items: center;
  width: 24px;
  height: 24px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
</style>
