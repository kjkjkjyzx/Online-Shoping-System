<template>
  <div class="order-manage">
    <div class="toolbar">
      <el-select v-model="statusFilter" placeholder="筛选状态" clearable style="width: 120px; margin-right: 12px" @change="loadOrders">
        <el-option label="全部" value="" />
        <el-option label="待支付" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="8" />
      </el-select>
      <el-input v-model="keyword" placeholder="订单号/用户" clearable style="width: 200px; margin-right: 12px" @keyup.enter="loadOrders" />
      <el-button type="primary" @click="loadOrders">搜索</el-button>
    </div>

    <el-table :data="orders" v-loading="loading" border style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column prop="totalAmount" label="金额">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" />
      <el-table-column prop="receiverPhone" label="手机号" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="160" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="primary" @click="handleShip(row.id)">发货</el-button>
          <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadOrders"
      style="margin-top: 20px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const statusFilter = ref('')
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusText = (s) => ({ 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 8: '已取消' }[s] ?? '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'info', 2: 'primary', 3: 'success', 8: 'danger' }[s] ?? 'info')

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (statusFilter.value !== '') params.status = statusFilter.value
    if (keyword.value) params.keyword = keyword.value
    const res = await request.get('/orders/admin', { params })
    orders.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleShip = async (id) => {
  await ElMessageBox.confirm('确认发货？', '提示', { type: 'warning' })
  await request.put(`/orders/${id}/ship`)
  ElMessage.success('发货成功')
  loadOrders()
}

const viewDetail = (id) => {
  window.open(`/#/order/${id}`, '_blank')
}

onMounted(loadOrders)
</script>

<style scoped>
.order-manage {
  padding: var(--space-lg);
}
.toolbar {
  margin-bottom: var(--space-lg);
  display: flex;
  align-items: center;
}
</style>
