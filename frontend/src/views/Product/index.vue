<template>
  <div class="product-page">
    <div class="page-back">
      <el-button class="back-btn" @click="router.push('/')"><el-icon><ArrowLeft /></el-icon>返回首页</el-button>
    </div>
    <main class="main-content">
      <!-- 侧边筛选 -->
      <aside class="sidebar">
        <div class="filter-section">
          <h3>商品分类</h3>
          <ul>
            <li :class="{ active: categoryId === null }" @click="selectCategory(null)">
              <div class="category-item">全部</div>
            </li>
            <li :class="{ active: categoryId === 1 }" @click="selectCategory(1)"><div class="category-item">服装</div></li>
            <li :class="{ active: categoryId === 2 }" @click="selectCategory(2)"><div class="category-item">数码</div></li>
            <li :class="{ active: categoryId === 3 }" @click="selectCategory(3)"><div class="category-item">家居</div></li>
            <li :class="{ active: categoryId === 4 }" @click="selectCategory(4)"><div class="category-item">美妆</div></li>
            <li :class="{ active: categoryId === 5 }" @click="selectCategory(5)"><div class="category-item">食品</div></li>
            <li :class="{ active: categoryId === 6 }" @click="selectCategory(6)"><div class="category-item">图书</div></li>
          </ul>
        </div>
      </aside>

      <!-- 商品列表 -->
      <section class="product-section">
        <div class="sort-bar">
          <span class="sort-label">排序：</span>
          <a class="sort-item" :class="{ active: sortType === 'default' }" @click="sortType = 'default'">综合</a>
          <a class="sort-item" :class="{ active: sortType === 'sales' }" @click="sortType = 'sales'">销量</a>
          <a class="sort-item" :class="{ active: sortType === 'priceAsc' }" @click="sortType = 'priceAsc'">价格↑</a>
          <a class="sort-item" :class="{ active: sortType === 'priceDesc' }" @click="sortType = 'priceDesc'">价格↓</a>
          <a class="sort-item" :class="{ active: sortType === 'new' }" @click="sortType = 'new'">新品</a>
        </div>

        <div v-if="sortedList.length === 0" class="empty-wrap">
          <el-empty description="暂无商品" />
        </div>

        <div class="product-list" v-else>
          <div class="product-card" v-for="i in sortedList" :key="i.id" @click="goToDetail(i.id)">
            <div class="card-img-wrap">
              <img :src="resolveImage(i.images, productPlaceholder)" alt="商品" />
              <div v-if="userStore.role === 1" class="hover-cart" @click.stop="handleAddToCart(i)">
                <el-icon><ShoppingCart /></el-icon>
                <span>加入购物车</span>
              </div>
            </div>
            <div class="card-body">
              <h3 class="card-name">{{ i.name }}</h3>
              <p class="price">{{ i.price }}</p>
              <p class="sales">销量 {{ i.sales || 0 }}</p>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next, jumper"
            @current-change="handlePageChange"
          />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ShoppingCart } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'
import { addToCart } from '@/api/cart'
import { productPlaceholder } from '@/utils/placeholders'
import { resolveImage } from '@/utils/image'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const keyword = ref(route.query.keyword || '')
const categoryId = ref(null)
const sortType = ref('default')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const productList = ref([])

const sortedList = computed(() => {
  const list = [...productList.value]
  if (sortType.value === 'sales') return list.sort((a, b) => (b.sales || 0) - (a.sales || 0))
  if (sortType.value === 'priceAsc') return list.sort((a, b) => parseFloat(a.price) - parseFloat(b.price))
  if (sortType.value === 'priceDesc') return list.sort((a, b) => parseFloat(b.price) - parseFloat(a.price))
  if (sortType.value === 'new') return list.sort((a, b) => (b.id || 0) - (a.id || 0))
  return list
})

onMounted(() => {
  loadProducts()
})

watch(() => route.query.keyword, (newKeyword) => {
  keyword.value = newKeyword || ''
  pageNum.value = 1
  loadProducts()
})

const selectCategory = (id) => {
  categoryId.value = id
  pageNum.value = 1
  loadProducts()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleAddToCart = async (product) => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await addToCart({ productId: product.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch {
    ElMessage.error('加入失败，请重试')
  }
}

const loadProducts = async () => {
  try {
    const res = await getProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: categoryId.value,
      keyword: keyword.value || undefined
    })
    productList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    productList.value = []
    total.value = 0
  }
}
</script>

