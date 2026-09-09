<script setup>
import { reactive, computed, watch } from 'vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseSelect from '@/components/common/BaseSelect.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseDatePicker from '@/components/common/BaseDatePicker.vue'

const props = defineProps({
  actionPoint: { type: Object, required: true }, // { title, assigneeHint, dueDate, priority }
  members: { type: Array, default: () => [] },
  registered: { type: Boolean, default: false },
})
const emit = defineEmits(['register'])

const PRIORITY_OPTIONS = [
  { label: '높음', value: 'HIGH' },
  { label: '보통', value: 'MEDIUM' },
  { label: '낮음', value: 'LOW' },
]

const form = reactive({ title: '', assigneeId: '', dueDate: '', priority: 'MEDIUM' })

const assigneeOptions = computed(() => [
  { label: '미지정', value: '' },
  ...props.members.map((m) => ({ label: m.name, value: String(m.userId) })),
])

watch(
  () => props.actionPoint,
  (ap) => {
    form.title = ap.title ?? ''
    form.dueDate = ap.dueDate ?? ''
    form.priority = ap.priority ?? 'MEDIUM'
    // 담당자 후보 이름이 멤버와 일치하면 미리 선택
    const match = props.members.find((m) => m.name === ap.assigneeHint)
    form.assigneeId = match ? String(match.userId) : ''
  },
  { immediate: true },
)

function register() {
  emit('register', {
    title: form.title.trim(),
    assigneeId: form.assigneeId ? Number(form.assigneeId) : null,
    dueDate: form.dueDate || null,
    priority: form.priority,
  })
}
</script>

<template>
  <div class="ap" :class="{ 'is-registered': registered }">
    <BaseInput v-model="form.title" label="업무명" :disabled="registered" />
    <div class="ap__row">
      <BaseSelect
        v-model="form.assigneeId"
        label="담당자"
        :options="assigneeOptions"
        :disabled="registered"
      />
      <BaseDatePicker
        v-model="form.dueDate"
        label="기한"
        placeholder="기한 선택"
        :disabled="registered"
      />
      <BaseSelect
        v-model="form.priority"
        label="우선순위"
        :options="PRIORITY_OPTIONS"
        :disabled="registered"
      />
    </div>
    <p v-if="actionPoint.assigneeHint && !form.assigneeId && !registered" class="ap__hint">
      AI 추정 담당자: “{{ actionPoint.assigneeHint }}” — 멤버와 매칭되지 않았습니다.
    </p>
    <div class="ap__foot">
      <span v-if="registered" class="ap__done">✓ 업무로 등록됨</span>
      <BaseButton v-else variant="primary" size="sm" @click="register">업무로 등록</BaseButton>
    </div>
  </div>
</template>

<style scoped>
.ap {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
  padding: var(--sp-4);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface-alt);
}
.ap.is-registered {
  opacity: 0.6;
}
.ap__row {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr;
  gap: var(--sp-2);
}
.ap__hint {
  font-size: var(--fs-xs);
  color: var(--c-accent);
}
.ap__foot {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--sp-2);
}
.ap__done {
  font-size: var(--fs-sm);
  color: var(--c-success);
  font-weight: 600;
}

@media (max-width: 1080px) {
  .ap__row {
    grid-template-columns: 1fr;
  }
}
</style>
