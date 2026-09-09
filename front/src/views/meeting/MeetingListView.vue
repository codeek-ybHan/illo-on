<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchProjects } from '@/api/project'
import { fetchMeetings, createMeeting } from '@/api/meeting'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import MeetingCard from '@/components/meeting/MeetingCard.vue'
import MeetingForm from '@/components/meeting/MeetingForm.vue'

const loading = ref(true)
const error = ref('')
const projects = ref([])
const meetings = ref([])
const showCreate = ref(false)

const FILTERS = [
  { label: '전체', value: 'ALL' },
  { label: 'AI 분석됨', value: 'ANALYZED' },
  { label: '분석 대기', value: 'PENDING' },
]
const filter = ref('ALL')

const visible = computed(() => {
  if (filter.value === 'ANALYZED') return meetings.value.filter((m) => m.hasSummary)
  if (filter.value === 'PENDING') return meetings.value.filter((m) => !m.hasSummary && m.hasContent)
  return meetings.value
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    projects.value = await fetchProjects()
    const lists = await Promise.all(projects.value.map((p) => fetchMeetings(p.projectId)))
    meetings.value = lists
      .flat()
      .sort((a, b) => new Date(b.meetingAt || b.createdAt) - new Date(a.meetingAt || a.createdAt))
  } catch (e) {
    error.value = e.normalizedMessage || '회의를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

onMounted(load)

async function handleCreate(payload, projectId) {
  const created = await createMeeting(projectId, payload)
  await load()
  return created
}
</script>

<template>
  <PagePlaceholder title="회의" subtitle="프로젝트에서 진행된 회의와 AI 분석 상태를 확인하세요.">
    <template #actions>
      <BaseButton
        variant="primary"
        size="sm"
        :disabled="!projects.length"
        @click="showCreate = true"
      >
        <template #icon><AppIcon name="plus" :size="16" /></template>
        회의 생성
      </BaseButton>
    </template>

    <p v-if="error" class="mv-error">{{ error }}</p>

    <div class="filter-bar">
      <button
        v-for="f in FILTERS"
        :key="f.value"
        class="filter"
        :class="{ 'is-active': filter === f.value }"
        @click="filter = f.value"
      >
        {{ f.label }}
      </button>
    </div>

    <div v-if="loading" class="mv-list">
      <div v-for="n in 3" :key="n" class="skeleton" style="height: 60px" />
    </div>

    <BaseCard v-else-if="!projects.length" padding="lg">
      <p class="empty-hint">참여 중인 프로젝트가 없습니다. 먼저 프로젝트를 만드세요.</p>
    </BaseCard>

    <BaseCard v-else-if="!visible.length" padding="lg">
      <p class="empty-hint">
        {{ meetings.length ? '조건에 맞는 회의가 없습니다.' : '아직 회의가 없습니다.' }}
      </p>
    </BaseCard>

    <div v-else class="mv-list">
      <MeetingCard v-for="m in visible" :key="m.meetingId" :meeting="m" show-project />
    </div>
  </PagePlaceholder>

  <MeetingForm v-model:open="showCreate" :projects="projects" :submit-fn="handleCreate" />
</template>

<style scoped>
.mv-error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.filter-bar {
  display: flex;
  gap: var(--sp-2);
}
.filter {
  padding: 6px 14px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.filter.is-active {
  background: var(--c-primary);
  border-color: var(--c-primary);
  color: var(--c-primary-contrast);
}
.mv-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
</style>
