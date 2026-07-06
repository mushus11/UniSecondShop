import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

api.interceptors.request.use(config => {
  const jwt = localStorage.getItem('jwt')
  if (jwt) {
    config.headers.Authorization = `Bearer ${jwt}`
  }
  return config
}, error => Promise.reject(error))

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt')
      localStorage.removeItem('id')
      localStorage.removeItem('username')
      localStorage.removeItem('access')
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/Login'
    } else if (error.response?.status === 403) {
      ElMessage.error('没有权限执行此操作')
    } else {
      const msg = error.response?.data?.message || error.response?.data || '请求失败，请稍后重试'
      ElMessage.error(typeof msg === 'string' ? msg : '请求失败，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default api
