<script setup>
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import StatCard from '@/components/common/StatCard.vue'
import SummaryChip from '@/components/common/SummaryChip.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'

const summaries = [
  { tone: 'lavender', icon: 'project', label: '내 Task' },
  { tone: 'peach', icon: 'meeting', label: '예정 회의' },
  { tone: 'cream', icon: 'sparkle', label: 'Action Point' },
  { tone: 'mint', icon: 'calendar', label: '마감 임박' },
]
</script>

<template>
  <PagePlaceholder title="좋은 아침이에요 👋" subtitle="오늘의 업무와 일정을 한눈에 확인하세요.">
    <!-- 스탯 카드 3열 -->
    <div class="grid-3">
      <StatCard
        icon="project"
        title="내 프로젝트"
        action-label="전체 보기"
        metric-label="평균 진행률"
        metric-value="—%"
        :progress="0"
        progress-color="var(--c-lavender-ink)"
      />
      <StatCard
        icon="sprint"
        title="현재 Sprint"
        action-label="Sprint 보기"
        metric-label="남은 기간"
        metric-value="— 일"
        :progress="0"
        progress-color="var(--c-accent)"
      />
      <StatCard
        icon="calendar"
        title="오늘 할 일"
        action-label="Task 보기"
        metric-label="완료 / 전체"
        metric-value="— / —"
        :progress="0"
        progress-color="var(--c-success)"
      />
    </div>

    <!-- 요약 칩 -->
    <section>
      <h2 class="section-title">한눈에 보기</h2>
      <div class="grid-4">
        <SummaryChip
          v-for="s in summaries"
          :key="s.label"
          :tone="s.tone"
          :icon="s.icon"
          :label="s.label"
        />
      </div>
    </section>

    <!-- 오늘의 일정 -->
    <section>
      <h2 class="section-title">오늘의 일정</h2>
      <div class="schedule">
        <BaseCard>
          <template #header>이번 달</template>
          <div class="calendar-mini">
            <span v-for="d in 35" :key="d" class="calendar-mini__cell" />
          </div>
        </BaseCard>

        <div class="schedule__events">
          <BaseCard v-for="n in 2" :key="n" padding="sm">
            <div class="event">
              <div class="event__row">
                <span class="skeleton-text skeleton-text--title" />
                <span class="badge">예정</span>
              </div>
              <span class="skeleton-text" />
              <ProgressBar :value="0" color="var(--c-accent)" />
            </div>
          </BaseCard>
        </div>
      </div>
    </section>
  </PagePlaceholder>
</template>

<style scoped>
.grid-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--sp-4);
}
.grid-4 {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--sp-3);
}
.section-title {
  font-size: var(--fs-lg);
  margin-bottom: var(--sp-3);
}
.schedule {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: var(--sp-4);
}
.schedule__events {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.calendar-mini {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}
.calendar-mini__cell {
  aspect-ratio: 1;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
}
.event {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.event__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.badge {
  padding: 2px 8px;
  border-radius: var(--r-full);
  background: var(--c-accent-soft);
  color: var(--c-accent);
  font-size: var(--fs-xs);
  font-weight: 600;
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 60%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 45%;
  height: 12px;
}

@media (max-width: 1080px) {
  .grid-3,
  .grid-4,
  .schedule {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
