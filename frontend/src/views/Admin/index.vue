<template>
  <BackendLayout title="管理后台" :menu-items="menuItems" />
</template>

<script setup>
import { markRaw } from 'vue'
import { DataAnalysis, Goods, Document, User, Menu } from '@element-plus/icons-vue'
import BackendLayout from '@/components/BackendLayout.vue'

const menuItems = [
  { path: '/admin/dashboard', icon: markRaw(DataAnalysis), label: '控制台' },
  { path: '/admin/products',  icon: markRaw(Goods),        label: '商品管理' },
  { path: '/admin/orders',    icon: markRaw(Document),     label: '订单管理' },
  { path: '/admin/users',     icon: markRaw(User),         label: '用户管理' },
  { path: '/admin/categories',icon: markRaw(Menu),         label: '分类管理' },
]
</script>
