<script setup>
import { ref } from 'vue'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'

const tabs = ['Overview', 'Sprint', 'Tasks', 'Meetings']
const activeTab = ref('Overview')
</script>

<template>
  <PagePlaceholder
    title="프로젝트 상세"
    subtitle="프로젝트의 업무 · 회의 · Sprint를 한곳에서 관리하세요."
  >
    <BaseCard>
      <div class="project-head">
        <span class="skeleton-text skeleton-text--title" />
        <span class="skeleton-text" />
        <div class="project-head__progress">
          <span class="u-muted">진행률</span>
          <ProgressBar :value="0" color="var(--c-accent)" show-label />
        </div>
      </div>
    </BaseCard>

    <div class="tabs">
      <button
        v-for="t in tabs"
        :key="t"
        class="tab"
        :class="{ 'is-active': activeTab === t }"
        @click="activeTab = t"
      >
        {{ t }}
      </button>
    </div>

    <BaseCard>
      <p class="empty-hint">"{{ activeTab }}" 패널 — 레이아웃 자리만 잡힌 상태입니다.</p>
    </BaseCard>
  </PagePlaceholder>
</template>

<style scoped>
.project-head {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.project-head__progress {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  max-width: 320px;
  font-size: var(--fs-sm);
}
.tabs {
  display: flex;
  gap: var(--sp-1);
  border-bottom: 1px solid var(--c-border);
}
.tab {
  padding: var(--sp-3) var(--sp-4);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.tab.is-active {
  color: var(--c-text);
  font-weight: 600;
  border-bottom-color: var(--c-primary);
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 60%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 35%;
  height: 14px;
}
</style>
