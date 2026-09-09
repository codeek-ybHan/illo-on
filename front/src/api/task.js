import http from './axios'

/** 프로젝트 Task 목록. opts: { sprintId, meetingId } 로 필터 */
export function fetchTasks(projectId, opts = {}) {
  const params = { projectId }
  if (opts.sprintId != null) params.sprintId = opts.sprintId
  if (opts.meetingId != null) params.meetingId = opts.meetingId
  return http.get('/tasks', { params }).then((res) => res.data)
}

/** 내 Task (담당자 = 나) */
export function fetchMyTasks() {
  return http.get('/me/tasks').then((res) => res.data)
}

export function fetchTask(taskId) {
  return http.get(`/tasks/${taskId}`).then((res) => res.data)
}

/** 생성 — { projectId, title, description?, assigneeId?, dueDate?, priority?, meetingId?, sprintId? } */
export function createTask(payload) {
  return http.post('/tasks', payload).then((res) => res.data)
}

/** 수정 (PUT 전체 교체 — 상태 변경 · Sprint 배정 포함) */
export function updateTask(taskId, payload) {
  return http.put(`/tasks/${taskId}`, payload).then((res) => res.data)
}

export function deleteTask(taskId) {
  return http.delete(`/tasks/${taskId}`)
}
