<template>
  <div class="order-page">
    <main class="main-content">
      <div class="page-header">
        <el-button class="back-btn" @click="router.push('/')"><el-icon><ArrowLeft /></el-icon>返回首页</el-button>
        <h1>我的订单</h1>
      </div>

      <div class="order-tabs">
        <el-radio-group v-model="statusFilter" @change="loadOrders">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="0">待支付</el-radio-button>
          <el-radio-button value="1">待发货</el-radio-button>
          <el-radio-button value="2">待收货</el-radio-button>
          <el-radio-button value="3">已完成</el-radio-button>
        </el-radio-group>
      </div>

      <div v-if="orderList.length === 0" class="empty-wrap">
        <el-empty description="暂无订单记录">
          <el-button type="primary" @click="router.push('/product')">去购物</el-button>
        </el-empty>
      </div>

      <div v-else class="order-list">
        <div :class="['order-item', getStatusBorderClass(order.status)]" v-for="order in orderList" :key="order.id">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
            <el-tag :type="getTagType(order.status)" size="small" effect="light">{{ getStatusText(order.status) }}</el-tag>
          </div>
          <div class="order-products">
            <div v-if="!order.items || order.items.length === 0" class="no-products">暂无商品信息</div>
            <div class="product-info" v-for="item in order.items" :key="item.id">
              <img :src="item.image || productPlaceholder" alt="商品" />
              <span class="product-name">{{ item.name }}</span>
              <span class="product-qty">{{ item.quantity }}</span>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-amount">订单金额：<strong class="price">{{ order.totalAmount }}</strong></span>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="warning" size="small" :icon="CreditCard" @click="handlePay(order.id)">去支付</el-button>
              <el-button v-if="order.status === 0" type="danger" size="small" plain @click="handleCancelOrder(order.id)">取消订单</el-button>
              <el-button v-if="order.status === 2" type="success" size="small" @click="handleConfirmOrder(order.id)">确认收货</el-button>
              <el-button size="small" plain @click="viewDetail(order.id)">查看详情</el-button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, CreditCard } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder as cancelOrderApi, confirmOrder as confirmOrderApi, payOrder as payOrderApi } from '@/api/order'
import { productPlaceholder } from '@/utils/placeholders'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const statusFilter = ref('')
const orderList = ref([])

onMounted(() => { loadOrders() })

const loadOrders = async () => {
  if (!userStore.userId) { orderList.value = []; return }
  try {
    const params = { userId: userStore.userId }
    if (statusFilter.value !== '') params.status = statusFilter.value
    const res = await getOrderList(params)
    orderList.value = (res.data || []).map(order => ({ ...order, items: order.items || [] }))
  } catch {
    orderList.value = []
  }
}

const formatTime = (t) => t ? t.replace('T', ' ').substring(0, 19) : '-'

const handlePay = async (id) => {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '模拟支付', { confirmButtonText: '确认支付', cancelButtonText: '取消', type: 'success' })
    await payOrderApi(id)
    ElMessage.success('支付成功')
    loadOrders()
  } catch { }
}

const handleCancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await cancelOrderApi(id)
    ElMessage.success('取消成功')
    loadOrders()
  } catch { }
}

const handleConfirmOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await confirmOrderApi(id)
    ElMessage.success('确认成功')
    loadOrders()
  } catch { }
}

const viewDetail = (id) => { router.push(`/order/${id}`) }

const getTagType = (status) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }[status] ?? 'info')
const getStatusBorderClass = (status) => ({ 0: 'border-warning', 1: 'border-primary', 2: 'border-success', 3: 'border-info' }[status] ?? '')
const getStatusText = (status) => ({ 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成' }[status] ?? '未知状态')
</script>

<style scoped>
.order-page {
  min-height: 80vh;
  background: var(--color-bg-page);
}
.main-content {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--space-lg);
}
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: var(--space-lg);
}
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-xs);
  color: var(--color-primary) !important;
  border: 1.5px solid var(--color-primary) !important;
  border-radius: var(--radius-pill) !important;
  padding: 0 var(--space-md) !important;
  height: 34px !important;
  font-size: var(--font-size-base);
  background: var(--color-bg-card) !important;
  transition: var(--transition);
}
.back-btn:hover {
  background: var(--color-primary) !important;
  color: var(--color-text-inverse) !important;
}
.page-header h1 { margin: 0; font-size: var(--font-size-xl); color: var(--color-text-primary); }
.order-tabs {
  background: var(--color-bg-card);
  padding: 14px var(--space-lg);
  margin-bottom: var(--space-md);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}
.empty-wrap { background: var(--color-bg-card); border-radius: var(--radius-lg); padding: 60px 0; box-shadow: var(--shadow-sm); }
.order-list { display: flex; flex-direction: column; gap: var(--space-md); }
.order-item {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border-left: 4px solid var(--color-border);
  transition: box-shadow 0.2s;
}
.order-item:hover { box-shadow: var(--shadow-lg); }
.border-warning { border-left-color: var(--color-warning); }
.border-primary  { border-left-color: var(--color-link); }
.border-success  { border-left-color: var(--color-success); }
.border-info     { border-left-color: var(--color-info); }
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px var(--space-lg);
  background: var(--color-bg-table-header);
  border-bottom: 1px solid var(--color-border-light);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  gap: 12px;
}
.order-no { font-weight: 600; color: var(--color-text-secondary); }
.order-time { flex: 1; text-align: center; }
.order-products { padding: var(--space-md) var(--space-lg); }
.no-products { color: var(--color-border-medium); font-size: var(--font-size-sm); text-align: center; padding: 12px 0; }
.product-info {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 8px 0;
  border-bottom: 1px solid var(--color-bg-page);
}
.product-info:last-child { border-bottom: none; }
.product-info img {
  width: 56px; height: 56px;
  object-fit: cover;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}
.product-name { flex: 1; font-size: var(--font-size-base); color: var(--color-text-primary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-qty { color: var(--color-text-placeholder); font-size: var(--font-size-sm); white-space: nowrap; }
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px var(--space-lg);
  border-top: 1px solid var(--color-border-light);
  background: var(--color-bg-table-header);
}
.order-amount { font-size: var(--font-size-base); color: var(--color-text-secondary); }
.price { color: var(--color-primary); font-size: 18px; font-weight: 700; }
.order-actions { display: flex; gap: var(--space-sm); align-items: center; }
</style>