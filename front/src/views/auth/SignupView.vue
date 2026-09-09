<script setup>
import BaseButton from '@/components/common/BaseButton.vue'
</script>

<template>
  <form class="auth-form" @submit.prevent>
    <h1 class="auth-form__title">회원가입</h1>
    <p class="auth-form__desc">이름, 이메일, 비밀번호만으로 시작할 수 있어요.</p>

    <label class="field">
      <span class="field__label">이름</span>
      <input class="field__input" type="text" autocomplete="name" />
    </label>

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
      <input class="field__input" type="password" autocomplete="new-password" />
    </label>

    <BaseButton variant="primary" block type="submit">회원가입</BaseButton>

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
