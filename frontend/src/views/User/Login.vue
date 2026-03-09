<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-brand">
        <span class="brand-icon">🛍️</span>
        <span class="brand-name">网上商城</span>
      </div>
      <h2>欢迎回来</h2>
      <p class="login-sub">登录您的购物账户</p>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" size="large" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="links">
          <router-link to="/register">没有账号？立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getUserInfo } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  try {
    const res = await login(form)
    userStore.setToken(res.data)
    // 登录后立即加载用户信息（含头像），使顶栏头像即时生效
    try {
      const infoRes = await getUserInfo(userStore.userId)
      userStore.setUserInfo(infoRes.data)
    } catch { }
    ElMessage.success('登录成功')
    // 按角色跳转：管理员→/admin，商家→/merchant，用户→首页或来源页
    const role = userStore.role
    const redirect = route.query.redirect
    if (role === 0) {
      router.push('/admin')
    } else if (role === 2) {
      router.push('/merchant')
    } else {
      router.push(redirect || '/')
    }
  } catch {
    // 错误消息已由请求拦截器统一提示
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-auth);
  padding: 20px;
}

.login-box {
  background: var(--color-bg-card);
  padding: 48px 40px 40px;
  border-radius: var(--radius-lg);
  width: 420px;
  box-shadow: var(--shadow-lg);
}

.login-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}

.brand-icon {
  font-size: 28px;
}

.brand-name {
  font-size: var(--font-size-2xl);
  font-weight: 800;
  color: var(--color-primary);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 6px;
  color: var(--color-text-primary);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

.login-sub {
  text-align: center;
  color: var(--color-text-placeholder);
  font-size: var(--font-size-sm);
  margin-bottom: 28px;
}

.links {
  text-align: center;
  margin-top: var(--space-md);
}

.links a {
  color: var(--color-primary);
  font-size: var(--font-size-base);
  transition: var(--transition);
}

.links a:hover {
  opacity: 0.75;
}
</style>
