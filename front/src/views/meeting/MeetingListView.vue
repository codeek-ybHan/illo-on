<script setup>
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import AppIcon from '@/components/common/AppIcon.vue'

const filters = ['전체', '내 회의', 'AI 분석 완료', '분석 대기']
</script>

<template>
  <PagePlaceholder title="회의" subtitle="프로젝트에서 진행된 회의와 AI 분석 상태를 확인하세요.">
    <template #actions>
      <BaseButton variant="primary" size="sm">
        <template #icon><AppIcon name="plus" :size="16" /></template>
        회의 생성
      </BaseButton>
    </template>

    <div class="filter-bar">
      <button v-for="(f, i) in filters" :key="f" class="filter" :class="{ 'is-active': i === 0 }">
        {{ f }}
      </button>
      <div class="filter-bar__spacer" />
      <BaseButton variant="ghost" size="sm">
        <template #icon><AppIcon name="search" :size="15" /></template>
        검색
      </BaseButton>
    </div>

    <BaseCard padding="none">
      <ul class="meeting-list">
        <li v-for="n in 4" :key="n" class="meeting-row">
          <span class="meeting-row__icon"><AppIcon name="meeting" :size="18" /></span>
          <div class="meeting-row__main">
            <span class="skeleton-text skeleton-text--title" />
            <span class="skeleton-text" />
          </div>
          <span class="meeting-row__badge">분석 대기</span>
          <AppIcon name="chevronRight" :size="16" />
        </li>
      </ul>
      <p class="empty-hint">회의 데이터는 아직 연결되지 않았습니다 (레이아웃 스켈레톤).</p>
    </BaseCard>
  </PagePlaceholder>
</template>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}
.filter-bar__spacer {
  flex: 1;
}
.filter {
  padding: 6px 14px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.filter.is-active {
  background: var(--c-primary);
  border-color: var(--c-primary);
  color: var(--c-primary-contrast);
}
.meeting-list {
  display: flex;
  flex-direction: column;
}
.meeting-row {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-4) var(--sp-5);
  border-bottom: 1px solid var(--c-border);
  color: var(--c-text-muted);
}
.meeting-row__icon {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
.meeting-row__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.meeting-row__badge {
  padding: 2px 10px;
  border-radius: var(--r-full);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
  font-weight: 600;
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 55%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 35%;
  height: 12px;
}
</style>
