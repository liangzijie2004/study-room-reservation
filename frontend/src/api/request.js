import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 统一 Axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：携带 token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一解包 {code,msg,data}
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 非标准响应（如文件流）直接返回
    if (res == null || typeof res.code === 'undefined') {
      return res
    }
    if (res.code === 200) {
      return res.data
    }
    // 登录失效
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.error(res.msg || '登录已失效，请重新登录')
      router.replace('/login')
      return Promise.reject(new Error(res.msg || '未登录'))
    }
    // 其它业务错误
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.error('登录已失效，请重新登录')
      router.replace('/login')
    } else {
      const msg = error.response?.data?.msg || error.message || '网络异常'
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default service