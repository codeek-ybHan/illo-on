<script setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import AppIcon from './AppIcon.vue'
import { useFeedbackStore } from '@/stores/feedback'
import { useAuthStore } from '@/stores/auth'
import { formatDateTime } from '@/utils/date'
import { toast } from '@/utils/toast'

/** 반영완료 처리 권한 노출용 — 실제 인가는 서버가 함(FeedbackService.ADMIN_EMAIL과 동일 값). */
const ADMIN_EMAIL = 'mylovehyb12@gmail.com'

defineEmits(['collapse'])

const feedback = useFeedbackStore()
const auth = useAuthStore()
const { items, loading } = storeToRefs(feedback)

const isAdmin = auth.user?.email === ADMIN_EMAIL

const draft = ref('')
const sending = ref(false)
const scrollEl = ref(null)

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

async function submit() {
  const text = draft.value.trim()
  if (!text || sending.value) return
  sending.value = true
  try {
    draft.value = ''
    await feedback.send(text)
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

      <div
        v-for="f in items"
        :key="f.feedbackId"
        class="fb__msg"
        :class="{ 'fb__msg--mine': f.userId === auth.user?.userId }"
      >
        <span v-if="f.userId !== auth.user?.userId" class="fb__author">{{ f.authorName }}</span>
        <div class="fb__bubble">{{ f.content }}</div>
        <div class="fb__meta">
          <span>{{ formatDateTime(f.createdAt) }}</span>
          <span v-if="f.resolved" class="fb__resolved">✓ 반영완료</span>
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
    </div>

    <footer class="fb__compose">
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
  gap: var(--sp-4);
}
.fb__empty {
  padding-top: var(--sp-6);
  text-align: center;
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.fb__msg {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}
.fb__msg--mine {
  align-items: flex-end;
}
.fb__author {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.fb__bubble {
  max-width: 85%;
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
  color: var(--c-text);
  font-size: var(--fs-sm);
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  border-bottom-left-radius: var(--r-sm);
}
.fb__msg--mine .fb__bubble {
  background: var(--c-primary);
  color: var(--c-primary-contrast);
  border-bottom-left-radius: var(--r-md);
  border-bottom-right-radius: var(--r-sm);
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
