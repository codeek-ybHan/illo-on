import http from './axios'

/** 전체 사용자 목록 (관리자 전용) */
export function fetchAdminUsers() {
  return http.get('/admin/users').then((res) => res.data)
}

/** 서비스 통계 (관리자 전용) */
export function fetchAdminStats() {
  return http.get('/admin/stats').then((res) => res.data)
}

/** 사용자 완전 삭제 (관리자 전용, 자기 자신 삭제 불가) */
export function deleteAdminUser(userId) {
  return http.delete(`/admin/users/${userId}`)
}
