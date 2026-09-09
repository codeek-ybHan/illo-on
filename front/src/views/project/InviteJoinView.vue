<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { joinByInvite } from '@/api/project'
import { useProjectStore } from '@/stores/project'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'

const route = useRoute()
const router = useRouter()
const projectStore = useProjectStore()

const state = ref('loading') // loading | success | error
const message = ref('')
const project = ref(null)

onMounted(async () => {
  try {
    project.value = await joinByInvite(route.params.token)
    state.value = 'success'
    // 목록 갱신
    projectStore.fetchProjects()
  } catch (e) {
    state.value = 'error'
    message.value = e.normalizedMessage || '초대 링크가 유효하지 않습니다.'
  }
})

function goProject() {
  router.replace({ name: 'project-detail', params: { id: project.value.projectId } })
}
</script>

<template>
  <div class="invite-page">
    <BaseCard padding="lg">
      <div v-if="state === 'loading'" class="invite-state">
        <p>초대 링크를 확인하는 중…</p>
      </div>

      <div v-else-if="state === 'success'" class="invite-state">
        <h2 class="invite-state__title">참여 완료 🎉</h2>
        <p>“{{ project.name }}” 프로젝트에 참여했습니다.</p>
        <BaseButton variant="primary" @click="goProject">프로젝트로 이동</BaseButton>
      </div>

      <div v-else class="invite-state">
        <h2 class="invite-state__title">참여할 수 없습니다</h2>
        <p class="invite-state__msg">{{ message }}</p>
        <BaseButton variant="ghost" @click="router.replace({ name: 'projects' })">
          내 프로젝트로
        </BaseButton>
      </div>
    </BaseCard>
  </div>
</template>

<style scoped>
.invite-page {
  max-width: 420px;
  margin: var(--sp-8) auto 0;
}
.invite-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-4);
  text-align: center;
  padding: var(--sp-4);
}
.invite-state__title {
  font-size: var(--fs-xl);
}
.invite-state__msg {
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
</style>
