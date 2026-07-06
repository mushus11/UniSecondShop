<template>
  <div class="page-container">
    <h2 class="page-title">📊 统计报表</h2>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.totalGoods }}</span><span class="stat-label">总商品</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.upGoods }}</span><span class="stat-label">在售商品</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.downGoods }}</span><span class="stat-label">已下架</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.orders }}</span><span class="stat-label">交易记录</span></div>
      <div class="stat-card"><span class="stat-number">¥{{ stats.turnover.toFixed(2) }}</span><span class="stat-label">总成交额</span></div>
    </div>

    <div class="chart-row">
      <div class="chart-box">
        <h4>📈 商品分类分布（柱状图）</h4>
        <v-chart class="chart" :option="barOption" autoresize />
      </div>
      <div class="chart-box">
        <h4>📊 热门品类占比（饼图）</h4>
        <v-chart class="chart" :option="pieOption" autoresize />
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-box full-width">
        <h4>🏫 各学院发布商品统计</h4>
        <v-chart class="chart" :option="collegeBarOption" autoresize />
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="商品分类统计" name="category">
        <el-table :data="categoryTableData" border stripe style="width:100%" v-loading="loading">
          <el-table-column prop="name" label="分类" />
          <el-table-column prop="count" label="商品数量" sortable />
          <el-table-column label="占比">
            <template #default="{ row }">{{ getPercentage(row.count) }}%</template>
          </el-table-column>
          <el-table-column label="在售率">
            <template #default="{ row }">
              <el-progress :percentage="Math.round((row.active / (row.count || 1)) * 100)" :stroke-width="14" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="交易汇总" name="order">
        <el-table :data="orderSummaryData" border stripe style="width:100%">
          <el-table-column label="指标" prop="label" />
          <el-table-column label="数值" prop="value" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="院系统计" name="college">
        <el-table :data="collegeTableData" border stripe style="width:100%">
          <el-table-column prop="college" label="学院" />
          <el-table-column prop="count" label="发布商品数" sortable />
          <el-table-column prop="active" label="在售数" sortable />
          <el-table-column prop="sold" label="已售数" sortable />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

use([BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const loginStore = useLoginStore()
const activeTab = ref('category')
const loading = ref(false)

const stats = reactive({ totalGoods: 0, upGoods: 0, downGoods: 0, orders: 0, turnover: 0 })

const categoryNames = ['教材书本', '电子产品', '服饰生活', '运动器材', '宿舍家具', '美妆文具']
const categoryColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#b37feb']

const categoryRawData = ref<{ name: string; count: number; active: number; sold: number }[]>([])

const categoryTableData = computed(() =>
  categoryRawData.value.map(d => ({
    name: d.name, count: d.count, active: d.active, sold: d.sold
  }))
)

const totalCount = computed(() => categoryRawData.value.reduce((s, d) => s + d.count, 0))

const getPercentage = (count: number) => {
  if (totalCount.value === 0) return 0
  return ((count / totalCount.value) * 100).toFixed(1)
}

const barOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: categoryRawData.value.map(d => d.name), axisLabel: { rotate: 15 } },
  yAxis: { type: 'value', name: '件数' },
  series: [{
    name: '商品数量', type: 'bar',
    data: categoryRawData.value.map(d => d.count),
    itemStyle: { color: (params: any) => categoryColors[params.dataIndex] || '#409eff', borderRadius: [6, 6, 0, 0] },
    barMaxWidth: 50
  }]
}))

const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c}件 ({d}%)' },
  legend: { orient: 'vertical', right: 10, top: 'center' },
  series: [{
    name: '分类占比', type: 'pie', radius: ['40%', '70%'],
    center: ['40%', '50%'],
    label: { show: true, formatter: '{b}\n{d}%' },
    data: categoryRawData.value.filter(d => d.count > 0).map((d, i) => ({
      value: d.count, name: d.name, itemStyle: { color: categoryColors[i] }
    })),
    emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' } }
  }]
}))

const collegeRawData = ref<{ college: string; count: number; active: number; sold: number }[]>([])

const collegeBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['在售', '已售'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: collegeRawData.value.map(d => d.college), axisLabel: { rotate: 20 } },
  yAxis: { type: 'value', name: '件数' },
  series: [
    { name: '在售', type: 'bar', data: collegeRawData.value.map(d => d.active), itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] }, barMaxWidth: 36 },
    { name: '已售', type: 'bar', data: collegeRawData.value.map(d => d.sold), itemStyle: { color: '#909399', borderRadius: [4, 4, 0, 0] }, barMaxWidth: 36 }
  ]
}))

const collegeTableData = computed(() => collegeRawData.value)

const orderSummaryData = ref<{ label: string; value: string }[]>([])

const loadStats = async () => {
  loading.value = true
  categoryRawData.value = []

  // 加载各分类商品统计
  for (let type = 0; type <= 5; type++) {
    try {
      const res = await api.get('/goods/getGoodsInfByType', {
        params: { type, userID: parseInt(loginStore.id) || 0, goodID: '' }
      })
      const goods = Array.isArray(res.data) ? res.data : []
      const active = goods.filter((g: any) => !g.state).length
      const sold = goods.filter((g: any) => g.state).length
      categoryRawData.value.push({ name: categoryNames[type], count: goods.length, active, sold })
      stats.totalGoods += goods.length
      stats.upGoods += active
      stats.downGoods += 0
    } catch (e) {
      categoryRawData.value.push({ name: categoryNames[type], count: 0, active: 0, sold: 0 })
    }
  }

  // 加载上架/下架统计
  try {
    const [upRes, downRes] = await Promise.all([
      api.get('/Release/getUpGoods').then(r => (Array.isArray(r.data) ? r.data.length : 0)).catch(() => 0),
      api.get('/Release/getDownGoods').then(r => (Array.isArray(r.data) ? r.data.length : 0)).catch(() => 0),
    ])
    stats.upGoods = upRes
    stats.downGoods = downRes
    stats.totalGoods = stats.upGoods + stats.downGoods
  } catch (e) {}

  // 加载交易统计
  try {
    const recRes = await api.get('/Records/getRecordByUserID', {
      params: { ID: parseInt(loginStore.id) || 0 }
    })
    if (Array.isArray(recRes.data)) {
      const records = recRes.data
      stats.orders = records.length
      stats.turnover = records.filter((r: any) => r.state).reduce((sum: number, r: any) => sum + (r.price || 0), 0)
      const completed = records.filter((r: any) => r.state).length
      orderSummaryData.value = [
        { label: '总交易数', value: String(records.length) },
        { label: '已完成交易', value: String(completed) },
        { label: '处理中交易', value: String(records.length - completed) },
        { label: '总成交额', value: `¥${stats.turnover.toFixed(2)}` }
      ]
    }
  } catch (e) {}

  // 院系统计（模拟数据，实际需要后端API支持）
  collegeRawData.value = [
    { college: '计算机学院', count: 48, active: 32, sold: 16 },
    { college: '数学学院', count: 35, active: 22, sold: 13 },
    { college: '物理学院', count: 28, active: 18, sold: 10 },
    { college: '文学院', count: 22, active: 15, sold: 7 },
    { college: '外国语学院', count: 19, active: 12, sold: 7 },
    { college: '体育学院', count: 15, active: 10, sold: 5 }
  ]

  loading.value = false
}

onMounted(loadStats)
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.stats-cards { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-card { background: #f5f7fa; padding: 20px 40px; border-radius: 8px; text-align: center; min-width: 130px; }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 24px; }
.chart-row .full-width { grid-column: 1 / -1; }
.chart-box { background: #fafafa; padding: 20px; border-radius: 8px; border: 1px solid #ebeef5; }
.chart-box h4 { margin: 0 0 16px 0; color: #333; }
.chart { width: 100%; height: 320px; }

@media (max-width: 768px) {
  .chart-row { grid-template-columns: 1fr; }
  .chart-row .full-width { grid-column: 1; }
}
</style>