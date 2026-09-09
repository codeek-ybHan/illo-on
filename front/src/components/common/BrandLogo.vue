<script setup>
/**
 * 일로ON 브랜드 로고 (심볼 + 워드마크 락업).
 *
 * - variant "full"  : 심볼 + "일로ON" (기본)
 * - variant "mark"  : 심볼만
 * - variant "wordmark" : 텍스트만
 * - mono: 단색 모드 — 어두운 배경/한 가지 색으로 찍어야 할 때 currentColor 사용
 * - tagline: 슬로건 한 줄 노출
 */
defineProps({
  variant: {
    type: String,
    default: 'full',
    validator: (v) => ['full', 'mark', 'wordmark'].includes(v),
  },
  size: { type: [Number, String], default: 28 },
  mono: { type: Boolean, default: false },
  tagline: { type: Boolean, default: false },
})
</script>

<template>
  <span class="brand" :class="{ 'brand--mono': mono }" :style="{ '--brand-size': `${size}px` }">
    <span class="brand__row">
      <svg
        v-if="variant !== 'wordmark'"
        class="brand__mark"
        viewBox="0 0 24 24"
        role="img"
        aria-label="일로ON"
      >
        <rect class="brand__pill" x="1.75" y="6" width="15" height="12" rx="6" />
        <circle class="brand__knob" cx="12" cy="12" r="3" />
        <g class="brand__rays" stroke-width="2" stroke-linecap="round" fill="none">
          <path d="M17.8 5.4 20.2 2.7" />
          <path d="M19 8.7 22.4 7.2" />
          <path d="M19.1 12 22.9 12" />
        </g>
      </svg>

      <span v-if="variant !== 'mark'" class="brand__word">
        <span class="brand__word-il">일로</span><span class="brand__word-on">ON</span>
      </span>
    </span>

    <span v-if="tagline" class="brand__tagline">회의를 행동으로, 함께 더 멀리</span>
  </span>
</template>

<style scoped>
.brand {
  display: inline-flex;
  flex-direction: column;
  gap: calc(var(--brand-size) * 0.18);
  line-height: 1;
}
.brand__row {
  display: inline-flex;
  align-items: center;
  gap: calc(var(--brand-size) * 0.34);
}
.brand__mark {
  width: calc(var(--brand-size) * 1.14);
  height: calc(var(--brand-size) * 1.14);
  flex-shrink: 0;
  overflow: visible;
}
.brand__pill {
  fill: var(--c-accent);
}
.brand__knob {
  fill: #fff;
}
.brand__rays {
  stroke: var(--c-accent);
}
.brand__word {
  font-family: var(--font-sans);
  font-size: var(--brand-size);
  font-weight: 800;
  letter-spacing: -0.02em;
}
.brand__word-il {
  color: var(--c-text);
}
.brand__word-on {
  color: var(--c-accent);
}
.brand__tagline {
  font-size: calc(var(--brand-size) * 0.42);
  font-weight: 500;
  letter-spacing: -0.01em;
  color: var(--c-text-muted);
}

/* 단색 모드 — 어두운 배경 위 등 */
.brand--mono .brand__pill,
.brand--mono .brand__word-il,
.brand--mono .brand__word-on {
  fill: currentColor;
  color: currentColor;
}
.brand--mono .brand__rays {
  stroke: currentColor;
}
.brand--mono .brand__knob {
  fill: transparent;
  stroke: currentColor;
  stroke-width: 2;
}
</style>
