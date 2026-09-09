<script setup>
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useProjectStore } from '@/stores/project'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import ProjectForm from '@/components/project/ProjectForm.vue'
import { formatDate } from '@/utils/date'

const router = useRouter()
const store = useProjectStore()
const { projects, loading, error } = storeToRefs(store)

const showCreate = ref(false)

onMounted(() => store.fetchProjects())

async function handleCreate(payload) {
  const created = await store.createProject(payload)
  router.push({ name: 'project-detail', params: { id: created.projectId } })
}
</script>

<template>
  <PagePlaceholder
    title="내 프로젝트"
    subtitle="참여 중인 프로젝트를 관리하고 새 프로젝트를 만드세요."
  >
    <template #actions>
      <BaseButton variant="primary" size="sm" @click="showCreate = true">
        <template #icon><AppIcon name="plus" :size="16" /></template>
        새 프로젝트
      </BaseButton>
    </template>

    <p v-if="error" class="list-error">{{ error }}</p>

    <div v-if="loading" class="grid">
      <BaseCard v-for="n in 3" :key="n"><div class="skeleton card-skel" /></BaseCard>
    </div>

    <BaseCard v-else-if="!projects.length" padding="lg">
      <p class="empty-hint">아직 참여 중인 프로젝트가 없습니다.<br />“새 프로젝트”로 시작하세요.</p>
    </BaseCard>

    <div v-else class="grid">
      <RouterLink
        v-for="p in projects"
        :key="p.projectId"
        class="pcard"
        :to="{ name: 'project-detail', params: { id: p.projectId } }"
      >
        <div class="pcard__top">
          <StatusBadge :status="p.status" />
          <span v-if="p.myRole === 'ADMIN'" class="pcard__role">관리자</span>
        </div>
        <h3 class="pcard__name">{{ p.name }}</h3>
        <p class="pcard__desc">{{ p.description || '설명 없음' }}</p>
        <div class="pcard__meta">
          <span><AppIcon name="project" :size="14" /> 멤버 {{ p.memberCount }}</span>
          <span v-if="p.endDate">~ {{ formatDate(p.endDate) }}</span>
        </div>
      </RouterLink>
    </div>
  </PagePlaceholder>

  <ProjectForm v-model:open="showCreate" :submit-fn="handleCreate" />
</template>

<style scoped>
.list-error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: var(--sp-4);
}
.card-skel {
  height: 96px;
}
.pcard {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
  padding: var(--sp-5);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-card);
  transition: border-color 0.15s ease;
}
.pcard:hover {
  border-color: var(--c-border-strong);
}
.pcard__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.pcard__role {
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.pcard__name {
  font-size: var(--fs-lg);
}
.pcard__desc {
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.pcard__meta {
  display: flex;
  gap: var(--sp-4);
  margin-top: var(--sp-2);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.pcard__meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
