<template>
  <div class="page-container">
    <h2 class="page-title">💰 交易管理</h2>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.total }}</span><span class="stat-label">总交易</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.pending }}</span><span class="stat-label">处理中</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.completed }}</span><span class="stat-label">已完成</span></div>
      <div class="stat-card"><span class="stat-number">¥{{ stats.totalAmount.toFixed(2) }}</span><span class="stat-label">总成交额</span></div>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品ID/交易单号" style="width: 260px" clearable />
      <el-select v-model="filterState" placeholder="全部状态" style="width: 130px; margin-left: 12px">
        <el-option label="全部" value="all" />
        <el-option label="处理中" value="false" />
        <el-option label="已完成" value="true" />
      </el-select>
      <el-select v-model="filterRole" placeholder="全部角色" style="width: 130px; margin-left: 12px">
        <el-option label="全部" value="all" />
        <el-option label="我是买家" value="buyer" />
        <el-option label="我是卖家" value="seller" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      <el-button type="success" style="margin-left: 8px" @click="handleCreate">创建交易</el-button>
    </div>

    <el-table :data="pagedList" border stripe style="width: 100%; margin-top: 16px" v-loading="loading">
      <el-table-column prop="id" label="交易单号" width="280" show-overflow-tooltip />
      <el-table-column prop="goodID" label="商品ID" width="280" show-overflow-tooltip />
      <el-table-column prop="buyerId" label="买家ID" width="100" />
      <el-table-column prop="sellerId" label="卖家ID" width="100" />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.state ? 'success' : 'warning'">{{ row.state ? '已完成' : '处理中' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="tradingLocation" label="交易地点" width="120" />
      <el-table-column prop="transactionTime" label="交易时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="handleComplete(row)" v-if="!row.state">完成</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="filteredList.length" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" />
    </div>

    <el-dialog v-model="detailDialogVisible" title="交易详情" width="500px">
      <div v-if="detailRecord" class="detail-grid">
        <div class="detail-item"><label>交易单号：</label><span>{{ detailRecord.id }}</span></div>
        <div class="detail-item"><label>商品ID：</label><span>{{ detailRecord.goodID }}</span></div>
        <div class="detail-item"><label>卖家ID：</label><span>{{ detailRecord.sellerId }}</span></div>
        <div class="detail-item"><label>买家ID：</label><span>{{ detailRecord.buyerId }}</span></div>
        <div class="detail-item"><label>交易价格：</label><span>¥{{ detailRecord.price?.toFixed(2) }}</span></div>
        <div class="detail-item"><label>交易地点：</label><span>{{ detailRecord.tradingLocation || '未设置' }}</span></div>
        <div class="detail-item"><label>备注：</label><span>{{ detailRecord.note || '无' }}</span></div>
        <div class="detail-item"><label>状态：</label><el-tag :type="detailRecord.state ? 'success' : 'warning'">{{ detailRecord.state ? '已完成' : '处理中' }}</el-tag></div>
        <div class="detail-item"><label>交易时间：</label><span>{{ detailRecord.transactionTime || '未记录' }}</span></div>
      </div>
      <template #footer><el-button @click="detailDialogVisible = false">关闭</el-button></template>
    </el-dialog>

    <el-dialog v-model="createDialogVisible" title="创建交易记录" width="550px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="商品ID" required>
          <el-input v-model="createForm.goodID" placeholder="请输入商品ID(UUID)" />
        </el-form-item>
        <el-form-item label="卖家ID" required>
          <el-input-number v-model="createForm.sellerID" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="买家ID" required>
          <el-input-number v-model="createForm.buyerID" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易价格" required>
          <el-input-number v-model="createForm.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易地点">
          <el-input v-model="createForm.tradingLocation" placeholder="如：图书馆门口" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.note" type="textarea" :rows="2" placeholder="交易备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate" :loading="creating">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

const loginStore = useLoginStore()
const userId = computed(() => parseInt(loginStore.id) || 0)

const stats = reactive({ total: 0, pending: 0, completed: 0, totalAmount: 0 })
const allList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterState = ref<string>('all')
const filterRole = ref('all')

const filteredList = computed(() => {
  let list = allList.value
  if (keyword.value.trim()) {
    const kw = keyword.value.trim().toLowerCase()
    list = list.filter(r => r.goodID?.toLowerCase().includes(kw) || r.id?.toLowerCase().includes(kw))
  }
  if (filterState.value !== 'all') {
    list = list.filter(r => String(r.state) === filterState.value)
  }
  return list
})

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

const loadData = async () => {
  loading.value = true
  try {
    let res
    if (filterRole.value === 'seller') {
      res = await api.get('/Records/getRecordBySellerID', { params: { ID: userId.value } })
    } else if (filterRole.value === 'buyer') {
      res = await api.get('/Records/getRecordByBuyerID', { params: { ID: userId.value } })
    } else {
      res = await api.get('/Records/getRecordByUserID', { params: { ID: userId.value } })
    }
    allList.value = Array.isArray(res.data) ? res.data : []
    stats.total = allList.value.length
    stats.pending = allList.value.filter(r => !r.state).length
    stats.completed = allList.value.filter(r => r.state).length
    stats.totalAmount = allList.value.filter(r => r.state).reduce((sum, r) => sum + (r.price || 0), 0)
  } catch (e) {
    console.error('加载交易记录失败:', e)
    allList.value = []
  } finally {
    loading.value = false
  }
}

const detailDialogVisible = ref(false)
const detailRecord = ref<any>(null)
const handleDetail = (row: any) => {
  detailRecord.value = row
  detailDialogVisible.value = true
}

const handleComplete = async (row: any) => {
  try {
    const res = await api.post('/Records/changeState', { id: row.id })
    if (res.data === 200) {
      ElMessage.success('交易已完成')
      loadData()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (e) {
    console.error('完成交易失败:', e)
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除交易记录吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete('/Records/deleteRecord', { params: { ID: row.id } })
      if (res.data === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (e) {
      console.error('删除失败:', e)
    }
  }).catch(() => {})
}

const createDialogVisible = ref(false)
const creating = ref(false)
const createForm = reactive({
  goodID: '',
  sellerID: 0,
  buyerID: userId.value,
  price: 0,
  tradingLocation: '',
  note: ''
})

const handleCreate = () => {
  createForm.goodID = ''
  createForm.sellerID = 0
  createForm.buyerID = userId.value
  createForm.price = 0
  createForm.tradingLocation = ''
  createForm.note = ''
  createDialogVisible.value = true
}

const submitCreate = async () => {
  if (!createForm.goodID.trim()) {
    ElMessage.warning('请输入商品ID')
    return
  }
  creating.value = true
  try {
    const res = await api.post('/Records/createRecord', {
      sellerID: createForm.sellerID,
      buyerID: createForm.buyerID,
      goodID: createForm.goodID,
      price: createForm.price,
      tradingLocation: createForm.tradingLocation,
      note: createForm.note,
      state: false
    })
    if (res.data === 200) {
      ElMessage.success('交易创建成功')
      createDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error('创建失败')
    }
  } catch (e) {
    console.error('创建交易失败:', e)
  } finally {
    creating.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.stats-cards { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-card { background: #f5f7fa; padding: 20px 40px; border-radius: 8px; text-align: center; min-width: 120px; }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.search-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 4px; margin-bottom: 4px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px 16px; }
.detail-item { display: flex; align-items: center; font-size: 14px; padding: 4px 0; }
.detail-item label { color: #909399; width: 80px; flex-shrink: 0; }
</style>