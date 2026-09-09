import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/api/meeting'

export const useMeetingStore = defineStore('meeting', () => {
  const meetings = ref([]) // 현재 프로젝트의 회의 목록
  const current = ref(null) // 회의 상세
  const loading = ref(false)
  const error = ref('')

  // 회의 생성 폼 → 상세 화면으로 넘기는 녹음본 (STT/AI 분석은 Phase 6)
  let pendingAudio = null
  function setPendingAudio(file) {
    pendingAudio = file
  }
  function takePendingAudio() {
    const f = pendingAudio
    pendingAudio = null
    return f
  }

  async function fetchMeetings(projectId) {
    loading.value = true
    error.value = ''
    try {
      meetings.value = await api.fetchMeetings(projectId)
    } catch (e) {
      error.value = e.normalizedMessage || '회의를 불러오지 못했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function createMeeting(projectId, payload) {
    const created = await api.createMeeting(projectId, payload)
    meetings.value = [created, ...meetings.value]
    return created
  }

  async function fetchMeeting(meetingId) {
    loading.value = true
    error.value = ''
    try {
      current.value = await api.fetchMeeting(meetingId)
      return current.value
    } catch (e) {
      error.value = e.normalizedMessage || '회의를 불러오지 못했습니다.'
      current.value = null
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateMeeting(meetingId, payload) {
    const updated = await api.updateMeeting(meetingId, payload)
    if (current.value?.meetingId === updated.meetingId) current.value = updated
    meetings.value = meetings.value.map((m) =>
      m.meetingId === updated.meetingId ? { ...m, ...updated } : m,
    )
    return updated
  }

  async function deleteMeeting(meetingId) {
    await api.deleteMeeting(meetingId)
    const id = Number(meetingId)
    meetings.value = meetings.value.filter((m) => m.meetingId !== id)
    if (current.value?.meetingId === id) current.value = null
  }

  return {
    meetings,
    current,
    loading,
    error,
    fetchMeetings,
    createMeeting,
    fetchMeeting,
    updateMeeting,
    deleteMeeting,
    setPendingAudio,
    takePendingAudio,
  }
})
