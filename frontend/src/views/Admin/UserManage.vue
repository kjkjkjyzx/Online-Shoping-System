<template>
  <div class="user-manage">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名/昵称"
        clearable
        style="width: 200px; margin-right: 12px"
        @keyup.enter="handleSearch"
      />
      <el-select v-model="roleFilter" placeholder="筛选角色" clearable style="width: 120px; margin-right: 12px" @change="handleSearch">
        <el-option label="管理员" :value="0" />
        <el-option label="普通用户" :value="1" />
        <el-option label="商家" :value="2" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <el-table :data="users" v-loading="loading" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.role)">{{ roleText(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-select
            :model-value="row.role"
            size="small"
            style="width: 90px; margin-right: 8px"
            @change="(val) => changeRole(row.id, val)"
          >
            <el-option label="管理员" :value="0" />
            <el-option label="用户" :value="1" />
            <el-option label="商家" :value="2" />
          </el-select>
          <el-button
            size="small"
            :type="row.status === 1 ? 'danger' : 'success'"
            @click="toggleStatus(row)"
          >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next, jumper"
      @current-change="loadUsers"
      style="margin-top: 20px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const users = ref([])
const keyword = ref('')
const roleFilter = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const roleText = (r) => ({ 0: '管理员', 1: '用户', 2: '商家' }[r] ?? '未知')
const roleTagType = (r) => ({ 0: 'danger', 1: 'info', 2: 'warning' }[r] ?? 'info')

const loadUsers = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    if (roleFilter.value !== null && roleFilter.value !== undefined) params.role = roleFilter.value
    const res = await request.get('/users', { params })
    users.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadUsers()
}

const changeRole = async (id, role) => {
  await request.put(`/users/${id}/role`, null, { params: { role } })
  ElMessage.success('角色修改成功')
  loadUsers()
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await request.put(`/users/${row.id}/status`, null, { params: { status: newStatus } })
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadUsers()
}

onMounted(loadUsers)
</script>

<style scoped>
.user-manage {
  padding: var(--space-lg);
}
.toolbar {
  margin-bottom: var(--space-lg);
  display: flex;
  align-items: center;
}
</style>
