import { defineStore } from 'pinia'
import { ref } from 'vue'

function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]))
  } catch (e) {
    return null
  }
}

export const useUserStore = defineStore('user', () => {
  const storedToken = localStorage.getItem('token') || ''
  const token = ref(storedToken)
  const userInfo = ref(null)

  // 从已存储的 token 中恢复 userId / role
  const payload = storedToken ? parseJwt(storedToken) : null
  const userId = ref(payload?.userId || null)
  const role = ref(payload?.role ?? null) // 0=admin 1=user 2=merchant

  /**
   * 设置 Token，并解析出 userId / role
   */
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    const parsed = parseJwt(newToken)
    userId.value = parsed?.userId || null
    role.value = parsed?.role ?? null
  }

  /**
   * 设置用户信息
   */
  function setUserInfo(info) {
    userInfo.value = info
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    userId.value = null
    role.value = null
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userId,
    role,
    userInfo,
    setToken,
    setUserInfo,
    logout
  }
})
