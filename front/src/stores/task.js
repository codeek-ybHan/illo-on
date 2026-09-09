import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/api/task'
import { toast } from '@/utils/toast'

/** Task 객체 → PUT 전체 교체 payload */
export function toUpdatePayload(task, overrides = {}) {
  return {
    title: task.title,
    description: task.description ?? null,
    assigneeId: task.assigneeId ?? null,
    dueDate: task.dueDate ?? null,
    priority: task.priority,
    status: task.status,
    sprintId: task.sprintId ?? null,
    meetingId: task.meetingId ?? null,
    ...overrides,
  }
}

export const useTaskStore = defineStore('task', () => {
  const tasks = ref([]) // 현재 프로젝트의 Task 목록
  const myTasks = ref([]) // 메인보드용 내 Task
  const current = ref(null)
  const loading = ref(false)
  const error = ref('')

  async function fetchTasks(projectId) {
    loading.value = true
    error.value = ''
    try {
      tasks.value = await api.fetchTasks(projectId)
    } catch (e) {
      error.value = e.normalizedMessage || 'Task를 불러오지 못했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function fetchMyTasks() {
    myTasks.value = await api.fetchMyTasks()
    return myTasks.value
  }

  async function fetchTask(taskId) {
    loading.value = true
    error.value = ''
    try {
      current.value = await api.fetchTask(taskId)
      return current.value
    } catch (e) {
      error.value = e.normalizedMessage || 'Task를 불러오지 못했습니다.'
      current.value = null
      throw e
    } finally {
      loading.value = false
    }
  }

  async function createTask(payload) {
    const created = await api.createTask(payload)
    tasks.value = [created, ...tasks.value]
    return created
  }

  async function updateTask(taskId, payload) {
    const updated = await api.updateTask(taskId, payload)
    patchLocal(updated)
    return updated
  }

  /** 상태만 빠르게 변경 (인라인 UI라 실패 시 토스트) */
  async function changeStatus(task, status) {
    try {
      return await updateTask(task.taskId, toUpdatePayload(task, { status }))
    } catch (e) {
      toast().error(e.normalizedMessage || '상태 변경에 실패했습니다.')
      throw e
    }
  }

  async function deleteTask(taskId) {
    await api.deleteTask(taskId)
    const id = Number(taskId)
    tasks.value = tasks.value.filter((t) => t.taskId !== id)
    myTasks.value = myTasks.value.filter((t) => t.taskId !== id)
    if (current.value?.taskId === id) current.value = null
  }

  function patchLocal(updated) {
    const replace = (arr) => arr.map((t) => (t.taskId === updated.taskId ? updated : t))
    tasks.value = replace(tasks.value)
    myTasks.value = replace(myTasks.value)
    if (current.value?.taskId === updated.taskId) current.value = updated
  }

  return {
    tasks,
    myTasks,
    current,
    loading,
    error,
    fetchTasks,
    fetchMyTasks,
    fetchTask,
    createTask,
    updateTask,
    changeStatus,
    deleteTask,
  }
})
