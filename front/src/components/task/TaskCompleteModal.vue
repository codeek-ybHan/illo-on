<script setup>
import { ref, watch } from 'vue'
import BaseModal from '@/components/common/BaseModal.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseDatePicker from '@/components/common/BaseDatePicker.vue'
import { toISODate } from '@/utils/date'

const props = defineProps({
  open: { type: Boolean, default: false },
})
const emit = defineEmits(['confirm', 'cancel'])

const date = ref('')

watch(
  () => props.open,
  (isOpen) => {
    if (isOpen) date.value = toISODate(new Date())
  },
)

function confirm() {
  emit('confirm', date.value || toISODate(new Date()))
}
</script>

<template>
  <BaseModal
    :open="open"
    title="Task 완료"
    size="sm"
    @update:open="(v) => !v && emit('cancel')"
  >
    <p class="tcm__desc">완료일자를 입력해 주세요.</p>
    <BaseDatePicker v-model="date" label="완료일자" placeholder="완료일자 선택" />
    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="emit('cancel')">취소</BaseButton>
      <BaseButton variant="primary" size="sm" @click="confirm">완료 처리</BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
.tcm__desc {
  margin-bottom: var(--sp-3);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
</style>
