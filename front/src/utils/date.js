/**
 * 날짜 유틸. 백엔드는 ISO-8601 문자열을 주고받는다고 가정한다.
 */

/** Date | string → 'YYYY-MM-DD' (input[type=date] 값, 서버 전송용) */
export function toISODate(value) {
  if (!value) return ''
  const d = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  return d.toISOString().slice(0, 10)
}

/** ISO 문자열 → '2026. 9. 8.' 형태 (화면 표시용) */
export function formatDate(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
}

/** ISO 문자열 → '2026. 9. 8. 오후 2:00' (일시 표시용) */
export function formatDateTime(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  })
}

/** 마감까지 남은 일수 (양수: 남음, 0: 오늘, 음수: 지남) */
export function daysUntil(value) {
  if (!value) return null
  const target = new Date(value)
  if (Number.isNaN(target.getTime())) return null
  const startOfDay = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate())
  const diffMs = startOfDay(target) - startOfDay(new Date())
  return Math.round(diffMs / 86_400_000)
}

/** 오늘인지 여부 */
export function isToday(value) {
  return daysUntil(value) === 0
}
