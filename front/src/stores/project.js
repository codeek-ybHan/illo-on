import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/api/project'

export const useProjectStore = defineStore('project', () => {
  const projects = ref([]) // 내가 참여 중인 프로젝트 목록
  const current = ref(null) // 현재 열람 중인 프로젝트 상세
  const members = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchProjects() {
    loading.value = true
    error.value = ''
    try {
      projects.value = await api.fetchProjects()
    } catch (e) {
      error.value = e.normalizedMessage || '프로젝트를 불러오지 못했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function createProject(payload) {
    const created = await api.createProject(payload)
    projects.value = [created, ...projects.value]
    return created
  }

  async function fetchProject(projectId) {
    loading.value = true
    error.value = ''
    try {
      current.value = await api.fetchProject(projectId)
      return current.value
    } catch (e) {
      error.value = e.normalizedMessage || '프로젝트를 불러오지 못했습니다.'
      current.value = null
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateProject(projectId, payload) {
    const updated = await api.updateProject(projectId, payload)
    current.value = updated
    projects.value = projects.value.map((p) => (p.projectId === updated.projectId ? updated : p))
    return updated
  }

  async function deleteProject(projectId) {
    await api.deleteProject(projectId)
    projects.value = projects.value.filter((p) => p.projectId !== Number(projectId))
    if (current.value?.projectId === Number(projectId)) current.value = null
  }

  async function fetchMembers(projectId) {
    members.value = await api.fetchMembers(projectId)
    return members.value
  }

  return {
    projects,
    current,
    members,
    loading,
    error,
    fetchProjects,
    createProject,
    fetchProject,
    updateProject,
    deleteProject,
    fetchMembers,
  }
})
