import { createRouter, createWebHistory } from 'vue-router'

import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import { getToken } from '@/utils/token'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'mainboard',
          component: () => import('@/views/MainBoardView.vue'),
          meta: { title: '메인보드', breadcrumb: ['일로ON', '메인보드'] },
        },
        {
          path: 'meetings',
          name: 'meetings',
          component: () => import('@/views/meeting/MeetingListView.vue'),
          meta: { title: '회의', breadcrumb: ['회의', '회의 목록'] },
        },
        {
          path: 'meetings/:id',
          name: 'meeting-detail',
          component: () => import('@/views/meeting/MeetingDetailView.vue'),
          meta: { title: '회의 상세', breadcrumb: ['회의', '회의 상세'], aiPanelOpen: true },
        },
        {
          path: 'projects',
          name: 'projects',
          component: () => import('@/views/project/ProjectListView.vue'),
          meta: { title: '프로젝트', breadcrumb: ['프로젝트', '내 프로젝트'] },
        },
        {
          path: 'projects/:id',
          name: 'project-detail',
          component: () => import('@/views/project/ProjectDetailView.vue'),
          meta: { title: '프로젝트', breadcrumb: ['프로젝트', '프로젝트 상세'] },
        },
        {
          path: 'invite/:token',
          name: 'invite-join',
          component: () => import('@/views/project/InviteJoinView.vue'),
          meta: { title: '프로젝트 참여', breadcrumb: ['프로젝트', '초대 수락'] },
        },
        {
          path: 'tasks/:id',
          name: 'task-detail',
          component: () => import('@/views/task/TaskDetailView.vue'),
          meta: { title: 'Task 상세', breadcrumb: ['Task', 'Task 상세'] },
        },
        {
          path: 'sprints',
          name: 'sprints',
          component: () => import('@/views/sprint/SprintView.vue'),
          meta: { title: 'Sprint', breadcrumb: ['Sprint', '현재 Sprint'] },
        },
        {
          path: 'calendar',
          name: 'calendar',
          component: () => import('@/views/CalendarView.vue'),
          meta: { title: '캘린더', breadcrumb: ['캘린더'] },
        },
      ],
    },
    {
      path: '/',
      component: AuthLayout,
      children: [
        {
          path: 'login',
          name: 'login',
          component: () => import('@/views/auth/LoginView.vue'),
          meta: { title: '로그인', public: true },
        },
        {
          path: 'signup',
          name: 'signup',
          component: () => import('@/views/auth/SignupView.vue'),
          meta: { title: '회원가입', public: true },
        },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

/**
 * 인증 가드
 * - public 라우트(login/signup): 이미 로그인 상태면 메인보드로
 * - 그 외: 토큰 없으면 로그인으로 (원위치는 redirect 쿼리에 보관)
 */
router.beforeEach((to) => {
  const authed = Boolean(getToken())

  if (to.meta.public) {
    return authed ? { name: 'mainboard' } : true
  }
  if (!authed) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
