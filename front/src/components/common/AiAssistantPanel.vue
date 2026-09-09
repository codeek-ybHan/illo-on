<script setup>
import AppIcon from './AppIcon.vue'

defineEmits(['collapse'])

const suggestions = ['회의 요약하기', 'Action Point 뽑기', '오늘 일정 정리']
const quickActions = [
  { icon: 'meeting', label: '회의록에서 할 일 추출' },
  { icon: 'sprint', label: '이번 Sprint 진행률 요약' },
  { icon: 'calendar', label: '마감 임박 Task 알려줘' },
]
</script>

<template>
  <aside class="ai">
    <header class="ai__head">
      <span class="ai__title">
        <span class="ai__title-icon"><AppIcon name="sparkle" :size="16" /></span>
        일로ON AI
      </span>
      <div class="ai__head-actions">
        <span class="ai__badge">Beta</span>
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

    <div class="ai__body">
      <div class="ai__empty">
        <div class="ai__empty-icon"><AppIcon name="sparkle" :size="26" /></div>
        <h3 class="ai__empty-title">무엇을 도와드릴까요?</h3>
        <p class="ai__empty-desc">
          회의 내용, Action Point, 프로젝트 일정에 대해 무엇이든 물어보세요.
        </p>
        <div class="ai__chips">
          <button v-for="s in suggestions" :key="s" class="ai__chip" type="button">{{ s }}</button>
        </div>
      </div>

      <ul class="ai__quick">
        <li v-for="q in quickActions" :key="q.label">
          <button class="ai__quick-item" type="button">
            <span class="ai__quick-icon"><AppIcon :name="q.icon" :size="16" /></span>
            {{ q.label }}
            <span class="ai__quick-chevron"><AppIcon name="chevronRight" :size="14" /></span>
          </button>
        </li>
      </ul>
    </div>

    <footer class="ai__compose">
      <input class="ai__input" type="text" placeholder="메시지를 입력하세요…" disabled />
      <div class="ai__compose-row">
        <div class="ai__compose-tools">
          <button type="button" aria-label="음성 입력"><AppIcon name="mic" :size="16" /></button>
          <button type="button" aria-label="링크 첨부"><AppIcon name="link" :size="16" /></button>
          <button type="button" aria-label="파일 첨부">
            <AppIcon name="paperclip" :size="16" />
          </button>
        </div>
        <button class="ai__send" type="button" aria-label="보내기">
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
  gap: var(--sp-6);
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
.ai__compose {
  border-top: 1px solid var(--c-border);
  padding: var(--sp-3) var(--sp-4) var(--sp-4);
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.ai__input {
  width: 100%;
  height: 40px;
  padding: 0 var(--sp-3);
  border-radius: var(--r-md);
  border: 1px solid var(--c-border);
  background: var(--c-surface-alt);
  font-size: var(--fs-sm);
}
.ai__compose-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.ai__compose-tools {
  display: flex;
  gap: var(--sp-1);
}
.ai__compose-tools button {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: var(--r-sm);
  color: var(--c-text-2);
}
.ai__compose-tools button:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.ai__send {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: var(--r-full);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.ai__send:hover {
  background: var(--c-primary-hover);
}
</style>
