<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectStore } from '@/stores/project'
import { useTaskStore } from '@/stores/task'
import { useMeetingStore } from '@/stores/meeting'
import { fetchSprints } from '@/api/sprint'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { formatDate, formatDateTime } from '@/utils/date'

const route = useRoute()
const projectStore = useProjectStore()
const taskStore = useTaskStore()
const meetingStore = useMeetingStore()

const loading = ref(true)
const loadError = ref('')
const sprints = ref([])

function printPage() {
  window.print()
}

function closePage() {
  window.close()
}

function meetingStatusLabel(m) {
  if (m.hasSummary) return 'AI 분석됨'
  if (m.hasContent) return '분석 대기'
  return '내용 없음'
}

onMounted(async () => {
  const projectId = route.params.id
  try {
    const [, , sprintList] = await Promise.all([
      projectStore.fetchProject(projectId),
      projectStore.fetchMembers(projectId),
      fetchSprints(projectId),
      taskStore.fetchTasks(projectId),
      meetingStore.fetchMeetings(projectId),
    ])
    sprints.value = sprintList
  } catch (e) {
    loadError.value = e.normalizedMessage || '내보낼 데이터를 불러오지 못했습니다.'
    return
  } finally {
    loading.value = false
  }
  await nextTick()
  window.print()
})
</script>

<template>
  <div class="export-page">
    <div v-if="loading" class="export-status">불러오는 중…</div>
    <div v-else-if="loadError" class="export-status export-status--error">{{ loadError }}</div>

    <template v-else>
      <div class="export-toolbar">
        <button type="button" class="export-toolbar__btn" @click="printPage">인쇄</button>
        <button type="button" class="export-toolbar__btn" @click="closePage">닫기</button>
      </div>

      <header class="export-header">
        <div class="export-brand">
          <span class="export-brand__name">일로ON</span>
          <span class="export-brand__tagline">
            회의에서 나온 일을, 메신저에서 결정된 일을, 일로ON.
          </span>
        </div>
        <h1 class="export-title">{{ projectStore.current?.name }}</h1>
        <div class="export-meta">
          <StatusBadge v-if="projectStore.current?.status" :status="projectStore.current.status" />
          <span>내보낸 날짜 {{ formatDate(new Date()) }}</span>
        </div>
      </header>

      <section class="export-section">
        <h2 class="export-section__title">개요</h2>
        <p class="export-overview__dates">
          {{ formatDate(projectStore.current?.startDate) }} ~
          {{ formatDate(projectStore.current?.endDate) }}
        </p>
        <p class="export-overview__desc">
          {{ projectStore.current?.description || '설명이 없습니다.' }}
        </p>
      </section>

      <section class="export-section">
        <h2 class="export-section__title">멤버 ({{ projectStore.members.length }})</h2>
        <table class="export-table">
          <thead>
            <tr>
              <th>이름</th>
              <th>직급/역할</th>
              <th>이메일</th>
              <th>권한</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in projectStore.members" :key="m.userId">
              <td>{{ m.name }}</td>
              <td>{{ m.jobTitle || '-' }}</td>
              <td>{{ m.email }}</td>
              <td>{{ m.role === 'ADMIN' ? '관리자' : '멤버' }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section class="export-section">
        <h2 class="export-section__title">Sprint ({{ sprints.length }})</h2>
        <p v-if="!sprints.length" class="export-empty">Sprint 없음</p>
        <table v-else class="export-table">
          <thead>
            <tr>
              <th>이름</th>
              <th>상태</th>
              <th>기간</th>
              <th>진행률</th>
              <th>완료/전체</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in sprints" :key="s.sprintId">
              <td>{{ s.name }}</td>
              <td><StatusBadge :status="s.status" /></td>
              <td>{{ formatDate(s.startDate) }} ~ {{ formatDate(s.endDate) }}</td>
              <td>{{ Math.round(s.progress) }}%</td>
              <td>{{ s.doneCount }} / {{ s.taskCount }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section class="export-section">
        <h2 class="export-section__title">Task ({{ taskStore.tasks.length }})</h2>
        <p v-if="!taskStore.tasks.length" class="export-empty">Task 없음</p>
        <table v-else class="export-table">
          <thead>
            <tr>
              <th>제목</th>
              <th>상태</th>
              <th>우선순위</th>
              <th>담당자</th>
              <th>마감일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="t in taskStore.tasks" :key="t.taskId">
              <td>{{ t.title }}</td>
              <td><StatusBadge :status="t.status" /></td>
              <td><StatusBadge :status="t.priority" /></td>
              <td>{{ t.assigneeName || '미지정' }}</td>
              <td>{{ t.dueDate ? formatDate(t.dueDate) : '-' }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section class="export-section">
        <h2 class="export-section__title">회의 ({{ meetingStore.meetings.length }})</h2>
        <p v-if="!meetingStore.meetings.length" class="export-empty">회의 없음</p>
        <table v-else class="export-table">
          <thead>
            <tr>
              <th>제목</th>
              <th>일시</th>
              <th>참석자</th>
              <th>AI 분석 상태</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in meetingStore.meetings" :key="m.meetingId">
              <td>{{ m.title }}</td>
              <td>{{ m.meetingAt ? formatDateTime(m.meetingAt) : '일시 미정' }}</td>
              <td>{{ m.attendeeCount }}</td>
              <td>{{ meetingStatusLabel(m) }}</td>
            </tr>
          </tbody>
        </table>
      </section>
    </template>
  </div>
</template>

<style scoped>
.export-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 24px 80px;
  color: #1e2124;
  background: #fff;
  font-size: 14px;
  line-height: 1.6;
}
.export-status {
  padding: 80px 0;
  text-align: center;
  color: #5b6470;
}
.export-status--error {
  color: #c0392b;
}
.export-toolbar {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-bottom: 24px;
}
.export-toolbar__btn {
  padding: 8px 16px;
  border: 1px solid #d0d3d8;
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}
.export-toolbar__btn:hover {
  background: #f5f5f6;
}
.export-header {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 2px solid #1e2124;
}
.export-brand {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 16px;
}
.export-brand__name {
  font-size: 18px;
  font-weight: 800;
}
.export-brand__tagline {
  font-size: 12px;
  color: #5b6470;
}
.export-title {
  margin-bottom: 10px;
  font-size: 26px;
  font-weight: 700;
}
.export-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #5b6470;
}
.export-section {
  margin-bottom: 28px;
  break-inside: avoid;
}
.export-section__title {
  margin-bottom: 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e2e4e8;
  font-size: 16px;
  font-weight: 700;
}
.export-overview__dates {
  margin-bottom: 8px;
  color: #5b6470;
}
.export-empty {
  color: #9aa0a8;
  font-size: 13px;
}
.export-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.export-table th,
.export-table td {
  padding: 8px 10px;
  border-bottom: 1px solid #e2e4e8;
  text-align: left;
}
.export-table th {
  color: #5b6470;
  font-weight: 600;
}
.export-table tr {
  break-inside: avoid;
}

@media print {
  .export-toolbar {
    display: none;
  }
  .export-page {
    padding: 0;
  }
}

@page {
  size: A4;
  margin: 16mm;
}
</style>
