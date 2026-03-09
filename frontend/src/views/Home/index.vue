<template>
  <div class="home">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">发现好物，尽在<span class="brand">网上商城</span></h1>
          <p class="hero-subtitle">精选优质商品，极速配送到家，品质生活从这里开始</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" class="hero-btn" @click="router.push('/product')">立即购物</el-button>
            <el-button size="large" class="hero-btn-outline" @click="router.push('/product')">浏览全部商品</el-button>
          </div>
        </div>
        <div class="hero-deco">
          <div class="deco-circle c1"></div>
          <div class="deco-circle c2"></div>
          <div class="deco-circle c3"></div>
          <div class="deco-icon">🛒</div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <main class="main-content">

      <!-- 分类卡片 -->
      <section class="category-section">
        <div class="g-section-header">
          <h2 class="g-section-title">商品分类</h2>
        </div>
        <div class="category-cards">
          <div class="cat-card" v-for="cat in categories" :key="cat.id" @click="goToCategory(cat.id)">
            <div class="cat-icon">{{ cat.icon }}</div>
            <span class="cat-name">{{ cat.name }}</span>
          </div>
        </div>
      </section>

      <!-- 热销商品 -->
      <section class="product-section">
        <div class="g-section-header">
          <h2 class="g-section-title"><span class="section-badge">🔥</span> 热销榜单</h2>
          <a class="g-view-all" @click.prevent="router.push('/product')">查看全部 →</a>
        </div>
        <div class="product-list">
          <div class="product-card" v-for="item in hotProducts" :key="item.id">
            <div class="card-img-wrap" @click="goToDetail(item.id)">
              <img :src="productPlaceholder" alt="商品" />
              <div class="card-badge" v-if="item.sales >= 100">热销</div>
            </div>
            <div class="card-body" @click="goToDetail(item.id)">
              <h3>{{ item.name }}</h3>
              <p class="price">￥<span class="price-num">{{ item.price }}</span></p>
              <p class="sales">已售 {{ item.sales }} 件</p>
            </div>
            <div class="card-footer" @click.stop>
              <el-button
                v-if="userRole === 1"
                type="primary"
                size="small"
                round
                @click="handleAddToCart(item)"
              >加入购物车</el-button>
              <el-button v-else size="small" round @click="goToDetail(item.id)">查看详情</el-button>
            </div>
          </div>
        </div>
      </section>

      <!-- 新品上架 -->
      <section class="product-section new-section">
        <div class="g-section-header">
          <h2 class="g-section-title"><span class="section-badge">✨</span> 新品上架</h2>
          <a class="g-view-all" @click.prevent="router.push('/product')">查看全部 →</a>
        </div>
        <div class="product-list">
          <div class="product-card" v-for="item in newProducts" :key="item.id">
            <div class="card-img-wrap" @click="goToDetail(item.id)">
              <img :src="productPlaceholder" alt="商品" />
              <div class="card-badge new-badge">新品</div>
            </div>
            <div class="card-body" @click="goToDetail(item.id)">
              <h3>{{ item.name }}</h3>
              <p class="price">￥<span class="price-num">{{ item.price }}</span></p>
              <p class="sales">已售 {{ item.sales }} 件</p>
            </div>
            <div class="card-footer" @click.stop>
              <el-button
                v-if="userRole === 1"
                type="primary"
                size="small"
                round
                @click="handleAddToCart(item)"
              >加入购物车</el-button>
              <el-button v-else size="small" round @click="goToDetail(item.id)">查看详情</el-button>
            </div>
          </div>
        </div>
      </section>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotProducts, getProductList } from '@/api/product'
import { addToCart } from '@/api/cart'
import { productPlaceholder } from '@/utils/placeholders'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userRole = userStore.role

const hotProducts = ref([])
const newProducts = ref([])

const categories = [
  { id: 1, name: '服装', icon: '👗' },
  { id: 2, name: '数码', icon: '📱' },
  { id: 3, name: '家居', icon: '🏠' },
  { id: 4, name: '美妆', icon: '💄' },
  { id: 5, name: '食品', icon: '🍎' },
  { id: 6, name: '图书', icon: '📚' },
]

onMounted(() => {
  loadHotProducts()
  loadNewProducts()
})

const loadHotProducts = async () => {
  try {
    const res = await getHotProducts({ limit: 8 })
    hotProducts.value = res.data || []
  } catch {
    hotProducts.value = []
  }
}

