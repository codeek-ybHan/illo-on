<script setup>
import { ref, reactive, computed } from 'vue'
import ActionPointCard from './ActionPointCard.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseDatePicker from '@/components/common/BaseDatePicker.vue'
import { useMeetingStore } from '@/stores/meeting'
import { toast } from '@/utils/toast'

const props = defineProps({
  meetingId: { type: [Number, String], required: true },
  briefing: { type: Object, required: true }, // { overview, highlights, decisions, actionPoints, source }
  members: { type: Array, default: () => [] },
  registeredIndexes: { type: Array, default: () => [] },
})
defineEmits(['register'])

const store = useMeetingStore()

const PRIORITY_OPTIONS = [
  { label: '높음', value: 'HIGH' },
  { label: '보통', value: 'MEDIUM' },
  { label: '낮음', value: 'LOW' },
]

const editing = ref(false)
const saving = ref(false)
const draft = reactive({ overview: '', highlights: [], decisions: [], actionPoints: [] })

const assigneeOptions = computed(() => [
  { label: '미지정', value: '' },
  ...props.members.map((m) => ({ label: m.name, value: String(m.userId) })),
])

/** AI가 추정한 이름 → 멤버 매칭 (공백 제거, 부분 일치 허용). ActionPointCard.vue 와 동일 로직. */
function matchAssignee(hint) {
  if (!hint) return null
  const h = String(hint).replace(/\s/g, '')
  return (
    props.members.find((m) => m.name.replace(/\s/g, '') === h) ||
    props.members.find((m) => {
      const n = m.name.replace(/\s/g, '')
      return n.includes(h) || h.includes(n)
    }) ||
    null
  )
}

