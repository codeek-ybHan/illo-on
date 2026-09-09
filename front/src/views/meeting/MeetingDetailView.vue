<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useMeetingStore } from '@/stores/meeting'
import { useProjectStore } from '@/stores/project'
import { createTask } from '@/api/task'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import MeetingForm from '@/components/meeting/MeetingForm.vue'
import AiBriefing from '@/components/meeting/AiBriefing.vue'
import { formatDateTime } from '@/utils/date'

const route = useRoute()
const router = useRouter()
const store = useMeetingStore()
const projectStore = useProjectStore()
const { current, briefing, error, analyzing } = storeToRefs(store)
const { members } = storeToRefs(projectStore)

const inputTab = ref('text')
const contentDraft = ref('')
const savingContent = ref(false)
const contentSaved = ref(false)
const showEdit = ref(false)
const showDelete = ref(false)
const deleting = ref(false)
const audioFile = ref(null)
const registeredIndexes = ref([])

function onAudioPick(e) {
  audioFile.value = e.target.files?.[0] ?? null
}

watch(
  () => route.params.id,
  async (id) => {
    registeredIndexes.value = []
    try {
      const m = await store.fetchMeeting(id)
      contentDraft.value = m?.content ?? ''
      if (m?.projectId) projectStore.fetchMembers(m.projectId)
      // 회의 생성 폼에서 넘어온 녹음본
      const pending = store.takePendingAudio()
      if (pending) {
        audioFile.value = pending
        inputTab.value = 'audio'
      }
    } catch {
      /* store.error */
    }
  },
  { immediate: true },
)

async function runAnalyze() {
  const useAudio = inputTab.value === 'audio' && audioFile.value
  try {
    // 텍스트 분석 전 초안 저장
    if (!useAudio && contentDraft.value.trim() !== (current.value.content ?? '')) {
      await saveContent()
    }
    await store.analyzeMeeting(route.params.id, useAudio ? audioFile.value : null)
    registeredIndexes.value = []
    if (useAudio) {
      contentDraft.value = store.current?.content ?? ''
      inputTab.value = 'text'
    }
  } catch {
    /* store.error */
  }
}

async function registerActionPoint(index, payload) {
  await createTask({
    projectId: current.value.projectId,
    meetingId: current.value.meetingId,
    ...payload,
  })
  registeredIndexes.value = [...registeredIndexes.value, index]
}

async function saveContent() {
  savingContent.value = true
  contentSaved.value = false
  try {
    await store.updateMeeting(route.params.id, {
      title: current.value.title,
      content: contentDraft.value.trim() || null,
      meetingAt: current.value.meetingAt,
      attendeeIds: current.value.attendees.map((a) => a.userId),
    })
    contentSaved.value = true
    setTimeout(() => (contentSaved.value = false), 2000)
  } finally {
    savingContent.value = false
  }
}

async function handleEdit(payload) {
  await store.updateMeeting(route.params.id, payload)
  contentDraft.value = current.value?.content ?? ''
}

async function handleDelete() {
  deleting.value = true
  try {
    const projectId = current.value?.projectId
    await store.deleteMeeting(route.params.id)
    router.replace(
      projectId ? { name: 'project-detail', params: { id: projectId } } : { name: 'meetings' },
    )
  } finally {
    deleting.value = false
  }
}
</script>

