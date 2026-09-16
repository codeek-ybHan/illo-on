<script setup>
import { ref, reactive, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseButton from '@/components/common/BaseButton.vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  members: { type: Array, default: () => [] },
  /** async (changes: { userId, jobTitle }[]) => void — 성공 시 모달 닫힘, 실패 시 에러 표시 */
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const form = reactive({})
const formError = ref('')
const submitting = ref(false)

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    formError.value = ''
    Object.keys(form).forEach((k) => delete form[k])
    props.members.forEach((m) => {
      form[m.userId] = m.jobTitle ?? ''
    })
  },
)

async function onSubmit() {
  formError.value = ''
  const changes = props.members
    .filter((m) => (form[m.userId] ?? '').trim() !== (m.jobTitle ?? ''))
    .map((m) => ({ userId: m.userId, jobTitle: form[m.userId].trim() || null }))
  if (!changes.length) {
    emit('update:open', false)
    return
  }
  submitting.value = true
  try {
    await props.submitFn(changes)
    emit('update:open', false)
  } catch (e) {
    formError.value = e.normalizedMessage || '저장에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <BaseModal :open="open" title="직급/역할 설정" @update:open="emit('update:open', $event)">
    <form class="mrform" @submit.prevent="onSubmit">
      <p v-if="formError" class="mrform__error">{{ formError }}</p>

      <ul class="mrform__list">
        <li v-for="m in members" :key="m.userId" class="mrform__row">
          <span class="mrform__avatar">{{ m.name?.charAt(0) || '?' }}</span>
          <span class="mrform__name">{{ m.name }}</span>
          <input
            v-model="form[m.userId]"
            class="mrform__input"
            type="text"
            placeholder="예: 팀장, 선임 개발자"
            maxlength="50"
          />
        </li>
      </ul>
    </form>

    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="emit('update:open', false)">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="submitting" @click="onSubmit">
        {{ submitting ? '저장 중…' : '저장' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.mrform {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.mrform__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.mrform__list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.mrform__row {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
}
.mrform__avatar {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  border-radius: var(--r-full);
  background: var(--c-accent-soft);
  color: var(--c-accent);
  font-size: var(--fs-xs);
  font-weight: 700;
}
.mrform__name {
  width: 88px;
  flex-shrink: 0;
  font-size: var(--fs-sm);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mrform__input {
  flex: 1;
  min-width: 0;
  height: 36px;
  padding: 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  font-size: var(--fs-sm);
  font-family: inherit;
}
.mrform__input:focus {
  outline: none;
  border-color: var(--c-primary);
}
</style>
