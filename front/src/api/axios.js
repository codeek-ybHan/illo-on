import axios from 'axios'
import router from '@/router'
import { getToken, clearToken, clearStoredUser } from '@/utils/token'
import { toast } from '@/utils/toast'

/**
 * 공용 axios 인스턴스.
 * - baseURL: .env 의 VITE_API_BASE_URL (예: http://localhost:8080/api)
 * - 요청 시 JWT Authorization 헤더 자동 첨부
 * - 401: 토큰 제거 + 로그인 화면(원위치 복귀용 redirect)
 * - 5xx / 네트워크 오류: 전역 토스트
 * - 그 외(4xx): normalizedMessage 만 붙여 호출부에서 처리
 */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const serverMsg = error.response?.data?.message

    let message
    if (status === 401) {
      clearToken()
      clearStoredUser()
      const current = router.currentRoute.value
      if (current.name !== 'login') {
        router.push({ name: 'login', query: { redirect: current.fullPath } })
        toast().info('세션이 만료되었습니다. 다시 로그인해 주세요.')
      }
      message = serverMsg || '인증이 필요합니다.'
    } else if (!error.response) {
      message =
        error.code === 'ECONNABORTED' ? '요청 시간이 초과되었습니다.' : '서버에 연결할 수 없습니다.'
      toast().error(message)
    } else if (status >= 500) {
      message = serverMsg || '서버 오류가 발생했습니다.'
      toast().error(message)
    } else {
      message = serverMsg || error.message || '요청 처리 중 오류가 발생했습니다.'
    }

    return Promise.reject(Object.assign(error, { normalizedMessage: message }))
  },
)

export default http
