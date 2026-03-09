<template>
  <header class="header">
    <div class="header-inner">
      <div class="logo">
        <router-link to="/">网上商城</router-link>
      </div>
      <div class="search-box">
        <input type="text" placeholder="搜索商品" v-model="keyword" @keyup.enter="handleSearch" />
        <button @click="handleSearch">搜索</button>
      </div>
      <div class="user-actions">
        <template v-if="isLogin">
          <!-- 仅普通用户显示购物车和订单 -->
          <template v-if="userRole === 1">
            <router-link to="/cart" class="nav-item">
              <div class="nav-icon-wrap">
                <el-icon><ShoppingCart /></el-icon>
                <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
              </div>
              <span class="nav-label">购物车</span>
            </router-link>
            <router-link to="/order" class="nav-item">
              <div class="nav-icon-wrap"><el-icon><List /></el-icon></div>
              <span class="nav-label">我的订单</span>
            </router-link>
          </template>
          <!-- 管理员入口 -->
          <router-link v-if="userRole === 0" to="/admin" class="nav-item">
            <div class="nav-icon-wrap"><el-icon><Setting /></el-icon></div>
            <span class="nav-label">管理后台</span>
          </router-link>
          <!-- 商家入口 -->
          <router-link v-if="userRole === 2" to="/merchant" class="nav-item">
            <div class="nav-icon-wrap"><el-icon><Shop /></el-icon></div>
            <span class="nav-label">商家后台</span>
          </router-link>
          <router-link to="/user" class="nav-item">
            <div class="nav-icon-wrap">
              <img v-if="userAvatar" :src="userAvatar" class="header-avatar" alt="头像" />
              <el-icon v-else><UserFilled /></el-icon>
            </div>
            <span class="nav-label">{{ userName }}</span>
          </router-link>
          <a class="nav-item logout" @click="handleLogout">
            <div class="nav-icon-wrap"><el-icon><SwitchButton /></el-icon></div>
            <span class="nav-label">退出</span>
          </a>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-item">
            <div class="nav-icon-wrap"><el-icon><User /></el-icon></div>
            <span class="nav-label">登录</span>
          </router-link>
          <router-link to="/register" class="nav-item">
            <div class="nav-icon-wrap"><el-icon><EditPen /></el-icon></div>
            <span class="nav-label">注册</span>
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, List, UserFilled, SwitchButton, User, EditPen, Setting, Shop } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getCartList } from '@/api/cart'

const router = useRouter()
const userStore = useUserStore()

const keyword = ref('')
const cartCount = ref(0)

const isLogin = computed(() => !!userStore.token)
const userName = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '用户')
const userRole = computed(() => userStore.role)
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

const handleSearch = () => {
  router.push({ path: '/product', query: keyword.value ? { keyword: keyword.value } : undefined })
    .catch(() => {})
}

const handleLogout = () => {
  userStore.logout()
  cartCount.value = 0
  ElMessage.success('退出成功')
  router.push('/')
}

const loadCartCount = async () => {
  if (isLogin.value && userStore.role === 1 && userStore.userId) {
    try {
      const res = await getCartList({ userId: userStore.userId })
      cartCount.value = (res.data || []).filter(i => i.selected === 1 || i.selected === true).length
    } catch {
      cartCount.value = 0
    }
  } else {
    cartCount.value = 0
  }
}

onMounted(() => {
  loadCartCount()
})

// 登录状态变化时刷新购物车数量
watch(() => userStore.token, (val) => {
  if (val) loadCartCount()
  else cartCount.value = 0
})
</script>

<style scoped>
.header {
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-sm);
}

.header-inner {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 0 var(--space-xl);
  height: var(--header-height);
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 24px;
}

.logo a {
  font-size: var(--font-size-2xl);
  font-weight: 800;
  color: var(--color-primary);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.search-box {
  display: flex;
  width: 480px;
}

.search-box input {
  flex: 1;
  padding: 0 16px;
  height: 38px;
  border: 1.5px solid var(--color-border);
  border-right: none;
  border-radius: var(--radius-pill) 0 0 var(--radius-pill);
  outline: none;
  font-size: var(--font-size-base);
  transition: var(--transition);
  background: var(--color-bg-page);
}

.search-box input:focus {
  border-color: var(--color-primary);
  background: var(--color-bg-card);
}

.search-box button {
  padding: 0 20px;
  height: 38px;
  background: var(--color-primary);
  color: var(--color-text-inverse);
  border: none;
  border-radius: 0 var(--radius-pill) var(--radius-pill) 0;
  cursor: pointer;
  font-size: var(--font-size-base);
  font-weight: 600;
  transition: var(--transition);
  white-space: nowrap;
}

.search-box button:hover {
  background: var(--color-primary-hover);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 2px;
  white-space: nowrap;
  flex-shrink: 0;
  justify-self: end;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  padding: 6px 14px;
  min-width: 58px;
  color: var(--color-text-secondary);
  text-decoration: none;
  border-radius: var(--radius-md);
  transition: var(--transition);
  cursor: pointer;
  line-height: 1;
}

.nav-item:hover {
  color: var(--color-primary);
  background: var(--color-primary-bg);
}

.nav-item.logout:hover {
  color: var(--color-danger);
  background: rgba(245, 108, 108, 0.08);
}

.nav-icon-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.nav-label {
  font-size: var(--font-size-sm);
  font-weight: 500;
}

.header-avatar {
  width: 24px; height: 24px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -10px;
  background: var(--color-primary);
  color: var(--color-text-inverse);
  font-size: 10px;
  font-weight: bold;
  padding: 1px 4px;
  border-radius: var(--radius-pill);
  min-width: 16px;
  text-align: center;
  line-height: 16px;
}
</style>
