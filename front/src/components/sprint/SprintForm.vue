<script setup>
import { ref, reactive, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import { required, maxLength, dateRange, firstError } from '@/utils/validation'

const props = defineProps({
  open: { type: Boolean, default: false },
  sprint: { type: Object, default: null },
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const STATUS_OPTIONS = [
  { label: '예정', value: 'PLANNED' },
  { label: '진행 중', value: 'ACTIVE' },
  { label: '완료', value: 'COMPLETED' },
]

const form = reactive({ name: '', startDate: '', endDate: '', status: 'PLANNED' })
const errors = reactive({ name: '', dates: '' })
const formError = ref('')
const submitting = ref(false)

watch(
  () => props.open,
  (o) => {
    if (!o) return
    formError.value = ''
    errors.name = ''
    errors.dates = ''
    const s = props.sprint
    Object.assign(form, {
      name: s?.name ?? '',
      startDate: s?.startDate ?? '',
      endDate: s?.endDate ?? '',
      status: s?.status ?? 'PLANNED',
    })
  },
)

function validate() {
  errors.name = required(form.name, 'Sprint 이름') || maxLength(form.name, 100, 'Sprint 이름')
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
      startDate: form.startDate || null,
      endDate: form.endDate || null,
    }
    if (props.sprint) payload.status = form.status
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
    :title="sprint ? 'Sprint 수정' : '새 Sprint'"
    size="sm"
    @update:open="emit('update:open', $event)"
  >
    <form class="sform" @submit.prevent="onSubmit">
      <p v-if="formError" class="sform__error">{{ formError }}</p>
      <BaseInput v-model="form.name" label="Sprint 이름" required :error="errors.name" />
      <div class="sform__row">
        <BaseInput v-model="form.startDate" label="시작일" type="date" />
        <BaseInput v-model="form.endDate" label="종료일" type="date" :error="errors.dates" />
      </div>
      <BaseSelect v-if="sprint" v-model="form.status" label="상태" :options="STATUS_OPTIONS" />
    </form>
    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="emit('update:open', false)">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="submitting" @click="onSubmit">
        {{ submitting ? '저장 중…' : sprint ? '저장' : '생성' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.sform {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.sform__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.sform__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-3);
}
</style>
