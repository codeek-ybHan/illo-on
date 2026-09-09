<script setup>
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'

const weekdays = ['월', '화', '수', '목', '금', '토', '일']
</script>

<template>
  <PagePlaceholder title="캘린더" subtitle="회의 일정과 Task 마감일을 한 달 단위로 확인하세요.">
    <div class="cal-grid">
      <BaseCard>
        <template #header>2026년 9월</template>
        <div class="cal">
          <span v-for="w in weekdays" :key="w" class="cal__wd">{{ w }}</span>
          <div v-for="d in 35" :key="d" class="cal__day">
            <span class="cal__num">{{ d <= 30 ? d : '' }}</span>
          </div>
        </div>
      </BaseCard>

      <BaseCard>
        <template #header>오늘의 일정</template>
        <ul class="agenda">
          <li v-for="n in 3" :key="n" class="agenda__item">
            <span class="agenda__time">— :—</span>
            <div class="agenda__text">
              <span class="skeleton-text skeleton-text--title" />
              <span class="skeleton-text skeleton-text--sm" />
            </div>
          </li>
        </ul>
        <p class="empty-hint">일정 데이터 미연결 (레이아웃 스켈레톤).</p>
      </BaseCard>
    </div>
  </PagePlaceholder>
</template>

<style scoped>
.cal-grid {
  display: grid;
  grid-template-columns: 1.7fr 1fr;
  gap: var(--sp-4);
  align-items: start;
}
.cal {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}
.cal__wd {
  text-align: center;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  padding-bottom: var(--sp-1);
}
.cal__day {
  aspect-ratio: 1 / 0.9;
  border: 1px solid var(--c-border);
  border-radius: var(--r-sm);
  padding: 6px;
  background: var(--c-surface-alt);
}
.cal__num {
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.agenda {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.agenda__item {
  display: flex;
  gap: var(--sp-3);
}
.agenda__time {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  min-width: 44px;
}
.agenda__text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 75%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 55%;
  height: 11px;
}
.skeleton-text--sm {
  width: 35%;
  height: 8px;
}

@media (max-width: 1080px) {
  .cal-grid {
    grid-template-columns: 1fr;
  }
}
</style>
