<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AppIcon from './AppIcon.vue'

const router = useRouter()
const auth = useAuthStore()
const emit = defineEmits(['toggle-feedback'])

const menu = [
  { to: { name: 'mainboard' }, icon: 'home', label: '메인보드' },
  { to: { name: 'meetings' }, icon: 'meeting', label: '회의' },
  { to: { name: 'calendar' }, icon: 'calendar', label: '캘린더' },
  { to: { name: 'projects' }, icon: 'project', label: '프로젝트' },
  { to: { name: 'sprints' }, icon: 'sprint', label: 'Sprint' },
]

const accountOpen = ref(false)
const initial = computed(() => auth.user?.name?.trim()?.charAt(0) || '?')

function logout() {
  accountOpen.value = false
  auth.logout()
  router.replace({ name: 'login' })
}
</script>

<template>
  <nav class="rail">
    <RouterLink :to="{ name: 'mainboard' }" class="rail__logo" aria-label="일로ON 홈">
      <AppIcon name="logo" :size="22" />
    </RouterLink>

    <div class="rail__group">
      <span class="rail__caption">메뉴</span>
      <ul>
        <li v-for="item in menu" :key="item.label">
          <RouterLink :to="item.to" class="rail__item" :title="item.label">
            <AppIcon :name="item.icon" :size="20" />
            <span class="rail__tooltip">{{ item.label }}</span>
          </RouterLink>
        </li>
      </ul>
    </div>

    <div class="rail__group rail__group--settings">
      <span class="rail__caption">계정</span>
      <ul>
        <li>
          <RouterLink :to="{ name: 'settings' }" class="rail__item" title="설정">
            <AppIcon name="settings" :size="20" />
            <span class="rail__tooltip">설정</span>
          </RouterLink>
        </li>
      </ul>
    </div>

    <div class="rail__spacer" />

    <div class="rail__group rail__group--feedback">
      <button class="rail__item" type="button" title="피드백" @click="emit('toggle-feedback')">
        <AppIcon name="chat" :size="20" />
        <span class="rail__tooltip">피드백</span>
      </button>
    </div>

    <div class="rail__account">
      <button
        class="rail__avatar"
        type="button"
        aria-label="내 계정"
        :aria-expanded="accountOpen"
        @click="accountOpen = !accountOpen"
      >
        {{ initial }}
      </button>

      <div v-if="accountOpen" class="account-pop">
        <p class="account-pop__name">{{ auth.user?.name || '사용자' }}</p>
        <p class="account-pop__email">{{ auth.user?.email }}</p>
        <RouterLink
          v-if="auth.user?.isAdmin"
          :to="{ name: 'admin' }"
          class="account-pop__admin-link"
          @click="accountOpen = false"
        >
          관리자 페이지
        </RouterLink>
        <button class="account-pop__logout" type="button" @click="logout">로그아웃</button>
      </div>
      <div v-if="accountOpen" class="rail__backdrop" @click="accountOpen = false" />
    </div>
  </nav>
</template>

<style scoped>
.rail {
  width: var(--rail-w);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-4);
  padding: var(--sp-4) 0;
  border-right: 1px solid var(--c-border);
}
.rail__logo {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: var(--r-md);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.rail__group {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-2);
}
.rail__caption {
  font-size: 10px;
  letter-spacing: 0.04em;
  color: var(--c-text-muted);
}
.rail__group ul {
  display: flex;
  flex-direction: column;
  gap: var(--sp-1);
}
.rail__item {
  position: relative;
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: var(--r-md);
  color: var(--c-text-2);
  transition:
    background-color 0.15s ease,
    color 0.15s ease;
}
.rail__item:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.rail__item.router-link-exact-active {
  background: var(--c-primary);
  color: var(--c-primary-contrast);
}
.rail__item[disabled] {
  opacity: 0.4;
  cursor: not-allowed;
}
.rail__tooltip {
  position: absolute;
  left: calc(100% + 10px);
  top: 50%;
  transform: translateY(-50%);
  padding: 4px 8px;
  border-radius: var(--r-sm);
  background: var(--c-primary);
  color: var(--c-primary-contrast);
  font-size: var(--fs-xs);
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.12s ease;
  z-index: 20;
}
.rail__item:hover .rail__tooltip {
  opacity: 1;
}
.rail__spacer {
  flex: 1;
}
.rail__account {
  position: relative;
}
.rail__avatar {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: var(--r-full);
  background: var(--c-accent-soft);
  color: var(--c-accent);
  font-size: var(--fs-sm);
  font-weight: 700;
}
.rail__backdrop {
  position: fixed;
  inset: 0;
  z-index: 30;
}
.account-pop {
  position: absolute;
  left: calc(100% + 10px);
  bottom: 0;
  z-index: 40;
  width: 200px;
  padding: var(--sp-3);
  background: var(--c-surface);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  box-shadow: var(--shadow-pop);
}
.account-pop__name {
  font-size: var(--fs-sm);
  font-weight: 600;
}
.account-pop__email {
  margin-top: 2px;
  font-size: var(--fs-xs);
  color: var(--c-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
}
.account-pop__admin-link {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: var(--sp-3);
  width: 100%;
  height: 34px;
  border-radius: var(--r-sm);
  border: 1px solid var(--c-border);
  font-size: var(--fs-sm);
  font-weight: 500;
  color: var(--c-text-2);
}
.account-pop__admin-link:hover {
  background: var(--c-surface-alt);
  color: var(--c-text);
}
.account-pop__logout {
  margin-top: var(--sp-2);
  width: 100%;
  height: 34px;
  border-radius: var(--r-sm);
  background: var(--c-surface-alt);
  font-size: var(--fs-sm);
  font-weight: 500;
}
.account-pop__logout:hover {
  background: var(--c-border);
}

@media (max-width: 640px) {
  .rail {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 40;
    width: 100%;
    height: var(--rail-h-m);
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    gap: var(--sp-2);
    padding: 0 var(--sp-3);
    border-right: none;
    border-top: 1px solid var(--c-border);
    background: var(--c-shell);
  }
  .rail__logo,
  .rail__group--settings,
  .rail__group--feedback,
  .rail__spacer,
  .rail__tooltip {
    display: none;
  }
  .rail__group {
    width: auto;
    flex-direction: row;
  }
  .rail__group ul {
    flex-direction: row;
    gap: var(--sp-1);
  }
  .account-pop {
    left: auto;
    right: 0;
    bottom: calc(100% + 10px);
  }
}
</style>
