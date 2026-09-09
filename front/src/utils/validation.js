/**
 * 폼 입력 검증. 각 함수는 유효하면 '' (빈 문자열), 아니면 에러 메시지를 반환한다.
 */

export function required(value, label = '값') {
  const v = typeof value === 'string' ? value.trim() : value
  return v ? '' : `${label}을(를) 입력해 주세요.`
}

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function email(value) {
  if (!value) return '이메일을 입력해 주세요.'
  return EMAIL_RE.test(value) ? '' : '올바른 이메일 형식이 아닙니다.'
}

/** 비밀번호: 8자 이상 */
export function password(value) {
  if (!value) return '비밀번호를 입력해 주세요.'
  return value.length >= 8 ? '' : '비밀번호는 8자 이상이어야 합니다.'
}

export function minLength(value, n, label = '값') {
  if (!value || value.length < n) return `${label}은(는) ${n}자 이상이어야 합니다.`
  return ''
}

export function maxLength(value, n, label = '값') {
  if (value && value.length > n) return `${label}은(는) ${n}자 이하여야 합니다.`
  return ''
}

/** 종료일이 시작일보다 빠르지 않은지 */
export function dateRange(start, end) {
  if (!start || !end) return ''
  return new Date(start) <= new Date(end) ? '' : '종료일은 시작일보다 빠를 수 없습니다.'
}

/**
 * 여러 검증 결과 중 첫 에러를 반환한다.
 * validateAll([required(name, '이름'), email(mail)])
 */
export function firstError(results) {
  return results.find((r) => r) || ''
}
