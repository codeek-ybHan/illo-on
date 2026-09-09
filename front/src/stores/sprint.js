import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/api/sprint'

export const useSprintStore = defineStore('sprint', () => {
  const sprints = ref([]) // 현재 프로젝트의 Sprint 목록
  const loading = ref(false)
  const error = ref('')

  // 늦게 도착한 목록 응답이 최신 상태를 덮어쓰지 않도록 요청 토큰으로 가드
  let reqToken = 0

  async function fetchSprints(projectId) {
    const token = ++reqToken
    loading.value = true
    error.value = ''
    try {
      const data = await api.fetchSprints(projectId)
      if (token === reqToken) sprints.value = data
    } catch (e) {
      if (token === reqToken) error.value = e.normalizedMessage || 'Sprint를 불러오지 못했습니다.'
    } finally {
      if (token === reqToken) loading.value = false
    }
  }

  async function createSprint(projectId, payload) {
    const created = await api.createSprint(projectId, payload)
    reqToken++ // 진행 중이던 목록 요청 무효화
    loading.value = false
    error.value = ''
    sprints.value = [...sprints.value, created]
    return created
  }

  async function updateSprint(sprintId, payload) {
    const updated = await api.updateSprint(sprintId, payload)
    sprints.value = sprints.value.map((s) => (s.sprintId === updated.sprintId ? updated : s))
    return updated
  }

  async function deleteSprint(sprintId) {
    await api.deleteSprint(sprintId)
    reqToken++
    sprints.value = sprints.value.filter((s) => s.sprintId !== Number(sprintId))
  }

  /** Task 배정/상태 변경 후 진행률 갱신 */
  async function refreshSprint(sprintId, projectId) {
    try {
      const fresh = await api.fetchSprint(sprintId)
      sprints.value = sprints.value.map((s) => (s.sprintId === fresh.sprintId ? fresh : s))
    } catch {
      if (projectId) fetchSprints(projectId)
    }
  }

  return {
    sprints,
    loading,
    error,
    fetchSprints,
    createSprint,
    updateSprint,
    deleteSprint,
    refreshSprint,
  }
})
