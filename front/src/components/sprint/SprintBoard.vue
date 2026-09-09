<script setup>
import { computed } from 'vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import AppIcon from '@/components/common/AppIcon.vue'

const props = defineProps({
  tasks: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})
defineEmits(['change-status', 'unassign'])

const COLUMNS = [
  { key: 'TODO', label: '할 일' },
  { key: 'IN_PROGRESS', label: '진행 중' },
  { key: 'DONE', label: '완료' },
]

const grouped = computed(() =>
  COLUMNS.map((c) => ({ ...c, items: props.tasks.filter((t) => t.status === c.key) })),
)
</script>

<template>
  <div v-if="loading" class="skeleton" style="height: 160px" />
  <div v-else class="board">
    <div v-for="col in grouped" :key="col.key" class="board__col">
      <header class="board__head">
        <StatusBadge :status="col.key" />
        <span class="board__count">{{ col.items.length }}</span>
      </header>
      <div class="board__body">
        <article v-for="t in col.items" :key="t.taskId" class="bcard">
          <RouterLink class="bcard__title" :to="{ name: 'task-detail', params: { id: t.taskId } }">
            {{ t.title }}
          </RouterLink>
          <div class="bcard__meta">
            <StatusBadge :status="t.priority" />
            <span>{{ t.assigneeName || '미지정' }}</span>
          </div>
          <div class="bcard__actions">
            <select
              class="bcard__status"
              :value="t.status"
              @change="$emit('change-status', t, $event.target.value)"
            >
              <option value="TODO">할 일</option>
              <option value="IN_PROGRESS">진행 중</option>
              <option value="DONE">완료</option>
            </select>
            <button class="bcard__remove" type="button" @click="$emit('unassign', t)">
              <AppIcon name="plus" :size="14" style="transform: rotate(45deg)" />
              빼기
            </button>
          </div>
        </article>
        <p v-if="!col.items.length" class="board__empty">없음</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.board {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--sp-3);
  align-items: start;
}
.board__col {
  background: var(--c-surface-alt);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  padding: var(--sp-3);
}
.board__head {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  padding-bottom: var(--sp-3);
}
.board__count {
  margin-left: auto;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.board__body {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.bcard {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  padding: var(--sp-3);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
}
.bcard__title {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.bcard__title:hover {
  text-decoration: underline;
}
.bcard__meta {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.bcard__actions {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}
.bcard__status {
  flex: 1;
  height: 28px;
  padding: 0 var(--sp-2);
  border: 1px solid var(--c-border);
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
}
.bcard__remove {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.bcard__remove:hover {
  color: var(--c-danger);
}
.board__empty {
  padding: var(--sp-3);
  text-align: center;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}

@media (max-width: 1080px) {
  .board {
    grid-template-columns: 1fr;
  }
}
</style>
