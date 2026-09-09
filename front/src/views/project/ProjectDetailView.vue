<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useProjectStore } from '@/stores/project'
import { useTaskStore } from '@/stores/task'
import { createInvite as apiCreateInvite } from '@/api/project'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import ProjectForm from '@/components/project/ProjectForm.vue'
import TaskList from '@/components/task/TaskList.vue'
import TaskForm from '@/components/task/TaskForm.vue'
import SprintPanel from '@/components/sprint/SprintPanel.vue'
import { formatDate } from '@/utils/date'

const route = useRoute()
const router = useRouter()
const store = useProjectStore()
const taskStore = useTaskStore()
const { current, members, loading, error } = storeToRefs(store)
const { tasks, loading: tasksLoading } = storeToRefs(taskStore)

const tabs = ['Overview', 'Sprint', 'Tasks', 'Meetings']
const activeTab = ref('Overview')
const showTaskCreate = ref(false)

const showEdit = ref(false)
const showDelete = ref(false)
const deleting = ref(false)

const invite = ref(null)
const inviting = ref(false)
const inviteError = ref('')
const copied = ref(false)

const isAdmin = computed(() => current.value?.myRole === 'ADMIN')

watch(
  () => route.params.id,
  async (id) => {
    invite.value = null
    activeTab.value = 'Overview'
    try {
      await store.fetchProject(id)
      await store.fetchMembers(id)
    } catch {
      /* error 는 store.error 로 표시 */
    }
  },
  { immediate: true },
)

// Tasks 탭 진입 시 로드
watch([activeTab, () => route.params.id], ([tab, id]) => {
  if (tab === 'Tasks' && id) taskStore.fetchTasks(id)
})

async function handleTaskCreate(payload) {
  await taskStore.createTask(payload)
}

function handleStatusChange(task, status) {
  taskStore.changeStatus(task, status)
}

async function handleEdit(payload) {
  await store.updateProject(route.params.id, payload)
}

async function handleDelete() {
  deleting.value = true
  try {
    await store.deleteProject(route.params.id)
    router.replace({ name: 'projects' })
  } finally {
    deleting.value = false
    showDelete.value = false
  }
}

async function generateInvite() {
  inviting.value = true
  inviteError.value = ''
  copied.value = false
  try {
    invite.value = await apiCreateInvite(route.params.id)
  } catch (e) {
    inviteError.value = e.normalizedMessage || '초대 링크 생성에 실패했습니다.'
  } finally {
    inviting.value = false
  }
}

async function copyInvite() {
  try {
    await navigator.clipboard.writeText(invite.value.inviteUrl)
    copied.value = true
    setTimeout(() => (copied.value = false), 2000)
  } catch {
    /* 클립보드 권한 없음 — 사용자가 직접 복사 */
  }
}
</script>

