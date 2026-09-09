<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppRail from '@/components/common/AppRail.vue'
import AppHeader from '@/components/common/AppHeader.vue'
import AiAssistantPanel from '@/components/common/AiAssistantPanel.vue'

const route = useRoute()
const aiCollapsed = ref(true)

// 회의 상세처럼 meta.aiPanelOpen 이 있는 화면은 패널을 펼친 상태로 진입
watch(
  () => route.meta.aiPanelOpen,
  (open) => {
    if (open) aiCollapsed.value = false
  },
  { immediate: true },
)
</script>

<template>
  <div class="layout">
    <div class="layout__shell">
      <AppRail />

      <div class="layout__main">
        <AppHeader :ai-panel-collapsed="aiCollapsed" @toggle-ai="aiCollapsed = false" />
        <main class="layout__content">
          <div class="layout__content-inner">
            <RouterView />
          </div>
        </main>
      </div>

      <AiAssistantPanel v-if="!aiCollapsed" @collapse="aiCollapsed = true" />
    </div>
  </div>
</template>

<style scoped>
.layout {
  height: 100%;
  padding: var(--shell-gap);
  background: var(--c-page);
}
.layout__shell {
  display: flex;
  height: 100%;
  overflow: hidden;
  background: var(--c-shell);
  border: 1px solid var(--c-border);
  border-radius: var(--r-shell);
  box-shadow: var(--shadow-shell);
}
.layout__main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.layout__content {
  flex: 1;
  overflow-y: auto;
  background: var(--c-surface-alt);
}
.layout__content-inner {
  max-width: var(--content-max);
  margin: 0 auto;
  padding: var(--sp-6) var(--sp-6) var(--sp-8);
}
</style>
