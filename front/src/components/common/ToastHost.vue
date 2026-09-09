<script setup>
import { toast } from '@/utils/toast'
import AppIcon from './AppIcon.vue'

const t = toast()
const ICON = { success: 'sparkle', error: 'bell', info: 'chat' }
</script>

<template>
  <Teleport to="body">
    <div class="toasts">
      <TransitionGroup name="toast">
        <div
          v-for="item in t.items"
          :key="item.id"
          class="toast"
          :class="`toast--${item.type}`"
          @click="t.dismiss(item.id)"
        >
          <AppIcon :name="ICON[item.type] || 'chat'" :size="16" />
          <span class="toast__msg">{{ item.message }}</span>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toasts {
  position: fixed;
  right: var(--sp-5);
  bottom: var(--sp-5);
  z-index: 300;
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  max-width: 360px;
}
.toast {
  display: flex;
  align-items: flex-start;
  gap: var(--sp-2);
  padding: var(--sp-3) var(--sp-4);
  border-radius: var(--r-md);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
  font-size: var(--fs-sm);
  box-shadow: var(--shadow-pop);
  cursor: pointer;
}
.toast--success {
  background: var(--c-success);
}
.toast--error {
  background: var(--c-danger);
}
.toast__msg {
  line-height: 1.4;
}
.toast-enter-active,
.toast-leave-active {
  transition: all 0.2s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
