<template>
  <div class="product-manage">
    <div class="toolbar">
      <el-button type="primary" @click="openAddDialog">新增商品</el-button>
    </div>

    <el-table :data="pagedProducts" v-loading="loading" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="price" label="价格">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="90" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: center"
      @current-change="handlePageChange"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑商品' : '新增商品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="img-upload-wrap">
            <div class="img-preview" v-if="form.images">
              <img :src="form.images" alt="商品图片" />
              <div class="img-mask">
                <el-icon class="del-icon" @click="form.images = ''"><Delete /></el-icon>
              </div>
            </div>
            <label v-else class="upload-area">
              <el-icon><Plus /></el-icon>
              <span>点击上传</span>
              <input type="file" accept="image/*" @change="handleImgUpload" hidden />
            </label>
          </div>
          <el-input v-model="form.images" placeholder="或粘贴图片链接" style="margin-top: 8px" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)
const products = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const pageNum = ref(1)
const pageSize = ref(15)
const total = ref(0)

const pagedProducts = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return products.value.slice(start, start + pageSize.value)
})

const handlePageChange = (page) => { pageNum.value = page }

const form = reactive({
  name: '',
  categoryId: null,
  price: 0,
  stock: 0,
  description: '',
  images: '',
  status: 1
})

const loadProducts = async () => {
  loading.value = true
  try {
    // 使用 /products/mine 接口，商家只能获取自己的商品（含下架）
    const res = await request.get('/products/mine')
    products.value = res.data || []
    total.value = products.value.length
    pageNum.value = 1
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await request.get('/categories')
  categories.value = res.data || []
}

const openAddDialog = () => {
  editingId.value = null
  Object.assign(form, { id: null, name: '', categoryId: null, price: 0, stock: 0, description: '', images: '', status: 1 })
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    const payload = { ...form, merchantId: userStore.userId }
    if (editingId.value) {
      await request.put(`/products/${editingId.value}`, payload)
      ElMessage.success('更新成功')
    } else {
      await request.post('/products', payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadProducts()
  } catch {
    // 错误由请求拦截器统一提示
  }
}

const handleImgUpload = (e) => {
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
      const MAX = 400
      const ratio = Math.min(MAX / img.width, MAX / img.height, 1)
      const canvas = document.createElement('canvas')
      canvas.width = Math.round(img.width * ratio)
      canvas.height = Math.round(img.height * ratio)
      canvas.getContext('2d').drawImage(img, 0, 0, canvas.width, canvas.height)
      form.images = canvas.toDataURL('image/jpeg', 0.8)
    }
    img.src = ev.target.result
  }
  reader.readAsDataURL(file)
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该商品？', '提示', { type: 'warning' })
  await request.delete(`/products/${id}`)
  ElMessage.success('删除成功')
  loadProducts()
}

onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
.product-manage {
  padding: var(--space-lg);
}
.toolbar {
  margin-bottom: var(--space-lg);
}
.img-upload-wrap {
  width: 100px;
  height: 100px;
}
.img-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--color-border);
}
.img-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.img-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: var(--transition);
}
.img-preview:hover .img-mask {
  opacity: 1;
}
.del-icon {
  color: #fff;
  font-size: 22px;
  cursor: pointer;
}
.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  border: 1.5px dashed var(--color-border-medium);
  border-radius: var(--radius-md);
  cursor: pointer;
  color: var(--color-text-placeholder);
  font-size: var(--font-size-sm);
  gap: 6px;
  transition: var(--transition);
}
.upload-area:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-bg);
}
.upload-area .el-icon {
  font-size: 24px;
}
</style>
