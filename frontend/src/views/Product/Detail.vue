<template>
  <div class="detail-page">
    <main class="main-content">
      <div class="back-nav">
        <el-button class="back-btn" @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
      </div>
      <div class="product-info" v-if="product">
        <div class="product-image">
          <img :src="product.images || productPlaceholder" alt="商品" />
        </div>
        <div class="product-detail">
          <h1>{{ product.name }}</h1>
          <p class="price">¥{{ product.price }} <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span></p>
          <p class="description">{{ product.description }}</p>
          <div class="specs">
            <div class="spec-item">
              <span>数量</span>
              <el-input-number v-model="quantity" :min="1" :max="product.stock || 99" />
            </div>
          </div>
          <div class="actions">
            <el-button type="warning" size="large" @click="addToCart" :disabled="!product.stock">加入购物车</el-button>
            <el-button type="primary" size="large" @click="buyNow" :disabled="!product.stock">立即购买</el-button>
          </div>
        </div>
      </div>
      <div v-else-if="loading" class="loading">加载中...</div>
      <div v-else class="error">商品不存在或已下架</div>

      <div class="product-desc" v-if="product">
        <h2>商品详情</h2>
        <div class="desc-content">
          <p>{{ product.description }}</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getProductDetail } from '@/api/product'
import { addToCart as apiAddToCart } from '@/api/cart'
import { productPlaceholder } from '@/utils/placeholders'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)

onMounted(() => {
  loadProduct()
})

const loadProduct = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getProductDetail(id)
    product.value = res.data
  } catch (error) {
    product.value = null
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await apiAddToCart({ userId: userStore.userId, productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已添加到购物车')
  } catch (error) {
    // 错误消息已由请求拦截器统一提示
  }
}

const buyNow = async () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await apiAddToCart({ userId: userStore.userId, productId: product.value.id, quantity: quantity.value })
    router.push('/cart')
  } catch (error) {
    // 错误消息已由请求拦截器统一提示
  }
}
</script>

<style scoped>
.main-content {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: var(--space-lg);
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
.product-info {
  background: var(--color-bg-card);
  padding: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  gap: 40px;
  margin-bottom: var(--space-lg);
  min-height: 500px;
  align-items: center;
  box-shadow: var(--shadow-sm);
}
.product-image img {
  width: 400px;
  height: 400px;
  object-fit: cover;
  border-radius: var(--radius-md);
}
.product-detail { flex: 1; }
.product-detail h1 {
  font-size: var(--font-size-xl);
  color: var(--color-text-primary);
  margin-bottom: 15px;
  line-height: 1.4;
}
.product-detail .price {
  color: var(--color-primary);
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 15px;
}
.original-price {
  color: var(--color-text-placeholder);
  font-size: var(--font-size-md);
  text-decoration: line-through;
  margin-left: 10px;
  font-weight: normal;
}
.description {
  color: var(--color-text-secondary);
  margin-bottom: var(--space-lg);
  line-height: 1.8;
}
.specs { margin-bottom: 30px; }
.spec-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}
.spec-item span {
  width: 60px;
  color: var(--color-text-secondary);
}
.actions { display: flex; gap: 15px; }
.product-desc {
  background: var(--color-bg-card);
  padding: 30px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}
.product-desc h2 {
  font-size: var(--font-size-lg);
  margin-bottom: var(--space-lg);
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 10px;
  color: var(--color-text-primary);
}
.desc-content { color: var(--color-text-secondary); line-height: 1.8; }
</style>
