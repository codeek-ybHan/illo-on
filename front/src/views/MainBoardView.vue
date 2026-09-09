<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTaskStore } from '@/stores/task'
import { fetchBoard } from '@/api/board'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import StatCard from '@/components/common/StatCard.vue'
import SummaryChip from '@/components/common/SummaryChip.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import TaskList from '@/components/task/TaskList.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import { formatDate, formatDue, daysUntil } from '@/utils/date'

const router = useRouter()
const auth = useAuthStore()
const taskStore = useTaskStore()
const { myTasks, loading: tasksLoading } = storeToRefs(taskStore)

const board = ref(null)
const boardLoading = ref(true)

const greeting = computed(() => `${auth.user?.name || ''}님, 좋은 아침이에요 👋`.trim())
const openCount = computed(() => myTasks.value.filter((t) => t.status !== 'DONE').length)

// 요약 칩으로 아래 Task 목록을 좁혀 보기
const tasksSectionEl = ref(null)
const scheduleSectionEl = ref(null)
const taskScope = ref('open') // 'open' | 'today' | 'soon'

const SCOPE_LABEL = { open: '미완료', today: '오늘 마감', soon: '마감 임박(3일)' }

function isSameDay(dt) {
  if (!dt) return false
  const d = new Date(dt)
  const n = new Date()
  return d.toDateString() === n.toDateString()
}
function withinDays(dt, n) {
  if (!dt) return false
  const d = new Date(dt)
  d.setHours(0, 0, 0, 0)
  const from = new Date()
  from.setHours(0, 0, 0, 0)
  const to = new Date(from)
  to.setDate(to.getDate() + n)
  return d >= from && d <= to
}

const scopedTasks = computed(() => {
  const open = myTasks.value.filter((t) => t.status !== 'DONE')
  if (taskScope.value === 'today') return open.filter((t) => isSameDay(t.dueDate))
  if (taskScope.value === 'soon') return open.filter((t) => withinDays(t.dueDate, 3))
  return open
})

async function focusTasks(scope) {
  taskScope.value = scope
  await nextTick()
  tasksSectionEl.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}
