<script setup>
import { ref, nextTick, watch, computed } from 'vue'
import { storeToRefs } from 'pinia'
import AppIcon from './AppIcon.vue'
import { useAiStore } from '@/stores/ai'

defineEmits(['collapse'])

const ai = useAiStore()
const { messages, sending, provider } = storeToRefs(ai)

const draft = ref('')
const scrollEl = ref(null)

const hasChat = computed(() => messages.value.length > 0)
const mockNotice = computed(() => provider.value === 'mock')

const suggestions = [
  { label: '오늘 할 일', prompt: '오늘 해야 할 일과 일정을 알려줘' },
  { label: '이번 주 마감', prompt: '이번 주에 마감인 내 업무가 있어?' },
  { label: '프로젝트 현황', prompt: '내 프로젝트들 진행 상황을 요약해줘' },
]
const quickActions = [
  {
    icon: 'meeting',
    label: '회의록에서 할 일 추출',
    prompt: '최근 회의에서 나온 할 일(Action Point)을 정리해줘',
  },
  {
    icon: 'sprint',
    label: '이번 Sprint 진행률 요약',
    prompt: '진행 중인 Sprint의 진행 상황을 요약해줘',
  },
  {
    icon: 'calendar',
    label: '마감 임박 Task 알려줘',
    prompt: '마감이 임박했거나 지난 내 업무를 알려줘',
  },
]

async function scrollToBottom() {
  await nextTick()
  const el = scrollEl.value
  if (el) el.scrollTop = el.scrollHeight
}

watch([messages, sending], scrollToBottom, { deep: true })

function submit() {
  const text = draft.value.trim()
  if (!text || sending.value) return
  draft.value = ''
  ai.send(text)
}

function onKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    submit()
  }
}

function ask(prompt) {
  if (sending.value) return
  ai.send(prompt)
}
</script>

