<template>
  <div class="page-container">
    <h2 class="page-title">💰 交易管理</h2>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.total }}</span><span class="stat-label">总订单</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.pending }}</span><span class="stat-label">待处理</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.completed }}</span><span class="stat-label">已完成</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.totalAmount }}</span><span class="stat-label">总成交额(元)</span></div>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索订单号/商品名" style="width: 250px" clearable />
      <el-select v-model="filterStatus" placeholder="全部状态" style="width: 130px; margin-left: 12px">
        <el-option label="全部" value="" />
        <el-option label="待付款" value="pending" />
        <el-option label="已预约" value="reserved" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="margin-left: 12px" />
      <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      <el-button type="success" style="margin-left: 12px" @click="handleExport">导出报表</el-button>
    </div>

    <el-table :data="list" border stripe style="width: 100%; margin-top: 16px">
      <el-table-column prop="order_id" label="订单编号" width="130" />
      <el-table-column prop="product_name" label="商品名称" width="150" />
      <el-table-column prop="buyer" label="买家" width="80" />
      <el-table-column prop="seller" label="卖家" width="80" />
      <el-table-column prop="price" label="单价" width="80"><template #default="{ row }">¥{{ row.price }}</template></el-table-column>
      <el-table-column prop="quantity" label="数量" width="60" />
      <el-table-column prop="total" label="总价" width="80"><template #default="{ row }">¥{{ row.total }}</template></el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="statusTagMap[row.status]">{{ statusMap[row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="meetup_place" label="面交地点" width="120" />
      <el-table-column prop="created_at" label="下单时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="handleComplete(row)" v-if="row.status === 'reserved'">完成</el-button>
          <el-button size="small" type="danger" @click="handleCancel(row)" v-if="row.status === 'pending'">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination"><el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="total" layout="total, sizes, prev, pager, next" /></div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const stats = reactive({ total: 0, pending: 0, completed: 0, totalAmount: 0 })
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterStatus = ref('')
const dateRange = ref([])

const statusMap = { pending: '待付款', reserved: '已预约', completed: '已完成', cancelled: '已取消' }
const statusTagMap = { pending: 'warning', reserved: 'primary', completed: 'success', cancelled: 'danger' }

const loadData = () => {
  const mock = [
    { order_id: 'O20260701001', product_name: '高等数学教材', buyer: '张三', seller: '李四', price: 25, quantity: 1, total: 25, status: 'completed', meetup_place: '图书馆门口', created_at: '2026-07-01 10:30' },
    { order_id: 'O20260702002', product_name: '蓝牙耳机', buyer: '王五', seller: '赵六', price: 120, quantity: 1, total: 120, status: 'reserved', meetup_place: '食堂门口', created_at: '2026-07-02 14:00' },
    { order_id: 'O20260703003', product_name: '宿舍台灯', buyer: '孙七', seller: '周八', price: 45, quantity: 1, total: 45, status: 'pending', meetup_place: '宿舍楼下', created_at: '2026-07-03 09:15' }
  ]
  list.value = mock
  total.value = mock.length
  stats.total = 28
  stats.pending = 5
  stats.completed = 18
  stats.totalAmount = 3280
}

const handleDetail = (row) => { ElMessage.info(`查看订单: ${row.order_id}`) }
const handleComplete = (row) => { row.status = 'completed'; ElMessage.success('订单已完成') }
const handleCancel = (row) => { row.status = 'cancelled'; ElMessage.success('订单已取消') }
const handleExport = () => { ElMessage.success('报表导出成功') }

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.stats-cards { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-card { background: #f5f7fa; padding: 20px 40px; border-radius: 8px; text-align: center; min-width: 120px; }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.search-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 4px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>