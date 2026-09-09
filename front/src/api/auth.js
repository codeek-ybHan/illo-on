import http from './axios'

/** 회원가입 — 성공 시 { userId, name, email } (토큰 없음, 이어서 로그인) */
export function signup(payload) {
  return http.post('/auth/signup', payload).then((res) => res.data)
}

/** 로그인 — 성공 시 { token, user: { userId, name, email } } */
export function login(payload) {
  return http.post('/auth/login', payload).then((res) => res.data)
}
