<template>
  <div class="error-page">
    <div class="error-content">
      <h1>403</h1>
      <p>抱歉，您没有权限访问该页面</p>
      <el-button type="primary" @click="router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.error-content {
  text-align: center;
}
h1 {
  font-size: 100px;
  color: #f56c6c;
  margin: 0;
}
p {
  font-size: 18px;
  color: #606266;
  margin: 16px 0 32px;
}
</style>
