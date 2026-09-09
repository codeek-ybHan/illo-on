import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 전역 UI 상태 — 상단 헤더의 "만들기" 메뉴와 검색 팔레트를
 * 화면 어디서든 열 수 있게 한다. (실제 모달/오버레이는 DefaultLayout 에 마운트)
 */
export const useUiStore = defineStore('ui', () => {
  /** 'project' | 'meeting' | 'task' | null */
  const createTarget = ref(null)
  const searchOpen = ref(false)

  function openCreate(target) {
    searchOpen.value = false
    createTarget.value = target
  }
  function closeCreate() {
    createTarget.value = null
  }
  function openSearch() {
    createTarget.value = null
    searchOpen.value = true
  }
  function closeSearch() {
    searchOpen.value = false
  }
  function toggleSearch() {
    searchOpen.value = !searchOpen.value
  }

  return {
    createTarget,
    searchOpen,
    openCreate,
    closeCreate,
    openSearch,
    closeSearch,
    toggleSearch,
  }
})
