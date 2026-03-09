<template>
  <div class="register-page">
    <div class="register-box">
      <el-button class="back-btn" :icon="ArrowLeft" link @click="router.push('/user/login')">返回</el-button>
      <h2>用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="账号类型">
          <el-radio-group v-model="form.role">
            <el-radio :value="1">普通用户</el-radio>
            <el-radio :value="2">商家入驻</el-radio>
            <el-radio :value="0">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.role === 0" prop="adminSecret">
          <el-input
            v-model="form.adminSecret"
            type="password"
            show-password
            placeholder="请输入管理员密钥"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleRegister">注册</el-button>
        </el-form-item>
        <div class="links">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, ArrowLeft } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()
const formRef = ref(null)

const roleOptions = [
  { value: 1, icon: '👤', label: '普通用户' },
  { value: 2, icon: '🏪', label: '商家入驻' },
  { value: 0, icon: '🔒', label: '管理员' },
]

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  role: 1, // 默认普通用户
  adminSecret: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  adminSecret: [
    {
      validator: (rule, value, callback) => {
        if (form.role === 0 && (!value || value.trim() === '')) {
          callback(new Error('请输入管理员密钥'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  try {
    await register(form)
    ElMessage.success('注册成功')
    router.push('/login')
  } catch {
    // 错误消息已由请求拦截器统一提示
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-auth);
  padding: 20px;
}

.register-box {
  background: var(--color-bg-card);
  padding: 40px;
  border-radius: var(--radius-lg);
  width: 480px;
  box-shadow: var(--shadow-lg);
}

.register-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 16px;
}

.brand-icon {
  font-size: 26px;
}

.brand-name {
  font-size: var(--font-size-2xl);
  font-weight: 800;
  color: var(--color-primary);
}

.register-box h2 {
  text-align: center;
  margin-bottom: 4px;
  color: var(--color-text-primary);
  font-size: var(--font-size-xl);
  font-weight: 700;
}

.register-sub {
  text-align: center;
  color: var(--color-text-placeholder);
  font-size: var(--font-size-sm);
  margin-bottom: 24px;
}

/* 角色选择卡片 */
.role-cards {
  display: flex;
  gap: var(--space-sm);
  width: 100%;
}

.role-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition);
}

.role-card:hover {
  border-color: var(--color-primary-light);
  background: var(--color-primary-bg);
}

.role-card.active {
  border-color: var(--color-primary);
  background: var(--color-primary-bg);
  box-shadow: 0 0 0 2px var(--color-primary-shadow);
}

.role-icon {
  font-size: 22px;
}

.role-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  font-weight: 500;
}

.role-card.active .role-label {
  color: var(--color-primary);
  font-weight: 700;
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
