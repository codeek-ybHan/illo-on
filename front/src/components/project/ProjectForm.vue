<script setup>
import { ref, reactive, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseDatePicker from '@/components/common/BaseDatePicker.vue'
import { required, maxLength, dateRange, firstError } from '@/utils/validation'

const props = defineProps({
  open: { type: Boolean, default: false },
  /** 있으면 수정 모드 */
  project: { type: Object, default: null },
  /** async (payload) => void — 성공 시 모달 닫힘, 실패 시 에러 표시 */
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const STATUS_OPTIONS = [
  { label: '예정', value: 'PLANNED' },
  { label: '진행 중', value: 'ACTIVE' },
  { label: '완료', value: 'COMPLETED' },
]

const form = reactive({ name: '', description: '', startDate: '', endDate: '', status: 'PLANNED' })
const errors = reactive({ name: '', dates: '' })
const formError = ref('')
const submitting = ref(false)

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    formError.value = ''
    errors.name = ''
    errors.dates = ''
    if (props.project) {
      form.name = props.project.name ?? ''
      form.description = props.project.description ?? ''
      form.startDate = props.project.startDate ?? ''
      form.endDate = props.project.endDate ?? ''
      form.status = props.project.status ?? 'PLANNED'
    } else {
      Object.assign(form, {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        status: 'PLANNED',
      })
    }
  },
)

function validate() {
  errors.name = required(form.name, '프로젝트명') || maxLength(form.name, 100, '프로젝트명')
  errors.dates = dateRange(form.startDate, form.endDate)
  return !firstError([errors.name, errors.dates])
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) return
  submitting.value = true
  try {
    const payload = {
      name: form.name.trim(),
      description: form.description.trim() || null,
      startDate: form.startDate || null,
      endDate: form.endDate || null,
    }
    payload.status = form.status
    await props.submitFn(payload)
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
    :title="project ? '프로젝트 수정' : '새 프로젝트'"
    @update:open="emit('update:open', $event)"
  >
    <form class="pform" @submit.prevent="onSubmit">
      <p v-if="formError" class="pform__error">{{ formError }}</p>

      <BaseInput v-model="form.name" label="프로젝트명" required :error="errors.name" />

      <label class="pform__field">
        <span class="pform__label">설명</span>
        <textarea v-model="form.description" class="pform__textarea" rows="3" />
      </label>

      <div class="pform__row">
        <BaseDatePicker v-model="form.startDate" label="시작일" placeholder="시작일 선택" />
        <BaseDatePicker
          v-model="form.endDate"
          label="종료일"
          placeholder="종료일 선택"
          :error="errors.dates"
        />
      </div>

      <BaseSelect v-model="form.status" label="상태" :options="STATUS_OPTIONS" />
    </form>

    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="emit('update:open', false)">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="submitting" @click="onSubmit">
        {{ submitting ? '저장 중…' : project ? '저장' : '생성' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.pform {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.pform__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.pform__field {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.pform__label {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.pform__textarea {
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  resize: vertical;
  font-family: inherit;
}
.pform__textarea:focus {
  outline: none;
  border-color: var(--c-primary);
}
.pform__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-3);
}
</style>
