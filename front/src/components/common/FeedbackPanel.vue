<script setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import AppIcon from './AppIcon.vue'
import { useFeedbackStore } from '@/stores/feedback'
import { useAuthStore } from '@/stores/auth'
import { formatTime } from '@/utils/date'
import { toast } from '@/utils/toast'

defineEmits(['collapse'])

const feedback = useFeedbackStore()
const auth = useAuthStore()
const { items, loading } = storeToRefs(feedback)

const isAdmin = auth.user?.isAdmin ?? false

const draft = ref('')
const sending = ref(false)
const scrollEl = ref(null)
const replyingTo = ref(null)

async function scrollToBottom() {
  await nextTick()
  const el = scrollEl.value
  if (el) el.scrollTop = el.scrollHeight
}

watch(items, scrollToBottom, { deep: true })

onMounted(async () => {
  await feedback.fetchAll()
  scrollToBottom()
})

function dateLabel(iso) {
  const d = new Date(iso)
  return `${d.getMonth() + 1}월 ${d.getDate()}일`
}

function showDivider(index) {
  if (index === 0) return true
  const prev = new Date(items.value[index - 1].createdAt)
  const curr = new Date(items.value[index].createdAt)
  return prev.toDateString() !== curr.toDateString()
}

function replyTarget(f) {
  if (!f.replyToId) return null
  return items.value.find((i) => i.feedbackId === f.replyToId) || null
}

function truncate(text, n = 40) {
  if (!text) return ''
  return text.length > n ? `${text.slice(0, n)}…` : text
}

function startReply(f) {
  replyingTo.value = f
}

function cancelReply() {
  replyingTo.value = null
}

async function submit() {
  const text = draft.value.trim()
  if (!text || sending.value) return
  sending.value = true
  try {
    draft.value = ''
    const replyToId = replyingTo.value?.feedbackId ?? null
    replyingTo.value = null
    await feedback.send(text, replyToId)
  } catch (e) {
    toast().error(e.normalizedMessage || '전송에 실패했습니다.')
  } finally {
    sending.value = false
  }
}

function onKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    submit()
  }
}

async function toggleResolved(f) {
  try {
    await feedback.resolve(f.feedbackId, !f.resolved)
  } catch (e) {
    toast().error(e.normalizedMessage || '처리에 실패했습니다.')
  }
}

async function toggleLike(f) {
  try {
    await feedback.toggleLike(f.feedbackId)
  } catch (e) {
    toast().error(e.normalizedMessage || '처리에 실패했습니다.')
  }
}
</script>

<template>
  <aside class="fb">
    <header class="fb__head">
      <span class="fb__title">
        <span class="fb__title-icon"><AppIcon name="chat" :size="16" /></span>
        피드백
      </span>
      <button class="fb__collapse" type="button" aria-label="패널 접기" @click="$emit('collapse')">
        <AppIcon name="chevronRight" :size="18" />
      </button>
    </header>

    <div ref="scrollEl" class="fb__body">
      <p v-if="loading && !items.length" class="fb__empty">불러오는 중…</p>
      <p v-else-if="!items.length" class="fb__empty">
        아직 남겨진 피드백이 없습니다.<br />
        부담 없이 의견을 남겨주세요.
      </p>

      <template v-for="(f, i) in items" :key="f.feedbackId">
        <div v-if="showDivider(i)" class="fb__divider">
          <span>— {{ dateLabel(f.createdAt) }} —</span>
        </div>

        <div class="fb__msg" :class="f.authorIsAdmin ? 'fb__msg--admin' : 'fb__msg--user'">
          <span v-if="f.authorIsAdmin" class="fb__author">관리자</span>

          <div class="fb__msg-row">
            <div class="fb__bubble-wrap">
              <div v-if="replyTarget(f)" class="fb__quote">
                {{ truncate(replyTarget(f).content) }}
              </div>
              <div class="fb__bubble">{{ f.content }}</div>
            </div>
            <div class="fb__side-actions">
              <span v-if="!f.resolved" class="fb__pending">반영 전</span>
              <button
                type="button"
                class="fb__like"
                :class="{ 'is-liked': f.likedByMe }"
                @click="toggleLike(f)"
              >
                <AppIcon name="thumbsUp" :size="14" />
                <span v-if="f.likeCount">{{ f.likeCount }}</span>
              </button>
            </div>
          </div>

          <div class="fb__meta">
            <span>{{ formatTime(f.createdAt) }}</span>
            <span v-if="f.resolved" class="fb__resolved">✓ 반영완료</span>
            <button type="button" class="fb__reply-btn" @click="startReply(f)">답장</button>
            <button
              v-if="isAdmin"
              type="button"
              class="fb__resolve-btn"
              @click="toggleResolved(f)"
            >
              {{ f.resolved ? '반영완료 취소' : '반영완료로 표시' }}
            </button>
          </div>
        </div>
      </template>
    </div>

    <footer class="fb__compose">
      <div v-if="replyingTo" class="fb__reply-bar">
        <span class="fb__reply-bar__text">
          답장: {{ truncate(replyingTo.content, 30) }}
        </span>
        <button type="button" aria-label="답장 취소" @click="cancelReply">
          <AppIcon name="plus" :size="14" style="transform: rotate(45deg)" />
        </button>
      </div>
      <div class="fb__compose-box">
        <textarea
          v-model="draft"
          class="fb__input"
          rows="1"
          placeholder="의견을 남겨주세요…  (Enter 전송)"
          @keydown="onKeydown"
        />
        <button
          class="fb__send"
          type="button"
          aria-label="보내기"
          :disabled="sending || !draft.trim()"
          @click="submit"
        >
          <AppIcon name="arrowUp" :size="16" />
        </button>
      </div>
    </footer>
  </aside>