<style scoped>
.product-page { background: var(--color-bg-page); min-height: 80vh; }
.page-back { max-width: var(--max-width); margin: 0 auto; padding: 12px var(--space-lg) 0; }
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
.main-content {
  max-width: var(--max-width); margin: 0 auto;
  padding: 16px var(--space-lg) 20px;
  display: flex; gap: var(--space-lg); align-items: stretch;
}
.sidebar { width: 200px; flex-shrink: 0; display: flex; flex-direction: column; }
.filter-section {
  background: var(--color-bg-card);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  flex: 1;
  display: flex;
  flex-direction: column;
}
.filter-section h3 { font-size: 15px; font-weight: 600; margin: 0 0 14px; color: var(--color-text-primary); text-align: center; }
.filter-section ul { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 10px; }
.filter-section li { cursor: pointer; }
.category-item {
  padding: 12px 14px; border-radius: var(--radius-md);
  font-size: var(--font-size-base); color: var(--color-text-secondary);
  background: var(--color-bg-page); border: 1px solid var(--color-border-light);
  transition: var(--transition);
  text-align: center;
}
.filter-section li:hover .category-item { background: var(--color-primary-bg-hover); color: var(--color-primary); border-color: #ffd0b0; }
.filter-section li.active .category-item {
  background: var(--color-primary); color: var(--color-text-inverse); border-color: var(--color-primary);
  font-weight: bold; box-shadow: var(--shadow-primary);
}
.product-section {
  flex: 1; background: var(--color-bg-card); padding: var(--space-lg); border-radius: var(--radius-md);
  display: flex; flex-direction: column;
  box-shadow: var(--shadow-sm);
  align-self: stretch;
  min-height: 740px;
}
.sort-bar {
  display: flex; align-items: center; gap: var(--space-xs);
  padding-bottom: 14px; border-bottom: 1px solid var(--color-border); margin-bottom: var(--space-lg);
}
.sort-label { color: var(--color-text-placeholder); font-size: var(--font-size-sm); margin-right: var(--space-xs); }
.sort-item {
  padding: 6px 14px; cursor: pointer; font-size: var(--font-size-base); color: var(--color-text-secondary);
  border-radius: var(--radius-sm); transition: var(--transition); user-select: none;
}
.sort-item:hover { color: var(--color-primary); background: var(--color-primary-bg-hover); }
.sort-item.active {
  color: var(--color-primary); font-weight: 600;
  border-bottom: 2px solid var(--color-primary); border-radius: 0;
}
.empty-wrap { padding: 60px 0; }
.product-list {
  display: grid; grid-template-columns: repeat(6, 1fr);
  gap: var(--space-md); align-content: start;
}
.product-card {
  border: 1px solid var(--color-border); border-radius: var(--radius-lg);
  overflow: hidden; cursor: pointer;
  transition: box-shadow 0.25s, transform 0.2s;
  background: var(--color-bg-card);
}
.product-card:hover { box-shadow: var(--shadow-lg); transform: translateY(-2px); }
.card-img-wrap { position: relative; overflow: hidden; }
.card-img-wrap img { width: 100%; aspect-ratio: 1; object-fit: cover; display: block; transition: transform 0.3s; }
.card-img-wrap:hover img { transform: scale(1.05); }
.hover-cart {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(255,103,0,0.88);
  color: var(--color-text-inverse); font-size: var(--font-size-sm); font-weight: 600;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 10px 0;
  transform: translateY(100%);
  transition: transform 0.22s ease;
}
.product-card:hover .hover-cart { transform: translateY(0); }
.card-body { padding: 12px; }
.card-name {
  font-size: var(--font-size-sm); color: var(--color-text-primary); margin: 0 0 8px;
  display: -webkit-box; -webkit-line-clamp: 2;
  -webkit-box-orient: vertical; overflow: hidden;
  line-height: 1.4;
}
.price { color: var(--color-primary); font-size: 17px; font-weight: 700; margin: 0 0 4px; }
.sales { color: var(--color-text-disabled); font-size: var(--font-size-sm); margin: 0; }
.pagination {
  margin-top: auto; padding-top: var(--space-lg); display: flex; justify-content: center;
  border-top: 1px solid var(--color-border-light);
}
:deep(.el-pager li.is-active) { background-color: var(--color-primary) !important; color: var(--color-text-inverse) !important; border-radius: var(--radius-sm); }
</style>

