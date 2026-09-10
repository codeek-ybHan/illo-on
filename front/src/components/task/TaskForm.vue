<script setup>
import { ref, reactive, computed, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseDatePicker from '@/components/common/BaseDatePicker.vue'
import { fetchMembers } from '@/api/project'
import { fetchSprints } from '@/api/sprint'
import { required, maxLength, firstError } from '@/utils/validation'

const props = defineProps({
  open: { type: Boolean, default: false },
  task: { type: Object, default: null }, // 있으면 수정
  /** 고정 프로젝트 (프로젝트/회의 상세에서 열 때) */
  projectId: { type: [Number, String], default: null },
  /** 프로젝트 선택이 필요할 때 (헤더 "만들기"에서 열 때) */
  projects: { type: Array, default: () => [] },
  members: { type: Array, default: () => [] }, // [{ userId, name }]
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const fixedProject = computed(() => props.projectId != null)
const pickedProjectId = ref('')
const effectiveProjectId = computed(() =>
  fixedProject.value ? props.projectId : pickedProjectId.value || null,
)
const projectOptions = computed(() =>
  props.projects.map((p) => ({ label: p.name, value: String(p.projectId) })),
)

const fetchedMembers = ref([])
const membersLoading = ref(false)
const resolvedMembers = computed(() =>
  props.members.length ? props.members : fetchedMembers.value,
)

const sprints = ref([])
const sprintOptions = computed(() => [
  { label: '없음', value: '' },
  ...sprints.value.map((s) => ({ label: s.name, value: String(s.sprintId) })),
])

watch(
  [() => props.open, effectiveProjectId],
  async ([isOpen, pid]) => {
    if (!isOpen || !pid) {
      fetchedMembers.value = []
      sprints.value = []
      return
    }
    // 담당자: 부모가 members 를 넘겼으면(고정 프로젝트) 그걸 쓰고, 아니면 직접 조회
    if (!fixedProject.value) {
      membersLoading.value = true
      try {
        fetchedMembers.value = await fetchMembers(pid)
      } catch {
        fetchedMembers.value = []
      } finally {
        membersLoading.value = false
      }
    }
    // Sprint 목록은 항상 직접 조회
    try {
      sprints.value = await fetchSprints(pid)
    } catch {
      sprints.value = []
    }
  },
  { immediate: true },
)

const PRIORITY_OPTIONS = [
  { label: '높음', value: 'HIGH' },
  { label: '보통', value: 'MEDIUM' },
  { label: '낮음', value: 'LOW' },
]
const STATUS_OPTIONS = [
  { label: '진행 전', value: 'TODO' },
  { label: '진행 중', value: 'IN_PROGRESS' },
  { label: '완료', value: 'DONE' },
]
const assigneeOptions = computed(() => [
  { label: '미지정', value: '' },
  ...resolvedMembers.value.map((m) => ({ label: m.name, value: String(m.userId) })),
])

const form = reactive({
  title: '',
  description: '',
  assigneeId: '',
  sprintId: '',
  dueDate: '',
  priority: 'MEDIUM',
  status: 'TODO',
})
const errors = reactive({ title: '', project: '' })
const formError = ref('')
const submitting = ref(false)

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    formError.value = ''
    errors.title = ''
    errors.project = ''
    pickedProjectId.value = ''
    const t = props.task
    Object.assign(form, {
      title: t?.title ?? '',
      description: t?.description ?? '',
      assigneeId: t?.assigneeId != null ? String(t.assigneeId) : '',
      sprintId: t?.sprintId != null ? String(t.sprintId) : '',
      dueDate: t?.dueDate ?? '',
      priority: t?.priority ?? 'MEDIUM',
      status: t?.status ?? 'TODO',
    })
  },
)

function validate() {
  errors.title = required(form.title, '업무명') || maxLength(form.title, 200, '업무명')
  errors.project =
    props.task || fixedProject.value || pickedProjectId.value ? '' : '프로젝트를 선택해 주세요.'
  return !firstError([errors.title, errors.project])
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
      sprintId: form.sprintId ? Number(form.sprintId) : null,
      dueDate: form.dueDate || null,
      priority: form.priority,
    }
    const payload = props.task
      ? {
          ...base,
          status: form.status,
          meetingId: props.task.meetingId ?? null,
        }
      : { ...base, projectId: Number(effectiveProjectId.value) }
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

      <BaseSelect
        v-if="!fixedProject && !task"
        v-model="pickedProjectId"
        label="프로젝트"
        required
        :options="projectOptions"
        placeholder="프로젝트 선택"
        :error="errors.project"
      />

      <BaseInput v-model="form.title" label="업무명" required :error="errors.title" />

      <p v-if="membersLoading" class="tform__hint">멤버 불러오는 중…</p>

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
        <BaseDatePicker
          v-model="form.dueDate"
          label="마감일시"
          placeholder="마감일시 선택"
          with-time
        />
      </div>

      <div class="tform__row">
        <BaseSelect v-model="form.priority" label="우선순위" :options="PRIORITY_OPTIONS" />
        <BaseSelect
          v-model="form.sprintId"
          label="Sprint"
          :options="sprintOptions"
          placeholder="없음"
        />
      </div>

      <BaseSelect
        v-if="task"
        v-model="form.status"
        label="상태"
        :options="STATUS_OPTIONS"
      />
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
.tform__hint {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
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
