<script setup>
import { RouterLink } from 'vue-router'
import AppIcon from '@/components/common/AppIcon.vue'
import { formatDateTime } from '@/utils/date'

defineProps({
  meeting: { type: Object, required: true },
  showProject: { type: Boolean, default: false },
})
</script>

<template>
  <RouterLink class="mcard" :to="{ name: 'meeting-detail', params: { id: meeting.meetingId } }">
    <span class="mcard__icon"><AppIcon name="meeting" :size="18" /></span>
    <div class="mcard__body">
      <span class="mcard__title">{{ meeting.title }}</span>
      <div class="mcard__meta">
        <span v-if="showProject" class="mcard__project">
          <AppIcon name="project" :size="12" /> {{ meeting.projectName }}
        </span>
        <span>{{ meeting.meetingAt ? formatDateTime(meeting.meetingAt) : '일시 미정' }}</span>
        <span>참석 {{ meeting.attendeeCount }}</span>
        <span v-if="meeting.taskCount" class="mcard__tasks">Task {{ meeting.taskCount }}</span>
      </div>
    </div>
    <span class="mcard__status" :class="{ 'is-analyzed': meeting.hasSummary }">
      {{ meeting.hasSummary ? 'AI 분석됨' : meeting.hasContent ? '분석 대기' : '내용 없음' }}
    </span>
    <AppIcon name="chevronRight" :size="16" />
  </RouterLink>
</template>

<style scoped>
.mcard {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-3) var(--sp-4);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  color: var(--c-text);
}
.mcard:hover {
  border-color: var(--c-border-strong);
}
.mcard__icon {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
.mcard__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.mcard__title {
  font-size: var(--fs-md);
  font-weight: 500;
}
.mcard__meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.mcard__project {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.mcard__tasks {
  color: var(--c-accent);
}
.mcard__status {
  flex-shrink: 0;
  padding: 2px 10px;
  border-radius: var(--r-full);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
  font-weight: 600;
  color: var(--c-text-2);
}
.mcard__status.is-analyzed {
  background: var(--c-mint);
  color: var(--c-mint-ink);
}
</style>
