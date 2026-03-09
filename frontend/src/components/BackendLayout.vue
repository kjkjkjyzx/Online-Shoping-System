<template>
  <div class="backend-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="asideWidth">
        <div class="aside-logo">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">{{ title }}</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#ff6700"
          router
          class="aside-menu"
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.path"
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主区域 -->
      <el-container class="main-container">
        <!-- 顶部栏 -->
        <el-header class="main-header">
          <div class="header-left">
            <el-button class="back-btn" size="small" @click="router.push('/')">
              <el-icon><ArrowLeft /></el-icon>返回首页
            </el-button>
          </div>
          <div class="header-right">
            <el-avatar :size="28" class="user-avatar" :src="userAvatar || undefined">
              {{ displayName.charAt(0) }}
            </el-avatar>
            <span class="user-name">{{ displayName }}</span>
            <el-button type="danger" size="small" plain @click="handleLogout">退出</el-button>
          </div>
        </el-header>

        <!-- 内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const props = defineProps({
  title: { type: String, default: '后台管理' },
  menuItems: { type: Array, default: () => [] },
  asideWidth: { type: String, default: '220px' }
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const displayName = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '用户')
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.backend-layout {
  min-height: 100vh;
}

/* ---- 侧边栏 ---- */
.el-aside {
  background-color: var(--color-bg-aside);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.aside-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-color: var(--color-bg-aside-header);
  padding: 0 16px;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 22px;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-inverse);
  white-space: nowrap;
}

.aside-menu {
  flex: 1;
  border-right: none !important;
  overflow-y: auto;
}

/* 菜单项激活指示条 */
:deep(.el-menu-item.is-active) {
  color: var(--color-primary) !important;
  background-color: rgba(255, 103, 0, 0.12) !important;
  border-right: 3px solid var(--color-primary);
}

:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.06) !important;
}

/* ---- 顶部栏 ---- */
.el-container {
  min-height: 100vh;
}

.main-container {
  flex: 1;
  min-height: 100vh;
  overflow: hidden;
}

.main-header {
  background-color: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border-medium);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-lg);
  height: 60px !important;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
}

.header-left {
  display: flex;
  align-items: center;
}

.back-btn {
  color: var(--color-primary) !important;
  border: 1.5px solid var(--color-primary) !important;
  border-radius: var(--radius-pill) !important;
  background: var(--color-bg-card) !important;
  transition: var(--transition);
}

.back-btn:hover {
  background: var(--color-primary) !important;
  color: var(--color-text-inverse) !important;
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.user-avatar {
  background: var(--gradient-avatar);
  color: var(--color-text-inverse);
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.user-name {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin-right: var(--space-xs);
}

/* ---- 内容区 ---- */
.main-content {
  background-color: var(--color-bg-main);
  padding: var(--space-lg);
  overflow-y: auto;
}
</style>
