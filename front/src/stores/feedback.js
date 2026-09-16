import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/api/feedback'

export const useFeedbackStore = defineStore('feedback', () => {
  const items = ref([])
  const loading = ref(false)
  const error = ref('')

  async function fetchAll() {
    loading.value = true
    error.value = ''
    try {
      items.value = await api.fetchFeedbacks()
    } catch (e) {
      error.value = e.normalizedMessage || '피드백을 불러오지 못했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function send(content, replyToId) {
    const created = await api.createFeedback({ content, replyToId: replyToId ?? null })
    items.value = [...items.value, created]
    return created
  }

  async function resolve(feedbackId, resolved) {
    const updated = await api.resolveFeedback(feedbackId, resolved)
    items.value = items.value.map((f) => (f.feedbackId === updated.feedbackId ? updated : f))
    return updated
  }

  async function toggleLike(feedbackId) {
    const updated = await api.toggleFeedbackLike(feedbackId)
    items.value = items.value.map((f) => (f.feedbackId === updated.feedbackId ? updated : f))
    return updated
  }

  async function update(feedbackId, content) {
    const updated = await api.updateFeedback(feedbackId, content)
    items.value = items.value.map((f) => (f.feedbackId === updated.feedbackId ? updated : f))
    return updated
  }

  async function remove(feedbackId) {
    await api.deleteFeedback(feedbackId)
    items.value = items.value.filter((f) => f.feedbackId !== feedbackId)
  }

  return { items, loading, error, fetchAll, send, resolve, toggleLike, update, remove }
})
