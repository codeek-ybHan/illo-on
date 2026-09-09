import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 프로젝트 상태 (스켈레톤).
 * Phase 2에서 api/project.js 연동 액션(fetchProjects, createProject, fetchProject, ...)을 채운다.
 */
export const useProjectStore = defineStore('project', () => {
  const projects = ref([]) // 내가 참여 중인 프로젝트 목록
  const current = ref(null) // 현재 열람 중인 프로젝트 상세
  const loading = ref(false)
  const error = ref('')

  return { projects, current, loading, error }
})
