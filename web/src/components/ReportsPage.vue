<template>
  <div class="page-container">
    <h2 class="page-title">📊 统计报表</h2>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.users }}</span><span class="stat-label">注册用户</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.products }}</span><span class="stat-label">发布商品</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.orders }}</span><span class="stat-label">成交订单</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.turnover }}</span><span class="stat-label">总成交额(元)</span></div>
    </div>

    <div class="chart-row">
      <div class="chart-box">
        <h4>📈 近7日交易趋势</h4>
        <div class="chart-placeholder">
          <div class="bar-chart">
            <div v-for="(value, index) in trendData" :key="index" class="bar-item">
              <div class="bar" :style="{ height: (value / maxTrend) * 150 + 'px' }"></div>
              <span class="bar-label">{{ ['一','二','三','四','五','六','日'][index] }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="chart-box">
        <h4>📊 热门品类分布</h4>
        <div class="pie-placeholder">
          <div v-for="(item, index) in categoryData" :key="index" class="pie-item">
            <span class="pie-color" :style="{ background: pieColors[index] }"></span>
            <span class="pie-label">{{ item.name }} {{ item.percentage }}%</span>
          </div>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户统计" name="user">
        <el-table :data="userStatsData" border stripe style="width:100%">
          <el-table-column prop="college" label="学院" />
          <el-table-column prop="student_count" label="学生数" />
          <el-table-column prop="teacher_count" label="教职工数" />
          <el-table-column prop="total" label="合计" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="商品统计" name="product">
        <el-table :data="productStatsData" border stripe style="width:100%">
          <el-table-column prop="category" label="分类" />
          <el-table-column prop="total" label="总数" />
          <el-table-column prop="active" label="在售" />
          <el-table-column prop="sold" label="已售" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="订单统计" name="order">
        <el-table :data="orderStatsData" border stripe style="width:100%">
          <el-table-column prop="month" label="月份" />
          <el-table-column prop="orders" label="订单数" />
          <el-table-column prop="amount" label="成交额(元)" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

const activeTab = ref('user')

const stats = reactive({
  users: 1256,
  products: 324,
  orders: 89,
  turnover: 18450
})

const trendData = [45, 52, 38, 65, 70, 58, 82]
const maxTrend = Math.max(...trendData)

const categoryData = [
  { name: '教材书本', percentage: 35 },
  { name: '电子产品', percentage: 25 },
  { name: '服饰生活', percentage: 18 },
  { name: '运动器材', percentage: 12 },
  { name: '宿舍家具', percentage: 10 }
]

const pieColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']

const userStatsData = [
  { college: '计算机学院', student_count: 256, teacher_count: 32, total: 288 },
  { college: '数学学院', student_count: 189, teacher_count: 25, total: 214 },
  { college: '物理学院', student_count: 145, teacher_count: 20, total: 165 },
  { college: '文学院', student_count: 210, teacher_count: 28, total: 238 }
]

const productStatsData = [
  { category: '教材书本', total: 58, active: 40, sold: 18 },
  { category: '电子产品', total: 42, active: 28, sold: 14 },
  { category: '服饰生活', total: 30, active: 20, sold: 10 },
  { category: '运动器材', total: 18, active: 12, sold: 6 }
]

const orderStatsData = [
  { month: '2026-05', orders: 23, amount: 4200 },
  { month: '2026-06', orders: 35, amount: 6800 },
  { month: '2026-07', orders: 31, amount: 7450 }
]

onMounted(() => {})
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.stats-cards { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-card { background: #f5f7fa; padding: 20px 40px; border-radius: 8px; text-align: center; min-width: 120px; }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 24px; }
.chart-box { background: #fafafa; padding: 20px; border-radius: 8px; border: 1px solid #ebeef5; }
.chart-box h4 { margin: 0 0 16px 0; color: #333; }

.bar-chart { display: flex; justify-content: space-around; align-items: flex-end; height: 180px; padding: 0 20px; }
.bar-item { display: flex; flex-direction: column; align-items: center; }
.bar { width: 30px; background: linear-gradient(to top, #409eff, #79bbff); border-radius: 4px 4px 0 0; transition: height 0.5s; }
.bar-label { margin-top: 6px; font-size: 12px; color: #909399; }

.pie-placeholder { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; padding: 10px; }
.pie-item { display: flex; align-items: center; gap: 8px; }
.pie-color { width: 16px; height: 16px; border-radius: 50%; display: inline-block; }
.pie-label { font-size: 14px; color: #333; }
</style>