import http from './axios'

/** 피드백 목록 (전체 사용자 공통) */
export function fetchFeedbacks() {
  return http.get('/feedbacks').then((res) => res.data)
}

/** 피드백 작성 — { content } */
export function createFeedback(payload) {
  return http.post('/feedbacks', payload).then((res) => res.data)
}

/** 반영완료 토글 (작성자 계정만 가능) — { resolved } */
export function resolveFeedback(feedbackId, resolved) {
  return http.patch(`/feedbacks/${feedbackId}/resolve`, { resolved }).then((res) => res.data)
}
