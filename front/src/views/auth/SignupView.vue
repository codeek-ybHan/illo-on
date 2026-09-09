<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  email as validateEmail,
  password as validatePassword,
  required,
  maxLength,
  firstError,
} from '@/utils/validation'
import BaseInput from '@/components/common/BaseInput.vue'
import BaseButton from '@/components/common/BaseButton.vue'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ name: '', email: '', password: '' })
const errors = reactive({ name: '', email: '', password: '' })
const formError = ref('')
const submitting = ref(false)

function validate() {
  errors.name = required(form.name, '이름') || maxLength(form.name, 50, '이름')
  errors.email = validateEmail(form.email)
  errors.password = validatePassword(form.password)
  return !firstError([errors.name, errors.email, errors.password])
}

async function onSubmit() {
  formError.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    await auth.signup({ ...form })
    router.replace({ name: 'login', query: { registered: '1' } })
  } catch (e) {
    formError.value = e.normalizedMessage || '회원가입에 실패했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <form class="auth-form" @submit.prevent="onSubmit">
    <h1 class="auth-form__title">회원가입</h1>
    <p class="auth-form__desc">이름, 이메일, 비밀번호만으로 시작할 수 있어요.</p>

    <p v-if="formError" class="auth-form__error">{{ formError }}</p>

    <BaseInput v-model="form.name" label="이름" autocomplete="name" :error="errors.name" />
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
      autocomplete="new-password"
      hint="8자 이상"
      :error="errors.password"
    />

    <BaseButton variant="primary" block type="submit" :disabled="submitting">
      {{ submitting ? '가입 중…' : '회원가입' }}
    </BaseButton>

    <p class="auth-form__switch">
      이미 계정이 있으신가요?
      <RouterLink :to="{ name: 'login' }">로그인</RouterLink>
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
