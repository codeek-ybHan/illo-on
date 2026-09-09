import http from './axios'

/** 메인보드 집계 */
export function fetchBoard() {
  return http.get('/me/board').then((res) => res.data)
}

/** 내 회의 (전 프로젝트). from/to: 'YYYY-MM-DD' */
export function fetchMyMeetings(from, to) {
  const params = {}
  if (from) params.from = from
  if (to) params.to = to
  return http.get('/me/meetings', { params }).then((res) => res.data)
}
