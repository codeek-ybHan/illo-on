<script setup>
import { ref, computed, watch } from 'vue'
import { fetchMyMeetings } from '@/api/board'
import { fetchMyTasks } from '@/api/task'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import { toISODate, parseISODate, formatDateShort } from '@/utils/date'

const WEEKDAYS = ['월', '화', '수', '목', '금', '토', '일']

const today = new Date()
const viewYear = ref(today.getFullYear())
const viewMonth = ref(today.getMonth()) // 0-11
const selected = ref(toISODate(today))
const loading = ref(false)

const meetings = ref([])
const tasks = ref([])

/** iso 'YYYY-MM-DD' → { meetings:[], tasks:[] } */
const eventsByDay = computed(() => {
  const map = {}
  const add = (iso, key, item) => {
    if (!iso) return
    map[iso] ??= { meetings: [], tasks: [] }
    map[iso][key].push(item)
  }
  meetings.value.forEach((m) => m.meetingAt && add(m.meetingAt.slice(0, 10), 'meetings', m))
  tasks.value.forEach((t) => t.dueDate && add(t.dueDate.slice(0, 10), 'tasks', t))
  return map
})

const grid = computed(() => {
  const first = new Date(viewYear.value, viewMonth.value, 1)
  const offset = (first.getDay() + 6) % 7
  const start = new Date(viewYear.value, viewMonth.value, 1 - offset)
  const todayIso = toISODate(today)
  return Array.from({ length: 42 }, (_, i) => {
    const d = new Date(start.getFullYear(), start.getMonth(), start.getDate() + i)
    const iso = toISODate(d)
    const ev = eventsByDay.value[iso] || { meetings: [], tasks: [] }
    return {
      iso,
      day: d.getDate(),
      inMonth: d.getMonth() === viewMonth.value,
      isToday: iso === todayIso,
      isSelected: iso === selected.value,
      meetingCount: ev.meetings.length,
      taskCount: ev.tasks.length,
    }
  })
})

const selectedEvents = computed(
  () => eventsByDay.value[selected.value] || { meetings: [], tasks: [] },
)

async function load() {
  loading.value = true
  try {
    const from = toISODate(new Date(viewYear.value, viewMonth.value, 1))
    const to = toISODate(new Date(viewYear.value, viewMonth.value + 1, 0))
    const [ms, ts] = await Promise.all([fetchMyMeetings(from, to), fetchMyTasks()])
    meetings.value = ms
    tasks.value = ts
  } finally {
    loading.value = false
  }
}
watch([viewYear, viewMonth], load, { immediate: true })

function shiftMonth(delta) {
  const m = viewMonth.value + delta
  viewYear.value += Math.floor(m / 12)
  viewMonth.value = ((m % 12) + 12) % 12
}
function goToday() {
  viewYear.value = today.getFullYear()
  viewMonth.value = today.getMonth()
  selected.value = toISODate(today)
}
function timeOf(iso) {
  const t = (iso || '').slice(11, 16)
  return /^\d{2}:\d{2}$/.test(t) ? t : '—'
}
</script>

