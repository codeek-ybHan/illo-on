<script setup>
import { ref } from 'vue'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import AppIcon from '@/components/common/AppIcon.vue'

const inputTab = ref('text')
</script>

<template>
  <PagePlaceholder
    title="회의 상세"
    subtitle="회의 내용을 입력하고 AI 분석으로 Action Point를 추출하세요."
  >
    <!-- 회의 메타 -->
    <BaseCard>
      <div class="meta">
        <span class="skeleton-text skeleton-text--title" />
        <div class="meta__row">
          <span class="meta__item"><AppIcon name="calendar" :size="14" /> 일시 미정</span>
          <span class="meta__item"><AppIcon name="project" :size="14" /> 프로젝트 미연결</span>
          <span class="meta__item">참석자 —</span>
        </div>
      </div>
    </BaseCard>

    <!-- 회의 내용 입력 -->
    <BaseCard>
      <template #header>회의 내용</template>
      <div class="tabs">
        <button
          class="tab"
          :class="{ 'is-active': inputTab === 'text' }"
          @click="inputTab = 'text'"
        >
          텍스트 입력
        </button>
        <button
          class="tab"
          :class="{ 'is-active': inputTab === 'audio' }"
          @click="inputTab = 'audio'"
        >
          녹음본 업로드
        </button>
      </div>

      <div v-if="inputTab === 'text'" class="input-area">
        <div class="skeleton input-area__box" />
        <p class="u-muted">회의록 또는 메신저 대화 내용을 붙여넣는 영역 (미구현).</p>
      </div>
      <div v-else class="input-area">
        <div class="upload-drop">
          <AppIcon name="paperclip" :size="20" />
          <span>녹음 파일을 끌어다 놓거나 선택 (미구현)</span>
        </div>
      </div>

      <div class="analyze-row">
        <BaseButton variant="primary">
          <template #icon><AppIcon name="sparkle" :size="16" /></template>
          AI 분석하기
        </BaseButton>
      </div>
    </BaseCard>

    <!-- AI 회의 브리핑 -->
    <BaseCard>
      <template #header>
        <span class="briefing-title">
          <span class="briefing-title__icon"><AppIcon name="sparkle" :size="15" /></span>
          AI 회의 브리핑
        </span>
      </template>

      <div class="briefing">
        <div class="briefing__block">
          <h3>한눈에 보기</h3>
          <div class="skeleton skeleton--summary" />
        </div>

        <div class="briefing__block">
          <h3>📌 결정사항</h3>
          <ul class="decisions">
            <li v-for="n in 2" :key="n"><span class="skeleton-text" /></li>
          </ul>
        </div>

        <div class="briefing__block">
          <h3>⚡ Action Point</h3>
          <div class="ap-grid">
            <div v-for="n in 2" :key="n" class="ap-card">
              <span class="skeleton-text skeleton-text--title" />
              <div class="ap-card__meta">
                <span class="tag">담당자 미지정</span>
                <span class="tag">기한 미정</span>
                <span class="tag tag--prio">우선순위 —</span>
              </div>
              <div class="ap-card__foot">
                <BaseButton variant="ghost" size="sm">수정</BaseButton>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="register-row">
        <BaseButton variant="primary">업무로 등록</BaseButton>
      </div>
    </BaseCard>
  </PagePlaceholder>
</template>

<style scoped>
.meta {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.meta__row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-4);
}
.meta__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.tabs {
  display: flex;
  gap: var(--sp-1);
  margin-bottom: var(--sp-4);
  border-bottom: 1px solid var(--c-border);
}
.tab {
  padding: var(--sp-2) var(--sp-3);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.tab.is-active {
  color: var(--c-text);
  font-weight: 600;
  border-bottom-color: var(--c-primary);
}
.input-area {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.input-area__box {
  height: 140px;
}
.upload-drop {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--sp-2);
  height: 140px;
  border: 1px dashed var(--c-border-strong);
  border-radius: var(--r-md);
  color: var(--c-text-muted);
  font-size: var(--fs-sm);
}
.analyze-row {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--sp-4);
}
.briefing-title {
  display: inline-flex;
  align-items: center;
  gap: var(--sp-2);
}
.briefing-title__icon {
  display: grid;
  place-items: center;
  width: 22px;
  height: 22px;
  border-radius: var(--r-sm);
  background: var(--c-accent-soft);
  color: var(--c-accent);
}
.briefing {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}
.briefing__block h3 {
  font-size: var(--fs-md);
  margin-bottom: var(--sp-3);
}
.skeleton--summary {
  height: 56px;
}
.decisions {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.ap-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-3);
}
.ap-card {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
  padding: var(--sp-4);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
}
.ap-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-2);
}
.tag {
  padding: 2px 8px;
  border-radius: var(--r-full);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.tag--prio {
  color: var(--c-accent);
}
.ap-card__foot {
  display: flex;
  justify-content: flex-end;
}
.register-row {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--sp-5);
}
.skeleton-text {
  display: block;
  height: 10px;
  width: 70%;
  border-radius: var(--r-full);
  background: var(--c-border);
}
.skeleton-text--title {
  width: 40%;
  height: 13px;
}

@media (max-width: 1080px) {
  .ap-grid {
    grid-template-columns: 1fr;
  }
}
</style>
