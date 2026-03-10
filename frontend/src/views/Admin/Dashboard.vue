<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--color-info)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--color-success)">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.todaySales }}</div>
              <div class="stat-label">今日销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--color-warning)">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingShip }}</div>
              <div class="stat-label">待发货</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--color-danger)">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: var(--space-xl)">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
            </div>
          </template>
          <div ref="lineChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>商品类别占比</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待处理订单 -->
    <el-card style="margin-top: var(--space-xl)">
      <template #header>
        <div class="card-header">
          <span>待处理订单</span>
          <el-button type="primary" size="small" @click="router.push('/admin/orders')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="orderList" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="totalAmount" label="金额">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Document, Money, ShoppingCart, Bell } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const router = useRouter()
const stats = ref({ todayOrders: 0, todaySales: '0.00', pendingShip: 0, totalUsers: 0 })
const orderList = ref([])
const lineChartRef = ref(null)
const pieChartRef = ref(null)
let lineChart = null
let pieChart = null

// 生成近7天日期标签
const getLast7Days = () => {
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - i))
    return d.toISOString().slice(5, 10) // MM-DD
  })
}

const initLineChart = (orders) => {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  const days = getLast7Days()
  // 按日期统计销售额
  const salesMap = {}
  days.forEach(d => { salesMap[d] = 0 })
  orders.forEach(o => {
    const day = o.createTime?.slice(5, 10)
    if (day && salesMap[day] !== undefined) {
      salesMap[day] += parseFloat(o.totalAmount || 0)
    }
  })
  const salesData = days.map(d => +salesMap[d].toFixed(2))

  lineChart.setOption({
    tooltip: { trigger: 'axis', formatter: (p) => `${p[0].name}<br/>销售额：¥${p[0].value}` },
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: days, axisLine: { lineStyle: { color: '#ddd' } }, axisLabel: { color: '#888' } },
    yAxis: { type: 'value', axisLabel: { color: '#888', formatter: '¥{value}' }, splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [{
      name: '销售额',
      type: 'line',
      data: salesData,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#ff6700', width: 2.5 },
      itemStyle: { color: '#ff6700' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(255,103,0,0.25)' },
        { offset: 1, color: 'rgba(255,103,0,0.02)' }
      ]) }
    }]
  })
}

const initPieChart = (products) => {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  const categoryMap = { 1: '服装', 2: '数码', 3: '家居', 4: '美妆', 5: '食品', 6: '图书' }
  const countMap = {}
  products.forEach(p => {
    const name = categoryMap[p.categoryId] || '其他'
    countMap[name] = (countMap[name] || 0) + 1
  })
  const pieData = Object.entries(countMap).map(([name, value]) => ({ name, value }))
  if (!pieData.length) pieData.push({ name: '暂无数据', value: 1 })

  const colors = ['#ff6700', '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}：{c} 件 ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { color: '#555', fontSize: 13 } },
    color: colors,
    series: [{
      type: 'pie',
      radius: ['40%', '68%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: true,
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: pieData
    }]
  })
}

const loadStats = async () => {
  try {
    // 获取全部订单（最多500条，用于介算分把和图表）
    const allRes = await request.get('/orders/admin', { params: { pageNum: 1, pageSize: 500 } })
    const orders = allRes.data?.records || []

    // 待发货订单
    const pendingOrders = orders.filter(o => o.status === 1)
    stats.value.pendingShip = pendingOrders.length

    // 待处理（展示待发货）
    orderList.value = pendingOrders.slice(0, 5)

    // 今日数据
    const today = new Date().toISOString().slice(0, 10)
    const todayOrders = orders.filter(o => o.createTime?.startsWith(today))
    stats.value.todayOrders = todayOrders.length
    stats.value.todaySales = todayOrders
      .reduce((sum, o) => sum + parseFloat(o.totalAmount || 0), 0)
      .toFixed(2)

    const userRes = await request.get('/users')
    stats.value.totalUsers = userRes.data?.total ?? (Array.isArray(userRes.data) ? userRes.data.length : 0)

    await nextTick()
    initLineChart(orders)

    // 加载商品分类数据
    const prodRes = await request.get('/products', { params: { pageNum: 1, pageSize: 200 } })
    const products = prodRes.data?.records || prodRes.data || []
    initPieChart(products)
  } catch {
    await nextTick()
    initLineChart([])
    initPieChart([])
  }
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 8: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 8: '已取消' }
  return map[status] || '未知'
}

const handleView = (row) => {
  router.push('/admin/orders')
}

onMounted(loadStats)

onBeforeUnmount(() => {
  lineChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.stat-cards {
  margin-top: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--color-text-primary);
}

.stat-label {
  font-size: var(--font-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--color-text-primary);
}

.chart-box {
  height: 280px;
  width: 100%;
}
</style>