async function focusSchedule() {
  await nextTick()
  scheduleSectionEl.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const avgProgress = computed(() => {
  const ps = board.value?.projects ?? []
  if (!ps.length) return 0
  return Math.round(ps.reduce((s, p) => s + p.progress, 0) / ps.length)
})
const primarySprint = computed(() => board.value?.activeSprints?.[0] ?? null)
const sprintDaysLeft = computed(() =>
  primarySprint.value?.endDate ? daysUntil(primarySprint.value.endDate) : null,
)
const todayDoneRatio = computed(() => {
  const t = board.value
  if (!t) return { done: 0, total: 0, pct: 0 }
  const total = t.todayTaskCount
  const done = myTasks.value.filter(
    (x) =>
      x.status === 'DONE' &&
      x.dueDate &&
      new Date(x.dueDate).toDateString() === new Date().toDateString(),
  ).length
  return {
    done,
    total: total + done,
    pct: total + done === 0 ? 0 : Math.round((done / (total + done)) * 100),
  }
})

const summaries = computed(() => [
  {
    tone: 'lavender',
    icon: 'project',
    label: '내 Task',
    count: board.value?.openTaskCount ?? openCount.value,
    action: () => focusTasks('open'),
  },
  {
    tone: 'peach',
    icon: 'meeting',
    label: '예정 회의',
    count: board.value?.upcomingMeetingCount ?? 0,
    action: () => router.push({ name: 'meetings' }),
  },
  {
    tone: 'cream',
    icon: 'calendar',
    label: '오늘 마감',
    count: board.value?.todayTaskCount ?? 0,
    action: () => focusTasks('today'),
  },
  {
    tone: 'mint',
    icon: 'sprint',
    label: '마감 임박',
    count: board.value?.dueSoonCount ?? 0,
    action: () => focusTasks('soon'),
  },
])

onMounted(async () => {
  taskStore.fetchMyTasks()
  try {
    board.value = await fetchBoard()
  } finally {
    boardLoading.value = false
  }
})

async function handleStatusChange(task, status) {
  try {
    await taskStore.changeStatus(task, status)
    board.value = await fetchBoard()
  } catch {
    /* changeStatus 가 토스트 처리 */
  }
}
</script>

<template>
  <PagePlaceholder :title="greeting" subtitle="오늘의 업무와 일정을 한눈에 확인하세요.">
    <!-- 스탯 카드 3열 -->
    <div class="grid-3">
      <StatCard
        icon="project"
        title="내 프로젝트"
        :action-label="`${board?.projects?.length ?? 0}개`"
        metric-label="평균 진행률"
        :metric-value="`${avgProgress}%`"
        :progress="avgProgress"
        progress-color="var(--c-lavender-ink)"
        clickable
        @click="router.push({ name: 'projects' })"
      />
      <StatCard
        icon="sprint"
        title="현재 Sprint"
        :action-label="primarySprint ? primarySprint.projectName : '없음'"
        :metric-label="primarySprint?.name || '진행 중 Sprint 없음'"
        :metric-value="sprintDaysLeft != null ? `D-${Math.max(sprintDaysLeft, 0)}` : '—'"
        :progress="primarySprint?.progress ?? 0"
        progress-color="var(--c-accent)"
        clickable
        @click="router.push({ name: 'sprints' })"
      />
      <StatCard
        icon="calendar"
        title="오늘 할 일"
        :action-label="`회의 ${board?.todayMeetings?.length ?? 0}`"
        metric-label="완료 / 전체"
        :metric-value="`${todayDoneRatio.done} / ${todayDoneRatio.total}`"
        :progress="todayDoneRatio.pct"
        progress-color="var(--c-success)"
        clickable
        @click="focusSchedule()"
      />
    </div>

    <!-- 요약 칩 -->
    <section>
      <h2 class="section-title">한눈에 보기</h2>
      <div class="grid-4">
        <SummaryChip
          v-for="s in summaries"
          :key="s.label"
          :tone="s.tone"
          :icon="s.icon"
          :label="s.label"
          :count="s.count"
          @click="s.action()"
        />
      </div>
    </section>

    <!-- 내가 해야 할 Task -->
    <section ref="tasksSectionEl">
      <h2 class="section-title">
        내가 해야 할 Task
        <span class="section-title__count">{{ scopedTasks.length }}</span>
        <button
          v-if="taskScope !== 'open'"
          type="button"
          class="scope-pill"
          @click="taskScope = 'open'"
        >
          {{ SCOPE_LABEL[taskScope] }}
          <AppIcon name="plus" :size="12" class="scope-pill__x" />
        </button>
      </h2>
      <TaskList
        :tasks="scopedTasks"
        :loading="tasksLoading"
        show-project
        @change-status="handleStatusChange"
      />
    </section>

    <!-- 내 프로젝트 -->
    <section>
      <h2 class="section-title">내 프로젝트</h2>
      <div v-if="boardLoading" class="grid-3">
        <div v-for="n in 3" :key="n" class="skeleton" style="height: 90px" />
      </div>
      <p v-else-if="!board?.projects?.length" class="empty-hint">참여 중인 프로젝트가 없습니다.</p>
      <div v-else class="proj-grid">
        <RouterLink
          v-for="p in board.projects"
          :key="p.projectId"
          class="proj"
          :to="{ name: 'project-detail', params: { id: p.projectId } }"
        >
          <div class="proj__top">
            <span class="proj__name">{{ p.name }}</span>
            <StatusBadge :status="p.status" />
          </div>
          <ProgressBar :value="p.progress" color="var(--c-lavender-ink)" show-label />
          <span class="proj__meta">Task {{ p.taskCount }} · 멤버 {{ p.memberCount }}</span>
        </RouterLink>
      </div>
    </section>

    <!-- 오늘의 일정 -->
    <section ref="scheduleSectionEl">
      <h2 class="section-title">
        오늘의 일정 <span class="section-title__count">{{ formatDate(new Date()) }}</span>
      </h2>
      <div v-if="boardLoading" class="skeleton" style="height: 100px" />
      <div v-else class="today">
        <BaseCard v-if="board.todayMeetings.length || board.todayTasks.length" padding="sm">
          <ul class="today__list">
            <li v-for="m in board.todayMeetings" :key="'m' + m.meetingId" class="today__item">
              <span class="today__icon today__icon--meeting"
                ><AppIcon name="meeting" :size="14"
              /></span>
              <RouterLink
                :to="{ name: 'meeting-detail', params: { id: m.meetingId } }"
                class="today__title"
              >
                {{ m.title }}
              </RouterLink>
              <span class="today__time">{{ m.meetingAt ? formatDue(m.meetingAt) : '' }}</span>
            </li>
            <li v-for="t in board.todayTasks" :key="'t' + t.taskId" class="today__item">
              <span class="today__icon today__icon--task"
                ><AppIcon name="project" :size="14"
              /></span>
              <RouterLink
                :to="{ name: 'task-detail', params: { id: t.taskId } }"
                class="today__title"
              >
                {{ t.title }}
              </RouterLink>
              <StatusBadge :status="t.priority" />
            </li>
          </ul>
        </BaseCard>
        <p v-else class="empty-hint">오늘 예정된 회의·마감이 없습니다.</p>
      </div>
    </section>
  </PagePlaceholder>
</template>

<style scoped>
.grid-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--sp-4);
}
.grid-4 {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--sp-3);
}
.section-title {
  font-size: var(--fs-lg);
  margin-bottom: var(--sp-3);
}
.section-title__count {
  margin-left: var(--sp-2);
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.scope-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-left: var(--sp-2);
  padding: 2px 6px 2px 10px;
  border-radius: var(--r-full);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
  font-size: var(--fs-xs);
  vertical-align: middle;
}
.scope-pill__x {
  transform: rotate(45deg);
}
.proj-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--sp-3);
}
.proj {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  padding: var(--sp-4);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
}
.proj:hover {
  border-color: var(--c-border-strong);
}
.proj__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.proj__name {
  font-weight: 600;
}
.proj__meta {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.today__list {
  display: flex;
  flex-direction: column;
}
.today__item {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-2) 0;
  border-bottom: 1px solid var(--c-border);
}
.today__item:last-child {
  border-bottom: none;
}
.today__icon {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  border-radius: var(--r-sm);
  flex-shrink: 0;
}
.today__icon--meeting {
  background: var(--c-peach);
  color: var(--c-peach-ink);
}
.today__icon--task {
  background: var(--c-lavender);
  color: var(--c-lavender-ink);
}
.today__title {
  flex: 1;
  font-size: var(--fs-sm);
}
.today__title:hover {
  text-decoration: underline;
}
.today__time {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}

@media (max-width: 1080px) {
  .grid-3,
  .grid-4 {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
