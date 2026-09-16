import http from './axios'

/** 계정 정보 수정 — { name, email } */
export function updateProfile(payload) {
  return http.patch('/me', payload).then((res) => res.data)
}

/** 비밀번호 변경 — { currentPassword, newPassword } */
export function changePassword(payload) {
  return http.patch('/me/password', payload)
}