<template>
  <PagePlaceholder title="캘린더" subtitle="회의 일정과 Task 마감일을 한 달 단위로 확인하세요.">
    <template #actions>
      <BaseButton variant="ghost" size="sm" @click="goToday">오늘</BaseButton>
    </template>

    <div class="cal-grid">
      <BaseCard>
        <template #header>
          <div class="cal-nav">
            <button type="button" aria-label="이전 달" @click="shiftMonth(-1)">
              <AppIcon name="chevronLeft" :size="16" />
            </button>
            <span>{{ viewYear }}년 {{ viewMonth + 1 }}월</span>
            <button type="button" aria-label="다음 달" @click="shiftMonth(1)">
              <AppIcon name="chevronRight" :size="16" />
            </button>
          </div>
        </template>

        <div class="cal" :class="{ 'is-loading': loading }">
          <span v-for="w in WEEKDAYS" :key="w" class="cal__wd">{{ w }}</span>
          <button
            v-for="c in grid"
            :key="c.iso"
            type="button"
            class="cal__day"
            :class="{
              'is-muted': !c.inMonth,
              'is-today': c.isToday,
              'is-selected': c.isSelected,
            }"
            @click="selected = c.iso"
          >
            <span class="cal__num">{{ c.day }}</span>
            <span class="cal__dots">
              <span v-if="c.meetingCount" class="dot dot--meeting" />
              <span v-if="c.taskCount" class="dot dot--task" />
            </span>
          </button>
        </div>
        <div class="cal__legend">
          <span><span class="dot dot--meeting" /> 회의</span>
          <span><span class="dot dot--task" /> Task 마감</span>
        </div>
      </BaseCard>

      <BaseCard>
        <template #header>{{ formatDateShort(parseISODate(selected)) }}</template>
        <ul v-if="selectedEvents.meetings.length || selectedEvents.tasks.length" class="agenda">
          <li v-for="m in selectedEvents.meetings" :key="'m' + m.meetingId" class="agenda__item">
            <span class="agenda__time">{{ timeOf(m.meetingAt) }}</span>
            <span class="agenda__icon agenda__icon--meeting"
              ><AppIcon name="meeting" :size="13"
            /></span>
            <RouterLink
              :to="{ name: 'meeting-detail', params: { id: m.meetingId } }"
              class="agenda__title"
            >
              {{ m.title }}
              <span class="agenda__sub">{{ m.projectName }}</span>
            </RouterLink>
          </li>
          <li v-for="t in selectedEvents.tasks" :key="'t' + t.taskId" class="agenda__item">
            <span class="agenda__time">{{ timeOf(t.dueDate) }}</span>
            <span class="agenda__icon agenda__icon--task"
              ><AppIcon name="project" :size="13"
            /></span>
            <RouterLink
              :to="{ name: 'task-detail', params: { id: t.taskId } }"
              class="agenda__title"
            >
              {{ t.title }}
              <span class="agenda__sub">{{ t.projectName }} 마감</span>
            </RouterLink>
            <StatusBadge :status="t.status" />
          </li>
        </ul>
        <p v-else class="empty-hint">이 날 예정된 일정이 없습니다.</p>
      </BaseCard>
    </div>
  </PagePlaceholder>
</template>

<style scoped>
.cal-grid {
  display: grid;
  grid-template-columns: 1.7fr 1fr;
  gap: var(--sp-4);
  align-items: start;
}
.cal-nav {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  font-size: var(--fs-md);
}
.cal-nav button {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  border-radius: var(--r-sm);
  color: var(--c-text-2);
}
.cal-nav button:hover {
  background: var(--c-surface-alt);
}
.cal {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
  transition: opacity 0.15s ease;
}
.cal.is-loading {
  opacity: 0.5;
}
.cal__wd {
  text-align: center;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  padding-bottom: var(--sp-1);
}
.cal__day {
  aspect-ratio: 1 / 0.92;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  border: 1px solid var(--c-border);
  border-radius: var(--r-sm);
  padding: 6px;
  background: var(--c-surface);
}
.cal__day:hover {
  border-color: var(--c-border-strong);
}
.cal__day.is-muted {
  background: var(--c-surface-alt);
  opacity: 0.55;
}
.cal__day.is-today .cal__num {
  color: var(--c-accent);
  font-weight: 700;
}
.cal__day.is-selected {
  border-color: var(--c-primary);
  outline: 1px solid var(--c-primary);
}
.cal__num {
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.cal__dots {
  display: flex;
  gap: 3px;
}
.dot {
  width: 6px;
  height: 6px;
  border-radius: var(--r-full);
  display: inline-block;
}
.dot--meeting {
  background: var(--c-peach-ink);
}
.dot--task {
  background: var(--c-lavender-ink);
}
.cal__legend {
  display: flex;
  gap: var(--sp-4);
  margin-top: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.cal__legend span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.agenda {
  display: flex;
  flex-direction: column;
  gap: var(--sp-1);
}
.agenda__item {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  padding: var(--sp-2) 0;
  border-bottom: 1px solid var(--c-border);
}
.agenda__item:last-child {
  border-bottom: none;
}
.agenda__time {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  min-width: 40px;
}
.agenda__icon {
  display: grid;
  place-items: center;
  width: 24px;
  height: 24px;
  border-radius: var(--r-sm);
  flex-shrink: 0;
}
.agenda__icon--meeting {
  background: var(--c-peach);
  color: var(--c-peach-ink);
}
.agenda__icon--task {
  background: var(--c-lavender);
  color: var(--c-lavender-ink);
}
.agenda__title {
  flex: 1;
  display: flex;
  flex-direction: column;
  font-size: var(--fs-sm);
}
.agenda__title:hover {
  text-decoration: underline;
}
.agenda__sub {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}

@media (max-width: 1080px) {
  .cal-grid {
    grid-template-columns: 1fr;
  }
}
</style>
