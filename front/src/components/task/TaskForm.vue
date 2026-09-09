<script setup>
import { ref, reactive, computed, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import { required, maxLength, firstError } from '@/utils/validation'

const props = defineProps({
  open: { type: Boolean, default: false },
  task: { type: Object, default: null }, // 있으면 수정
  projectId: { type: [Number, String], required: true },
  members: { type: Array, default: () => [] }, // [{ userId, name }]
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const PRIORITY_OPTIONS = [
  { label: '높음', value: 'HIGH' },
  { label: '보통', value: 'MEDIUM' },
  { label: '낮음', value: 'LOW' },
]
const STATUS_OPTIONS = [
  { label: '할 일', value: 'TODO' },
  { label: '진행 중', value: 'IN_PROGRESS' },
  { label: '완료', value: 'DONE' },
]
const assigneeOptions = computed(() => [
  { label: '미지정', value: '' },
  ...props.members.map((m) => ({ label: m.name, value: String(m.userId) })),
])

const form = reactive({
  title: '',
  description: '',
  assigneeId: '',
  dueDate: '',
  priority: 'MEDIUM',
  status: 'TODO',
})
const errors = reactive({ title: '' })
const formError = ref('')
const submitting = ref(false)

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    formError.value = ''
    errors.title = ''
    const t = props.task
    Object.assign(form, {
      title: t?.title ?? '',
      description: t?.description ?? '',
      assigneeId: t?.assigneeId != null ? String(t.assigneeId) : '',
      dueDate: t?.dueDate ?? '',
      priority: t?.priority ?? 'MEDIUM',
      status: t?.status ?? 'TODO',
    })
  },
)

function validate() {
  errors.title = required(form.title, '업무명') || maxLength(form.title, 200, '업무명')
  return !firstError([errors.title])
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) return
  submitting.value = true
  try {
    const base = {
      title: form.title.trim(),
      description: form.description.trim() || null,
      assigneeId: form.assigneeId ? Number(form.assigneeId) : null,
      dueDate: form.dueDate || null,
      priority: form.priority,
    }
    const payload = props.task
      ? {
          ...base,
          status: form.status,
          sprintId: props.task.sprintId ?? null,
          meetingId: props.task.meetingId ?? null,
        }
      : { ...base, projectId: Number(props.projectId) }
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
    :title="task ? 'Task 수정' : '새 Task'"
    @update:open="emit('update:open', $event)"
  >
    <form class="tform" @submit.prevent="onSubmit">
      <p v-if="formError" class="tform__error">{{ formError }}</p>

      <BaseInput v-model="form.title" label="업무명" required :error="errors.title" />

      <label class="tform__field">
        <span class="tform__label">설명</span>
        <textarea v-model="form.description" class="tform__textarea" rows="3" />
      </label>

      <div class="tform__row">
        <BaseSelect
          v-model="form.assigneeId"
          label="담당자"
          :options="assigneeOptions"
          placeholder="미지정"
        />
        <BaseInput v-model="form.dueDate" label="마감일" type="date" />
      </div>

      <div class="tform__row">
        <BaseSelect v-model="form.priority" label="우선순위" :options="PRIORITY_OPTIONS" />
        <BaseSelect v-if="task" v-model="form.status" label="상태" :options="STATUS_OPTIONS" />
      </div>
    </form>

    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="emit('update:open', false)">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="submitting" @click="onSubmit">
        {{ submitting ? '저장 중…' : task ? '저장' : '생성' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.tform {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.tform__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.tform__field {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.tform__label {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.tform__textarea {
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  resize: vertical;
  font-family: inherit;
}
.tform__textarea:focus {
  outline: none;
  border-color: var(--c-primary);
}
.tform__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--sp-3);
}
</style>
