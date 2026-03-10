<template>
  <div class="cart-page">
    <main class="main-content">
      <div class="page-header">
        <el-button class="back-btn" @click="router.push('/')"><el-icon><ArrowLeft /></el-icon>返回首页</el-button>
        <h1>我的购物车</h1>
      </div>

      <div v-if="loading" class="empty-wrap" v-loading="true" style="min-height: 300px"></div>

      <!-- 空购物车 -->
      <div v-else-if="cartList.length === 0" class="empty-wrap">
        <el-empty description="购物车空空如也">
          <el-button type="primary" @click="router.push('/product')">去挑选商品</el-button>
        </el-empty>
      </div>

      <template v-else>
        <div class="cart-table">
          <div class="cart-header">
            <el-checkbox v-model="selectAll" @change="toggleSelectAll" />
            <span class="col-product">商品信息</span>
            <span class="col-price">单价</span>
            <span class="col-quantity">数量</span>
            <span class="col-subtotal">小计</span>
            <span class="col-action">操作</span>
          </div>

          <div class="cart-item" v-for="item in cartList" :key="item.id">
            <el-checkbox v-model="item.selected" @change="updateTotal" />
            <div class="col-product">
              <img :src="resolveImage(item.image, productPlaceholder)" alt="商品" />
              <span class="item-name">{{ item.name }}</span>
            </div>
            <span class="col-price">¥{{ item.price }}</span>
            <span class="col-quantity">
              <el-input-number v-model="item.quantity" :min="1" size="small" @change="handleUpdateQuantity(item)" />
            </span>
            <span class="col-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            <span class="col-action">
              <el-button type="danger" :icon="Delete" circle size="small" @click="deleteItem(item.id)" />
            </span>
          </div>
        </div>

        <div class="cart-footer">
          <div class="cart-total">
            <el-checkbox v-model="selectAll" @change="toggleSelectAll" />
            <span class="selected-info">已选 <strong>{{ selectedCount }}</strong> 件商品</span>
            <span class="total-label">合计：</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" :disabled="selectedCount === 0" @click="checkout">
            去结算（{{ selectedCount }}）
          </el-button>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Delete } from '@element-plus/icons-vue'
import { getCartList, updateQuantity as updateCartQuantity, deleteCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { productPlaceholder } from '@/utils/placeholders'
import { resolveImage } from '@/utils/image'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const selectAll = ref(false)
const cartList = ref([])
const loading = ref(false)

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected).length
})

const totalPrice = computed(() => {
  return cartList.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + parseFloat(item.price || 0) * item.quantity, 0)
})

onMounted(() => {
  loadCart()
})

const loadCart = async () => {
  if (!userStore.userId) {
    cartList.value = []
    return
  }
  loading.value = true
  try {
    const res = await getCartList()
    cartList.value = res.data?.map(item => ({ ...item, selected: item.selected === 1 || item.selected === true })) || []
    updateTotal()
  } catch (error) {
    cartList.value = []
  } finally {
    loading.value = false
  }
}

const toggleSelectAll = () => {
  cartList.value.forEach(item => {
    item.selected = selectAll.value
  })
}

const updateTotal = () => {
  selectAll.value = cartList.value.length > 0 && cartList.value.every(item => item.selected)
}

const handleUpdateQuantity = async (item) => {
  try {
    await updateCartQuantity(item.id, { quantity: item.quantity })
  } catch (error) {
    // 更新失败时重新加载购物车以恢复正确数量
    loadCart()
  }
}

const deleteItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCart(id)
    ElMessage.success('删除成功')
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      // 错误消息已由请求拦截器统一提示
    }
  }
}

const checkout = async () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  try {
    const selectedItems = cartList.value.filter(item => item.selected)
    const items = selectedItems.map(item => ({
      productId: item.productId,
      productName: item.name,
      productImage: resolveImage(item.image, productPlaceholder),
      price: item.price,
      quantity: item.quantity
    }))
    await createOrder({
      totalAmount: parseFloat(totalPrice.value.toFixed(2)),
      items
    })
    // 下单成功后删除已选中的购物车商品
    await Promise.all(selectedItems.map(item => deleteCart(item.id)))
    ElMessage.success('下单成功')
    router.push('/order')
  } catch (error) {
    // 错误消息已由请求拦截器统一提示
  }
}
</script>

<style scoped>
.cart-page {
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

.page-header h1 {
  margin: 0;
  font-size: var(--font-size-xl);
  color: var(--color-text-primary);
}

.empty-wrap {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: 80px 0;
  text-align: center;
  box-shadow: var(--shadow-sm);
}

.cart-table {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.cart-header,
.cart-item {
  display: flex;
  align-items: center;
  padding: var(--space-md) var(--space-lg);
  border-bottom: 1px solid var(--color-border-light);
}

.cart-header {
  background: var(--color-bg-table-header);
  font-size: var(--font-size-sm);
  color: var(--color-text-placeholder);
  font-weight: 600;
  letter-spacing: 0.3px;
}

.col-product {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.col-product img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.item-name {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.col-price {
  width: 120px;
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

.col-quantity {
  width: 150px;
  text-align: center;
}

.col-subtotal {
  width: 120px;
  text-align: center;
  font-size: var(--font-size-md);
  font-weight: 700;
  color: var(--color-primary);
}

.col-action {
  width: 80px;
  text-align: center;
}

.cart-footer {
  background: var(--color-bg-card);
  padding: var(--space-md) var(--space-lg);
  margin-top: var(--space-md);
  border-radius: var(--radius-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-md);
  position: sticky;
  bottom: 16px;
}

.cart-total {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.selected-info {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

.total-label {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

.total-price {
  font-size: var(--font-size-2xl);
  font-weight: 800;
  color: var(--color-primary);
}
</style>




