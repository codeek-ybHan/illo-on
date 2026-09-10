<script setup>
/**
 * 상태/우선순위 배지. 전 계층 공통 enum 문자열을 받는다.
 */
const LABELS = {
  // project / sprint
  PLANNED: '예정',
  ACTIVE: '진행 중',
  COMPLETED: '완료',
  // task status
  TODO: '진행 전',
  IN_PROGRESS: '진행 중',
  DONE: '완료',
  // priority
  HIGH: '높음',
  MEDIUM: '보통',
  LOW: '낮음',
}

const TONES = {
  PLANNED: 'muted',
  ACTIVE: 'accent',
  COMPLETED: 'success',
  TODO: 'muted',
  IN_PROGRESS: 'accent',
  DONE: 'success',
  HIGH: 'danger',
  MEDIUM: 'accent',
  LOW: 'muted',
}

defineProps({
  status: { type: String, required: true },
})
</script>

<template>
  <span class="badge" :class="`badge--${TONES[status] || 'muted'}`">
    {{ LABELS[status] || status }}
  </span>
</template>

<style scoped>
.badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: var(--r-full);
  font-size: var(--fs-xs);
  font-weight: 600;
  white-space: nowrap;
}
.badge--muted {
  background: var(--c-surface-alt);
  color: var(--c-text-2);
}
.badge--accent {
  background: var(--c-accent-soft);
  color: var(--c-accent);
}
.badge--success {
  background: var(--c-mint);
  color: var(--c-mint-ink);
}
.badge--danger {
  background: var(--c-peach);
  color: var(--c-danger);
}
</style>
