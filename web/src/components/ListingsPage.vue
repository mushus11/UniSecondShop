<template>
  <div class="page-container">
    <h2 class="page-title">📝 交易发布管理</h2>

    <div class="action-bar">
      <el-button type="primary" size="large" @click="handlePublish">➕ 发布商品</el-button>
      <el-button type="warning" @click="handleBatchDown">批量下架</el-button>
      <el-button type="success" @click="showReservationDialog = true">📅 预约登记</el-button>
      <el-button type="info" @click="showMeetupDialog = true">📍 线下面交登记</el-button>
    </div>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.up }}</span><span class="stat-label">在售</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.down }}</span><span class="stat-label">已下架</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.sold }}</span><span class="stat-label">已售出</span></div>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px" clearable />
      <el-select v-model="filterState" placeholder="全部状态" style="width: 130px; margin-left: 12px">
        <el-option label="全部" :value="null" />
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="2" />
        <el-option label="售出" :value="3" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      <el-button style="margin-left: 8px" @click="loadData">刷新</el-button>
    </div>

    <el-table :data="pagedList" border stripe style="width: 100%; margin-top: 16px" v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="发布编号" width="240" show-overflow-tooltip />
      <el-table-column prop="goodId" label="商品ID" width="240" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="stateTagMap[row.state]">{{ stateMap[row.state] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="急出" width="65">
        <template #default="{ row }">
          <el-tag :type="row.hurryMark ? 'danger' : 'info'" size="small">{{ row.hurryMark ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="置顶" width="65">
        <template #default="{ row }">
          <el-tag :type="row.topMark ? 'warning' : 'info'" size="small">{{ row.topMark ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="upLoadTime" label="上架时间" width="170" />
      <el-table-column label="操作" width="360" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="handleStateChange(row, 'up')" v-if="row.state !== 1">上架</el-button>
          <el-button size="small" type="warning" @click="handleStateChange(row, 'down')" v-if="row.state === 1">下架</el-button>
          <el-button size="small" type="info" @click="handleStateChange(row, 'out')" v-if="row.state === 1">售出</el-button>
          <el-button size="small" type="danger" @click="handleToggleTop(row)">{{ row.topMark ? '取消置顶' : '置顶' }}</el-button>
          <el-button size="small" type="warning" @click="handleToggleHurry(row)">{{ row.hurryMark ? '取消急出' : '急出' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="filteredList.length" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" />
    </div>

    <el-dialog v-model="dialogVisible" title="发布商品" width="600px">
      <el-form :model="publishForm" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="publishForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="publishForm.type" style="width:100%">
            <el-option v-for="(label, key) in categoryMap" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="publishForm.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="publishForm.text" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="立即上架">
          <el-switch v-model="publishForm.state" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPublish" :loading="publishing">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReservationDialog" title="📅 交易预约登记" width="600px">
      <el-form :model="reservationForm" label-width="100px">
        <el-form-item label="商品ID" required>
          <el-input v-model="reservationForm.goodID" placeholder="请输入商品UUID" />
        </el-form-item>
        <el-form-item label="卖家ID" required>
          <el-input-number v-model="reservationForm.sellerID" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易价格" required>
          <el-input-number v-model="reservationForm.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易地点">
          <el-input v-model="reservationForm.tradingLocation" placeholder="如：图书馆门口" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="reservationForm.note" type="textarea" :rows="2" placeholder="预约备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReservationDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReservation" :loading="reserving">确认预约</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showMeetupDialog" title="📍 线下面交登记" width="600px">
      <el-alert title="线下面交说明" type="info" :closable="false" style="margin-bottom:16px">
        线下自提无线上资金流转，请双方确认身份后面交完成交易。
      </el-alert>
      <el-form :model="meetupForm" label-width="100px">
        <el-form-item label="商品ID" required>
          <el-input v-model="meetupForm.goodID" placeholder="请输入商品UUID" />
        </el-form-item>
        <el-form-item label="卖家ID" required>
          <el-input-number v-model="meetupForm.sellerID" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="买家ID" required>
          <el-input-number v-model="meetupForm.buyerID" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易价格" required>
          <el-input-number v-model="meetupForm.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="面交地点" required>
          <el-input v-model="meetupForm.tradingLocation" placeholder="如：校园食堂门口" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="meetupForm.note" type="textarea" :rows="2" placeholder="面交备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showMeetupDialog = false">取消</el-button>
        <el-button type="primary" @click="submitMeetup" :loading="meeting">确认登记</el-button>
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

const categoryMap: Record<number, string> = {
  0: '教材书本', 1: '电子产品', 2: '服饰生活', 3: '运动器材', 4: '宿舍家具', 5: '美妆文具'
}

const stateMap: Record<number, string> = { 0: '未处理', 1: '上架', 2: '下架', 3: '售出' }
const stateTagMap: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }

const stats = reactive({ up: 0, down: 0, sold: 0 })
const allList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterState = ref<number | null>(null)
const selectedRows = ref<any[]>([])

const filteredList = computed(() => {
  let list = allList.value
  if (keyword.value.trim()) {
    const kw = keyword.value.trim().toLowerCase()
    list = list.filter(r => r.goodId?.toLowerCase().includes(kw) || r.id?.toLowerCase().includes(kw))
  }
  if (filterState.value !== null) {
    list = list.filter(r => r.state === filterState.value)
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
    const [upRes, downRes] = await Promise.all([
      api.get('/Release/getUpGoods').then(r => (Array.isArray(r.data) ? r.data : [])).catch(() => []),
      api.get('/Release/getDownGoods').then(r => (Array.isArray(r.data) ? r.data : [])).catch(() => [])
    ])
    allList.value = [...upRes, ...downRes]
    stats.up = upRes.length
    stats.down = downRes.length
    stats.sold = allList.value.filter(r => r.state === 3).length
  } catch (e) { console.error('加载发布列表失败:', e) } finally { loading.value = false }
}

const handleSelectionChange = (rows: any[]) => { selectedRows.value = rows }

const handleStateChange = async (row: any, action: 'up' | 'down' | 'out') => {
  const actionMap = { up: 'changeStateUp', down: 'changeStateDown', out: 'changeStateOut' }
  const labelMap = { up: '上架', down: '下架', out: '售出' }
  try {
    const formData = new FormData()
    formData.append('ID', row.goodId || row.id)
    const res = await api.post(`/Release/${actionMap[action]}`, formData)
    if (res.data === 200) { ElMessage.success(`${labelMap[action]}成功`); loadData() }
    else { ElMessage.error(`${labelMap[action]}失败`) }
  } catch (e) { console.error('状态变更失败:', e) }
}

const handleToggleTop = async (row: any) => {
  try {
    const formData = new FormData()
    formData.append('ID', row.goodId || row.id)
    const res = await api.post('/Release/changeTop', formData)
    if (res.data === 200) { ElMessage.success(row.topMark ? '已取消置顶' : '已置顶'); loadData() }
    else { ElMessage.error('操作失败') }
  } catch (e) { console.error('置顶操作失败:', e) }
}

const handleToggleHurry = async (row: any) => {
  try {
    const formData = new FormData()
    formData.append('ID', row.goodId || row.id)
    const res = await api.post('/Release/changeHurry', formData)
    if (res.data === 200) { ElMessage.success(row.hurryMark ? '已取消急出' : '已标记急出'); loadData() }
    else { ElMessage.error('操作失败') }
  } catch (e) { console.error('急出操作失败:', e) }
}

const handleBatchDown = async () => {
  if (selectedRows.value.length === 0) { ElMessage.warning('请先勾选需要下架的商品'); return }
  ElMessageBox.confirm(`确定批量下架 ${selectedRows.value.length} 件商品吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    for (const row of selectedRows.value) {
      try {
        const formData = new FormData()
        formData.append('ID', row.goodId || row.id)
        await api.post('/Release/changeStateDown', formData)
      } catch (e) {}
    }
    ElMessage.success('批量下架完成')
    loadData()
  }).catch(() => {})
}

const dialogVisible = ref(false)
const publishing = ref(false)
const publishForm = reactive({ name: '', type: 0, price: 0, text: '', state: true })

const handlePublish = () => {
  publishForm.name = ''; publishForm.type = 0; publishForm.price = 0; publishForm.text = ''; publishForm.state = true
  dialogVisible.value = true
}

const submitPublish = async () => {
  if (!publishForm.name.trim()) { ElMessage.warning('请输入商品名称'); return }
  publishing.value = true
  try {
    const res = await api.post('/goods/uploadGoodInf', {
      userId: parseInt(loginStore.id) || 0,
      name: publishForm.name, type: publishForm.type, price: publishForm.price,
      text: publishForm.text, state: publishForm.state
    })
    if (res.data && res.data.goodID) {
      if (publishForm.state) {
        const formData = new FormData(); formData.append('ID', res.data.goodID)
        await api.post('/Release/changeStateUp', formData)
      }
      ElMessage.success('发布成功'); dialogVisible.value = false; loadData()
    } else { ElMessage.error('发布失败') }
  } catch (e) { console.error('发布失败:', e) } finally { publishing.value = false }
}

const showReservationDialog = ref(false)
const reserving = ref(false)
const reservationForm = reactive({
  goodID: '', sellerID: 0, price: 0, tradingLocation: '', note: ''
})

const submitReservation = async () => {
  if (!reservationForm.goodID.trim()) { ElMessage.warning('请输入商品ID'); return }
  reserving.value = true
  try {
    const res = await api.post('/Records/createRecord', {
      sellerID: reservationForm.sellerID,
      buyerID: parseInt(loginStore.id) || 0,
      goodID: reservationForm.goodID,
      price: reservationForm.price,
      tradingLocation: reservationForm.tradingLocation,
      note: reservationForm.note,
      state: false
    })
    if (res.data === 200) {
      ElMessage.success('预约登记成功！请按时进行线下面交')
      showReservationDialog.value = false
      reservationForm.goodID = ''; reservationForm.sellerID = 0; reservationForm.price = 0
      reservationForm.tradingLocation = ''; reservationForm.note = ''
    } else { ElMessage.error('预约失败') }
  } catch (e) { console.error('预约失败:', e) } finally { reserving.value = false }
}

// ---- 面交登记 ----
const showMeetupDialog = ref(false)
const meeting = ref(false)
const meetupForm = reactive({
  goodID: '', sellerID: 0, buyerID: 0, price: 0, tradingLocation: '', note: ''
})

const submitMeetup = async () => {
  if (!meetupForm.goodID.trim()) { ElMessage.warning('请输入商品ID'); return }
  if (!meetupForm.tradingLocation.trim()) { ElMessage.warning('请输入面交地点'); return }
  meeting.value = true
  try {
    const res = await api.post('/Records/createRecord', {
      sellerID: meetupForm.sellerID,
      buyerID: meetupForm.buyerID,
      goodID: meetupForm.goodID,
      price: meetupForm.price,
      tradingLocation: meetupForm.tradingLocation,
      note: meetupForm.note || '线下面交',
      state: true
    })
    if (res.data === 200) {
      ElMessage.success('面交登记完成！')
      showMeetupDialog.value = false
      meetupForm.goodID = ''; meetupForm.sellerID = 0; meetupForm.buyerID = 0
      meetupForm.price = 0; meetupForm.tradingLocation = ''; meetupForm.note = ''
    } else { ElMessage.error('登记失败') }
  } catch (e) { console.error('面交登记失败:', e) } finally { meeting.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.action-bar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.stats-cards { display: flex; gap: 16px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-card { background: #f5f7fa; padding: 20px 40px; border-radius: 8px; text-align: center; min-width: 120px; }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.search-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 4px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
