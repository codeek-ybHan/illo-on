import http from './axios'

/** 피드백 목록 (전체 사용자 공통) */
export function fetchFeedbacks() {
  return http.get('/feedbacks').then((res) => res.data)
}

/** 피드백 작성 — { content, replyToId? } */
export function createFeedback(payload) {
  return http.post('/feedbacks', payload).then((res) => res.data)
}

/** 반영완료 토글 (관리자 계정만 가능) — { resolved } */
export function resolveFeedback(feedbackId, resolved) {
  return http.patch(`/feedbacks/${feedbackId}/resolve`, { resolved }).then((res) => res.data)
}

/** 피드백 내용 수정 (작성자 본인만 가능) */
export function updateFeedback(feedbackId, content) {
  return http.patch(`/feedbacks/${feedbackId}`, { content }).then((res) => res.data)
}

/** 피드백 삭제 (관리자 계정만 가능) */
export function deleteFeedback(feedbackId) {
  return http.delete(`/feedbacks/${feedbackId}`)
}

/** 공감 토글 */
export function toggleFeedbackLike(feedbackId) {
  return http.post(`/feedbacks/${feedbackId}/like`).then((res) => res.data)
}
