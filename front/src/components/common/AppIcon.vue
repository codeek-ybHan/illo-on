<script setup>
/**
 * 라인 아이콘 세트 (24x24, stroke 기반).
 * 레이아웃 스캐폴드용 최소 세트 — 필요 시 여기에 추가한다.
 */
const PATHS = {
  home: '<path d="M4 10.5 12 4l8 6.5V19a1 1 0 0 1-1 1h-4v-6H9v6H5a1 1 0 0 1-1-1z"/>',
  calendar: '<rect x="4" y="5" width="16" height="15" rx="2"/><path d="M4 9h16M9 3v4M15 3v4"/>',
  meeting:
    '<path d="M5 4h14a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H9l-4 4V5a1 1 0 0 1 1-1z"/><path d="M8 9h8M8 12h5"/>',
  project: '<path d="M4 7a2 2 0 0 1 2-2h4l2 2h6a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2z"/>',
  sprint: '<circle cx="12" cy="12" r="8"/><path d="M12 12 12 7M12 12l4 2"/>',
  settings:
    '<circle cx="12" cy="12" r="3"/><path d="M19.4 13a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 0 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.5-1H3a2 2 0 0 1 0-4h.1a1.7 1.7 0 0 0 1.5-1.1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 0 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 0 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/>',
  bell: '<path d="M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6"/><path d="M10 19a2 2 0 0 0 4 0"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>',
  chat: '<path d="M4 5h16a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H9l-5 4V6a1 1 0 0 1 1-1z"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  chevronRight: '<path d="m9 6 6 6-6 6"/>',
  chevronLeft: '<path d="m15 6-6 6 6 6"/>',
  sparkle:
    '<path d="M12 3l1.8 4.9L18.7 10l-4.9 1.8L12 16.7 10.2 11.8 5.3 10l4.9-1.8z"/><path d="M18 15l.7 1.9L20.7 18l-1.9.7L18 20.7 17.3 18.7 15.3 18l2-.7z"/>',
  mic: '<rect x="9" y="3" width="6" height="12" rx="3"/><path d="M6 11a6 6 0 0 0 12 0M12 17v4"/>',
  link: '<path d="M9 15 15 9M8 12H6a4 4 0 0 1 0-8h2M16 12h2a4 4 0 0 1 0 8h-2"/>',
  paperclip:
    '<path d="M20 11.5 12 19a5 5 0 0 1-7-7l8-8a3.5 3.5 0 0 1 5 5l-8 8a2 2 0 0 1-3-3l7-7"/>',
  arrowUp: '<path d="M12 19V5M6 11l6-6 6 6"/>',
  // 토글 스위치가 ON 으로 켜진 모양 + 빛 발산 — 일로ON 심볼
  logo: '<rect x="1.75" y="6" width="15" height="12" rx="6"/><circle cx="12" cy="12" r="3" fill="currentColor" stroke="none"/><path d="M17.8 5.4 20.2 2.7"/><path d="M19 8.7 22.4 7.2"/><path d="M19.1 12 22.9 12"/>',
}

defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 20 },
})
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.6"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
    v-html="PATHS[name] || ''"
  />
</template>
