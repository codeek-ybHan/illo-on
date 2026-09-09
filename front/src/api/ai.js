import http from './axios'

/**
 * 회의 분석. audioFile 이 있으면 multipart(STT), 없으면 회의 내용을 분석.
 * → { meetingId, summary, decisions[], actionPoints[], source, analyzedAt }
 */
export function analyzeMeeting(meetingId, audioFile) {
  if (audioFile) {
    const fd = new FormData()
    fd.append('audio', audioFile)
    return http
      .post(`/meetings/${meetingId}/analyze`, fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 120000, // STT + LLM
      })
      .then((res) => res.data)
  }
  return http
    .post(`/meetings/${meetingId}/analyze`, null, { timeout: 60000 })
    .then((res) => res.data)
}

/** 저장된 브리핑 조회 (없으면 404) */
export function fetchSummary(meetingId) {
  return http.get(`/meetings/${meetingId}/summary`).then((res) => res.data)
}