<template>
  <PagePlaceholder
    :title="current?.name || '프로젝트'"
    subtitle="프로젝트의 업무 · 회의 · Sprint를 한곳에서 관리하세요."
  >
    <template v-if="isAdmin" #actions>
      <BaseButton variant="ghost" size="sm" @click="showEdit = true">수정</BaseButton>
      <BaseButton variant="ghost" size="sm" @click="showDelete = true">삭제</BaseButton>
    </template>

    <p v-if="error" class="detail-error">{{ error }}</p>

    <template v-else-if="current">
      <BaseCard>
        <div class="phead">
          <div class="phead__title">
            <StatusBadge :status="current.status" />
            <span v-if="current.startDate || current.endDate" class="phead__dates">
              {{ formatDate(current.startDate) }} ~ {{ formatDate(current.endDate) }}
            </span>
          </div>
          <p class="phead__desc">{{ current.description || '설명이 없습니다.' }}</p>
        </div>
      </BaseCard>

      <div class="tabs">
        <button
          v-for="t in tabs"
          :key="t"
          class="tab"
          :class="{ 'is-active': activeTab === t }"
          @click="activeTab = t"
        >
          {{ t }}
        </button>
      </div>

      <!-- Overview -->
      <div v-if="activeTab === 'Overview'" class="overview">
        <BaseCard>
          <template #header>
            <span>멤버 {{ members.length }}</span>
            <BaseButton
              v-if="isAdmin"
              variant="soft"
              size="sm"
              :disabled="inviting"
              @click="generateInvite"
            >
              {{ inviting ? '생성 중…' : '초대 링크 생성' }}
            </BaseButton>
          </template>

          <p v-if="inviteError" class="detail-error">{{ inviteError }}</p>

          <div v-if="invite" class="invite">
            <input class="invite__url" :value="invite.inviteUrl" readonly />
            <BaseButton variant="ghost" size="sm" @click="copyInvite">
              {{ copied ? '복사됨' : '복사' }}
            </BaseButton>
          </div>
          <p v-if="invite" class="invite__hint">
            {{ formatDate(invite.expiresAt) }}까지 유효한 링크입니다.
          </p>

          <ul class="members">
            <li v-for="m in members" :key="m.userId" class="member">
              <span class="member__avatar">{{ m.name?.charAt(0) || '?' }}</span>
              <span class="member__info">
                <span class="member__name">{{ m.name }}</span>
                <span class="member__email">{{ m.email }}</span>
              </span>
              <span class="member__role" :class="{ 'is-admin': m.role === 'ADMIN' }">
                {{ m.role === 'ADMIN' ? '관리자' : '멤버' }}
              </span>
            </li>
          </ul>
        </BaseCard>
      </div>

      <!-- Tasks -->
      <div v-else-if="activeTab === 'Tasks'" class="tasks-tab">
        <div class="tasks-tab__head">
          <h2 class="tasks-tab__title">Task</h2>
          <BaseButton variant="primary" size="sm" @click="showTaskCreate = true">
            <template #icon><AppIcon name="plus" :size="16" /></template>
            새 Task
          </BaseButton>
        </div>
        <TaskList :tasks="tasks" :loading="tasksLoading" @change-status="handleStatusChange" />
      </div>

      <!-- Sprint -->
      <SprintPanel
        v-else-if="activeTab === 'Sprint'"
        :project-id="current.projectId"
        :is-admin="isAdmin"
      />

      <!-- Meetings: Phase 5 -->
      <BaseCard v-else>
        <p class="empty-hint">"Meetings" — Phase 5에서 연결됩니다.</p>
      </BaseCard>
    </template>

    <div v-else-if="loading" class="skeleton" style="height: 160px" />
  </PagePlaceholder>

  <ProjectForm v-if="current" v-model:open="showEdit" :project="current" :submit-fn="handleEdit" />

  <TaskForm
    v-if="current"
    v-model:open="showTaskCreate"
    :project-id="current.projectId"
    :members="members"
    :submit-fn="handleTaskCreate"
  />

  <BaseModal v-model:open="showDelete" title="프로젝트 삭제" size="sm">
    <p>정말 이 프로젝트를 삭제할까요? 관련 데이터가 모두 사라집니다.</p>
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
.phead {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.phead__title {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}
.phead__dates {
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.phead__desc {
  font-size: var(--fs-md);
  color: var(--c-text-2);
  white-space: pre-wrap;
}
.tasks-tab {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.tasks-tab__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tasks-tab__title {
  font-size: var(--fs-lg);
}
.tabs {
  display: flex;
  gap: var(--sp-1);
  border-bottom: 1px solid var(--c-border);
}
.tab {
  padding: var(--sp-3) var(--sp-4);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.tab.is-active {
  color: var(--c-text);
  font-weight: 600;
  border-bottom-color: var(--c-primary);
}
.invite {
  display: flex;
  gap: var(--sp-2);
  margin-bottom: var(--sp-2);
}
.invite__url {
  flex: 1;
  height: 36px;
  padding: 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
  font-size: var(--fs-sm);
}
.invite__hint {
  margin-bottom: var(--sp-4);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.members {
  display: flex;
  flex-direction: column;
}
.member {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-3) 0;
  border-bottom: 1px solid var(--c-border);
}
.member:last-child {
  border-bottom: none;
}
.member__avatar {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border-radius: var(--r-full);
  background: var(--c-accent-soft);
  color: var(--c-accent);
  font-size: var(--fs-sm);
  font-weight: 700;
}
.member__info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.member__name {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.member__email {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.member__role {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.member__role.is-admin {
  color: var(--c-accent);
  font-weight: 600;
}
</style>
