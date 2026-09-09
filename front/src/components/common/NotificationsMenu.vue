<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { fetchBoard } from '@/api/board'
import AppIcon from './AppIcon.vue'

const router = useRouter()
const open = ref(false)
const root = ref(null)
const board = ref(null)
const loading = ref(false)
const seen = ref(false)

async function load() {
  loading.value = true
  try {
    board.value = await fetchBoard()
  } catch {
    board.value = null
  } finally {
    loading.value = false
  }
}

const items = computed(() => {
  const b = board.value
  if (!b) return []
  const list = []
  if (b.overdueCount > 0) {
    list.push({
      key: 'overdue',
      text: `마감이 지난 업무 ${b.overdueCount}건`,
      tone: 'danger',
      to: { name: 'mainboard' },
    })
  }
  if (b.dueSoonCount > 0) {
    list.push({
      key: 'duesoon',
      text: `3일 내 마감 업무 ${b.dueSoonCount}건`,
      tone: 'accent',
      to: { name: 'mainboard' },
    })
  }
  for (const m of b.todayMeetings || []) {
    list.push({
      key: `mtg-${m.meetingId}`,
      text: `오늘 회의 · ${m.title}`,
      sub: m.meetingAt ? m.meetingAt.slice(11, 16) : '',
      to: { name: 'meeting-detail', params: { id: m.meetingId } },
    })
  }
  for (const t of b.todayTasks || []) {
    list.push({
      key: `task-${t.taskId}`,
      text: `오늘 마감 · ${t.title}`,
      to: { name: 'task-detail', params: { id: t.taskId } },
    })
  }
  return list
})

const hasAlert = computed(() => !seen.value && items.value.length > 0)

function toggle() {
  open.value = !open.value
  if (open.value) {
    seen.value = true
    if (!board.value) load()
  }
}
function go(item) {
  open.value = false
  router.push(item.to)
}

function onDocClick(e) {
  if (root.value && !root.value.contains(e.target)) open.value = false
}
function onKey(e) {
  if (e.key === 'Escape') open.value = false
}

// 여는 클릭이 같은 tick 에 바깥 클릭으로 잡혀 바로 닫히는 것을 피하려고
// open 일 때만, 다음 tick 부터 바깥 클릭을 감시한다.
watch(open, (v) => {
  if (v) {
    setTimeout(() => document.addEventListener('click', onDocClick), 0)
    document.addEventListener('keydown', onKey)
  } else {
    document.removeEventListener('click', onDocClick)
    document.removeEventListener('keydown', onKey)
  }
})

onMounted(load)
onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick)
  document.removeEventListener('keydown', onKey)
})
</script>

<template>
  <div ref="root" class="nmenu">
    <button class="nmenu__btn" type="button" aria-label="알림" @click.stop="toggle">
      <AppIcon name="bell" :size="18" />
      <span v-if="hasAlert" class="nmenu__dot" />
    </button>

    <div v-if="open" class="nmenu__pop" role="menu">
      <header class="nmenu__head">알림</header>
      <p v-if="loading" class="nmenu__empty">불러오는 중…</p>
      <p v-else-if="!items.length" class="nmenu__empty">
        새로운 알림이 없습니다. 오늘도 화이팅!
      </p>
      <ul v-else class="nmenu__list">
        <li v-for="it in items" :key="it.key">
          <button class="nmenu__item" type="button" @click="go(it)">
            <span class="nmenu__marker" :class="it.tone ? `is-${it.tone}` : ''" />
            <span class="nmenu__text">{{ it.text }}</span>
            <span v-if="it.sub" class="nmenu__sub">{{ it.sub }}</span>
          </button>
        </li>
      </ul>
      <footer v-if="board" class="nmenu__foot">
        <button type="button" @click="go({ to: { name: 'mainboard' } })">메인보드에서 보기</button>
        <span class="nmenu__spacer" />
        <button type="button" @click="load()">새로고침</button>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.nmenu {
  position: relative;
}
.nmenu__btn {
  position: relative;
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: var(--r-full);
  color: var(--c-text-2);
}
.nmenu__btn:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.nmenu__dot {
  position: absolute;
  top: 7px;
  right: 8px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--c-accent);
  border: 2px solid var(--c-surface);
}
.nmenu__pop {
  position: absolute;
  right: 0;
  top: calc(100% + 6px);
  z-index: 40;
  width: 280px;
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}
.nmenu__head {
  padding: var(--sp-3) var(--sp-4);
  font-size: var(--fs-sm);
  font-weight: 600;
  border-bottom: 1px solid var(--c-border);
}
.nmenu__empty {
  padding: var(--sp-5) var(--sp-4);
  text-align: center;
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.nmenu__list {
  max-height: 320px;
  overflow-y: auto;
  padding: var(--sp-1);
}
.nmenu__item {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  width: 100%;
  padding: var(--sp-3);
  border-radius: var(--r-sm);
  text-align: left;
}
.nmenu__item:hover {
  background: var(--c-surface-alt);
}
.nmenu__marker {
  width: 6px;
  height: 6px;
  flex-shrink: 0;
  border-radius: 50%;
  background: var(--c-text-muted);
}
.nmenu__marker.is-danger {
  background: var(--c-danger);
}
.nmenu__marker.is-accent {
  background: var(--c-accent);
}
.nmenu__text {
  flex: 1;
  font-size: var(--fs-sm);
  color: var(--c-text);
}
.nmenu__sub {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.nmenu__foot {
  display: flex;
  align-items: center;
  padding: var(--sp-2) var(--sp-3);
  border-top: 1px solid var(--c-border);
  font-size: var(--fs-xs);
}
.nmenu__foot button {
  color: var(--c-text-2);
  padding: 4px;
}
.nmenu__foot button:hover {
  color: var(--c-text);
}
.nmenu__spacer {
  flex: 1;
}
</style>
