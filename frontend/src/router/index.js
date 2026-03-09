import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home/index.vue')
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/Product/index.vue')
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/Product/Detail.vue')
      },
      {
        path: 'cart',
        name: 'Cart',
        meta: { requiresAuth: true, roles: [1] },
        component: () => import('@/views/Cart/index.vue')
      },
      {
        path: 'order',
        name: 'Order',
        meta: { requiresAuth: true, roles: [1] },
        component: () => import('@/views/Order/index.vue')
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        meta: { requiresAuth: true, roles: [1] },
        component: () => import('@/views/Order/Detail.vue')
      },
      {
        path: 'user',
        name: 'User',
        meta: { requiresAuth: true },
        component: () => import('@/views/User/index.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/User/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/User/Register.vue')
  },
  {
    path: '/admin',
    meta: { requiresAuth: true, roles: [0] },
    component: () => import('@/views/Admin/index.vue'),
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/Admin/Dashboard.vue')
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/Admin/ProductManage.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/Admin/OrderManage.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/Admin/UserManage.vue')
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/Admin/CategoryManage.vue')
      }
    ]
  },
  {
    path: '/merchant',
    meta: { requiresAuth: true, roles: [2] },
    component: () => import('@/views/Merchant/index.vue'),
    children: [
      {
        path: '',
        redirect: '/merchant/products'
      },
      {
        path: 'products',
        name: 'MerchantProducts',
        component: () => import('@/views/Merchant/ProductManage.vue')
      },
      {
        path: 'orders',
        name: 'MerchantOrders',
        component: () => import('@/views/Merchant/OrderManage.vue')
      },
      {
        path: 'profile',
        name: 'MerchantProfile',
        component: () => import('@/views/Merchant/Profile.vue')
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Error/403.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫
router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  const role = token ? (() => {
    try { return JSON.parse(atob(token.split('.')[1]))?.role ?? null } catch { return null }
  })() : null

  if (to.meta.requiresAuth && !token) {
    return { name: 'Login', query: { redirect: to.fullPath } }
  }

  if (to.meta.roles && role !== null && !to.meta.roles.includes(role)) {
    return { path: '/403' }
  }
})

export default router
