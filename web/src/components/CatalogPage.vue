<template>
  <div class="page-container">
    <h2 class="page-title">📦 商品档案管理</h2>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.total }}</span><span class="stat-label">总商品</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.pending }}</span><span class="stat-label">待审核</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.active }}</span><span class="stat-label">在售</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.sold }}</span><span class="stat-label">已售</span></div>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品名称" style="width: 250px" clearable />
      <el-select v-model="category" placeholder="全部分类" style="width: 150px; margin-left: 12px">
        <el-option label="全部" value="" />
        <el-option label="教材书本" value="books" />
        <el-option label="电子产品" value="electronics" />
        <el-option label="服饰生活" value="clothing" />
        <el-option label="运动器材" value="sports" />
        <el-option label="宿舍家具" value="furniture" />
      </el-select>
      <el-select v-model="status" placeholder="全部状态" style="width: 130px; margin-left: 12px">
        <el-option label="全部" value="" />
        <el-option label="待审核" value="pending" />
        <el-option label="在售" value="active" />
        <el-option label="已售" value="sold" />
        <el-option label="已下架" value="offline" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      <el-button type="success" style="margin-left: 12px" @click="handleAdd">添加商品</el-button>
    </div>

    <el-table :data="list" border stripe style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="商品编号" width="100" />
      <el-table-column prop="name" label="商品名称" width="150" />
      <el-table-column label="图片" width="80">
        <template><el-image style="width:50px;height:50px;border-radius:4px" :src="defaultImage" fit="cover" /></template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }"><el-tag>{{ categoryMap[row.category] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="80">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="condition" label="新旧程度" width="90">
        <template #default="{ row }">{{ conditionMap[row.condition] }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTagMap[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="seller" label="发布人" width="80" />
      <el-table-column prop="created_at" label="发布时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="info" @click="handleDetail(row)">详情</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="total" layout="total, sizes, prev, pager, next" />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="教材书本" value="books" />
            <el-option label="电子产品" value="electronics" />
            <el-option label="服饰生活" value="clothing" />
            <el-option label="运动器材" value="sports" />
            <el-option label="宿舍家具" value="furniture" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="新旧程度">
          <el-select v-model="form.condition" style="width:100%">
            <el-option label="全新" value="new" />
            <el-option label="几乎全新" value="like_new" />
            <el-option label="九成新" value="good" />
            <el-option label="八成新" value="fair" />
            <el-option label="有使用痕迹" value="used" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="实拍图片"><el-upload action="#" :auto-upload="false"><el-button>上传图片</el-button></el-upload></el-form-item>
        <el-form-item label="购买凭证"><el-upload action="#" :auto-upload="false"><el-button>上传凭证</el-button></el-upload></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveData">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 24 24"%3E%3Crect width="24" height="24" fill="%23f0f0f0"/%3E%3Ctext x="4" y="16" font-size="12" fill="%23999"%3E暂无%3C/text%3E%3C/svg%3E'

const stats = reactive({ total: 0, pending: 0, active: 0, sold: 0 })
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const category = ref('')
const status = ref('')

const categoryMap = { books: '教材书本', electronics: '电子产品', clothing: '服饰生活', sports: '运动器材', furniture: '宿舍家具' }
const conditionMap = { new: '全新', like_new: '几乎全新', good: '九成新', fair: '八成新', used: '有使用痕迹' }
const statusMap = { pending: '待审核', active: '在售', sold: '已售', offline: '已下架' }
const statusTagMap = { pending: 'warning', active: 'success', sold: 'info', offline: 'danger' }

const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const form = reactive({ id: '', name: '', category: '', price: 0, condition: '', description: '', status: 'pending' })

const loadData = () => {
  const mock = [
    { id: 'P001', name: '高等数学教材', category: 'books', price: 25, condition: 'good', status: 'active', seller: '张三', created_at: '2026-07-01 14:30' },
    { id: 'P002', name: 'iPhone 13', category: 'electronics', price: 3200, condition: 'like_new', status: 'pending', seller: '李四', created_at: '2026-07-02 10:00' },
    { id: 'P003', name: '宿舍书桌', category: 'furniture', price: 150, condition: 'used', status: 'active', seller: '王五', created_at: '2026-07-03 09:20' }
  ]
  list.value = mock
  total.value = mock.length
  stats.total = 15
  stats.pending = 3
  stats.active = 10
  stats.sold = 2
}

const handleAdd = () => { dialogTitle.value = '添加商品'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑商品'; Object.assign(form, row); dialogVisible.value = true }
const handleDetail = (row) => { ElMessage.info(`查看商品: ${row.name}`) }
const handleDelete = (row) => { ElMessage.success(`已删除: ${row.name}`); loadData() }
const saveData = () => { ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }

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