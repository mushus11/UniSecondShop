<template>
  <div class="page-container">
    <h2 class="page-title">📝 交易发布管理</h2>

    <div class="action-bar">
      <el-button type="primary" size="large" @click="handlePublish">➕ 发布商品</el-button>
      <el-button type="warning" @click="handleBatchOffline">批量下架</el-button>
      <el-button type="info" @click="handleAppointments">📅 预约登记</el-button>
      <el-button type="success" @click="handleMeetup">📍 线下面交</el-button>
    </div>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.onShelf }}</span><span class="stat-label">在售</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.offShelf }}</span><span class="stat-label">已下架</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.reserved }}</span><span class="stat-label">已预约</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.traded }}</span><span class="stat-label">已成交</span></div>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px" clearable />
      <el-select v-model="filterStatus" placeholder="全部状态" style="width: 130px; margin-left: 12px">
        <el-option label="全部" value="" />
        <el-option label="在售" value="on" />
        <el-option label="已下架" value="off" />
        <el-option label="已预约" value="reserved" />
        <el-option label="已成交" value="traded" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      <el-button style="margin-left: 12px" @click="loadData">刷新</el-button>
    </div>

    <el-table :data="list" border stripe style="width: 100%; margin-top: 16px">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="编号" width="90" />
      <el-table-column prop="name" label="商品名称" width="150" />
      <el-table-column prop="price" label="价格" width="80"><template #default="{ row }">¥{{ row.price }}</template></el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="statusTagMap[row.status]">{{ statusMap[row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="buyer" label="预约人" width="80" />
      <el-table-column prop="meetup_time" label="面交时间" width="160" />
      <el-table-column prop="created_at" label="发布时间" width="160" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="success" @click="handleShelf(row)" v-if="row.status === 'off'">上架</el-button>
          <el-button size="small" type="warning" @click="handleOffline(row)" v-if="row.status === 'on'">下架</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination"><el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="total" layout="total, sizes, prev, pager, next" /></div>

    <el-dialog v-model="dialogVisible" title="发布商品" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类"><el-select v-model="form.category" style="width:100%"><el-option label="教材书本" value="books" /><el-option label="电子产品" value="electronics" /><el-option label="服饰生活" value="clothing" /><el-option label="运动器材" value="sports" /><el-option label="宿舍家具" value="furniture" /></el-select></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="新旧程度"><el-select v-model="form.condition" style="width:100%"><el-option label="全新" value="new" /><el-option label="几乎全新" value="like_new" /><el-option label="九成新" value="good" /><el-option label="八成新" value="fair" /><el-option label="有使用痕迹" value="used" /></el-select></el-form-item>
        <el-form-item label="商品描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="面交地点"><el-input v-model="form.meetup_place" placeholder="如：校园食堂门口" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="saveData">发布</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const stats = reactive({ onShelf: 0, offShelf: 0, reserved: 0, traded: 0 })
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterStatus = ref('')

const statusMap = { on: '在售', off: '已下架', reserved: '已预约', traded: '已成交' }
const statusTagMap = { on: 'success', off: 'danger', reserved: 'warning', traded: 'info' }

const dialogVisible = ref(false)
const form = reactive({ name: '', category: '', price: 0, condition: '', description: '', meetup_place: '' })

const loadData = () => {
  const mock = [
    { id: 'L001', name: '考研英语真题', price: 30, status: 'on', buyer: '-', meetup_time: '-', created_at: '2026-07-01' },
    { id: 'L002', name: '蓝牙耳机', price: 120, status: 'reserved', buyer: '李四', meetup_time: '2026-07-08 15:00', created_at: '2026-07-02' },
    { id: 'L003', name: '宿舍台灯', price: 45, status: 'off', buyer: '-', meetup_time: '-', created_at: '2026-06-28' }
  ]
  list.value = mock
  total.value = mock.length
  stats.onShelf = 5
  stats.offShelf = 3
  stats.reserved = 2
  stats.traded = 1
}

const handlePublish = () => { dialogVisible.value = true }
const handleBatchOffline = () => { ElMessage.success('批量下架成功') }
const handleAppointments = () => { ElMessage.info('查看预约登记列表') }
const handleMeetup = () => { ElMessage.info('查看线下面交登记') }
const handleEdit = (row) => { ElMessage.info(`编辑: ${row.name}`) }
const handleShelf = (row) => { row.status = 'on'; ElMessage.success('上架成功') }
const handleOffline = (row) => { row.status = 'off'; ElMessage.success('下架成功') }
const handleDelete = (row) => { ElMessage.success(`已删除: ${row.name}`); loadData() }
const saveData = () => { ElMessage.success('发布成功'); dialogVisible.value = false; loadData() }

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