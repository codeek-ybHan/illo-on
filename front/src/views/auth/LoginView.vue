<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { email as validateEmail, required, firstError } from '@/utils/validation'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const form = reactive({ email: '', password: '' })
const errors = reactive({ email: '', password: '' })
const formError = ref('')
const submitting = ref(false)

function validate() {
  errors.email = validateEmail(form.email)
  errors.password = required(form.password, '비밀번호')
  return !firstError([errors.email, errors.password])
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await auth.login({ email: form.email, password: form.password })
    const redirect = route.query.redirect
    router.replace(typeof redirect === 'string' ? redirect : { name: 'mainboard' })
  } catch (e) {
    formError.value = e.normalizedMessage || '로그인에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <form class="auth-form" @submit.prevent="onSubmit">
    <h1 class="auth-form__title">로그인</h1>
    <p class="auth-form__desc">이메일로 일로ON에 로그인하세요.</p>

    <p v-if="route.query.registered" class="auth-form__notice">
      회원가입이 완료되었습니다. 로그인해 주세요.
    </p>
    <p v-if="formError" class="auth-form__error">{{ formError }}</p>

    <BaseInput
      v-model="form.email"
      label="이메일"
      type="email"
      placeholder="name@company.com"
      autocomplete="email"
      :error="errors.email"
    />
    <BaseInput
      v-model="form.password"
      label="비밀번호"
      type="password"
      autocomplete="current-password"
      :error="errors.password"
    />

    <BaseButton variant="primary" block type="submit" :disabled="submitting">
      {{ submitting ? '로그인 중…' : '로그인' }}
    </BaseButton>

    <p class="auth-form__switch">
      아직 계정이 없으신가요?
      <RouterLink :to="{ name: 'signup' }">회원가입</RouterLink>
    </p>
  </form>
</template>

<style scoped>
.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}
.auth-form__title {
  font-size: var(--fs-xl);
}
.auth-form__desc {
  margin-top: calc(var(--sp-3) * -1);
  font-size: var(--fs-sm);
  color: var(--c-text-2);
}
.auth-form__notice {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-mint);
  color: var(--c-mint-ink);
  font-size: var(--fs-sm);
}
.auth-form__error {
  padding: var(--sp-3);
  border-radius: var(--r-md);
  background: var(--c-peach);
  color: var(--c-danger);
  font-size: var(--fs-sm);
}
.auth-form__switch {
  font-size: var(--fs-sm);
  color: var(--c-text-2);
  text-align: center;
}
.auth-form__switch a {
  color: var(--c-text);
  font-weight: 600;
}
</style>
