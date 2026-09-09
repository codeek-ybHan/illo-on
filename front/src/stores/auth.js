import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '@/api/auth'
import {
  getToken,
  setToken,
  clearToken,
  getStoredUser,
  setStoredUser,
  clearStoredUser,
} from '@/utils/token'

/**
 * 인증 상태.
 * token / user 는 localStorage 에 함께 보관해 새로고침 후에도 유지된다.
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref(getToken())
  const user = ref(getStoredUser()) // { userId, name, email }

  const isAuthenticated = computed(() => Boolean(token.value))

  function setAuth({ token: newToken, user: newUser }) {
    token.value = newToken
    setToken(newToken)
    if (newUser) {
      user.value = newUser
      setStoredUser(newUser)
    }
  }

  function logout() {
    token.value = null
    user.value = null
    clearToken()
    clearStoredUser()
  }

  /** 회원가입 (토큰 발급 없음 — 이어서 login 호출) */
  async function signup(payload) {
    return authApi.signup(payload)
  }

  /** 로그인 — 성공 시 상태/스토리지 갱신 */
  async function login(payload) {
    const data = await authApi.login(payload)
    setAuth(data)
    return data
  }

  return { token, user, isAuthenticated, setAuth, logout, signup, login }
})
