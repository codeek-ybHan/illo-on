<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminUsers, fetchAdminStats, deleteAdminUser } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import StatCard from '@/components/common/StatCard.vue'
import { formatDate } from '@/utils/date'
import { toast } from '@/utils/toast'

const auth = useAuthStore()

const loading = ref(true)
const error = ref('')
const stats = ref(null)
const users = ref([])

const showDelete = ref(false)
const deleteTarget = ref(null)
const deleting = ref(false)

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

function openDelete(user) {
  deleteTarget.value = user
  showDelete.value = true
}

async function confirmDelete() {
  deleting.value = true
  try {
    await deleteAdminUser(deleteTarget.value.userId)
    users.value = users.value.filter((u) => u.userId !== deleteTarget.value.userId)
    if (stats.value) stats.value.totalUsers -= 1
    toast().success('사용자를 삭제했습니다.')
    showDelete.value = false
  } catch (e) {
    toast().error(e.normalizedMessage || '삭제에 실패했습니다.')
  } finally {
    deleting.value = false
  }
}
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
              <th></th>
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
              <td class="admin-table__actions">
                <BaseButton
                  v-if="u.userId !== auth.user?.userId"
                  variant="ghost"
                  size="sm"
                  @click="openDelete(u)"
                >
                  삭제
                </BaseButton>
              </td>
            </tr>
          </tbody>
        </table>
      </BaseCard>
    </template>
  </PagePlaceholder>

  <BaseModal v-model:open="showDelete" title="사용자 삭제" size="sm">
    <p>
      <strong>{{ deleteTarget?.name }}</strong>({{ deleteTarget?.email }}) 계정을 완전히
      삭제할까요?
    </p>
    <p class="admin-delete-warning">
      되돌릴 수 없습니다. 참여 중인 프로젝트에서 제외되고, 배정된 Task는 미지정으로 바뀝니다.
      해당 사용자가 만든 프로젝트·Task·회의·피드백 자체는 삭제되지 않습니다.
    </p>
    <template #footer>
      <BaseButton variant="ghost" size="sm" @click="showDelete = false">취소</BaseButton>
      <BaseButton variant="primary" size="sm" :disabled="deleting" @click="confirmDelete">
        {{ deleting ? '삭제 중…' : '삭제' }}
      </BaseButton>
    </template>
  </BaseModal>
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
.admin-table__actions {
  text-align: right;
}
.admin-delete-warning {
  margin-top: var(--sp-2);
  font-size: var(--fs-sm);
  color: var(--c-text-muted);
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