<template>
  <PagePlaceholder
    :title="current?.title || '회의 상세'"
    subtitle="회의 내용을 입력하고 AI 분석으로 Action Point를 추출하세요."
  >
    <template v-if="current" #actions>
      <BaseButton variant="ghost" size="sm" @click="showEdit = true">수정</BaseButton>
      <BaseButton variant="ghost" size="sm" @click="showDelete = true">삭제</BaseButton>
    </template>

    <p v-if="error" class="detail-error">{{ error }}</p>

    <template v-else-if="current">
      <!-- 회의 메타 -->
      <BaseCard>
        <div class="meta">
          <div class="meta__row">
            <span class="meta__item">
              <AppIcon name="calendar" :size="14" />
              {{ current.meetingAt ? formatDateTime(current.meetingAt) : '일시 미정' }}
            </span>
            <RouterLink
              class="meta__item meta__link"
              :to="{ name: 'project-detail', params: { id: current.projectId } }"
            >
              <AppIcon name="project" :size="14" /> {{ current.projectName }}
            </RouterLink>
          </div>
          <div class="attendees">
            <span v-for="a in current.attendees" :key="a.userId" class="attendee" :title="a.email">
              {{ a.name }}
            </span>
            <span v-if="!current.attendees.length" class="u-muted">참석자 미지정</span>
          </div>
        </div>
      </BaseCard>

      <!-- 회의 내용 입력 -->
      <BaseCard>
        <template #header>회의 내용</template>
        <div class="tabs">
          <button
            class="tab"
            :class="{ 'is-active': inputTab === 'text' }"
            @click="inputTab = 'text'"
          >
            텍스트 입력
          </button>
          <button
            class="tab"
            :class="{ 'is-active': inputTab === 'audio' }"
            @click="inputTab = 'audio'"
          >
            녹음본 업로드
          </button>
        </div>

        <div v-if="inputTab === 'text'" class="input-area">
          <textarea
            v-model="contentDraft"
            class="input-area__textarea"
            rows="8"
            placeholder="회의 내용을 입력하거나 메신저 대화를 붙여넣으세요."
          />
          <div class="input-area__foot">
            <span v-if="contentSaved" class="input-area__saved">저장됨</span>
            <BaseButton variant="soft" size="sm" :disabled="savingContent" @click="saveContent">
              {{ savingContent ? '저장 중…' : '내용 저장' }}
            </BaseButton>
          </div>
        </div>
        <div v-else class="upload-area">
          <label class="upload-drop">
            <input type="file" accept="audio/*,.txt,.vtt,.srt" hidden @change="onAudioPick" />
            <AppIcon name="paperclip" :size="20" />
            <span v-if="audioFile">{{ audioFile.name }}</span>
            <span v-else>녹음 파일 선택 (mp3, m4a, wav…)</span>
          </label>
          <p class="upload-hint">“AI 분석하기”를 누르면 음성 → 텍스트(STT) 변환 후 분석합니다.</p>
        </div>
      </BaseCard>

      <!-- AI 브리핑 -->
      <BaseCard>
        <template #header>
          <span class="briefing-title">
            <span class="briefing-title__icon"><AppIcon name="sparkle" :size="15" /></span>
            AI 회의 브리핑
          </span>
          <BaseButton variant="primary" size="sm" :disabled="analyzing" @click="runAnalyze">
            <template #icon><AppIcon name="sparkle" :size="15" /></template>
            {{ analyzing ? '분석 중…' : briefing ? '다시 분석' : 'AI 분석하기' }}
          </BaseButton>
        </template>

        <div v-if="analyzing" class="skeleton" style="height: 120px" />
        <AiBriefing
          v-else-if="briefing"
          :briefing="briefing"
          :members="members"
          :registered-indexes="registeredIndexes"
          @register="registerActionPoint"
        />
        <p v-else class="empty-hint">
          회의 내용을 입력하거나 녹음본을 올린 뒤 “AI 분석하기”를 누르세요.<br />
          요약 · 결정사항 · Action Point가 추출되고, Action Point를 검토해 업무로 등록할 수
          있습니다.
        </p>
      </BaseCard>
    </template>
  </PagePlaceholder>

  <MeetingForm
    v-if="current"
    v-model:open="showEdit"
    :meeting="current"
    :project-id="current.projectId"
    :submit-fn="handleEdit"
  />

  <BaseModal v-model:open="showDelete" title="회의 삭제" size="sm">
    <p>이 회의를 삭제할까요? 회의에서 만든 Task는 유지되고 연결만 끊어집니다.</p>
    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="showDelete = false">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="deleting" @click="handleDelete">
        {{ deleting ? '삭제 중…' : '삭제' }}
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.detail-error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.meta {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.meta__row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-4);
}
.meta__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.meta__link:hover {
  color: var(--c-accent);
}
.attendees {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-2);
}
.attendee {
  padding: 2px 10px;
  border-radius: var(--r-full);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
}
.tabs {
  display: flex;
  gap: var(--sp-1);
  margin-bottom: var(--sp-4);
  border-bottom: 1px solid var(--c-border);
}
.tab {
  padding: var(--sp-2) var(--sp-3);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}
.tab.is-active {
  color: var(--c-text);
  font-weight: 600;
  border-bottom-color: var(--c-primary);
}
.input-area {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.input-area__textarea {
  padding: var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}
.input-area__textarea:focus {
  outline: none;
  border-color: var(--c-primary);
}
.input-area__foot {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--sp-3);
}
.input-area__saved {
  font-size: var(--fs-xs);
  color: var(--c-success);
}
.upload-area {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.upload-drop {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--sp-2);
  min-height: 120px;
  padding: var(--sp-4);
  border: 1px dashed var(--c-border-strong);
  border-radius: var(--r-md);
  color: var(--c-text-muted);
  font-size: var(--fs-sm);
  cursor: pointer;
  word-break: break-all;
  text-align: center;
}
.upload-drop:hover {
  background: var(--c-surface-alt);
}
.upload-hint {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.briefing-title {
  display: inline-flex;
  align-items: center;
  gap: var(--sp-2);
}
.briefing-title__icon {
  display: grid;
  place-items: center;
  width: 22px;
  height: 22px;
  border-radius: var(--r-sm);
  background: var(--c-accent-soft);
  color: var(--c-accent);
}
</style>
