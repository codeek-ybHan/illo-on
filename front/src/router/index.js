import { createRouter, createWebHistory } from 'vue-router'

import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'

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
          path: 'projects/:id',
          name: 'project-detail',
          component: () => import('@/views/project/ProjectDetailView.vue'),
          meta: { title: '프로젝트', breadcrumb: ['프로젝트', '프로젝트 상세'] },
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
          meta: { title: '로그인' },
        },
        {
          path: 'signup',
          name: 'signup',
          component: () => import('@/views/auth/SignupView.vue'),
          meta: { title: '회원가입' },
        },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

// 인증 가드 자리 (이번 범위 밖)
// router.beforeEach((to) => { ... })

export default router