const loadNewProducts = async () => {
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 4 })
    newProducts.value = res.data?.records || res.data || []
  } catch {
    newProducts.value = []
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const goToCategory = (id) => {
  router.push({ path: '/product', query: { categoryId: id } })
}

const handleAddToCart = async (item) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  try {
    await addToCart({ userId: userStore.userId, productId: item.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch {
    ElMessage.error('加入失败，请重试')
  }
}
</script>

<style scoped>
/* ========== Hero Banner ========== */
.hero-banner {
  background: linear-gradient(135deg, #ff6700 0%, #ff9a3c 50%, #ffb347 100%);
  padding: 60px 20px;
  overflow: hidden;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.hero-text {
  flex: 1;
  max-width: 560px;
}

.hero-title {
  font-size: 36px;
  font-weight: 800;
  color: #fff;
  line-height: 1.3;
  margin: 0 0 16px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.hero-title .brand {
  color: #fff3e0;
}

.hero-subtitle {
  font-size: 16px;
  color: rgba(255,255,255,0.9);
  margin: 0 0 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-btn {
  background: #fff !important;
  color: #ff6700 !important;
  border: none !important;
  font-weight: bold;
  padding: 12px 32px;
  font-size: 15px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.hero-btn:hover {
  background: #fff3e0 !important;
}

.hero-btn-outline {
  background: transparent !important;
  color: #fff !important;
  border: 2px solid rgba(255,255,255,0.8) !important;
  font-size: 15px;
}

.hero-btn-outline:hover {
  background: rgba(255,255,255,0.15) !important;
}

.hero-deco {
  position: relative;
  width: 260px;
  height: 220px;
  flex-shrink: 0;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.25;
  background: #fff;
}

.c1 { width: 180px; height: 180px; top: 10px; left: 20px; }
.c2 { width: 120px; height: 120px; top: 60px; left: 100px; opacity: 0.15; }
.c3 { width: 70px; height: 70px; top: 0; left: 150px; opacity: 0.2; }

.deco-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 80px;
  filter: drop-shadow(0 4px 12px rgba(0,0,0,0.2));
}

/* ========== 主内容区 ========== */
.main-content {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 30px var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* ========== 分类卡片 ========== */
.category-section {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  box-shadow: var(--shadow-sm);
}

.category-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.cat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 10px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: var(--transition);
  border: 1px solid var(--color-border-light);
}

.cat-card:hover {
  background: var(--color-primary-bg);
  transform: translateY(-4px);
  border-color: #ffcf9d;
  box-shadow: var(--shadow-md);
}

.cat-icon {
  font-size: 36px;
  line-height: 1;
}

.cat-name {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-text-primary);
}

/* ========== 商品区块 ========== */
.product-section {
  background: var(--color-bg-card);
  padding: var(--space-xl);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.new-section {
  border-top: 3px solid var(--color-success);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-lg);
}

.section-header h2 {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
}

.section-badge {
  margin-right: 6px;
}

.view-all {
  font-size: var(--font-size-base);
  color: var(--color-primary);
  cursor: pointer;
  transition: var(--transition);
}

.view-all:hover {
  opacity: 0.7;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: box-shadow 0.25s, transform 0.25s;
  display: flex;
  flex-direction: column;
  background: var(--color-bg-card);
}

.product-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
}

.card-img-wrap {
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.card-img-wrap img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
  transition: transform 0.3s;
}

.card-img-wrap:hover img {
  transform: scale(1.05);
}

.card-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: var(--color-primary);
  color: var(--color-text-inverse);
  font-size: var(--font-size-xs);
  font-weight: bold;
  padding: 2px 8px;
  border-radius: var(--radius-pill);
}

.new-badge {
  background: var(--color-success);
}

.card-body {
  padding: 12px 14px 8px;
  flex: 1;
  cursor: pointer;
}

.card-body h3 {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.price {
  color: var(--color-primary);
  font-size: var(--font-size-sm);
  font-weight: bold;
  margin: 0 0 4px;
}

.price-num {
  font-size: var(--font-size-xl);
}

.sales {
  color: var(--color-text-disabled);
  font-size: var(--font-size-xs);
  margin: 0;
}

.card-footer {
  padding: 10px 14px;
  border-top: 1px solid var(--color-border-light);
  display: flex;
  justify-content: flex-end;
}

/* ========== 页脚 - 已移至 Layout.vue ========== */
.site-footer { display: none; }
</style>
