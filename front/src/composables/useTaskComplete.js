import { ref } from 'vue'
import { toISODate } from '@/utils/date'

/**
 * Task 상태 변경 요청을 감싸서, DONE으로 바뀌는 경우에만 완료일자 입력 모달을 띄운다.
 * @param {(task: object, status: string, extra?: object) => Promise|void} apply 실제 상태 변경 수행 함수
 */
export function useTaskComplete(apply) {
  const pendingTask = ref(null)

  function request(task, status) {
    if (status === 'DONE') {
      pendingTask.value = task
      return
    }
    apply(task, status)
  }

  function confirm(date) {
    const task = pendingTask.value
    pendingTask.value = null
    if (task) apply(task, 'DONE', { completedAt: date || toISODate(new Date()) })
  }

  function cancel() {
    pendingTask.value = null
  }

  return { pendingTask, request, confirm, cancel }
}
