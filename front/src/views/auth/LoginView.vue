<script setup>
import BaseButton from '@/components/common/BaseButton.vue'
</script>

<template>
  <form class="auth-form" @submit.prevent>
    <h1 class="auth-form__title">로그인</h1>
    <p class="auth-form__desc">이메일로 일로ON에 로그인하세요.</p>

    <label class="field">
      <span class="field__label">이메일</span>
      <input
        class="field__input"
        type="email"
        placeholder="name@company.com"
        autocomplete="email"
      />
    </label>

    <label class="field">
      <span class="field__label">비밀번호</span>
      <input class="field__input" type="password" autocomplete="current-password" />
    </label>

    <BaseButton variant="primary" block type="submit">로그인</BaseButton>

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
.field {
  display: flex;
  flex-direction: column;
  gap: var(--sp-2);
}
.field__label {
  font-size: var(--fs-sm);
  font-weight: 500;
}
.field__input {
  height: 40px;
  padding: 0 var(--sp-3);
  border: 1px solid var(--c-border);
  border-radius: var(--r-md);
  background: var(--c-surface);
}
.field__input:focus {
  outline: none;
  border-color: var(--c-primary);
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