<template>
  <aside class="ai">
    <header class="ai__head">
      <span class="ai__title">
        <span class="ai__title-icon"><AppIcon name="sparkle" :size="16" /></span>
        일로ON AI
      </span>
      <div class="ai__head-actions">
        <button
          v-if="hasChat"
          class="ai__ghost-btn"
          type="button"
          @click="ai.reset()"
        >
          새 대화
        </button>
        <span v-else class="ai__badge">Beta</span>
        <button
          class="ai__collapse"
          type="button"
          aria-label="패널 접기"
          @click="$emit('collapse')"
        >
          <AppIcon name="chevronRight" :size="18" />
        </button>
      </div>
    </header>

    <div ref="scrollEl" class="ai__body">
      <!-- 대화 전: 빈 상태 -->
      <template v-if="!hasChat">
        <div class="ai__empty">
          <div class="ai__empty-icon"><AppIcon name="sparkle" :size="26" /></div>
          <h3 class="ai__empty-title">무엇을 도와드릴까요?</h3>
          <p class="ai__empty-desc">
            회의 내용, Action Point, 프로젝트 일정에 대해 무엇이든 물어보세요.
          </p>
          <div class="ai__chips">
            <button
              v-for="s in suggestions"
              :key="s.label"
              class="ai__chip"
              type="button"
              @click="ask(s.prompt)"
            >
              {{ s.label }}
            </button>
          </div>
        </div>

        <ul class="ai__quick">
          <li v-for="q in quickActions" :key="q.label">
            <button class="ai__quick-item" type="button" @click="ask(q.prompt)">
              <span class="ai__quick-icon"><AppIcon :name="q.icon" :size="16" /></span>
              {{ q.label }}
              <span class="ai__quick-chevron"><AppIcon name="chevronRight" :size="14" /></span>
            </button>
          </li>
        </ul>
      </template>

      <!-- 대화 -->
      <template v-else>
        <div
          v-for="(m, i) in messages"
          :key="i"
          class="ai__msg"
          :class="[`ai__msg--${m.role}`, { 'is-error': m.error }]"
        >
          <div class="ai__bubble">{{ m.content }}</div>
        </div>
        <div v-if="sending" class="ai__msg ai__msg--assistant">
          <div class="ai__bubble ai__typing">
            <span></span><span></span><span></span>
          </div>
        </div>
      </template>
    </div>

    <footer class="ai__compose">
      <p v-if="mockNotice" class="ai__mock-note">
        규칙 기반 모드입니다. 실제 AI 대화는 OpenAI 설정 시 활성화됩니다.
      </p>
      <div class="ai__compose-box">
        <textarea
          v-model="draft"
          class="ai__input"
          rows="1"
          placeholder="메시지를 입력하세요…  (Enter 전송)"
          @keydown="onKeydown"
        />
        <button
          class="ai__send"
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
.ai {
  width: var(--ai-panel-w);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-left: 1px solid var(--c-border);
  background: var(--c-surface);
}
.ai__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-h);
  padding: 0 var(--sp-4);
  border-bottom: 1px solid var(--c-border);
}
.ai__title {
  display: inline-flex;
  align-items: center;
  gap: var(--sp-2);
  font-weight: 600;
}
.ai__title-icon {
  display: grid;
  place-items: center;
  width: 24px;
  height: 24px;
  border-radius: var(--r-sm);
  background: var(--c-accent-soft);
  color: var(--c-accent);
}
.ai__head-actions {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}
.ai__badge {
  padding: 2px 8px;
  border-radius: var(--r-full);
  background: var(--c-surface-alt);
  font-size: 10px;
  font-weight: 600;
  color: var(--c-text-2);
}
.ai__ghost-btn {
  padding: 4px 10px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.ai__ghost-btn:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.ai__collapse {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: var(--r-sm);
  color: var(--c-text-2);
}
.ai__collapse:hover {
  background: var(--c-surface-alt);
}
.ai__body {
  flex: 1;
  overflow-y: auto;
  padding: var(--sp-5) var(--sp-4);
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.ai__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--sp-3);
  padding-top: var(--sp-6);
}
.ai__empty-icon {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
  border-radius: var(--r-lg);
  background: var(--c-accent-soft);
  color: var(--c-accent);
}
.ai__empty-title {
  font-size: var(--fs-lg);
}
.ai__empty-desc {
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  max-width: 24ch;
}
.ai__chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--sp-2);
  margin-top: var(--sp-2);
}
.ai__chip {
  padding: 6px 12px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.ai__chip:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.ai__quick {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.ai__quick-item {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  width: 100%;
  padding: var(--sp-3);
  border-radius: var(--r-md);
  border: 1px solid var(--c-border);
  font-size: var(--fs-sm);
  text-align: left;
  color: var(--c-text);
}
.ai__quick-item:hover {
  background: var(--c-surface-alt);
}
.ai__quick-icon {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
.ai__quick-chevron {
  margin-left: auto;
  color: var(--c-text-muted);
}

/* 대화 버블 */
.ai__msg {
  display: flex;
}
.ai__msg--user {
  justify-content: flex-end;
}
.ai__bubble {
  max-width: 85%;
  padding: var(--sp-3);
  border-radius: var(--r-md);
  font-size: var(--fs-sm);
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}
.ai__msg--user .ai__bubble {
  background: var(--c-primary);
  color: var(--c-primary-contrast);
  border-bottom-right-radius: var(--r-sm);
}
.ai__msg--assistant .ai__bubble {
  background: var(--c-surface-alt);
  color: var(--c-text);
  border-bottom-left-radius: var(--r-sm);
}
.ai__msg.is-error .ai__bubble {
  background: var(--c-peach);
  color: var(--c-danger);
}
.ai__typing {
  display: inline-flex;
  gap: 4px;
  align-items: center;
}
.ai__typing span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--c-text-muted);
  animation: ai-blink 1.2s infinite ease-in-out both;
}
.ai__typing span:nth-child(2) {
  animation-delay: 0.2s;
}
.ai__typing span:nth-child(3) {
  animation-delay: 0.4s;
}
@keyframes ai-blink {
  0%,
  80%,
  100% {
    opacity: 0.2;
  }
  40% {
    opacity: 1;
  }
}

.ai__compose {
  border-top: 1px solid var(--c-border);
  padding: var(--sp-3) var(--sp-4) var(--sp-4);
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.ai__mock-note {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.ai__compose-box {
  display: flex;
  align-items: flex-end;
  gap: var(--sp-2);
  padding: var(--sp-2);
  border-radius: var(--r-md);
  border: 1px solid var(--c-border);
  background: var(--c-surface-alt);
}
.ai__compose-box:focus-within {
  border-color: var(--c-primary);
}
.ai__input {
  flex: 1;
  border: none;
  background: transparent;
  resize: none;
  max-height: 120px;
  font-family: inherit;
  font-size: var(--fs-sm);
  line-height: 1.5;
}
.ai__input:focus {
  outline: none;
}
.ai__send {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  border-radius: var(--r-full);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.ai__send:hover {
  background: var(--c-primary-hover);
}
.ai__send:disabled {
  opacity: 0.4;
}
</style>
