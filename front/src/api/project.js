import http from './axios'

/** 내 프로젝트 목록 */
export function fetchProjects() {
  return http.get('/projects').then((res) => res.data)
}

/** 프로젝트 생성 — { name, description?, startDate?, endDate? } */
export function createProject(payload) {
  return http.post('/projects', payload).then((res) => res.data)
}

/** 프로젝트 상세 */
export function fetchProject(projectId) {
  return http.get(`/projects/${projectId}`).then((res) => res.data)
}

/** 프로젝트 수정 (ADMIN) — PUT: 전체 필드 전송 */
export function updateProject(projectId, payload) {
  return http.put(`/projects/${projectId}`, payload).then((res) => res.data)
}

/** 프로젝트 삭제 (ADMIN) */
export function deleteProject(projectId) {
  return http.delete(`/projects/${projectId}`)
}

/** 멤버 목록 */
export function fetchMembers(projectId) {
  return http.get(`/projects/${projectId}/members`).then((res) => res.data)
}

/** 초대 링크 생성 (ADMIN) — { token, inviteUrl, expiresAt } */
export function createInvite(projectId) {
  return http.post(`/projects/${projectId}/invites`).then((res) => res.data)
}

/** 초대 링크로 참여 — 참여한 프로젝트 반환 */
export function joinByInvite(token) {
  return http.post(`/invites/${token}/join`).then((res) => res.data)
}
