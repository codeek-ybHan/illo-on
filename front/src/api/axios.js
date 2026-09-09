import axios from 'axios'
import router from '@/router'
import { getToken, clearToken } from '@/utils/token'

/**
 * 공용 axios 인스턴스.
 * - baseURL: .env 의 VITE_API_BASE_URL (예: http://localhost:8080/api)
 * - 요청 시 JWT Authorization 헤더 자동 첨부
 * - 401 응답 시 토큰 제거 후 로그인 화면으로 이동 (원위치 복귀용 redirect 쿼리 포함)
 */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status

    if (status === 401) {
      clearToken()
      const current = router.currentRoute.value
      if (current.name !== 'login') {
        router.push({ name: 'login', query: { redirect: current.fullPath } })
      }
    }

    // 공통 에러 메시지 정규화 (백엔드 { message } 규약 가정)
    const message =
      error.response?.data?.message || error.message || '요청 처리 중 오류가 발생했습니다.'
    return Promise.reject(Object.assign(error, { normalizedMessage: message }))
  },
)

export default http
