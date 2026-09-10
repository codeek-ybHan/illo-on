import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/api/meeting'
import * as aiApi from '@/api/ai'

export const useMeetingStore = defineStore('meeting', () => {
  const meetings = ref([]) // 현재 프로젝트의 회의 목록
  const current = ref(null) // 회의 상세
  const briefing = ref(null) // 현재 회의의 AI 분석 결과
  const loading = ref(false)
  const analyzing = ref(false)
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
    briefing.value = null
    try {
      current.value = await api.fetchMeeting(meetingId)
      if (current.value?.hasSummary) {
        briefing.value = await aiApi.fetchSummary(meetingId).catch(() => null)
      }
      return current.value
    } catch (e) {
      error.value = e.normalizedMessage || '회의를 불러오지 못했습니다.'
      current.value = null
      throw e
    } finally {
      loading.value = false
    }
  }

  /** AI 분석 실행. audioFile 있으면 STT 경로. 실패는 호출부(토스트)에 위임 — 페이지는 유지 */
  async function analyzeMeeting(meetingId, audioFile) {
    analyzing.value = true
    try {
      briefing.value = await aiApi.analyzeMeeting(meetingId, audioFile)
      // STT로 내용이 바뀌었을 수 있고 hasSummary가 true가 됨
      current.value = await api.fetchMeeting(meetingId)
      return briefing.value
    } finally {
      analyzing.value = false
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
    briefing,
    loading,
    analyzing,
    error,
    fetchMeetings,
    createMeeting,
    fetchMeeting,
    updateMeeting,
    deleteMeeting,
    analyzeMeeting,
    setPendingAudio,
    takePendingAudio,
  }
})
