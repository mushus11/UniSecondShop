<template>
  <div class="page-container">
    <h2 class="page-title">👤 用户管理</h2>
    <p class="page-subtitle">管理员可查看和管理全平台所有用户信息</p>

    <!-- ===== 统计卡片 ===== -->
    <div class="stats-cards">
      <div class="stat-card">
        <span class="stat-number">{{ userStats.total }}</span>
        <span class="stat-label">总用户</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userStats.students }}</span>
        <span class="stat-label">学生</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userStats.teachers }}</span>
        <span class="stat-label">教职工</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userStats.auditors }}</span>
        <span class="stat-label">审核员</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userStats.admins }}</span>
        <span class="stat-label">管理员</span>
      </div>
      <div class="stat-card">
        <span class="stat-number">{{ userStats.active }}</span>
        <span class="stat-label">活跃用户</span>
      </div>
    </div>

    <!-- ===== 搜索和筛选 ===== -->
    <div class="search-bar">
      <el-input
          v-model="searchKeyword"
          placeholder="搜索用户姓名/学号/学院"
          style="width: 260px"
          clearable
          @keyup.enter="applyFilters"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>

      <el-select v-model="filterRole" placeholder="全部角色" style="width: 140px; margin-left: 12px" @change="applyFilters">
        <el-option label="全部角色" value="" />
        <el-option label="学生" value="student" />
        <el-option label="教职工" value="teacher" />
        <el-option label="审核员" value="auditor" />
        <el-option label="管理员" value="admin" />
      </el-select>

      <el-select v-model="filterStatus" placeholder="全部状态" style="width: 130px; margin-left: 12px" @change="applyFilters">
        <el-option label="全部状态" value="" />
        <el-option label="正常" value="active" />
        <el-option label="禁用" value="disabled" />
        <el-option label="未认证" value="pending" />
      </el-select>

      <el-button type="primary" style="margin-left: 12px" @click="applyFilters">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button style="margin-left: 8px" @click="resetFilters">重置</el-button>
      <el-button type="success" style="margin-left: 8px" @click="handleAdd">
        <el-icon><Plus /></el-icon> 添加用户
      </el-button>
    </div>

    <!-- ===== 用户表格 ===== -->
    <el-table
        :data="displayList"
        border
        stripe
        style="width: 100%; margin-top: 16px"
        v-loading="loading"
        @sort-change="handleSortChange"
    >
      <el-table-column prop="id" label="学号/工号" width="130" sortable="custom" />
      <el-table-column prop="username" label="姓名" width="100" sortable="custom" />
      <el-table-column prop="college" label="学院" width="140" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="phone" label="联系方式" width="130" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="roleTagMap[row.role]" size="small">{{ roleMap[row.role] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagMap[row.status]" size="small">
            {{ statusMap[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="verified" label="认证" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.verified ? 'success' : 'danger'" size="small">
            {{ row.verified ? '已认证' : '未认证' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="handleView(row)">查看</el-button>
          <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="handlePermission(row)">权限</el-button>
          <el-button
              size="small"
              :type="row.status === 'active' ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
          >
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- ===== 分页 ===== -->
    <div class="pagination">
      <el-pagination
          v-model:page-size="pageSize"
          v-model:current-page="currentPage"
          :total="filteredList.length"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="applyFilters"
          @current-change="applyFilters"
      />
    </div>

    <!-- ===== 查看用户详情弹窗 ===== -->
    <el-dialog v-model="viewDialogVisible" title="用户详情" width="600px">
      <div v-if="viewUser" class="user-detail">
        <div class="detail-avatar">
          <el-avatar :size="80" :src="viewUser.avatar || defaultAvatar" />
        </div>
        <div class="detail-grid">
          <div class="detail-item"><label>学号：</label><span>{{ viewUser.id }}</span></div>
          <div class="detail-item"><label>姓名：</label><span>{{ viewUser.username }}</span></div>
          <div class="detail-item"><label>学院：</label><span>{{ viewUser.college || '未设置' }}</span></div>
          <div class="detail-item"><label>年级：</label><span>{{ viewUser.grade || '未设置' }}</span></div>
          <div class="detail-item"><label>联系方式：</label><span>{{ viewUser.phone || '未设置' }}</span></div>
          <div class="detail-item"><label>角色：</label><el-tag :type="roleTagMap[viewUser.role]">{{ roleMap[viewUser.role] }}</el-tag></div>
          <div class="detail-item"><label>状态：</label><el-tag :type="statusTagMap[viewUser.status]">{{ statusMap[viewUser.status] }}</el-tag></div>
          <div class="detail-item"><label>认证：</label><el-tag :type="viewUser.verified ? 'success' : 'danger'">{{ viewUser.verified ? '已认证' : '未认证' }}</el-tag></div>
          <div class="detail-item full-width"><label>个人简介：</label><span>{{ viewUser.bio || '暂无简介' }}</span></div>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ===== 编辑用户弹窗 ===== -->
    <el-dialog v-model="formDialogVisible" :title="formDialogTitle" width="560px">
      <el-form :model="editForm" label-width="100px" ref="formRef" :rules="formRules">
        <el-form-item label="学号" prop="id">
          <el-input v-model="editForm.id" placeholder="请输入学号/工号" :disabled="isEditMode" />
        </el-form-item>
        <el-form-item label="姓名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="学院" prop="college">
          <el-input v-model="editForm.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="editForm.grade" style="width: 100%">
            <el-option label="大一" value="大一" />
            <el-option label="大二" value="大二" />
            <el-option label="大三" value="大三" />
            <el-option label="大四" value="大四" />
            <el-option label="研究生" value="研究生" />
            <el-option label="教职工" value="教职工" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="教职工" value="teacher" />
            <el-option label="审核员" value="auditor" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="认证状态" prop="verified">
          <el-switch v-model="editForm.verified" active-text="已认证" inactive-text="未认证" />
        </el-form-item>
        <el-form-item label="个人简介" prop="bio">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" placeholder="请输入个人简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- ===== 权限管理弹窗 ===== -->
    <el-dialog v-model="permDialogVisible" title="权限管理" width="480px">
      <div v-if="permUser" class="perm-section">
        <p><strong>用户：</strong>{{ permUser.username }}（{{ permUser.id }}）</p>
        <p><strong>当前角色：</strong><el-tag :type="roleTagMap[permUser.role]">{{ roleMap[permUser.role] }}</el-tag></p>
        <el-divider />
        <el-form label-width="100px">
          <el-form-item label="分配角色">
            <el-select v-model="permForm.role" style="width: 100%">
              <el-option label="学生" value="student" />
              <el-option label="教职工" value="teacher" />
              <el-option label="审核员" value="auditor" />
              <el-option label="管理员" value="admin" />
            </el-select>
          </el-form-item>
          <el-form-item label="权限备注">
            <el-input v-model="permForm.remark" type="textarea" :rows="2" placeholder="备注权限变更原因" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermission" :loading="savingPerm">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import api from '@/api'

// ===== 加载状态 =====
const loading = ref(false)
const saving = ref(false)
const savingPerm = ref(false)

// ===== 默认头像 =====
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// ===== 映射表 =====
const roleMap = {
  student: '学生',
  teacher: '教职工',
  auditor: '审核员',
  admin: '管理员'
}
const roleTagMap = {
  student: 'primary',
  teacher: 'success',
  auditor: 'warning',
  admin: 'danger'
}

const accessToRole = {
  '0': 'student',
  '1': 'teacher',
  '2': 'auditor',
  '3': 'admin'
}
const roleToAccess = {
  student: '0',
  teacher: '1',
  auditor: '2',
  admin: '3'
}

const statusMap = {
  active: '正常',
  disabled: '禁用',
  pending: '未认证'
}
const statusTagMap = {
  active: 'success',
  disabled: 'danger',
  pending: 'warning'
}

// ===== 统计数据 =====
const userStats = reactive({
  total: 0,
  students: 0,
  teachers: 0,
  auditors: 0,
  admins: 0,
  active: 0
})

// ===== 全量数据与展示数据 =====
const allUsers = ref([])
const displayList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// ===== 搜索筛选 =====
const searchKeyword = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const sortField = ref('')
const sortOrder = ref('')

// ===== 过滤后的数据(分页前) =====
const filteredList = computed(() => {
  let list = allUsers.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(u =>
      (u.username && u.username.toLowerCase().includes(keyword)) ||
      (String(u.id) && String(u.id).includes(keyword)) ||
      (u.college && u.college.toLowerCase().includes(keyword))
    )
  }
  if (filterRole.value) {
    list = list.filter(u => u.role === filterRole.value)
  }
  if (filterStatus.value) {
    list = list.filter(u => u.status === filterStatus.value)
  }

  if (sortField.value) {
    list = [...list].sort((a, b) => {
      const valA = a[sortField.value] ?? ''
      const valB = b[sortField.value] ?? ''
      if (sortOrder.value === 'ascending') {
        return valA > valB ? 1 : (valA < valB ? -1 : 0)
      } else {
        return valA < valB ? 1 : (valA > valB ? -1 : 0)
      }
    })
  }

  return list
})

const applyFilters = () => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  displayList.value = filteredList.value.slice(start, end)
}

// ===== 更新统计数据 =====
const updateStats = () => {
  userStats.total = allUsers.value.length
  userStats.students = allUsers.value.filter(u => u.role === 'student').length
  userStats.teachers = allUsers.value.filter(u => u.role === 'teacher').length
  userStats.auditors = allUsers.value.filter(u => u.role === 'auditor').length
  userStats.admins = allUsers.value.filter(u => u.role === 'admin').length
  userStats.active = allUsers.value.filter(u => u.status === 'active').length
}

// ===== 从API响应映射到页面数据 =====
const mapUser = (apiUser) => {
  const accessStr = apiUser.access || '0'
  const role = accessToRole[accessStr] || 'student'
  const certified = apiUser.certified === true || apiUser.certified === 'true'

  const localStatus = allUsers.value.find(u => u.id === apiUser.id)?.status
  const status = localStatus || (certified ? 'active' : 'pending')

  return {
    id: apiUser.id,
    username: apiUser.name || '未命名',
    college: apiUser.college || '',
    grade: apiUser.grade || '',
    phone: apiUser.telephone || '',
    role: role,
    status: status,
    verified: certified,
    bio: apiUser.profile || '',
    avatar: apiUser.image || '',
    access: accessStr
  }
}

// ===== 加载所有用户 =====
const loadData = async () => {
  loading.value = true
  try {
    const res = await api.get('/user/getAllUser')
    const dataList = Array.isArray(res.data) ? res.data : []
    allUsers.value = dataList.map(mapUser)
    updateStats()
    currentPage.value = 1
    applyFilters()
  } catch (e) {
    console.error('加载用户列表失败:', e)
    allUsers.value = []
    displayList.value = []
  } finally {
    loading.value = false
  }
}

// ===== 重置筛选 =====
const resetFilters = () => {
  searchKeyword.value = ''
  filterRole.value = ''
  filterStatus.value = ''
  sortField.value = ''
  sortOrder.value = ''
  currentPage.value = 1
  applyFilters()
}

// ===== 排序 =====
const handleSortChange = ({ prop, order }) => {
  sortField.value = prop
  sortOrder.value = order
  currentPage.value = 1
  applyFilters()
}

// ===== 查看详情 =====
const viewDialogVisible = ref(false)
const viewUser = ref(null)

const handleView = (row) => {
  viewUser.value = { ...row }
  viewDialogVisible.value = true
}

// ===== 添加/编辑用户 =====
const formDialogVisible = ref(false)
const formDialogTitle = ref('添加用户')
const formRef = ref()
const isEditMode = ref(false)

const editForm = reactive({
  id: '',
  username: '',
  college: '',
  grade: '',
  phone: '',
  role: 'student',
  verified: false,
  bio: ''
})

const formRules = {
  id: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const resetEditForm = () => {
  Object.assign(editForm, {
    id: '',
    username: '',
    college: '',
    grade: '',
    phone: '',
    role: 'student',
    verified: false,
    bio: ''
  })
}

const handleAdd = () => {
  formDialogTitle.value = '添加用户'
  isEditMode.value = false
  resetEditForm()
  formDialogVisible.value = true
}

const handleEdit = (row) => {
  formDialogTitle.value = '编辑用户'
  isEditMode.value = true
  Object.assign(editForm, {
    id: row.id,
    username: row.username,
    college: row.college,
    grade: row.grade,
    phone: row.phone,
    role: row.role,
    verified: row.verified,
    bio: row.bio
  })
  formDialogVisible.value = true
}

const saveUser = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      const payload = {
        id: parseInt(editForm.id),
        name: editForm.username,
        college: editForm.college,
        grade: editForm.grade,
        telephone: editForm.phone,
        profile: editForm.bio,
        certified: editForm.verified
      }

      const res = await api.post('/user/reviseBaseInf', payload)

      if (res.data === 200) {
        ElMessage.success(formDialogTitle.value + '成功')
        formDialogVisible.value = false
        await loadData()
      } else {
        ElMessage.error('操作失败，请重试')
      }
    } catch (e) {
      console.error('保存用户失败:', e)
    } finally {
      saving.value = false
    }
  })
}

// ===== 权限管理 =====
const permDialogVisible = ref(false)
const permUser = ref(null)
const permForm = reactive({
  role: '',
  remark: ''
})

const handlePermission = (row) => {
  permUser.value = { ...row }
  permForm.role = row.role
  permForm.remark = ''
  permDialogVisible.value = true
}

const savePermission = async () => {
  if (!permForm.role) {
    ElMessage.warning('请选择角色')
    return
  }

  savingPerm.value = true
  try {
    const res = await api.post('/user/reviseBaseInf', {
      id: permUser.value.id,
      name: permUser.value.username,
      college: permUser.value.college,
      grade: permUser.value.grade,
      telephone: permUser.value.phone,
      profile: permUser.value.bio,
      certified: permUser.value.verified
    })

    if (res.data === 200) {
      ElMessage.success(`已为 ${permUser.value.username} 分配角色：${roleMap[permForm.role]}`)
      permDialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error('权限分配失败')
    }
  } catch (e) {
    console.error('权限分配失败:', e)
  } finally {
    savingPerm.value = false
  }
}

// ===== 切换用户状态（启用/禁用） =====
const handleToggleStatus = (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  ElMessageBox.confirm(
    `确定要${action}用户 "${row.username}" 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    row.status = row.status === 'active' ? 'disabled' : 'active'
    ElMessage.success(`已${action}用户`)
    updateStats()
    applyFilters()
  }).catch(() => {})
}

// ===== 初始化 =====
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* ===== 容器 ===== */
.page-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}

/* ===== 统计卡片 ===== */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #f5f7fa;
  padding: 16px 20px;
  border-radius: 10px;
  text-align: center;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.stat-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.12);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  display: block;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* ===== 搜索栏 ===== */
.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
  padding: 16px 0 4px 0;
}

/* ===== 分页 ===== */
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* ===== 用户详情 ===== */
.user-detail {
  display: flex;
  gap: 24px;
  padding: 8px 0;
}

.detail-avatar {
  flex-shrink: 0;
}

.detail-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 24px;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  padding: 4px 0;
}

.detail-item label {
  color: #909399;
  width: 72px;
  flex-shrink: 0;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

/* ===== 权限管理 ===== */
.perm-section {
  padding: 8px 0;
}

.perm-section p {
  margin: 8px 0;
  font-size: 14px;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .stats-cards {
    grid-template-columns: repeat(3, 1fr);
  }

  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-bar > * {
    width: 100% !important;
    margin-left: 0 !important;
    margin-bottom: 8px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .user-detail {
    flex-direction: column;
    align-items: center;
  }

  .detail-item.full-width {
    grid-column: 1;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-card {
    padding: 12px;
  }

  .stat-number {
    font-size: 22px;
  }
}
</style>