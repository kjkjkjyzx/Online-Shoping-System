<template>
  <BackendLayout title="商家后台" :menu-items="menuItems" />
</template>

<script setup>
import { markRaw } from 'vue'
import { Goods, Document, UserFilled } from '@element-plus/icons-vue'
import BackendLayout from '@/components/BackendLayout.vue'

const menuItems = [
  { path: '/merchant/products', icon: markRaw(Goods),       label: '商品管理' },
  { path: '/merchant/orders',   icon: markRaw(Document),    label: '订单管理' },
  { path: '/merchant/profile',  icon: markRaw(UserFilled),  label: '用户中心' },
]
</script>