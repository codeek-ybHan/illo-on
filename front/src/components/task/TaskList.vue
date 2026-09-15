<script setup>
import { ref, computed } from 'vue'
import TaskCard from './TaskCard.vue'

const props = defineProps({
  tasks: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  showProject: { type: Boolean, default: false },
  defaultFilter: { type: String, default: 'ALL' },
  maxItems: { type: Number, default: null },
})
defineEmits(['change-status'])

const FILTERS = [
  { label: '전체', value: 'ALL' },
  { label: '진행 전', value: 'TODO' },
  { label: '진행 중', value: 'IN_PROGRESS' },
  { label: '완료', value: 'DONE' },
]
const filter = ref(props.defaultFilter)
const expanded = ref(false)

function selectFilter(value) {
  filter.value = value
  expanded.value = false
}

const filtered = computed(() =>
  filter.value === 'ALL' ? props.tasks : props.tasks.filter((t) => t.status === filter.value),
)
const isCapped = computed(() => props.maxItems != null && filtered.value.length > props.maxItems)
const visible = computed(() =>
  isCapped.value && !expanded.value ? filtered.value.slice(0, props.maxItems) : filtered.value,
)
const hiddenCount = computed(() => filtered.value.length - props.maxItems)
</script>

<template>
  <div class="tlist">
    <div class="tlist__filters">
      <button
        v-for="f in FILTERS"
        :key="f.value"
        class="tlist__filter"
        :class="{ 'is-active': filter === f.value }"
        @click="selectFilter(f.value)"
      >
        {{ f.label }}
        <span v-if="f.value === 'ALL'" class="tlist__count">{{ tasks.length }}</span>
      </button>
    </div>

    <div v-if="loading" class="tlist__body">
      <div v-for="n in 3" :key="n" class="skeleton" style="height: 60px" />
    </div>

    <p v-else-if="!visible.length" class="empty-hint">
      {{ tasks.length ? '해당 상태의 Task가 없습니다.' : '아직 Task가 없습니다.' }}
    </p>

    <div v-else class="tlist__body">
      <TaskCard
        v-for="t in visible"
        :key="t.taskId"
        :task="t"
        :show-project="showProject"
        @change-status="(task, status) => $emit('change-status', task, status)"
      />
      <button v-if="isCapped && !expanded" class="tlist__more" type="button" @click="expanded = true">
        더보기 (+{{ hiddenCount }})
      </button>
    </div>
  </div>
</template>

<style scoped>
.tlist {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.tlist__filters {
  display: flex;
  gap: var(--sp-2);
}
.tlist__filter {
  padding: 6px 12px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.tlist__filter.is-active {
  background: var(--c-primary);
  border-color: var(--c-primary);
  color: var(--c-primary-contrast);
}
.tlist__count {
  margin-left: 4px;
  opacity: 0.7;
}
.tlist__body {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.tlist__more {
  align-self: center;
  padding: var(--sp-2) var(--sp-4);
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.tlist__more:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
</style>
