<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMembers } from '@/api/project'
import { useMeetingStore } from '@/stores/meeting'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import { required, maxLength, firstError } from '@/utils/validation'

const props = defineProps({
  open: { type: Boolean, default: false },
  meeting: { type: Object, default: null },
  /** 고정 프로젝트 (프로젝트 상세에서 열 때) */
  projectId: { type: [Number, String], default: null },
  /** 프로젝트 선택이 필요할 때 (전체 회의 목록에서 열 때) */
  projects: { type: Array, default: () => [] },
  submitFn: { type: Function, required: true },
})
const emit = defineEmits(['update:open'])

const router = useRouter()
const meetingStore = useMeetingStore()

const form = reactive({
  projectId: '',
  title: '',
  meetingAt: '',
  content: '',
  attendeeIds: [],
})
const errors = reactive({ title: '', project: '' })
const formError = ref('')
const submitting = ref(false)
const members = ref([])
const membersLoading = ref(false)
const inputMode = ref('text') // 'text' | 'audio'
const audioFile = ref(null)

const fixedProject = computed(() => props.projectId != null)
const effectiveProjectId = computed(() =>
  fixedProject.value ? props.projectId : form.projectId || null,
)
const projectOptions = computed(() =>
  props.projects.map((p) => ({ label: p.name, value: String(p.projectId) })),
)

watch(
  () => props.open,
  (o) => {
    if (!o) return
    formError.value = ''
    errors.title = ''
    errors.project = ''
    inputMode.value = 'text'
    audioFile.value = null
    const m = props.meeting
    form.projectId = m?.projectId != null ? String(m.projectId) : ''
    form.title = m?.title ?? ''
    form.meetingAt = m?.meetingAt ? m.meetingAt.slice(0, 16) : ''
    form.content = m?.content ?? ''
    form.attendeeIds = m?.attendees ? m.attendees.map((a) => a.userId) : []
  },
)

watch(
  [() => props.open, effectiveProjectId],
  async ([isOpen, pid]) => {
    if (!isOpen) return
    if (!pid) {
      members.value = []
      return
    }
    membersLoading.value = true
    try {
      members.value = await fetchMembers(pid)
    } catch {
      members.value = []
    } finally {
      membersLoading.value = false
    }
  },
  { immediate: true },
)

function toggleAttendee(userId) {
  const i = form.attendeeIds.indexOf(userId)
  if (i >= 0) form.attendeeIds.splice(i, 1)
  else form.attendeeIds.push(userId)
}

function onAudioPick(e) {
  audioFile.value = e.target.files?.[0] ?? null
}

function validate() {
  errors.title = required(form.title, '회의 제목') || maxLength(form.title, 200, '회의 제목')
  errors.project = fixedProject.value || form.projectId ? '' : '프로젝트를 선택해 주세요.'
  return !firstError([errors.title, errors.project])
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) return
  submitting.value = true
  try {
    const useAudio = inputMode.value === 'audio' && audioFile.value
    const payload = {
      title: form.title.trim(),
      content: useAudio ? null : form.content.trim() || null,
      meetingAt: form.meetingAt ? `${form.meetingAt}:00` : null,
      attendeeIds: form.attendeeIds,
    }
    const created = await props.submitFn(payload, Number(effectiveProjectId.value))
    emit('update:open', false)

    // 녹음본은 상세 화면으로 넘겨서 AI 분석(Phase 6) 시 STT 변환
    if (useAudio && created?.meetingId) {
      meetingStore.setPendingAudio(audioFile.value)
      router.push({ name: 'meeting-detail', params: { id: created.meetingId } })
    }
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
    :title="meeting ? '회의 수정' : '새 회의'"
    @update:open="emit('update:open', $event)"
  >
    <form class="mform" @submit.prevent="onSubmit">
      <p v-if="formError" class="mform__error">{{ formError }}</p>

      <BaseSelect
        v-if="!fixedProject"
        v-model="form.projectId"
        label="프로젝트"
        required
        :options="projectOptions"
        :error="errors.project"
      />

      <BaseInput v-model="form.title" label="회의 제목" required :error="errors.title" />
      <BaseInput v-model="form.meetingAt" label="회의 일시" type="datetime-local" />

      <div class="mform__field">
        <span class="mform__label">참석자</span>
        <p v-if="membersLoading" class="mform__hint">멤버 불러오는 중…</p>
        <p v-else-if="!members.length" class="mform__hint">
          {{ effectiveProjectId ? '멤버가 없습니다.' : '먼저 프로젝트를 선택하세요.' }}
        </p>
        <div v-else class="mform__attendees">
          <label v-for="mem in members" :key="mem.userId" class="mform__chk">
            <input
              type="checkbox"
              :checked="form.attendeeIds.includes(mem.userId)"
              @change="toggleAttendee(mem.userId)"
            />
            {{ mem.name }}
          </label>
        </div>
      </div>

      <div class="mform__field">
        <div class="mform__tabs">
          <button
            type="button"
            class="mform__tab"
            :class="{ 'is-active': inputMode === 'text' }"
            @click="inputMode = 'text'"
          >
            텍스트 입력
          </button>
          <button
            v-if="!meeting"
            type="button"
            class="mform__tab"
            :class="{ 'is-active': inputMode === 'audio' }"
            @click="inputMode = 'audio'"
          >
            녹음본 업로드
          </button>
        </div>

        <textarea
          v-if="inputMode === 'text'"
          v-model="form.content"
          class="mform__textarea"
          rows="5"
          placeholder="회의 내용을 입력하거나 메신저 대화를 붙여넣으세요."
        />

        <label v-else class="mform__audio">
          <input type="file" accept="audio/*" hidden @change="onAudioPick" />
          <AppIcon name="paperclip" :size="18" />
          <span v-if="audioFile">{{ audioFile.name }}</span>
          <span v-else>녹음 파일 선택 (mp3, m4a, wav…)</span>
        </label>
        <p v-if="inputMode === 'audio'" class="mform__hint">
          회의 생성 후 상세 화면에서 “AI 분석”을 실행하면 음성이 텍스트로 변환됩니다 (Phase 6).
        </p>
      </div>
    </form>

    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="emit('update:open', false)">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="submitting" @click="onSubmit">
        {{ submitting ? '저장 중…' : meeting ? '저장' : '생성' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.mform {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.mform__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.mform__field {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.mform__label {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.mform__hint {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.mform__attendees {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-2);
}
.mform__chk {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border: 1px solid var(--c-border);
  border-radius: var(--r-full);
  font-size: var(--fs-sm);
  cursor: pointer;
}
.mform__textarea {
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}
.mform__textarea:focus {
  outline: none;
  border-color: var(--c-primary);
}
.mform__tabs {
  display: flex;
  gap: var(--sp-1);
  border-bottom: 1px solid var(--c-border);
  margin-bottom: var(--sp-1);
}
.mform__tab {
  padding: var(--sp-2) var(--sp-3);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.mform__tab.is-active {
  color: var(--c-text);
  font-weight: 600;
  border-bottom-color: var(--c-primary);
}
.mform__audio {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--sp-2);
  padding: var(--sp-5);
  border: 1px dashed var(--c-border-strong);
  border-radius: var(--r-md);
  color: var(--c-text-muted);
  font-size: var(--fs-sm);
  cursor: pointer;
  word-break: break-all;
  text-align: center;
}
.mform__audio:hover {
  background: var(--c-surface-alt);
}
</style>
