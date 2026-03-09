<template>
  <div class="order-manage">
    <div class="toolbar">
      <el-select v-model="statusFilter" placeholder="筛选状态" clearable @change="loadOrders">
        <el-option label="全部" value="" />
        <el-option label="待支付" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="8" />
      </el-select>
    </div>

    <el-table :data="pagedOrders" v-loading="loading" border style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="totalAmount" label="金额">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="username" label="买家" />
      <el-table-column prop="userPhone" label="手机号" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="160" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)
const orders = ref([])
const statusFilter = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const pagedOrders = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return orders.value.slice(start, start + pageSize.value)
})

const handlePageChange = (page) => { pageNum.value = page }

const statusText = (s) => {
  const map = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 8: '已取消' }
  return map[s] ?? '未知'
}
const statusType = (s) => {
  const map = { 0: 'warning', 1: 'info', 2: 'primary', 3: 'success', 8: 'danger' }
  return map[s] ?? 'info'
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { merchantId: userStore.userId } // 只查询当前商家的订单
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await request.get('/orders/merchant', { params })
    orders.value = res.data || []
    total.value = orders.value.length
    pageNum.value = 1
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

onMounted(loadOrders)
</script>

<style scoped>
.order-manage {
  padding: var(--space-lg);
}
.toolbar {
  margin-bottom: var(--space-lg);
}
</style>
