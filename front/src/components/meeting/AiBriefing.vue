<script setup>
import ActionPointCard from './ActionPointCard.vue'

defineProps({
  briefing: { type: Object, required: true }, // { overview, highlights, decisions, actionPoints, source }
  members: { type: Array, default: () => [] },
  registeredIndexes: { type: Array, default: () => [] },
})
defineEmits(['register'])
</script>

<template>
  <div class="briefing">
    <section class="briefing__block">
      <h3 class="briefing__h">
        한눈에 보기
        <span class="briefing__src">{{
          briefing.source === 'AUDIO' ? '🎙 음성' : '📝 텍스트'
        }}</span>
      </h3>
      <p class="briefing__overview">{{ briefing.overview || '요약 없음' }}</p>
      <ul v-if="briefing.highlights?.length" class="bullets">
        <li v-for="(h, i) in briefing.highlights" :key="i">{{ h }}</li>
      </ul>
      <p v-if="briefing.provider === 'mock'" class="briefing__note">
        규칙 기반 요약입니다. 정교한 요약·정리는 <code>AI_PROVIDER=openai</code> 설정 시 제공됩니다.
      </p>
    </section>

    <section v-if="briefing.decisions.length" class="briefing__block">
      <h3 class="briefing__h">📌 결정사항</h3>
      <ul class="bullets bullets--accent">
        <li v-for="(d, i) in briefing.decisions" :key="i">{{ d }}</li>
      </ul>
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
.briefing__h {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}
.briefing__src {
  font-size: var(--fs-xs);
  font-weight: 400;
  color: var(--c-text-muted);
}
.briefing__overview {
  font-size: var(--fs-md);
  font-weight: 600;
  line-height: 1.6;
  color: var(--c-text);
  margin-bottom: var(--sp-3);
}
.briefing__empty {
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.briefing__note {
  margin-top: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.briefing__note code {
  padding: 1px 5px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  font-size: 11px;
}
.bullets {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  font-size: var(--fs-sm);
  line-height: 1.6;
}
.bullets li {
  padding-left: var(--sp-4);
  position: relative;
}
.bullets li::before {
  content: '•';
  position: absolute;
  left: var(--sp-2);
  color: var(--c-text-muted);
}
.bullets--accent li::before {
  color: var(--c-accent);
}
.ap-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
</style>
