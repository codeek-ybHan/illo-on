import http from './axios'

/** 프로젝트 Sprint 목록 */
export function fetchSprints(projectId) {
  return http.get(`/projects/${projectId}/sprints`).then((res) => res.data)
}

/** Sprint 생성 (ADMIN) — { name, startDate?, endDate? } */
export function createSprint(projectId, payload) {
  return http.post(`/projects/${projectId}/sprints`, payload).then((res) => res.data)
}

export function fetchSprint(sprintId) {
  return http.get(`/sprints/${sprintId}`).then((res) => res.data)
}

/** Sprint 수정 (ADMIN) — PUT 전체 교체 */
export function updateSprint(sprintId, payload) {
  return http.put(`/sprints/${sprintId}`, payload).then((res) => res.data)
}

export function deleteSprint(sprintId) {
  return http.delete(`/sprints/${sprintId}`)
}
