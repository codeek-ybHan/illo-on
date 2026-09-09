<script setup>
import { computed, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useUiStore } from '@/stores/ui'
import { useProjectStore } from '@/stores/project'
import { useMeetingStore } from '@/stores/meeting'
import { useTaskStore } from '@/stores/task'
import { toast } from '@/utils/toast'
import ProjectForm from '@/components/project/ProjectForm.vue'
import MeetingForm from '@/components/meeting/MeetingForm.vue'
import TaskForm from '@/components/task/TaskForm.vue'

const router = useRouter()
const ui = useUiStore()
const projectStore = useProjectStore()
const meetingStore = useMeetingStore()
const taskStore = useTaskStore()
const { projects } = storeToRefs(projectStore)

const target = computed(() => ui.createTarget)

// 메뉴에서 회의/업무 만들기를 누르면 프로젝트 선택이 필요 → 목록 확보
watch(target, (t) => {
  if ((t === 'meeting' || t === 'task') && !projects.value.length) {
    projectStore.fetchProjects()
  }
})

function onOpenChange(open) {
  if (!open) ui.closeCreate()
}

async function createProject(payload) {
  const created = await projectStore.createProject(payload)
  toast().success('프로젝트를 만들었습니다.')
  ui.closeCreate()
  router.push({ name: 'project-detail', params: { id: created.projectId } })
}

async function createMeeting(payload, projectId) {
  const created = await meetingStore.createMeeting(projectId, payload)
  toast().success('회의를 만들었습니다.')
  ui.closeCreate()
  router.push({ name: 'meeting-detail', params: { id: created.meetingId } })
}

async function createTask(payload) {
  const created = await taskStore.createTask(payload)
  toast().success('업무를 만들었습니다.')
  ui.closeCreate()
  router.push({ name: 'task-detail', params: { id: created.taskId } })
}
</script>

<template>
  <ProjectForm
    :open="target === 'project'"
    :submit-fn="createProject"
    @update:open="onOpenChange"
  />
  <MeetingForm
    :open="target === 'meeting'"
    :projects="projects"
    :submit-fn="createMeeting"
    @update:open="onOpenChange"
  />
  <TaskForm
    :open="target === 'task'"
    :projects="projects"
    :submit-fn="createTask"
    @update:open="onOpenChange"
  />
</template>
