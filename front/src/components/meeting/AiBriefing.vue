<script setup>
import ActionPointCard from './ActionPointCard.vue'

defineProps({
  briefing: { type: Object, required: true }, // { summary, decisions, actionPoints, source, analyzedAt }
  members: { type: Array, default: () => [] },
  registeredIndexes: { type: Array, default: () => [] },
})
defineEmits(['register'])
</script>

<template>
  <div class="briefing">
    <section class="briefing__block">
      <h3 class="briefing__h">한눈에 보기</h3>
      <p class="briefing__summary">{{ briefing.summary || '요약 없음' }}</p>
      <p class="briefing__src">
        {{ briefing.source === 'AUDIO' ? '🎙 음성 분석' : '📝 텍스트 분석' }}
      </p>
    </section>

    <section class="briefing__block">
      <h3 class="briefing__h">📌 결정사항</h3>
      <ul v-if="briefing.decisions.length" class="decisions">
        <li v-for="(d, i) in briefing.decisions" :key="i">{{ d }}</li>
      </ul>
      <p v-else class="briefing__empty">추출된 결정사항이 없습니다.</p>
    </section>

    <section class="briefing__block">
      <h3 class="briefing__h">
        ⚡ Action Point <span class="briefing__count">{{ briefing.actionPoints.length }}</span>
      </h3>
      <p v-if="!briefing.actionPoints.length" class="briefing__empty">
        추출된 Action Point가 없습니다.
      </p>
      <div v-else class="ap-list">
        <ActionPointCard
          v-for="(ap, i) in briefing.actionPoints"
          :key="i"
          :action-point="ap"
          :members="members"
          :registered="registeredIndexes.includes(i)"
          @register="(payload) => $emit('register', i, payload)"
        />
      </div>
    </section>
  </div>
</template>

<style scoped>
.briefing {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}
.briefing__h {
  font-size: var(--fs-md);
  margin-bottom: var(--sp-3);
}
.briefing__count {
  margin-left: 4px;
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.briefing__summary {
  font-size: var(--fs-md);
  line-height: 1.7;
  color: var(--c-text);
}
.briefing__src {
  margin-top: var(--sp-2);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.briefing__empty {
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.decisions {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  font-size: var(--fs-sm);
  line-height: 1.6;
}
.decisions li {
  padding-left: var(--sp-4);
  position: relative;
}
.decisions li::before {
  content: '•';
  position: absolute;
  left: var(--sp-2);
  color: var(--c-accent);
}
.ap-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
</style>
