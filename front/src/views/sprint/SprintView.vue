<script setup>
import { ref, onMounted } from 'vue'
import { fetchProjects } from '@/api/project'
import { fetchSprints } from '@/api/sprint'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import ProgressBar from '@/components/common/ProgressBar.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import { formatDate } from '@/utils/date'

const loading = ref(true)
const error = ref('')
const rows = ref([]) // { project, sprints: [] }

onMounted(async () => {
  try {
    const projects = await fetchProjects()
    rows.value = await Promise.all(
      projects.map(async (p) => ({
        project: p,
        sprints: await fetchSprints(p.projectId),
      })),
    )
  } catch (e) {
    error.value = e.normalizedMessage || 'Sprint 현황을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
})

function activeFirst(sprints) {
  return [...sprints].sort((a, b) => (a.status === 'ACTIVE' ? -1 : b.status === 'ACTIVE' ? 1 : 0))
}
</script>

<template>
  <PagePlaceholder title="Sprint 현황" subtitle="참여 중인 프로젝트의 Sprint 진행 상황입니다.">
    <p v-if="error" class="sv-error">{{ error }}</p>

    <div v-if="loading" class="sv-list">
      <div v-for="n in 2" :key="n" class="skeleton" style="height: 120px" />
    </div>

    <BaseCard v-else-if="!rows.length" padding="lg">
      <p class="empty-hint">참여 중인 프로젝트가 없습니다.</p>
    </BaseCard>

    <div v-else class="sv-list">
      <BaseCard v-for="row in rows" :key="row.project.projectId">
        <template #header>
          <RouterLink
            :to="{ name: 'project-detail', params: { id: row.project.projectId } }"
            class="sv-project"
          >
            {{ row.project.name }}
            <AppIcon name="chevronRight" :size="14" />
          </RouterLink>
        </template>

        <p v-if="!row.sprints.length" class="empty-hint">Sprint 없음</p>
        <ul v-else class="sv-sprints">
          <li v-for="s in activeFirst(row.sprints)" :key="s.sprintId" class="sv-sprint">
            <div class="sv-sprint__info">
              <span class="sv-sprint__name">{{ s.name }}</span>
              <StatusBadge :status="s.status" />
              <span class="u-muted">
                {{ s.startDate ? formatDate(s.startDate) : '미정' }} ~
                {{ s.endDate ? formatDate(s.endDate) : '미정' }}
              </span>
            </div>
            <div class="sv-sprint__progress">
              <ProgressBar :value="s.progress" color="var(--c-accent)" show-label />
              <span class="u-muted">{{ s.doneCount }} / {{ s.taskCount }}</span>
            </div>
          </li>
        </ul>
      </BaseCard>
    </div>
  </PagePlaceholder>
</template>

<style scoped>
.sv-error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.sv-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.sv-project {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.sv-project:hover {
  color: var(--c-accent);
}
.sv-sprints {
  display: flex;
  flex-direction: column;
  gap: var(--sp-3);
}
.sv-sprint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-4);
  padding: var(--sp-3) 0;
  border-bottom: 1px solid var(--c-border);
}
.sv-sprint:last-child {
  border-bottom: none;
}
.sv-sprint__info {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  font-size: var(--fs-sm);
}
.sv-sprint__name {
  font-weight: 600;
}
.sv-sprint__progress {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  min-width: 240px;
  font-size: var(--fs-xs);
}
</style>
