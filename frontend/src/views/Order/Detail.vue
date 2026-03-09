<template>
  <div class="order-detail-page">
    <main class="main-content">
      <div class="back-nav">
        <el-button class="back-btn" @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>返回订单列表
        </el-button>
      </div>

      <div v-if="loading" class="loading-box">
        <el-skeleton :rows="6" animated />
      </div>

      <template v-else-if="order">
        <h1>订单详情</h1>

        <el-card class="info-card">
          <template #header><span>订单信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
            <el-descriptions-item label="订单金额">¥{{ order.totalAmount }}</el-descriptions-item>
            <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ order.receiverPhone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ order.receiverAddress }}</el-descriptions-item>
            <el-descriptions-item v-if="order.remark" label="备注" :span="2">{{ order.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="items-card">
          <template #header><span>商品列表</span></template>
          <el-table :data="order.items || []" border>
            <el-table-column label="商品图片" width="80">
              <template #default="{ row }">
                <img :src="row.productImage || productPlaceholder" style="width:50px;height:50px;object-fit:cover" />
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="price" label="单价">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="小计">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="total-card">
          <div class="total-row">
            <span>商品总额：</span><span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="total-row">
            <span>运费：</span><span>¥{{ order.freightAmount || '0.00' }}</span>
          </div>
          <div class="total-row total-highlight">
            <span>实付金额：</span><span class="price">¥{{ order.payAmount }}</span>
          </div>
        </el-card>

        <div class="actions">
          <el-button v-if="order.status === 0" type="danger" @click="handleCancel">取消订单</el-button>
          <el-button v-if="order.status === 2" type="primary" @click="handleConfirm">确认收货</el-button>
        </div>
      </template>

      <el-empty v-else description="订单不存在" />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder, confirmOrder } from '@/api/order'
import { productPlaceholder } from '@/utils/placeholders'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const order = ref(null)

const statusText = (s) => ({ 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 8: '已取消' }[s] ?? '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'info', 2: '', 3: 'success', 8: 'danger' }[s] ?? '')

const loadOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch {
    order.value = null
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
  await cancelOrder(order.value.id)
  ElMessage.success('取消成功')
  loadOrder()
}

const handleConfirm = async () => {
  await ElMessageBox.confirm('确认已收到商品吗？', '提示', { type: 'warning' })
  await confirmOrder(order.value.id)
  ElMessage.success('确认收货成功')
  loadOrder()
}

onMounted(loadOrder)
</script>

<style scoped>
.main-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px var(--space-md);
}
.back-nav { margin-bottom: var(--space-md); }
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-xs);
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
  border-radius: var(--radius-pill);
  padding: 0 var(--space-md);
  height: 34px;
  font-size: var(--font-size-base);
  background: var(--color-bg-card);
  transition: var(--transition);
  cursor: pointer;
}
.back-btn:hover {
  background: var(--color-primary);
  color: var(--color-text-inverse);
}
h1 {
  margin-bottom: var(--space-lg);
  color: var(--color-text-primary);
  font-size: var(--font-size-xl);
}
.info-card, .items-card, .total-card {
  margin-bottom: var(--space-lg);
}
.total-row {
  display: flex;
  justify-content: flex-end;
  gap: 40px;
  padding: 6px var(--space-lg);
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}
.total-highlight {
  font-size: var(--font-size-md);
  font-weight: 600;
  border-top: 1px solid var(--color-border);
  margin-top: var(--space-xs);
  padding-top: 12px;
  color: var(--color-text-primary);
}
.price { color: var(--color-danger); font-size: var(--font-size-md); font-weight: 700; }
.loading-box { padding: var(--space-lg); }
.actions { display: flex; gap: 12px; justify-content: flex-end; }
</style>
