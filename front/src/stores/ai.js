import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chat as chatApi } from '@/api/ai'

/**
 * 우측 AI 어시스턴트 패널의 대화 상태.
 * 패널을 접었다 펴도 대화가 유지되도록 스토어에 둔다.
 */
export const useAiStore = defineStore('ai', () => {
  /** { role: 'user' | 'assistant', content, error? } */
  const messages = ref([])
  const sending = ref(false)
  /** 마지막 응답의 provider: 'openai' | 'mock' | null */
  const provider = ref(null)

  async function send(text) {
    const message = (text || '').trim()
    if (!message || sending.value) return

    messages.value.push({ role: 'user', content: message })
    sending.value = true

    // 최근 대화 몇 턴만 컨텍스트로 (현재 질문 제외)
    const history = messages.value
      .slice(0, -1)
      .filter((m) => !m.error)
      .slice(-8)
      .map((m) => ({ role: m.role, content: m.content }))

    try {
      const res = await chatApi(message, history)
      provider.value = res.provider
      messages.value.push({ role: 'assistant', content: res.reply })
    } catch (e) {
      messages.value.push({
        role: 'assistant',
        content: e.normalizedMessage || 'AI 응답을 가져오지 못했습니다.',
        error: true,
      })
    } finally {
      sending.value = false
    }
  }

  function reset() {
    messages.value = []
    provider.value = null
  }

  return { messages, sending, provider, send, reset }
})
