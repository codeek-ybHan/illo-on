<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useTaskStore } from '@/stores/task'
import { useProjectStore } from '@/stores/project'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import TaskForm from '@/components/task/TaskForm.vue'
import { formatDate, formatDue } from '@/utils/date'
import { toast } from '@/utils/toast'

const route = useRoute()
const router = useRouter()
const store = useTaskStore()
const projectStore = useProjectStore()
const { current, error } = storeToRefs(store)
const { members } = storeToRefs(projectStore)

const STATUS_FLOW = ['TODO', 'IN_PROGRESS', 'DONE']
const showEdit = ref(false)
const showDelete = ref(false)
const deleting = ref(false)

const nextStatus = computed(() => {
  if (!current.value) return null
  const i = STATUS_FLOW.indexOf(current.value.status)
  return i >= 0 && i < STATUS_FLOW.length - 1 ? STATUS_FLOW[i + 1] : null
})
const nextStatusLabel = computed(
  () => ({ IN_PROGRESS: '진행 중으로', DONE: '완료로' })[nextStatus.value],
)

watch(
  () => route.params.id,
  async (id) => {
    try {
      const task = await store.fetchTask(id)
      if (task?.projectId) projectStore.fetchMembers(task.projectId)
    } catch {
      /* store.error */
    }
  },
  { immediate: true },
)

function advanceStatus() {
  if (nextStatus.value) store.changeStatus(current.value, nextStatus.value)
}

async function handleEdit(payload) {
  await store.updateTask(route.params.id, payload)
  toast().success('저장했습니다.')
}

async function handleDelete() {
  deleting.value = true
  try {
    const projectId = current.value?.projectId
    await store.deleteTask(route.params.id)
    toast().success('Task를 삭제했습니다.')
    router.replace(
      projectId ? { name: 'project-detail', params: { id: projectId } } : { name: 'mainboard' },
    )
  } catch (e) {
    toast().error(e.normalizedMessage || '삭제에 실패했습니다.')
  } finally {
    deleting.value = false
    showDelete.value = false
  }
}
</script>

<template>
  <PagePlaceholder
    :title="current?.title || 'Task'"
    subtitle="업무 정보와 이 업무가 생성된 회의 맥락을 확인하세요."
  >
    <template v-if="current" #actions>
      <BaseButton v-if="nextStatus" variant="primary" size="sm" @click="advanceStatus">
        {{ nextStatusLabel }}
      </BaseButton>
      <BaseButton variant="ghost" size="sm" @click="showEdit = true">수정</BaseButton>
      <BaseButton variant="ghost" size="sm" @click="showDelete = true">삭제</BaseButton>
    </template>

    <p v-if="error" class="detail-error">{{ error }}</p>

    <div v-else-if="current" class="task-grid">
      <BaseCard>
        <template #header>업무 정보</template>
        <div class="task-body">
          <p class="task-desc">{{ current.description || '설명이 없습니다.' }}</p>
          <dl class="fields">
            <div class="field">
              <dt>상태</dt>
              <dd><StatusBadge :status="current.status" /></dd>
            </div>
            <div class="field">
              <dt>우선순위</dt>
              <dd><StatusBadge :status="current.priority" /></dd>
            </div>
            <div class="field">
              <dt>담당자</dt>
              <dd>{{ current.assigneeName || '미지정' }}</dd>
            </div>
            <div class="field">
              <dt>마감일시</dt>
              <dd>{{ current.dueDate ? formatDue(current.dueDate) : '미정' }}</dd>
            </div>
            <div class="field">
              <dt>프로젝트</dt>
              <dd>
                <RouterLink
                  :to="{ name: 'project-detail', params: { id: current.projectId } }"
                  class="link"
                >
                  {{ current.projectName }}
                </RouterLink>
              </dd>
            </div>
            <div class="field">
              <dt>생성일</dt>
              <dd>{{ formatDate(current.createdAt) }}</dd>
            </div>
          </dl>
        </div>
      </BaseCard>

      <BaseCard>
        <template #header>관련 회의</template>
        <RouterLink
          v-if="current.meetingId"
          class="related-meeting"
          :to="{ name: 'meeting-detail', params: { id: current.meetingId } }"
        >
          <span class="related-meeting__icon"><AppIcon name="meeting" :size="18" /></span>
          <div class="related-meeting__text">
            <span class="related-meeting__label">이 업무가 논의된 회의</span>
            <span class="u-muted">회의로 이동</span>
          </div>
          <AppIcon name="chevronRight" :size="16" />
        </RouterLink>
        <p v-else class="empty-hint">회의에서 생성된 업무가 아닙니다.</p>
      </BaseCard>
    </div>
  </PagePlaceholder>

  <TaskForm
    v-if="current"
    v-model:open="showEdit"
    :task="current"
    :project-id="current.projectId"
    :members="members"
    :submit-fn="handleEdit"
  />

  <BaseModal v-model:open="showDelete" title="Task 삭제" size="sm">
    <p>이 Task를 삭제할까요?</p>
    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="showDelete = false">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="deleting" @click="handleDelete">
        {{ deleting ? '삭제 중…' : '삭제' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.detail-error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.task-grid {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: var(--sp-4);
  align-items: start;
}
.task-body {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}
.task-desc {
  font-size: var(--fs-md);
  color: var(--c-text-2);
  white-space: pre-wrap;
}
.fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-4) var(--sp-5);
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
.field dd {
  font-size: var(--fs-sm);
}
.link {
  color: var(--c-accent);
  font-weight: 500;
}
.related-meeting {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  width: 100%;
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
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
  gap: 4px;
  font-size: var(--fs-sm);
}
.related-meeting__label {
  font-weight: 500;
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
