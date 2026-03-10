<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>用户中心</h2>
    </div>

    <el-card class="info-card">
      <template #header><span>个人信息</span></template>
      <div class="info-layout">
        <div class="avatar-col">
          <label class="avatar-upload-wrap">
            <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar-img" alt="头像" />
            <div v-else class="avatar-circle">{{ (userForm.nickname || userForm.username || '?')[0].toUpperCase() }}</div>
            <div class="avatar-overlay">
              <el-icon :size="18"><Camera /></el-icon>
              <span>更换头像</span>
            </div>
            <input type="file" accept="image/*" @change="handleAvatarUpload" hidden />
          </label>
          <div class="avatar-name">{{ userForm.nickname || userForm.username || '-' }}</div>
          <el-tag type="warning" size="large">商家</el-tag>
        </div>
        <el-form :model="userForm" label-width="80px" v-loading="loading" class="user-form">
          <el-form-item label="用户名">
            <el-input v-model="userForm.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleUpdateUser">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="quick-links-card">
      <template #header><span>快捷入口</span></template>
      <div class="links">
        <router-link to="/merchant/products" class="link-card">
          <el-icon class="link-icon"><Goods /></el-icon>
          <span>商品管理</span>
        </router-link>
        <router-link to="/merchant/orders" class="link-card">
          <el-icon class="link-icon"><Document /></el-icon>
          <span>订单管理</span>
        </router-link>
        <router-link to="/" class="link-card">
          <el-icon class="link-icon"><HomeFilled /></el-icon>
          <span>前台首页</span>
        </router-link>
      </div>
      <div class="logout-row">
        <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, Document, HomeFilled, Camera } from '@element-plus/icons-vue'
import { getUserInfo, updateUser } from '@/api/user'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const userForm = reactive({ username: '', nickname: '', email: '', phone: '', avatar: '' })

const loadUserInfo = async () => {
  if (!userStore.userId) return
  loading.value = true
  try {
    const res = await getUserInfo(userStore.userId)
    const data = res.data || {}
    userForm.username = data.username || ''
    userForm.nickname = data.nickname || ''
    userForm.email = data.email || ''
    userForm.phone = data.phone || ''
    userForm.avatar = data.avatar || ''
    userStore.setUserInfo(data)
  } finally {
    loading.value = false
  }
}

const handleUpdateUser = async () => {
  saving.value = true
  try {
    await updateUser(userStore.userId, { nickname: userForm.nickname, email: userForm.email, phone: userForm.phone, avatar: userForm.avatar })
    ElMessage.success('保存成功')
    loadUserInfo()
  } finally {
    saving.value = false
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    userStore.logout()
    router.push('/login')
  } catch { }
}

const handleAvatarUpload = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const img = new Image()
    img.onload = () => {
      const MAX = 120
      const ratio = Math.min(MAX / img.width, MAX / img.height, 1)
      const canvas = document.createElement('canvas')
      canvas.width = Math.round(img.width * ratio)
      canvas.height = Math.round(img.height * ratio)
      canvas.getContext('2d').drawImage(img, 0, 0, canvas.width, canvas.height)
      userForm.avatar = canvas.toDataURL('image/jpeg', 0.75)
    }
    img.src = ev.target.result
  }
  reader.readAsDataURL(file)
}

onMounted(loadUserInfo)
</script>

<style scoped>
.profile-page { padding: 0; }
.page-header { margin-bottom: var(--space-xl); }
.page-header h2 { margin: 0; font-size: var(--font-xl); color: var(--color-text-primary); }
.info-card { margin-bottom: var(--space-xl); }
.info-layout { display: flex; gap: 40px; align-items: flex-start; }
.avatar-col {
  display: flex; flex-direction: column; align-items: center; gap: var(--space-md);
  min-width: 160px; padding: var(--space-lg);
  border-right: 1px solid var(--color-border);
}
.avatar-upload-wrap {
  position: relative;
  width: 80px; height: 80px;
  border-radius: 50%;
  cursor: pointer;
  display: block;
  overflow: hidden;
  flex-shrink: 0;
}
.avatar-img {
  width: 80px; height: 80px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}
.avatar-circle {
  width: 80px; height: 80px; border-radius: 50%;
  background: var(--gradient-avatar);
  color: #fff; font-size: 32px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  text-transform: uppercase;
  box-shadow: var(--shadow-primary);
  user-select: none;
}
.avatar-overlay {
  position: absolute; inset: 0;
  background: rgba(0, 0, 0, 0.45);
  border-radius: 50%;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 3px;
  color: #fff;
  font-size: 11px;
  opacity: 0;
  transition: opacity 0.2s;
}
.avatar-upload-wrap:hover .avatar-overlay { opacity: 1; }
.avatar-name { font-size: var(--font-md); font-weight: 600; color: var(--color-text-primary); }
.user-form { flex: 1; padding-top: 4px; }
.links {
  display: grid; grid-template-columns: repeat(3, 1fr);
  gap: var(--space-lg); margin-bottom: var(--space-xl);
}
.link-card {
  display: flex; flex-direction: column; align-items: center; gap: var(--space-md);
  padding: 28px var(--space-lg);
  border: 1px solid var(--color-border); border-radius: var(--radius-lg);
  text-decoration: none; color: var(--color-text-secondary);
  transition: all var(--transition); background: var(--color-bg-light);
}
.link-card:hover {
  border-color: var(--color-primary); color: var(--color-primary);
  background: var(--color-primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-primary);
}
.link-icon { font-size: 36px; }
.logout-row {
  display: flex; justify-content: flex-end;
  border-top: 1px solid var(--color-border); padding-top: var(--space-lg);
}
</style>
