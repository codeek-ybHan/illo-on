<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import StatusBadge from '@/components/common/StatusBadge.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import { formatDue, daysUntil } from '@/utils/date'

const props = defineProps({
  task: { type: Object, required: true },
  /** 프로젝트명 표시 여부 (내 Task 목록처럼 여러 프로젝트가 섞일 때) */
  showProject: { type: Boolean, default: false },
})
defineEmits(['change-status'])

const STATUS_OPTIONS = [
  { label: '할 일', value: 'TODO' },
  { label: '진행 중', value: 'IN_PROGRESS' },
  { label: '완료', value: 'DONE' },
]

const due = computed(() => {
  if (!props.task.dueDate) return null
  const d = daysUntil(props.task.dueDate)
  if (d === null) return null
  if (d < 0) return { text: `${-d}일 지남`, tone: 'over' }
  if (d === 0) return { text: '오늘 마감', tone: 'today' }
  if (d <= 3) return { text: `D-${d}`, tone: 'soon' }
  return { text: formatDue(props.task.dueDate), tone: 'normal' }
})
</script>

<template>
  <article class="tcard">
    <div class="tcard__main">
      <RouterLink class="tcard__title" :to="{ name: 'task-detail', params: { id: task.taskId } }">
        {{ task.title }}
      </RouterLink>
      <div class="tcard__meta">
        <StatusBadge :status="task.priority" />
        <span v-if="showProject" class="tcard__project">
          <AppIcon name="project" :size="12" /> {{ task.projectName }}
        </span>
        <span v-if="task.assigneeName" class="tcard__assignee">{{ task.assigneeName }}</span>
        <span v-else class="tcard__assignee tcard__assignee--none">미지정</span>
        <span v-if="due" class="tcard__due" :class="`is-${due.tone}`">{{ due.text }}</span>
      </div>
    </div>

    <select
      class="tcard__status"
      :value="task.status"
      @change="$emit('change-status', task, $event.target.value)"
    >
      <option v-for="o in STATUS_OPTIONS" :key="o.value" :value="o.value">{{ o.label }}</option>
    </select>
  </article>
</template>

<style scoped>
.tcard {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-3) var(--sp-4);
  background: transparent;
  border: 1px solid var(--c-border-strong);
  border-radius: var(--r-md);
}
.tcard__main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.tcard__title {
  font-size: var(--fs-md);
  font-weight: 500;
}
.tcard__title:hover {
  text-decoration: underline;
}
.tcard__meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.tcard__project,
.tcard__assignee {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.tcard__assignee--none {
  font-style: italic;
}
.tcard__due.is-over {
  color: var(--c-danger);
  font-weight: 600;
}
.tcard__due.is-today {
  color: var(--c-accent);
  font-weight: 600;
}
.tcard__due.is-soon {
  color: var(--c-accent);
}
.tcard__status {
  flex-shrink: 0;
  height: 32px;
  padding: 0 var(--sp-2);
  border: 1px solid var(--c-border);
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
}
</style>