function startEdit() {
  draft.overview = props.briefing.overview || ''
  draft.highlights = [...(props.briefing.highlights || [])]
  draft.decisions = [...(props.briefing.decisions || [])]
  draft.actionPoints = (props.briefing.actionPoints || []).map((ap) => {
    const match = matchAssignee(ap.assigneeHint)
    return {
      title: ap.title || '',
      assigneeId: match ? String(match.userId) : '',
      dueDate: ap.dueDate ? ap.dueDate.slice(0, 16) : '',
      priority: ap.priority || 'MEDIUM',
    }
  })
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

function addHighlight() {
  draft.highlights.push('')
}
function removeHighlight(i) {
  draft.highlights.splice(i, 1)
}
function addDecision() {
  draft.decisions.push('')
}
function removeDecision(i) {
  draft.decisions.splice(i, 1)
}
function addActionPoint() {
  draft.actionPoints.push({ title: '', assigneeId: '', dueDate: '', priority: 'MEDIUM' })
}
function removeActionPoint(i) {
  draft.actionPoints.splice(i, 1)
}

async function save() {
  saving.value = true
  try {
    const payload = {
      overview: draft.overview.trim(),
      highlights: draft.highlights.map((h) => h.trim()).filter(Boolean),
      decisions: draft.decisions.map((d) => d.trim()).filter(Boolean),
      actionPoints: draft.actionPoints
        .filter((ap) => ap.title.trim())
        .map((ap) => ({
          title: ap.title.trim(),
          assigneeHint: ap.assigneeId
            ? (props.members.find((m) => String(m.userId) === ap.assigneeId)?.name ?? null)
            : null,
          dueDate: ap.dueDate ? `${ap.dueDate}:00` : null,
          priority: ap.priority,
        })),
    }
    await store.updateSummary(props.meetingId, payload)
    editing.value = false
    toast().success('브리핑을 수정했습니다.')
  } catch (e) {
    toast().error(e.normalizedMessage || '저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="briefing">
    <div class="briefing__toolbar">
      <BaseButton v-if="!editing" variant="ghost" size="sm" @click="startEdit">수정</BaseButton>
      <template v-else>
        <BaseButton variant="ghost" size="sm" :disabled="saving" @click="cancelEdit">
          취소
        </BaseButton>
        <BaseButton variant="primary" size="sm" :disabled="saving" @click="save">
          {{ saving ? '저장 중…' : '저장' }}
        </BaseButton>
      </template>
    </div>

    <template v-if="!editing">
      <section class="briefing__block">
        <h3 class="briefing__h">
          한눈에 보기
          <span class="briefing__src">{{
            briefing.source === 'AUDIO' ? '🎙 음성' : '📝 텍스트'
          }}</span>
        </h3>
        <p class="briefing__overview">{{ briefing.overview || '요약 없음' }}</p>
        <ul v-if="briefing.highlights?.length" class="bullets">
          <li v-for="(h, i) in briefing.highlights" :key="i">{{ h }}</li>
        </ul>
        <p v-if="briefing.provider === 'mock'" class="briefing__note">
          규칙 기반 요약입니다. 정교한 요약·정리는 <code>AI_PROVIDER=openai</code> 설정 시 제공됩니다.
        </p>
      </section>

      <section v-if="briefing.decisions.length" class="briefing__block">
        <h3 class="briefing__h">📌 결정사항</h3>
        <ul class="bullets bullets--accent">
          <li v-for="(d, i) in briefing.decisions" :key="i">{{ d }}</li>
        </ul>
      </section>

      <section class="briefing__block">
        <h3 class="briefing__h">
          ⚡ Action Point <span class="briefing__count">{{ briefing.actionPoints.length }}</span>
        </h3>
        <p v-if="!briefing.actionPoints.length" class="briefing__empty">
          추출된 Action Point가 없습니다.
        </p>
        <div v-else class="ap-list">
          <ActionPointCard
            v-for="(ap, i) in briefing.actionPoints"
            :key="i"
            :action-point="ap"
            :members="members"
            :registered="registeredIndexes.includes(i)"
            @register="(payload) => $emit('register', i, payload)"
          />
        </div>
      </section>
    </template>

    <template v-else>
      <section class="briefing__block">
        <h3 class="briefing__h">한눈에 보기</h3>
        <textarea
          v-model="draft.overview"
          class="briefing__edit-textarea"
          rows="2"
          placeholder="회의 요약 한 문장"
        />
      </section>

      <section class="briefing__block">
        <h3 class="briefing__h">💡 하이라이트</h3>
        <div v-for="(h, i) in draft.highlights" :key="i" class="briefing__edit-row">
          <input v-model="draft.highlights[i]" type="text" class="briefing__edit-input" />
          <button type="button" class="briefing__edit-remove" aria-label="삭제" @click="removeHighlight(i)">
            ×
          </button>
        </div>
        <BaseButton variant="ghost" size="sm" @click="addHighlight">+ 항목 추가</BaseButton>
      </section>

      <section class="briefing__block">
        <h3 class="briefing__h">📌 결정사항</h3>
        <div v-for="(d, i) in draft.decisions" :key="i" class="briefing__edit-row">
          <input v-model="draft.decisions[i]" type="text" class="briefing__edit-input" />
          <button type="button" class="briefing__edit-remove" aria-label="삭제" @click="removeDecision(i)">
            ×
          </button>
        </div>
        <BaseButton variant="ghost" size="sm" @click="addDecision">+ 항목 추가</BaseButton>
      </section>

      <section class="briefing__block">
        <h3 class="briefing__h">⚡ Action Point</h3>
        <div v-for="(ap, i) in draft.actionPoints" :key="i" class="briefing__ap-edit">
          <BaseInput v-model="ap.title" label="업무명" />
          <div class="ap__row">
            <BaseSelect v-model="ap.assigneeId" label="담당자" :options="assigneeOptions" />
            <BaseDatePicker v-model="ap.dueDate" label="기한" placeholder="기한 선택" with-time />
            <BaseSelect v-model="ap.priority" label="우선순위" :options="PRIORITY_OPTIONS" />
          </div>
          <button type="button" class="briefing__ap-remove" @click="removeActionPoint(i)">
            이 항목 삭제
          </button>
        </div>
        <BaseButton variant="ghost" size="sm" @click="addActionPoint">
          + Action Point 추가
        </BaseButton>
      </section>
    </template>
  </div>
</template>

<style scoped>
.briefing {
  display: flex;
  flex-direction: column;
  gap: var(--sp-5);
}
.briefing__toolbar {
  display: flex;
  justify-content: flex-end;
  gap: var(--sp-2);
}
.briefing__h {
  font-size: var(--fs-md);
  margin-bottom: var(--sp-3);
}
.briefing__count {
  margin-left: 4px;
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.briefing__h {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
}
.briefing__src {
  font-size: var(--fs-xs);
  font-weight: 400;
  color: var(--c-text-muted);
}
.briefing__overview {
  font-size: var(--fs-md);
  font-weight: 600;
  line-height: 1.6;
  color: var(--c-text);
  margin-bottom: var(--sp-3);
}
.briefing__empty {
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.briefing__note {
  margin-top: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.briefing__note code {
  padding: 1px 5px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  font-size: 11px;
}
.bullets {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  font-size: var(--fs-sm);
  line-height: 1.6;
}
.bullets li {
  padding-left: var(--sp-4);
  position: relative;
}
.bullets li::before {
  content: '•';
  position: absolute;
  left: var(--sp-2);
  color: var(--c-text-muted);
}
.bullets--accent li::before {
  color: var(--c-accent);
}
.ap-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}

/* ---- 편집 모드 ---- */
.briefing__edit-textarea {
  width: 100%;
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  resize: vertical;
  font-family: inherit;
  font-size: var(--fs-sm);
  line-height: 1.6;
}
.briefing__edit-textarea:focus {
  outline: none;
  border-color: var(--c-primary);
}
.briefing__edit-row {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  margin-bottom: var(--sp-2);
}
.briefing__edit-input {
  flex: 1;
  height: 36px;
  padding: 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  font-size: var(--fs-sm);
}
.briefing__edit-input:focus {
  outline: none;
  border-color: var(--c-primary);
}
.briefing__edit-remove {
  display: grid;
  place-items: center;
  width: 24px;
  height: 24px;
  flex: none;
  border-radius: var(--r-full);
  color: var(--c-text-muted);
  font-size: 16px;
  line-height: 1;
}
.briefing__edit-remove:hover {
  background: var(--c-surface-alt);
  color: var(--c-danger);
}
.briefing__ap-edit {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  padding: var(--sp-4);
  margin-bottom: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
}
.briefing__ap-edit .ap__row {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: var(--sp-2);
}
.briefing__ap-remove {
  align-self: flex-end;
  font-size: var(--fs-xs);
  color: var(--c-danger);
}
.briefing__ap-remove:hover {
  text-decoration: underline;
}

@media (max-width: 1080px) {
  .briefing__ap-edit .ap__row {
    grid-template-columns: 1fr;
  }
}
</style>
