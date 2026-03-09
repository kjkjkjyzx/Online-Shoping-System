<template>
  <div class="category-manage">
    <div class="toolbar">
      <el-button type="primary" @click="openAddDialog">新增分类</el-button>
    </div>

    <el-table :data="categories" v-loading="loading" border style="width: 100%; max-width: 700px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="90" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)

const form = reactive({
  name: '',
  sortOrder: 0,
  status: 1
})

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await request.get('/categories')
    categories.value = res.data || []
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  editingId.value = null
  Object.assign(form, { name: '', sortOrder: 0, status: 1 })
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    if (editingId.value) {
      await request.put(`/categories/${editingId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/categories', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadCategories()
  } catch {}
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
  await request.delete(`/categories/${id}`)
  ElMessage.success('删除成功')
  loadCategories()
}

onMounted(loadCategories)
</script>

<style scoped>
.category-manage {
  padding: var(--space-lg);
}
.toolbar {
  margin-bottom: var(--space-lg);
}
</style>
