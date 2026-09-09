/**
 * JWT 토큰 로컬 저장소 접근.
 * axios 인터셉터와 auth 스토어가 공유한다 (순환 의존 방지용 분리).
 */
const TOKEN_KEY = 'illo-on.token'

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
