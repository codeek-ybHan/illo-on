import http from './axios'

/** 프로젝트 회의 목록 */
export function fetchMeetings(projectId) {
  return http.get(`/projects/${projectId}/meetings`).then((res) => res.data)
}

/** 회의 생성 — { title, content?, meetingAt?, attendeeIds? } */
export function createMeeting(projectId, payload) {
  return http.post(`/projects/${projectId}/meetings`, payload).then((res) => res.data)
}

export function fetchMeeting(meetingId) {
  return http.get(`/meetings/${meetingId}`).then((res) => res.data)
}

/** 회의 수정 — PUT 전체 교체 (제목·내용·일시·참석자) */
export function updateMeeting(meetingId, payload) {
  return http.put(`/meetings/${meetingId}`, payload).then((res) => res.data)
}

export function deleteMeeting(meetingId) {
  return http.delete(`/meetings/${meetingId}`)
}
