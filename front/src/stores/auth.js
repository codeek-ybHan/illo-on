import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, clearToken } from '@/utils/token'

/**
 * 인증 상태.
 * Phase 1에서 login / signup 액션(api/auth.js 호출)을 여기에 추가한다.
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref(getToken())
  const user = ref(null) // { userId, name, email }

  const isAuthenticated = computed(() => Boolean(token.value))

  /** 로그인 성공 시 호출 */
  function setAuth({ token: newToken, user: newUser }) {
    token.value = newToken
    setToken(newToken)
    if (newUser) user.value = newUser
  }

  function setUser(newUser) {
    user.value = newUser
  }

  function logout() {
    token.value = null
    user.value = null
    clearToken()
  }

  return { token, user, isAuthenticated, setAuth, setUser, logout }
})