</template>

<style scoped>
.fb {
  width: var(--ai-panel-w);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-left: 1px solid var(--c-border);
  background: var(--c-surface);
}
.fb__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-h);
  padding: 0 var(--sp-4);
  border-bottom: 1px solid var(--c-border);
}
.fb__title {
  display: inline-flex;
  align-items: center;
  gap: var(--sp-2);
  font-weight: 600;
}
.fb__title-icon {
  display: grid;
  place-items: center;
  width: 24px;
  height: 24px;
  border-radius: var(--r-sm);
  background: var(--c-accent-soft);
  color: var(--c-accent);
}
.fb__collapse {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: var(--r-sm);
  color: var(--c-text-2);
}
.fb__collapse:hover {
  background: var(--c-surface-alt);
}
.fb__body {
  flex: 1;
  overflow-y: auto;
  padding: var(--sp-5) var(--sp-4);
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.fb__empty {
  padding-top: var(--sp-6);
  text-align: center;
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.fb__divider {
  display: flex;
  justify-content: center;
  margin: var(--sp-2) 0;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.fb__msg {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}
.fb__msg--user {
  align-items: flex-end;
}
.fb__author {
  font-size: var(--fs-xs);
  font-weight: 600;
  color: var(--c-accent);
}
.fb__msg-row {
  display: flex;
  align-items: flex-end;
  gap: var(--sp-1);
  max-width: 100%;
}
.fb__msg--admin .fb__msg-row {
  flex-direction: row;
}
.fb__msg--user .fb__msg-row {
  flex-direction: row-reverse;
}
.fb__bubble-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}
.fb__quote {
  padding: 6px 10px;
  border-left: 2px solid var(--c-border-strong);
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  color: var(--c-text-muted);
  font-size: var(--fs-xs);
  white-space: pre-wrap;
  word-break: break-word;
}
.fb__bubble {
  max-width: 220px;
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
  color: var(--c-text);
  font-size: var(--fs-sm);
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  border-bottom-right-radius: var(--r-sm);
}
.fb__msg--admin .fb__bubble {
  /* 라이트/다크 테마와 무관하게 관리자 메시지는 항상 검정 말풍선으로 고정 */
  background: #1a1c1f;
  color: #fcfcfd;
  border-bottom-left-radius: var(--r-sm);
  border-bottom-right-radius: var(--r-md);
}
.fb__side-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}
.fb__pending {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  white-space: nowrap;
}
.fb__like {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  flex-shrink: 0;
  padding: 3px 6px;
  border-radius: var(--r-full);
  font-size: var(--fs-xs);
  font-weight: 600;
  color: var(--c-text-muted);
  transition:
    color 0.15s ease,
    background-color 0.15s ease;
}
.fb__like:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.fb__like.is-liked {
  color: var(--c-primary);
  background: var(--c-accent-soft);
}
.fb__meta {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.fb__resolved {
  color: var(--c-success);
  font-weight: 600;
}
.fb__reply-btn {
  color: var(--c-text-muted);
}
.fb__reply-btn:hover {
  color: var(--c-text);
  text-decoration: underline;
}
.fb__resolve-btn {
  padding: 2px 8px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  color: var(--c-text-2);
}
.fb__resolve-btn:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}

.fb__compose {
  border-top: 1px solid var(--c-border);
  padding: var(--sp-3) var(--sp-4) var(--sp-4);
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.fb__reply-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-2);
  padding: 6px var(--sp-3);
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.fb__reply-bar__text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.fb__compose-box {
  display: flex;
  align-items: flex-end;
  gap: var(--sp-2);
  padding: var(--sp-2);
  border-radius: var(--r-md);
  border: 1px solid var(--c-border);
  background: var(--c-surface-alt);
}
.fb__compose-box:focus-within {
  border-color: var(--c-primary);
}
.fb__input {
  flex: 1;
  border: none;
  background: transparent;
  resize: none;
  max-height: 120px;
  font-family: inherit;
  font-size: var(--fs-sm);
  line-height: 1.5;
}
.fb__input:focus {
  outline: none;
}
.fb__send {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  border-radius: var(--r-full);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.fb__send:hover {
  background: var(--c-primary-hover);
}
.fb__send:disabled {
  opacity: 0.4;
}

@media (max-width: 640px) {
  .fb {
    position: fixed;
    inset: 0;
    z-index: 50;
    width: 100%;
    border-left: none;
  }
}
</style>
