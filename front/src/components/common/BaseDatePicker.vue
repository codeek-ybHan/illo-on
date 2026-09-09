<script setup>
import { ref, computed, watch, nextTick, onBeforeUnmount, useId } from 'vue'
import AppIcon from './AppIcon.vue'
import { toISODate, parseISODate, formatDateShort } from '@/utils/date'

const props = defineProps({
  /** 'YYYY-MM-DD' (date) 또는 'YYYY-MM-DDTHH:mm' (withTime) */
  modelValue: { type: String, default: '' },
  label: { type: String, default: '' },
  error: { type: String, default: '' },
  hint: { type: String, default: '' },
  required: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  placeholder: { type: String, default: '날짜 선택' },
  /** 시각 입력 추가 */
  withTime: { type: Boolean, default: false },
})
const emit = defineEmits(['update:modelValue'])

const id = useId()
const open = ref(false)
const wrapRef = ref(null)
const triggerRef = ref(null)
const popStyle = ref({})

function positionPop() {
  const el = triggerRef.value
  if (!el) return
  const r = el.getBoundingClientRect()
  const popH = 360
  const below = window.innerHeight - r.bottom
  const openUp = below < popH && r.top > below
  popStyle.value = {
    position: 'fixed',
    left: `${Math.min(r.left, window.innerWidth - 296)}px`,
    ...(openUp ? { bottom: `${window.innerHeight - r.top + 6}px` } : { top: `${r.bottom + 6}px` }),
  }
}

/* ---- 값 파싱 ---- */
const datePart = computed(() => (props.modelValue || '').slice(0, 10))
const timePart = computed(() => {
  const t = (props.modelValue || '').slice(11, 16)
  return /^\d{2}:\d{2}$/.test(t) ? t : ''
})
const selectedDate = computed(() => parseISODate(datePart.value))

const displayText = computed(() => {
  if (!datePart.value) return props.placeholder
  const s = formatDateShort(datePart.value)
  return props.withTime && timePart.value ? `${s} ${timePart.value}` : s
})

/* ---- 달력 뷰 상태 ---- */
const today = new Date()
const viewYear = ref((selectedDate.value || today).getFullYear())
const viewMonth = ref((selectedDate.value || today).getMonth())

const popRef = ref(null)

watch(open, (isOpen) => {
  if (isOpen) {
    const base = selectedDate.value || today
    viewYear.value = base.getFullYear()
    viewMonth.value = base.getMonth()
    nextTick(positionPop)
    window.addEventListener('keydown', onKey)
    window.addEventListener('click', onOutside, true)
    window.addEventListener('resize', positionPop)
    window.addEventListener('scroll', positionPop, true)
  } else {
    removeListeners()
  }
})
onBeforeUnmount(removeListeners)

function removeListeners() {
  window.removeEventListener('keydown', onKey)
  window.removeEventListener('click', onOutside, true)
  window.removeEventListener('resize', positionPop)
  window.removeEventListener('scroll', positionPop, true)
}

function onKey(e) {
  if (e.key === 'Escape') open.value = false
}
function onOutside(e) {
  if (wrapRef.value?.contains(e.target)) return
  if (popRef.value?.contains(e.target)) return
  open.value = false
}

const WEEKDAYS = ['월', '화', '수', '목', '금', '토', '일']

const grid = computed(() => {
  const first = new Date(viewYear.value, viewMonth.value, 1)
  const offset = (first.getDay() + 6) % 7 // 월요일 시작
  const start = new Date(viewYear.value, viewMonth.value, 1 - offset)
  const sel = datePart.value
  const todayIso = toISODate(today)
  return Array.from({ length: 42 }, (_, i) => {
    const d = new Date(start.getFullYear(), start.getMonth(), start.getDate() + i)
    const iso = toISODate(d)
    return {
      iso,
      day: d.getDate(),
      inMonth: d.getMonth() === viewMonth.value,
      isToday: iso === todayIso,
      isSelected: iso === sel,
    }
  })
})

function shiftMonth(delta) {
  const m = viewMonth.value + delta
  viewYear.value += Math.floor(m / 12)
  viewMonth.value = ((m % 12) + 12) % 12
}

function emitDate(iso) {
  if (props.withTime) {
    emit('update:modelValue', iso ? `${iso}T${timePart.value || '09:00'}` : '')
  } else {
    emit('update:modelValue', iso)
  }
}

function pick(iso) {
  emitDate(iso)
  if (!props.withTime) open.value = false
}

function onTime(e) {
  const t = e.target.value
  emit('update:modelValue', datePart.value ? `${datePart.value}T${t || '09:00'}` : '')
}

/* ---- 빠른 선택 ---- */
function quick(kind) {
  const d = new Date()
  if (kind === 'tomorrow') d.setDate(d.getDate() + 1)
  if (kind === 'nextWeek') d.setDate(d.getDate() + 7)
  if (kind === 'friday') {
    const diff = (5 - d.getDay() + 7) % 7 || 7
    d.setDate(d.getDate() + diff)
  }
  pick(toISODate(d))
}
function clear() {
  emit('update:modelValue', '')
  open.value = false
}
</script>

