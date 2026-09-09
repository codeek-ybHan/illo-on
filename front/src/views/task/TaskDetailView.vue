<script setup>
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import AppIcon from '@/components/common/AppIcon.vue'

const fields = ['상태', '담당자', '마감일', '우선순위', '프로젝트', 'Sprint']
</script>

<template>
  <PagePlaceholder
    title="Task 상세"
    subtitle="업무 정보와 이 업무가 생성된 회의 맥락을 확인하세요."
  >
    <div class="task-grid">
      <BaseCard>
        <template #header>업무 정보</template>
        <div class="task-body">
          <span class="skeleton-text skeleton-text--title" />
          <div class="skeleton task-desc" />
          <dl class="fields">
            <div v-for="f in fields" :key="f" class="field">
              <dt>{{ f }}</dt>
              <dd><span class="skeleton-text skeleton-text--sm" /></dd>
            </div>
          </dl>
        </div>
      </BaseCard>

      <BaseCard>
        <template #header>관련 회의</template>
        <button class="related-meeting" type="button">
          <span class="related-meeting__icon"><AppIcon name="meeting" :size="18" /></span>
          <div class="related-meeting__text">
            <span class="skeleton-text skeleton-text--sm" />
            <span class="u-muted">이 업무가 논의된 회의로 이동</span>
          </div>
          <AppIcon name="chevronRight" :size="16" />
        </button>
      </BaseCard>
    </div>
  </PagePlaceholder>
</template>

<style scoped>
.task-grid {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: var(--sp-4);
  align-items: start;
}
.task-body {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.task-desc {
  height: 64px;
}
.fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-3) var(--sp-5);
}
.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.field dt {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.related-meeting {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  width: 100%;
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  text-align: left;
}
.related-meeting:hover {
  background: var(--c-surface-alt);
}
.related-meeting__icon {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
.related-meeting__text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: var(--fs-sm);
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 70%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 45%;
  height: 14px;
}
.skeleton-text--sm {
  width: 55%;
  height: 9px;
}

@media (max-width: 1080px) {
  .task-grid {
    grid-template-columns: 1fr;
  }
  .fields {
    grid-template-columns: 1fr;
  }
}
</style>
