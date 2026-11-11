import { createRouter, createWebHistory } from 'vue-router'

// 路由表：学生端 / 管理端分别使用对应布局
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/student',
    component: () => import('@/layouts/StudentLayout.vue'),
    meta: { role: 'STUDENT' },
    redirect: '/student/rooms',
    children: [
      {
        path: 'rooms',
        name: 'StudentRooms',
        component: () => import('@/views/student/Rooms.vue'),
        meta: { title: '自习室浏览', role: 'STUDENT' }
      },
      {
        path: 'reserve',
        name: 'StudentReserve',
        component: () => import('@/views/student/Reserve.vue'),
        meta: { title: '座位预约', role: 'STUDENT' }
      },
      {
        path: 'my-reservations',
        name: 'MyReservations',
        component: () => import('@/views/student/MyReservations.vue'),
        meta: { title: '我的预约', role: 'STUDENT' }
      },
      {
        path: 'notices',
        name: 'StudentNotices',
        component: () => import('@/views/student/Notices.vue'),
        meta: { title: '公告', role: 'STUDENT' }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人中心', role: 'STUDENT' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { role: 'ADMIN' },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据看板', role: 'ADMIN' }
      },
      {
        path: 'rooms',
        name: 'AdminRooms',
        component: () => import('@/views/admin/Rooms.vue'),
        meta: { title: '自习室管理', role: 'ADMIN' }
      },
      {
        path: 'seats',
        name: 'AdminSeats',
        component: () => import('@/views/admin/Seats.vue'),
        meta: { title: '座位管理', role: 'ADMIN' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', role: 'ADMIN' }
      },
      {
        path: 'reservations',
        name: 'AdminReservations',
        component: () => import('@/views/admin/Reservations.vue'),
        meta: { title: '预约管理', role: 'ADMIN' }
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/Notices.vue'),
        meta: { title: '公告管理', role: 'ADMIN' }
      }
    ]
  },
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳登录页，按角色限制访问
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.meta.title) {
    document.title = `${to.meta.title} - 自习室预约系统`
  }

  // 公开页面
  if (to.meta.public) {
    // 已登录访问登录页则按角色跳转到首页
    if (token && user) {
      return next(user.role === 'ADMIN' ? '/admin/dashboard' : '/student/rooms')
    }
    return next()
  }

  // 未登录
  if (!token) {
    return next('/login')
  }

  // 角色校验
  const requiredRole = to.meta.role
  if (requiredRole && user && user.role !== requiredRole) {
    return next(user.role === 'ADMIN' ? '/admin/dashboard' : '/student/rooms')
  }

  next()
})

export default router