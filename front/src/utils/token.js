/**
 * 인증 로컬 저장소 접근 (토큰 + 사용자).
 * axios 인터셉터·라우터 가드·auth 스토어가 공유한다 (순환 의존 방지용 분리).
 */
const TOKEN_KEY = 'illo-on.token'
const USER_KEY = 'illo-on.user'

export function getToken() {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

export function setToken(token) {
  try {
    localStorage.setItem(TOKEN_KEY, token)
  } catch {
    /* private mode 등 저장 불가 시 무시 */
  }
}

export function clearToken() {
  try {
    localStorage.removeItem(TOKEN_KEY)
  } catch {
    /* noop */
  }
}

export function getStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function setStoredUser(user) {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  } catch {
    /* noop */
  }
}

export function clearStoredUser() {
  try {
    localStorage.removeItem(USER_KEY)
  } catch {
    /* noop */
  }
}
