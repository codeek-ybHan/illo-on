<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useUiStore } from '@/stores/ui'
import { useProjectStore } from '@/stores/project'
import { useTaskStore } from '@/stores/task'
import { fetchMyMeetings } from '@/api/board'
import AppIcon from './AppIcon.vue'

const router = useRouter()
const ui = useUiStore()
const { searchOpen } = storeToRefs(ui)
const projectStore = useProjectStore()
const taskStore = useTaskStore()

const query = ref('')
const inputEl = ref(null)
const activeIndex = ref(0)
const loading = ref(false)
const loaded = ref(false)

const meetings = ref([])

async function loadIndex() {
  if (loaded.value) return
  loading.value = true
  try {
    await Promise.all([
      projectStore.projects.length ? null : projectStore.fetchProjects(),
      taskStore.fetchMyTasks(),
      fetchMyMeetings().then((m) => (meetings.value = m)),
    ])
    loaded.value = true
  } catch {
    /* 부분 실패해도 있는 것만 검색 */
  } finally {
    loading.value = false
  }
}

const results = computed(() => {
  const q = query.value.trim().toLowerCase()
  const out = []
  const take = (arr, type, toItem) => {
    for (const x of arr) {
      const item = toItem(x)
      if (!q || item.label.toLowerCase().includes(q)) out.push({ type, ...item })
    }
  }
  take(projectStore.projects, 'project', (p) => ({
    label: p.name,
    sub: p.status,
    to: { name: 'project-detail', params: { id: p.projectId } },
  }))
  take(meetings.value, 'meeting', (m) => ({
    label: m.title,
    sub: m.projectName || '회의',
    to: { name: 'meeting-detail', params: { id: m.meetingId } },
  }))
  take(taskStore.myTasks, 'task', (t) => ({
    label: t.title,
    sub: t.status,
    to: { name: 'task-detail', params: { id: t.taskId } },
  }))
  return out.slice(0, 12)
})

const iconFor = { project: 'project', meeting: 'meeting', task: 'sprint' }

watch(searchOpen, async (open) => {
  if (!open) return
  query.value = ''
  activeIndex.value = 0
  loadIndex()
  await nextTick()
  inputEl.value?.focus()
})
watch(query, () => (activeIndex.value = 0))

function move(delta) {
  const n = results.value.length
  if (!n) return
  activeIndex.value = (activeIndex.value + delta + n) % n
}
function choose(item) {
  if (!item) return
  ui.closeSearch()
  router.push(item.to)
}
function onKeydown(e) {
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    move(1)
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    move(-1)
  } else if (e.key === 'Enter') {
    e.preventDefault()
    choose(results.value[activeIndex.value])
  } else if (e.key === 'Escape') {
    ui.closeSearch()
  }
}
</script>

<template>
  <Teleport to="body">
    <div v-if="searchOpen" class="sp" @click.self="ui.closeSearch()">
      <div class="sp__panel" role="dialog" aria-label="검색">
        <div class="sp__inputrow">
          <AppIcon name="search" :size="18" />
          <input
            ref="inputEl"
            v-model="query"
            class="sp__input"
            type="text"
            placeholder="프로젝트 · 회의 · 업무 검색"
            @keydown="onKeydown"
          />
          <kbd class="sp__kbd">Esc</kbd>
        </div>

        <div class="sp__body">
          <p v-if="loading && !loaded" class="sp__hint">불러오는 중…</p>
          <p v-else-if="!results.length" class="sp__hint">
            {{ query ? '검색 결과가 없습니다.' : '검색어를 입력하세요.' }}
          </p>
          <ul v-else class="sp__list">
            <li
              v-for="(r, i) in results"
              :key="r.type + i"
              class="sp__item"
              :class="{ 'is-active': i === activeIndex }"
              @mouseenter="activeIndex = i"
              @click="choose(r)"
            >
              <span class="sp__item-icon"><AppIcon :name="iconFor[r.type]" :size="16" /></span>
              <span class="sp__item-label">{{ r.label }}</span>
              <span class="sp__item-sub">{{ r.sub }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.sp {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 12vh;
  background: rgba(0, 0, 0, 0.28);
}
.sp__panel {
  width: min(560px, 92vw);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-shell);
  overflow: hidden;
}
.sp__inputrow {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-4);
  border-bottom: 1px solid var(--c-border);
  color: var(--c-text-muted);
}
.sp__input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: var(--fs-md);
  color: var(--c-text);
}
.sp__input:focus {
  outline: none;
}
.sp__kbd {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  color: var(--c-text-2);
}
.sp__body {
  max-height: 52vh;
  overflow-y: auto;
  padding: var(--sp-2);
}
.sp__hint {
  padding: var(--sp-4);
  text-align: center;
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
}
.sp__list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.sp__item {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-3);
  border-radius: var(--r-md);
  cursor: pointer;
}
.sp__item.is-active {
  background: var(--c-surface-alt);
}
.sp__item-icon {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  flex-shrink: 0;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  color: var(--c-accent);
}
.sp__item.is-active .sp__item-icon {
  background: var(--c-surface);
}
.sp__item-label {
  flex: 1;
  font-size: var(--fs-sm);
  color: var(--c-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.sp__item-sub {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  flex-shrink: 0;
}
</style>
