<script setup>
import { ref, watch, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useSprintStore } from '@/stores/sprint'
import { useTaskStore, toUpdatePayload } from '@/stores/task'
import { fetchTasks as apiFetchTasks } from '@/api/task'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import SprintForm from './SprintForm.vue'
import SprintBoard from './SprintBoard.vue'
import { formatDate } from '@/utils/date'

const props = defineProps({
  projectId: { type: [Number, String], required: true },
  isAdmin: { type: Boolean, default: false },
})

const sprintStore = useSprintStore()
const taskStore = useTaskStore()
const { sprints, loading, error } = storeToRefs(sprintStore)

const selectedId = ref(null)
const boardTasks = ref([])
const boardLoading = ref(false)

const showCreate = ref(false)
const editing = ref(null)
const deleting = ref(null)
const showAssign = ref(false)
const unassignedTasks = ref([])
const assignLoading = ref(false)

const selected = computed(() => sprints.value.find((s) => s.sprintId === selectedId.value) || null)

watch(
  () => props.projectId,
  (id) => {
    selectedId.value = null
    boardTasks.value = []
    if (id) sprintStore.fetchSprints(id)
  },
  { immediate: true },
)

async function selectSprint(sprint) {
  selectedId.value = sprint.sprintId
  boardLoading.value = true
  try {
    boardTasks.value = await apiFetchTasks(props.projectId, { sprintId: sprint.sprintId })
  } finally {
    boardLoading.value = false
  }
}

async function handleCreate(payload) {
  const created = await sprintStore.createSprint(props.projectId, payload)
  await selectSprint(created)
}

async function handleEdit(payload) {
  await sprintStore.updateSprint(editing.value.sprintId, payload)
}

async function confirmDelete() {
  await sprintStore.deleteSprint(deleting.value.sprintId)
  if (selectedId.value === deleting.value.sprintId) {
    selectedId.value = null
    boardTasks.value = []
  }
  deleting.value = null
}

async function changeStatus(task, status) {
  const updated = await taskStore.updateTask(task.taskId, toUpdatePayload(task, { status }))
  boardTasks.value = boardTasks.value.map((t) => (t.taskId === updated.taskId ? updated : t))
  sprintStore.refreshSprint(selectedId.value, props.projectId)
}

async function unassign(task) {
  await taskStore.updateTask(task.taskId, toUpdatePayload(task, { sprintId: null }))
  boardTasks.value = boardTasks.value.filter((t) => t.taskId !== task.taskId)
  sprintStore.refreshSprint(selectedId.value, props.projectId)
}

async function openAssign() {
  showAssign.value = true
  assignLoading.value = true
  try {
    const all = await apiFetchTasks(props.projectId)
    unassignedTasks.value = all.filter((t) => t.sprintId == null)
  } finally {
    assignLoading.value = false
  }
}

async function assignTask(task) {
  const updated = await taskStore.updateTask(
    task.taskId,
    toUpdatePayload(task, { sprintId: selectedId.value }),
  )
  boardTasks.value = [updated, ...boardTasks.value]
  unassignedTasks.value = unassignedTasks.value.filter((t) => t.taskId !== task.taskId)
  sprintStore.refreshSprint(selectedId.value, props.projectId)
}
</script>

<template>
  <div class="spanel">
    <div class="spanel__head">
      <h2 class="spanel__title">Sprint</h2>
      <BaseButton v-if="isAdmin" variant="primary" size="sm" @click="showCreate = true">
        <template #icon><AppIcon name="plus" :size="16" /></template>
        새 Sprint
      </BaseButton>
      <span v-else class="spanel__hint">Sprint 생성은 프로젝트 관리자만 가능합니다</span>
    </div>

    <p v-if="error" class="spanel__error">{{ error }}</p>

    <div v-if="loading && !sprints.length" class="skeleton" style="height: 80px" />
    <p v-else-if="!sprints.length" class="empty-hint">아직 Sprint가 없습니다.</p>

    <ul v-else class="slist">
      <li
        v-for="s in sprints"
        :key="s.sprintId"
        class="scard"
        :class="{ 'is-selected': s.sprintId === selectedId }"
        @click="selectSprint(s)"
      >
        <div class="scard__top">
          <span class="scard__name">{{ s.name }}</span>
          <StatusBadge :status="s.status" />
        </div>
        <div class="scard__period">
          {{ s.startDate ? formatDate(s.startDate) : '미정' }} ~
          {{ s.endDate ? formatDate(s.endDate) : '미정' }}
        </div>
        <ProgressBar :value="s.progress" color="var(--c-accent)" show-label />
        <div class="scard__foot">
          <span class="u-muted">{{ s.doneCount }} / {{ s.taskCount }} 완료</span>
          <span v-if="isAdmin" class="scard__actions" @click.stop>
            <button @click="editing = s">수정</button>
            <button @click="deleting = s">삭제</button>
          </span>
        </div>
      </li>
    </ul>

    <BaseCard v-if="selected" class="spanel__board">
      <template #header>
        <span>{{ selected.name }} 보드</span>
        <BaseButton variant="soft" size="sm" @click="openAssign">Task 배정</BaseButton>
      </template>
      <SprintBoard
        :tasks="boardTasks"
        :loading="boardLoading"
        @change-status="changeStatus"
        @unassign="unassign"
      />
    </BaseCard>
  </div>

  <SprintForm v-model:open="showCreate" :submit-fn="handleCreate" />
  <SprintForm
    :open="Boolean(editing)"
    :sprint="editing"
    :submit-fn="handleEdit"
    @update:open="editing = $event ? editing : null"
  />

  <BaseModal :open="Boolean(deleting)" title="Sprint 삭제" size="sm" @update:open="deleting = null">
    <p>이 Sprint를 삭제할까요? 배정된 Task는 유지되고 배정만 해제됩니다.</p>
    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="deleting = null">취소</BaseButton>
      <BaseButton variant="primary" size="sm" @click="confirmDelete">삭제</BaseButton>
    </template>
  </BaseModal>

  <BaseModal v-model:open="showAssign" title="Sprint에 Task 배정">
    <div v-if="assignLoading" class="skeleton" style="height: 120px" />
    <p v-else-if="!unassignedTasks.length" class="empty-hint">배정 가능한 Task가 없습니다.</p>
    <ul v-else class="assign-list">
      <li v-for="t in unassignedTasks" :key="t.taskId" class="assign-item">
        <span class="assign-item__title">{{ t.title }}</span>
        <StatusBadge :status="t.status" />
        <BaseButton variant="soft" size="sm" @click="assignTask(t)">배정</BaseButton>
      </li>
    </ul>
  </BaseModal>
</template>

<style scoped>
.spanel {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.spanel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.spanel__title {
  font-size: var(--fs-lg);
}
.spanel__hint {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.spanel__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.slist {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--sp-3);
}
.scard {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  padding: var(--sp-4);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  cursor: pointer;
}
.scard.is-selected {
  border-color: var(--c-primary);
}
.scard__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.scard__name {
  font-weight: 600;
}
.scard__period {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.scard__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: var(--fs-xs);
}
.scard__actions {
  display: flex;
  gap: var(--sp-2);
}
.scard__actions button {
  color: var(--c-text-2);
}
.scard__actions button:hover {
  color: var(--c-text);
}
.spanel__board {
  margin-top: var(--sp-2);
}
.assign-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.assign-item {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-2) 0;
  border-bottom: 1px solid var(--c-border);
}
.assign-item__title {
  flex: 1;
  font-size: var(--fs-sm);
}
</style>
