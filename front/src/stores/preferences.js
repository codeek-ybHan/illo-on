import { defineStore } from 'pinia'
import { ref } from 'vue'

const THEME_KEY = 'illoon.theme'
const SHOW_OVERDUE_KEY = 'illoon.showOverdueAlerts'
const SHOW_DUE_SOON_KEY = 'illoon.showDueSoonAlerts'

function readBool(key, fallback) {
  const raw = localStorage.getItem(key)
  return raw === null ? fallback : raw === 'true'
}

function applyTheme(mode) {
  document.documentElement.dataset.theme = mode
}

/**
 * 개인화 설정 — 서버 저장 없이 localStorage 에만 영속.
 */
export const usePreferencesStore = defineStore('preferences', () => {
  const theme = ref(localStorage.getItem(THEME_KEY) || 'light')
  const showOverdueAlerts = ref(readBool(SHOW_OVERDUE_KEY, true))
  const showDueSoonAlerts = ref(readBool(SHOW_DUE_SOON_KEY, true))

  applyTheme(theme.value)

  function setTheme(mode) {
    theme.value = mode
    localStorage.setItem(THEME_KEY, mode)
    applyTheme(mode)
  }

  function setShowOverdueAlerts(value) {
    showOverdueAlerts.value = value
    localStorage.setItem(SHOW_OVERDUE_KEY, String(value))
  }

  function setShowDueSoonAlerts(value) {
    showDueSoonAlerts.value = value
    localStorage.setItem(SHOW_DUE_SOON_KEY, String(value))
  }

  return {
    theme,
    showOverdueAlerts,
    showDueSoonAlerts,
    setTheme,
    setShowOverdueAlerts,
    setShowDueSoonAlerts,
  }
})
