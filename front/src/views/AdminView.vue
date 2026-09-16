<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminUsers, fetchAdminStats } from '@/api/admin'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import StatCard from '@/components/common/StatCard.vue'
import { formatDate } from '@/utils/date'

const loading = ref(true)
const error = ref('')
const stats = ref(null)
const users = ref([])

onMounted(async () => {
  try {
    const [statsRes, usersRes] = await Promise.all([fetchAdminStats(), fetchAdminUsers()])
    stats.value = statsRes
    users.value = usersRes
  } catch (e) {
    error.value = e.normalizedMessage || '관리자 데이터를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <PagePlaceholder title="관리자" subtitle="서비스 전체 사용자와 현황을 확인하세요.">
    <p v-if="error" class="admin-error">{{ error }}</p>

    <div v-if="loading" class="admin-list">
      <div v-for="n in 2" :key="n" class="skeleton" style="height: 120px" />
    </div>

    <template v-else-if="stats">
      <div class="admin-stats">
        <StatCard
          icon="chat"
          title="총 사용자"
          metric-label="가입자 수"
          :metric-value="`${stats.totalUsers}명`"
        />
        <StatCard
          icon="project"
          title="총 프로젝트"
          metric-label="생성된 프로젝트"
          :metric-value="`${stats.totalProjects}개`"
        />
        <StatCard
          icon="sparkle"
          title="반영완료 피드백"
          metric-label="전체 피드백 중"
          :metric-value="`${stats.resolvedFeedbacks} / ${stats.totalFeedbacks}`"
        />
        <StatCard
          icon="bell"
          title="대기 중 피드백"
          metric-label="아직 반영 안 됨"
          :metric-value="`${stats.pendingFeedbacks}건`"
        />
      </div>

      <BaseCard>
        <template #header>전체 사용자 ({{ users.length }})</template>
        <table class="admin-table">
          <thead>
            <tr>
              <th>이름</th>
              <th>이메일</th>
              <th>가입일</th>
              <th>권한</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in users" :key="u.userId">
              <td>{{ u.name }}</td>
              <td>{{ u.email }}</td>
              <td>{{ formatDate(u.createdAt) }}</td>
              <td>
                <span class="admin-badge" :class="{ 'is-admin': u.isAdmin }">
                  {{ u.isAdmin ? '관리자' : '-' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </BaseCard>
    </template>
  </PagePlaceholder>
</template>

<style scoped>
.admin-error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.admin-list {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.admin-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--sp-4);
}
.admin-table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--fs-sm);
}
.admin-table th,
.admin-table td {
  padding: var(--sp-3) var(--sp-2);
  border-bottom: 1px solid var(--c-border);
  text-align: left;
}
.admin-table th {
  color: var(--c-text-2);
  font-weight: 600;
}
.admin-badge {
  padding: 2px 10px;
  border-radius: var(--r-full);
  background: var(--c-surface-alt);
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
}
.admin-badge.is-admin {
  background: var(--c-mint);
  color: var(--c-mint-ink);
  font-weight: 600;
}

@media (max-width: 1080px) {
  .admin-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .admin-stats {
    grid-template-columns: 1fr;
  }
}
</style>
