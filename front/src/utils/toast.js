import { reactive } from 'vue'

const state = reactive({ items: [] })
let seq = 0

function push(message, type, ms) {
  const id = ++seq
  state.items.push({ id, message, type })
  if (ms > 0) setTimeout(() => dismiss(id), ms)
  return id
}

function dismiss(id) {
  const i = state.items.findIndex((t) => t.id === id)
  if (i >= 0) state.items.splice(i, 1)
}

/**
 * 전역 토스트. 어디서든 import 해서 호출.
 *   toast().error('저장 실패')
 */
export function toast() {
  return {
    items: state.items,
    success: (m, ms = 3000) => push(m, 'success', ms),
    error: (m, ms = 6000) => push(m, 'error', ms),
    info: (m, ms = 4000) => push(m, 'info', ms),
    dismiss,
  }
}
