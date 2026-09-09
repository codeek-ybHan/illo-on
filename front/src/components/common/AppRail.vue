<script setup>
import AppIcon from './AppIcon.vue'

const menu = [
  { to: { name: 'mainboard' }, icon: 'home', label: '메인보드' },
  { to: { name: 'meetings' }, icon: 'meeting', label: '회의' },
  { to: { name: 'calendar' }, icon: 'calendar', label: '캘린더' },
  { to: { name: 'project-detail', params: { id: 1 } }, icon: 'project', label: '프로젝트' },
  { to: { name: 'sprints' }, icon: 'sprint', label: 'Sprint' },
]
</script>

<template>
  <nav class="rail">
    <RouterLink :to="{ name: 'mainboard' }" class="rail__logo" aria-label="일로ON 홈">
      <AppIcon name="logo" :size="22" />
    </RouterLink>

    <div class="rail__group">
      <span class="rail__caption">메뉴</span>
      <ul>
        <li v-for="item in menu" :key="item.label">
          <RouterLink :to="item.to" class="rail__item" :title="item.label">
            <AppIcon :name="item.icon" :size="20" />
            <span class="rail__tooltip">{{ item.label }}</span>
          </RouterLink>
        </li>
      </ul>
    </div>

    <div class="rail__group">
      <span class="rail__caption">계정</span>
      <ul>
        <li>
          <button class="rail__item" type="button" title="설정" disabled>
            <AppIcon name="settings" :size="20" />
            <span class="rail__tooltip">설정</span>
          </button>
        </li>
      </ul>
    </div>

    <div class="rail__spacer" />
    <button class="rail__avatar" type="button" title="내 계정" aria-label="내 계정">일</button>
  </nav>
</template>

<style scoped>
.rail {
  width: var(--rail-w);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-4);
  padding: var(--sp-4) 0;
  border-right: 1px solid var(--c-border);
}
.rail__logo {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: var(--r-md);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.rail__group {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-2);
}
.rail__caption {
  font-size: 10px;
  letter-spacing: 0.04em;
  color: var(--c-text-muted);
}
.rail__group ul {
  display: flex;
  flex-direction: column;
  gap: var(--sp-1);
}
.rail__item {
  position: relative;
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: var(--r-md);
  color: var(--c-text-2);
  transition:
    background-color 0.15s ease,
    color 0.15s ease;
}
.rail__item:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.rail__item.router-link-exact-active {
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.rail__item[disabled] {
  opacity: 0.4;
  cursor: not-allowed;
}
.rail__tooltip {
  position: absolute;
  left: calc(100% + 10px);
  top: 50%;
  transform: translateY(-50%);
  padding: 4px 8px;
  border-radius: var(--r-sm);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
  font-size: var(--fs-xs);
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.12s ease;
  z-index: 20;
}
.rail__item:hover .rail__tooltip {
  opacity: 1;
}
.rail__spacer {
  flex: 1;
}
.rail__avatar {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: var(--r-full);
  background: var(--c-accent-soft);
  color: var(--c-accent);
  font-size: var(--fs-sm);
  font-weight: 700;
}
</style>
