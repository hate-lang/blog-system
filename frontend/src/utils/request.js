// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router' // 引入路由用于打回登录页

const request = axios.create({
  // 优先读取环境变量，如果没有（比如本地开发），则回退到 localhost
  baseURL: import.meta.env.VITE_APP_BASE_URL || 'http://localhost:8080', //记得更换为服务器id地址
  timeout: 10000, // 10秒超时
})

// 1. 请求拦截器：发请求前，自动塞入 Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 2. 响应拦截器：收到响应后，统一处理报错和 401
request.interceptors.response.use(
  (response) => {
    // 这里可以根据你们后端的 code 统一拦截
    // 比如 if (response.data.code !== 200) { ElMessage.error(...) }
    return response
  },
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.warning('登录已过期，请重新登录')
        localStorage.removeItem('token') // 清除失效 token
        router.push('/login') // 自动踢回登录页
      } else {
        ElMessage.error(error.response.data?.message || '服务器异常')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  },
)

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 如果你要去的地方不是登录页，也不是前台公开页，而且你还没带 Token
  // (假设咱们后台的路由 name 都叫 layout, article 等)
  if (to.name !== 'login' && to.name !== 'frontHome' && !token) {
    ElMessage.warning('请先登录！')
    next('/login') // 强行打回登录页
  } else {
    next() // 放行
  }
})
export default request