<template>
  <div ref="wrapRef" class="dp" :class="{ 'dp--error': error }">
    <label v-if="label" :for="id" class="dp__label">
      {{ label }}
      <span v-if="required" class="dp__req" aria-hidden="true">*</span>
    </label>

    <button
      :id="id"
      ref="triggerRef"
      type="button"
      class="dp__trigger"
      :class="{ 'is-empty': !datePart, 'is-open': open }"
      :disabled="disabled"
      :aria-expanded="open"
      @click="open = !open"
    >
      <AppIcon name="calendar" :size="15" />
      <span class="dp__value">{{ displayText }}</span>
      <button
        v-if="datePart && !disabled"
        type="button"
        class="dp__clear"
        aria-label="지우기"
        @click.stop="clear"
      >
        <AppIcon name="plus" :size="13" style="transform: rotate(45deg)" />
      </button>
    </button>

    <Teleport to="body">
      <div v-if="open" ref="popRef" class="dp__pop" :style="popStyle">
        <div class="dp__quick">
          <button type="button" @click="quick('today')">오늘</button>
          <button type="button" @click="quick('tomorrow')">내일</button>
          <button type="button" @click="quick('friday')">이번 주 금요일</button>
          <button type="button" @click="quick('nextWeek')">다음 주</button>
        </div>

        <div class="dp__nav">
          <button type="button" aria-label="이전 달" @click="shiftMonth(-1)">
            <AppIcon name="chevronLeft" :size="16" />
          </button>
          <span>{{ viewYear }}년 {{ viewMonth + 1 }}월</span>
          <button type="button" aria-label="다음 달" @click="shiftMonth(1)">
            <AppIcon name="chevronRight" :size="16" />
          </button>
        </div>

        <div class="dp__grid dp__grid--head">
          <span v-for="w in WEEKDAYS" :key="w">{{ w }}</span>
        </div>
        <div class="dp__grid">
          <button
            v-for="c in grid"
            :key="c.iso"
            type="button"
            class="dp__day"
            :class="{
              'is-muted': !c.inMonth,
              'is-today': c.isToday,
              'is-selected': c.isSelected,
            }"
            @click="pick(c.iso)"
          >
            {{ c.day }}
          </button>
        </div>

        <label v-if="withTime" class="dp__time">
          시각
          <input type="time" :value="timePart || '09:00'" @input="onTime" />
        </label>
      </div>
    </Teleport>

    <p v-if="error" class="dp__msg dp__msg--error">{{ error }}</p>
    <p v-else-if="hint" class="dp__msg">{{ hint }}</p>
  </div>
</template>

<style scoped>
.dp {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  position: relative;
}
.dp__label {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.dp__req {
  color: var(--c-danger);
}
.dp__trigger {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  height: 40px;
  padding: 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
  color: var(--c-text);
  text-align: left;
  transition: border-color 0.15s ease;
}
.dp__trigger:hover:not(:disabled) {
  border-color: var(--c-border-strong);
}
.dp__trigger.is-open {
  border-color: var(--c-primary);
}
.dp__trigger.is-empty .dp__value {
  color: var(--c-text-muted);
}
.dp__trigger:disabled {
  background: var(--c-surface-alt);
  color: var(--c-text-muted);
}
.dp--error .dp__trigger {
  border-color: var(--c-danger);
}
.dp__value {
  flex: 1;
  font-size: var(--fs-sm);
}
.dp__clear {
  display: grid;
  place-items: center;
  width: 20px;
  height: 20px;
  border-radius: var(--r-full);
  color: var(--c-text-muted);
}
.dp__clear:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}

.dp__pop {
  position: fixed;
  z-index: 200;
  width: 280px;
  padding: var(--sp-3);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  box-shadow: var(--shadow-pop);
}
.dp__quick {
  display: flex;
  flex-wrap: wrap;
  gap: var(--sp-1);
  margin-bottom: var(--sp-3);
}
.dp__quick button {
  padding: 4px 8px;
  border-radius: var(--r-full);
  border: 1px solid var(--c-border);
  font-size: var(--fs-xs);
  color: var(--c-text-2);
}
.dp__quick button:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.dp__nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--sp-2);
  font-size: var(--fs-sm);
  font-weight: 600;
}
.dp__nav button {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: var(--r-sm);
  color: var(--c-text-2);
}
.dp__nav button:hover {
  background: var(--c-surface-alt);
}
.dp__grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}
.dp__grid--head {
  margin-bottom: 4px;
}
.dp__grid--head span {
  text-align: center;
  font-size: 11px;
  color: var(--c-text-muted);
  padding: 4px 0;
}
.dp__day {
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  border-radius: var(--r-sm);
  font-size: var(--fs-xs);
  color: var(--c-text);
}
.dp__day:hover {
  background: var(--c-surface-alt);
}
.dp__day.is-muted {
  color: var(--c-text-muted);
  opacity: 0.5;
}
.dp__day.is-today {
  font-weight: 700;
  color: var(--c-accent);
}
.dp__day.is-selected {
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.dp__time {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  margin-top: var(--sp-3);
  padding-top: var(--sp-3);
  border-top: 1px solid var(--c-border);
  font-size: var(--fs-sm);
}
.dp__time input {
  flex: 1;
  height: 32px;
  padding: 0 var(--sp-2);
  border: 1px solid var(--c-border);
  border-radius: var(--r-sm);
}
.dp__msg {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.dp__msg--error {
  color: var(--c-danger);
}
</style>
