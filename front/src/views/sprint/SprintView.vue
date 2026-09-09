<script setup>
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'

const columns = [
  { key: 'TODO', label: 'TODO', color: 'var(--c-status-todo)' },
  { key: 'IN_PROGRESS', label: 'IN PROGRESS', color: 'var(--c-status-progress)' },
  { key: 'DONE', label: 'DONE', color: 'var(--c-status-done)' },
]
</script>

<template>
  <PagePlaceholder title="현재 Sprint" subtitle="Sprint에 배정된 Task의 진행 상황을 관리하세요.">
    <BaseCard>
      <div class="sprint-head">
        <div>
          <span class="skeleton-text skeleton-text--title" />
          <span class="u-muted">기간 미설정</span>
        </div>
        <div class="sprint-head__progress">
          <span class="u-muted">진행률</span>
          <ProgressBar :value="0" color="var(--c-accent)" show-label />
        </div>
      </div>
    </BaseCard>

    <div class="board">
      <div v-for="col in columns" :key="col.key" class="board__col">
        <header class="board__col-head">
          <span class="dot" :style="{ background: col.color }" />
          {{ col.label }}
          <span class="board__count">0</span>
        </header>
        <div class="board__col-body">
          <div v-for="n in 2" :key="n" class="board__card">
            <span class="skeleton-text skeleton-text--title" />
            <span class="skeleton-text skeleton-text--sm" />
          </div>
        </div>
      </div>
    </div>
  </PagePlaceholder>
</template>

<style scoped>
.sprint-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-4);
  flex-wrap: wrap;
}
.sprint-head > div:first-child {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  font-size: var(--fs-sm);
}
.sprint-head__progress {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  min-width: 260px;
  font-size: var(--fs-sm);
}
.board {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--sp-4);
  align-items: start;
}
.board__col {
  background: var(--c-surface-alt);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  padding: var(--sp-3);
}
.board__col-head {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  padding: var(--sp-2) var(--sp-2) var(--sp-3);
  font-size: var(--fs-sm);
  font-weight: 600;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: var(--r-full);
}
.board__count {
  margin-left: auto;
  color: var(--c-text-muted);
  font-weight: 500;
}
.board__col-body {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.board__card {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  padding: var(--sp-3);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 80%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 55%;
  height: 12px;
}
.skeleton-text--sm {
  width: 40%;
  height: 8px;
}

@media (max-width: 1080px) {
  .board {
    grid-template-columns: 1fr;
  }
}
</style>
