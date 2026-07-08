<template>
  <div class="page-container">
    <h2 class="page-title">👤 用户管理</h2>
    <p class="page-subtitle">管理员可查看和管理全平台所有用户信息</p>

    <div class="stats-cards">
      <div class="stat-card">
        <span class="stat-number">{{ userList.length }}</span>
        <span class="stat-label">总用户</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userList.filter(u => u.certified).length }}</span>
        <span class="stat-label">已认证</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userList.filter(u => !u.certified).length }}</span>
        <span class="stat-label">未认证</span>
      </div>
    </div>

    <div class="search-bar">
      <el-input
          v-model="searchKeyword"
          placeholder="搜索用户姓名/学号/学院"
          style="width: 260px"
          clearable
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" style="margin-left: 12px" @click="loadData">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button style="margin-left: 8px" @click="resetFilters">重置</el-button>
    </div>

    <el-table
        :data="pagedList"
        border
        stripe
        style="width: 100%; margin-top: 16px"
        v-loading="loading"
    >
      <el-table-column prop="id" label="学号/工号" width="130" sortable="custom" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="college" label="学院" width="140" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="telephone" label="联系方式" width="130" />
      <el-table-column label="认证" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.certified ? 'success' : 'danger'" size="small">
            {{ row.certified ? '已认证' : '未认证' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleView(row)">查看</el-button>
          <el-button size="small" type="danger" @click="handleDeleteUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
          v-model:page-size="pageSize"
          v-model:current-page="currentPage"
          :total="filteredList.length"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
      />
    </div>

    <el-dialog v-model="viewDialogVisible" title="用户详情" width="600px">
      <div v-if="viewUser" class="user-detail">
        <div class="detail-grid">
          <div class="detail-item"><label>学号：</label><span>{{ viewUser.id }}</span></div>
          <div class="detail-item"><label>姓名：</label><span>{{ viewUser.name || '未设置' }}</span></div>
          <div class="detail-item"><label>学院：</label><span>{{ viewUser.college || '未设置' }}</span></div>
          <div class="detail-item"><label>年级：</label><span>{{ viewUser.grade || '未设置' }}</span></div>
          <div class="detail-item"><label>联系方式：</label><span>{{ viewUser.telephone || '未设置' }}</span></div>
          <div class="detail-item"><label>认证：</label><el-tag :type="viewUser.certified ? 'success' : 'danger'">{{ viewUser.certified ? '已认证' : '未认证' }}</el-tag></div>
          <div class="detail-item full-width"><label>个人简介：</label><span>{{ viewUser.profile || '暂无简介' }}</span></div>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

const loginStore = useLoginStore()
const loading = ref(false)
const userList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const filteredList = computed(() => {
  let list = userList.value
  if (searchKeyword.value.trim()) {
    const kw = searchKeyword.value.trim().toLowerCase()
    list = list.filter(u =>
      (u.name || '').toLowerCase().includes(kw) ||
      String(u.id || '').includes(kw) ||
      (u.college || '').toLowerCase().includes(kw)
    )
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
    const formData = new FormData()
    formData.append('id', loginStore.id)
    const res = await api.get('/user/getAllUser', { params: { id: parseInt(loginStore.id) || 0 } })
    if (Array.isArray(res.data)) {
      userList.value = res.data.filter((u: any) => u.code === 200)
    } else {
      userList.value = []
    }
  } catch (e) {
    console.error('加载用户列表失败:', e)
    userList.value = []
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  currentPage.value = 1
}

const viewDialogVisible = ref(false)
const viewUser = ref<any>(null)

const handleView = (row: any) => {
  viewUser.value = { ...row }
  viewDialogVisible.value = true
}

const handleDeleteUser = (row: any) => {
  ElMessageBox.confirm(`确定删除用户 "${row.name || row.id}" 吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete('/user/delete', {
        params: { id: row.id }
      })
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

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 24px; max-width: 1400px; margin: 0 auto; }
.page-title { font-size: 24px; font-weight: 600; color: #1a1a2e; margin-bottom: 4px; }
.page-subtitle { font-size: 14px; color: #909399; margin-bottom: 20px; }

.stats-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 16px; margin-bottom: 20px; }
.stat-card { background: #f5f7fa; padding: 16px 20px; border-radius: 10px; text-align: center; transition: all 0.3s; border: 1px solid transparent; }
.stat-card:hover { border-color: #409eff; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(64, 158, 255, 0.12); }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }

.search-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 4px; padding: 16px 0 4px 0; }

.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }

.user-detail { padding: 8px 0; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px 24px; }
.detail-item { display: flex; align-items: center; font-size: 14px; padding: 4px 0; }
.detail-item label { color: #909399; width: 72px; flex-shrink: 0; }
.detail-item.full-width { grid-column: 1 / -1; }

@media (max-width: 768px) {
  .page-container { padding: 16px; }
  .stats-cards { grid-template-columns: repeat(3, 1fr); }
  .detail-grid { grid-template-columns: 1fr; }
  .detail-item.full-width { grid-column: 1; }
}
</style>