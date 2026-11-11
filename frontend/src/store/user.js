import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getInfo } from '@/api/auth'

// 用户状态：token + 用户信息
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLogin = computed(() => !!token.value)
  const role = computed(() => user.value?.role || '')
  const isAdmin = computed(() => role.value === 'ADMIN')

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUser(val) {
    user.value = val
    localStorage.setItem('user', JSON.stringify(val))
  }

  // 登录
  async function login(form) {
    const data = await loginApi(form)
    setToken(data.token)
    setUser(data.user)
    return data
  }

  // 拉取最新用户信息
  async function fetchInfo() {
    const data = await getInfo()
    setUser(data)
    return data
  }

  // 退出登录
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLogin, role, isAdmin, setToken, setUser, login, fetchInfo, logout }
})