<script setup>
import { watch, onBeforeUnmount } from 'vue'
import AppIcon from './AppIcon.vue'

const props = defineProps({
  /** v-model:open */
  open: { type: Boolean, default: false },
  title: { type: String, default: '' },
  /** 배경 클릭으로 닫기 허용 */
  closeOnBackdrop: { type: Boolean, default: true },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md', 'lg'].includes(v),
  },
})
const emit = defineEmits(['update:open', 'close'])

function close() {
  emit('update:open', false)
  emit('close')
}

function onKeydown(e) {
  if (e.key === 'Escape') close()
}

watch(
  () => props.open,
  (isOpen) => {
    if (isOpen) {
      window.addEventListener('keydown', onKeydown)
      document.body.style.overflow = 'hidden'
    } else {
      window.removeEventListener('keydown', onKeydown)
      document.body.style.overflow = ''
    }
  },
)

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="open" class="modal__backdrop" @click.self="closeOnBackdrop && close()">
        <div
          class="modal"
          :class="`modal--${size}`"
          role="dialog"
          aria-modal="true"
          :aria-label="title || undefined"
        >
          <header v-if="title || $slots.header" class="modal__header">
            <slot name="header">
              <h2 class="modal__title">{{ title }}</h2>
            </slot>
            <button class="modal__close" type="button" aria-label="닫기" @click="close">
              <AppIcon name="plus" :size="18" style="transform: rotate(45deg)" />
            </button>
          </header>

          <div class="modal__body">
            <slot />
          </div>

          <footer v-if="$slots.footer" class="modal__footer">
            <slot name="footer" />
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal__backdrop {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: grid;
  place-items: center;
  padding: var(--sp-5);
  background: rgba(20, 20, 20, 0.35);
}
.modal {
  width: 100%;
  max-height: calc(100vh - var(--sp-7));
  display: flex;
  flex-direction: column;
  background: var(--c-surface);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-pop);
  overflow: hidden;
}
.modal--sm {
  max-width: 380px;
}
.modal--md {
  max-width: 520px;
}
.modal--lg {
  max-width: 720px;
}
.modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-3);
  padding: var(--sp-4) var(--sp-5);
  border-bottom: 1px solid var(--c-border);
}
.modal__title {
  font-size: var(--fs-lg);
}
.modal__close {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border-radius: var(--r-sm);
  color: var(--c-text-2);
}
.modal__close:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.modal__body {
  padding: var(--sp-5);
  overflow-y: auto;
}
.modal__footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--sp-2);
  padding: var(--sp-4) var(--sp-5);
  border-top: 1px solid var(--c-border);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.18s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
