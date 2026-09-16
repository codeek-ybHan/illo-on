<script setup>
import { ref, reactive, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import { maxLength } from '@/utils/validation'

const props = defineProps({
  open: { type: Boolean, default: false },
  member: { type: Object, default: null },
  /** async (payload) => void — 성공 시 모달 닫힘, 실패 시 에러 표시 */
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const form = reactive({ jobTitle: '' })
const errors = reactive({ jobTitle: '' })
const formError = ref('')
const submitting = ref(false)

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    formError.value = ''
    errors.jobTitle = ''
    form.jobTitle = props.member?.jobTitle ?? ''
  },
)

function validate() {
  errors.jobTitle = maxLength(form.jobTitle, 50, '직급/역할')
  return !errors.jobTitle
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) return
  submitting.value = true
  try {
    await props.submitFn({ jobTitle: form.jobTitle.trim() || null })
    emit('update:open', false)
  } catch (e) {
    formError.value = e.normalizedMessage || '저장에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <BaseModal
    :open="open"
    :title="`${member?.name ?? ''} 직급/역할 수정`"
    size="sm"
    @update:open="emit('update:open', $event)"
  >
    <form class="meform" @submit.prevent="onSubmit">
      <p v-if="formError" class="meform__error">{{ formError }}</p>
      <BaseInput
        v-model="form.jobTitle"
        label="직급/역할"
        placeholder="예: 팀장, 선임 개발자"
        :error="errors.jobTitle"
      />
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
.meform {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.meform__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
</style>
