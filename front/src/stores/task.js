import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * Task 상태 (스켈레톤).
 * Phase 3에서 api/task.js 연동 액션(fetchTasks, fetchMyTasks, createTask, updateTask, ...)을 채운다.
 */
export const useTaskStore = defineStore('task', () => {
  const tasks = ref([]) // 현재 컨텍스트(프로젝트/스프린트)의 Task 목록
  const myTasks = ref([]) // 메인보드용 내 Task
  const current = ref(null)
  const loading = ref(false)
  const error = ref('')

  return { tasks, myTasks, current, loading, error }
})
