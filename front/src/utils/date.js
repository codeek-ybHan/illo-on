/**
 * 날짜 유틸. 백엔드는 ISO-8601 문자열을 주고받는다고 가정한다.
 */

/** Date | string → 'YYYY-MM-DD' (로컬 기준, 서버 전송용) */
export function toISODate(value) {
  if (!value) return ''
  const d = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
}

/** 'YYYY-MM-DD' → 로컬 Date (자정). 잘못된 값이면 null */
export function parseISODate(str) {
  if (!str || typeof str !== 'string') return null
  const m = str.match(/^(\d{4})-(\d{2})-(\d{2})/)
  if (!m) return null
  const d = new Date(Number(m[1]), Number(m[2]) - 1, Number(m[3]))
  return Number.isNaN(d.getTime()) ? null : d
}

/** ISO → 'M월 D일 (요일)' */
export function formatDateShort(value) {
  const d = value instanceof Date ? value : parseISODate(value) || new Date(value)
  if (!d || Number.isNaN(d.getTime())) return ''
  const wd = ['일', '월', '화', '수', '목', '금', '토'][d.getDay()]
  return `${d.getMonth() + 1}월 ${d.getDate()}일 (${wd})`
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

/** 마감 표시: 시각이 있으면 'M월 D일 (요일) HH:mm', 자정이면 날짜만 */
export function formatDue(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  const base = formatDateShort(d)
  if (d.getHours() === 0 && d.getMinutes() === 0) return base
  const p = (n) => String(n).padStart(2, '0')
  return `${base} ${p(d.getHours())}:${p(d.getMinutes())}`
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

/** 현재 시각 기준 인사 문구 (이모지 포함) */
export function greetingPhrase(date = new Date()) {
  const h = date.getHours()
  if (h >= 5 && h < 11) return '좋은 아침이에요 🌅'
  if (h >= 11 && h < 14) return '점심 맛있게 드세요 🍚'
  if (h >= 14 && h < 18) return '활기찬 오후 보내세요 ☀️'
  if (h >= 18 && h < 22) return '오늘 하루도 고생 많으셨어요 🌇'
  return '늦은 시간까지 고생 많으세요 🌙' // 22시~05시
}
