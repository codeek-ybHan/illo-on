<script setup>
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { usePreferencesStore } from '@/stores/preferences'
import PagePlaceholder from '@/components/common/PagePlaceholder.vue'
import BaseCard from '@/components/common/BaseCard.vue'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'
import { required, email as emailRule, password as passwordRule, firstError } from '@/utils/validation'
import { toast } from '@/utils/toast'

const auth = useAuthStore()
const preferences = usePreferencesStore()

// 계정 정보
const profileForm = reactive({
  name: auth.user?.name ?? '',
  email: auth.user?.email ?? '',
})
const profileErrors = reactive({ name: '', email: '' })
const savingProfile = ref(false)

function validateProfile() {
  profileErrors.name = required(profileForm.name, '이름')
  profileErrors.email = emailRule(profileForm.email)
  return !firstError([profileErrors.name, profileErrors.email])
}

async function saveProfile() {
  if (!validateProfile()) return
  savingProfile.value = true
  try {
    await auth.updateProfile({ name: profileForm.name.trim(), email: profileForm.email.trim() })
    toast().success('계정 정보를 저장했습니다.')
  } catch (e) {
    toast().error(e.normalizedMessage || '저장에 실패했습니다.')
  } finally {
    savingProfile.value = false
  }
}

// 비밀번호 변경
const passwordForm = reactive({ current: '', next: '', confirm: '' })
const passwordErrors = reactive({ current: '', next: '', confirm: '' })
const changingPassword = ref(false)

function validatePassword() {
  passwordErrors.current = required(passwordForm.current, '현재 비밀번호')
  passwordErrors.next = passwordRule(passwordForm.next)
  passwordErrors.confirm =
    passwordForm.confirm !== passwordForm.next ? '새 비밀번호가 일치하지 않습니다.' : ''
  return !firstError([passwordErrors.current, passwordErrors.next, passwordErrors.confirm])
}

async function changePassword() {
  if (!validatePassword()) return
  changingPassword.value = true
  try {
    await auth.changePassword({
      currentPassword: passwordForm.current,
      newPassword: passwordForm.next,
    })
    passwordForm.current = ''
    passwordForm.next = ''
    passwordForm.confirm = ''
    toast().success('비밀번호를 변경했습니다.')
  } catch (e) {
    toast().error(e.normalizedMessage || '비밀번호 변경에 실패했습니다.')
  } finally {
    changingPassword.value = false
  }
}
</script>

<template>
  <PagePlaceholder title="설정" subtitle="계정 정보와 앱 환경을 관리하세요.">
    <BaseCard>
      <template #header>계정 정보</template>
      <form class="settings-form" @submit.prevent="saveProfile">
        <BaseInput v-model="profileForm.name" label="이름" required :error="profileErrors.name" />
        <BaseInput
          v-model="profileForm.email"
          label="이메일"
          type="email"
          required
          :error="profileErrors.email"
        />
        <div class="settings-form__foot">
          <BaseButton variant="primary" size="sm" :disabled="savingProfile" @click="saveProfile">
            {{ savingProfile ? '저장 중…' : '저장' }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>

    <BaseCard>
      <template #header>비밀번호 변경</template>
      <form class="settings-form" @submit.prevent="changePassword">
        <BaseInput
          v-model="passwordForm.current"
          label="현재 비밀번호"
          type="password"
          required
          :error="passwordErrors.current"
        />
        <BaseInput
          v-model="passwordForm.next"
          label="새 비밀번호"
          type="password"
          hint="8자 이상"
          required
          :error="passwordErrors.next"
        />
        <BaseInput
          v-model="passwordForm.confirm"
          label="새 비밀번호 확인"
          type="password"
          required
          :error="passwordErrors.confirm"
        />
        <div class="settings-form__foot">
          <BaseButton
            variant="primary"
            size="sm"
            :disabled="changingPassword"
            @click="changePassword"
          >
            {{ changingPassword ? '변경 중…' : '변경' }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>

    <BaseCard>
      <template #header>테마</template>
      <div class="settings-row">
        <span class="settings-row__label">화면 테마</span>
        <div class="theme-toggle">
          <BaseButton
            :variant="preferences.theme === 'light' ? 'primary' : 'ghost'"
            size="sm"
            @click="preferences.setTheme('light')"
          >
            라이트
          </BaseButton>
          <BaseButton
            :variant="preferences.theme === 'dark' ? 'primary' : 'ghost'"
            size="sm"
            @click="preferences.setTheme('dark')"
          >
            다크
          </BaseButton>
        </div>
      </div>
    </BaseCard>

    <BaseCard>
      <template #header>알림 표시</template>
      <div class="settings-row">
        <span class="settings-row__label">마감이 지난 업무 알림</span>
        <label class="switch">
          <input
            type="checkbox"
            :checked="preferences.showOverdueAlerts"
            @change="preferences.setShowOverdueAlerts($event.target.checked)"
          />
          <span class="switch__track"><span class="switch__thumb" /></span>
        </label>
      </div>
      <div class="settings-row">
        <span class="settings-row__label">마감 임박 업무 알림</span>
        <label class="switch">
          <input
            type="checkbox"
            :checked="preferences.showDueSoonAlerts"
            @change="preferences.setShowDueSoonAlerts($event.target.checked)"
          />
          <span class="switch__track"><span class="switch__thumb" /></span>
        </label>
      </div>
    </BaseCard>
  </PagePlaceholder>
</template>

<style scoped>
.settings-form {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.settings-form__foot {
  display: flex;
  justify-content: flex-end;
}
.settings-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--sp-4);
  padding: var(--sp-2) 0;
}
.settings-row + .settings-row {
  border-top: 1px solid var(--c-border);
}
.settings-row__label {
  font-size: var(--fs-sm);
}
.theme-toggle {
  display: flex;
  gap: var(--sp-2);
}
.switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 24px;
  flex-shrink: 0;
}
.switch input {
  position: absolute;
  opacity: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  cursor: pointer;
}
.switch__track {
  position: absolute;
  inset: 0;
  border-radius: var(--r-full);
  background: var(--c-border-strong);
  transition: background-color 0.15s ease;
}
.switch__thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 18px;
  height: 18px;
  border-radius: var(--r-full);
  background: var(--c-surface);
  transition: transform 0.15s ease;
}
.switch input:checked + .switch__track {
  background: var(--c-primary);
}
.switch input:checked + .switch__track .switch__thumb {
  transform: translateX(16px);
}
</style>
