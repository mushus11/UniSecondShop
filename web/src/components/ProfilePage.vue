<template>
  <div class="page-container">
    <h2 class="page-title">👤 个人中心</h2>

    <div class="profile-layout">
      <!-- ========== 左侧：头像 + 基本信息 ========== -->
      <div class="profile-sidebar">
        <!-- 头像 -->
        <div class="avatar-section">
          <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar" />
          <el-button size="small" style="margin-top:12px" @click="handleChangeAvatar">更换头像</el-button>
        </div>

        <!-- 基本信息 -->
        <div class="info-section">
          <p><strong>姓名：</strong>{{ userInfo.username }}</p>
          <p><strong>学号：</strong>{{ userInfo.id }}</p>
          <p><strong>学院：</strong>{{ userInfo.college || '未设置' }}</p>
          <p><strong>年级：</strong>{{ userInfo.grade || '未设置' }}</p>
          <p><strong>角色：</strong><el-tag :type="roleTagMap[userInfo.role]">{{ roleMap[userInfo.role] }}</el-tag></p>
          <p><strong>认证状态：</strong><el-tag type="success">已认证</el-tag></p>
        </div>
      </div>

      <!-- ========== 右侧：编辑表单 ========== -->
      <div class="profile-main">
        <el-form :model="userInfo" label-width="100px" ref="formRef">
          <el-form-item label="姓名" prop="username">
            <el-input v-model="userInfo.username" placeholder="请输入姓名" />
          </el-form-item>

          <el-form-item label="学院" prop="college">
            <el-input v-model="userInfo.college" placeholder="请输入学院" />
          </el-form-item>

          <el-form-item label="年级" prop="grade">
            <el-select v-model="userInfo.grade" style="width: 100%">
              <el-option label="大一" value="大一" />
              <el-option label="大二" value="大二" />
              <el-option label="大三" value="大三" />
              <el-option label="大四" value="大四" />
              <el-option label="研究生" value="研究生" />
              <el-option label="教职工" value="教职工" />
            </el-select>
          </el-form-item>

          <el-form-item label="联系方式" prop="phone">
            <el-input v-model="userInfo.phone" placeholder="请输入手机号" />
          </el-form-item>

          <el-form-item label="个人简介" prop="bio">
            <el-input
                v-model="userInfo.bio"
                type="textarea"
                :rows="4"
                placeholder="介绍一下你自己吧..."
                maxlength="200"
                show-word-limit
            />
          </el-form-item>

          <!-- 修改密码 -->
          <el-divider>修改密码</el-divider>

          <el-form-item label="当前密码" prop="oldPassword">
            <el-input v-model="userInfo.oldPassword" type="password" placeholder="请输入当前密码" show-password />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="userInfo.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="userInfo.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            <el-button @click="resetProfile">重置</el-button>
            <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- ========== 底部：我的发布 / 我的订单 ========== -->
    <div class="profile-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="📦 我的发布" name="myProducts">
          <el-table :data="myProducts" border stripe style="width: 100%">
            <el-table-column prop="id" label="编号" width="100" />
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '在售' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="created_at" label="发布时间" width="180" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="handleEditProduct(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteProduct(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination"><el-pagination :total="myProductsTotal" layout="prev, pager, next" /></div>
        </el-tab-pane>

        <el-tab-pane label="📋 我的订单" name="myOrders">
          <el-table :data="myOrders" border stripe style="width: 100%">
            <el-table-column prop="order_id" label="订单编号" width="150" />
            <el-table-column prop="product_name" label="商品名称" />
            <el-table-column prop="total" label="总价" width="100">
              <template #default="{ row }">¥{{ row.total }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="orderStatusTagMap[row.status]">{{ orderStatusMap[row.status] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="created_at" label="下单时间" width="180" />
          </el-table>
          <div class="pagination"><el-pagination :total="myOrdersTotal" layout="prev, pager, next" /></div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useLoginStore } from '@/store/UseLogin'

const router = useRouter()
const loginStore = useLoginStore()
const formRef = ref()
const saving = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 角色映射
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

// 订单状态映射
const orderStatusMap = {
  pending: '待付款',
  reserved: '已预约',
  completed: '已完成',
  cancelled: '已取消'
}
const orderStatusTagMap = {
  pending: 'warning',
  reserved: 'primary',
  completed: 'success',
  cancelled: 'danger'
}

// ========== 用户信息 ==========
const userInfo = reactive({
  id: '',
  username: '',
  college: '',
  grade: '',
  phone: '',
  bio: '',
  role: '',
  avatar: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// ========== 我的发布 ==========
const activeTab = ref('myProducts')
const myProducts = ref([
  { id: 'P001', name: '高等数学教材', price: 25, status: '在售', created_at: '2026-07-01 14:30' },
  { id: 'P002', name: '蓝牙耳机', price: 120, status: '已售', created_at: '2026-06-28 10:00' },
  { id: 'P003', name: '宿舍台灯', price: 45, status: '在售', created_at: '2026-06-25 09:20' }
])
const myProductsTotal = ref(3)

// ========== 我的订单 ==========
const myOrders = ref([
  { order_id: 'O20260701001', product_name: '考研英语真题', total: 30, status: 'completed', created_at: '2026-07-01 10:30' },
  { order_id: 'O20260702002', product_name: '机械键盘', total: 89, status: 'reserved', created_at: '2026-07-02 14:00' },
  { order_id: 'O20260703003', product_name: '收纳箱', total: 20, status: 'pending', created_at: '2026-07-03 09:15' }
])
const myOrdersTotal = ref(3)

// ========== 加载用户信息 ==========
const loadUserInfo = () => {
  // 从 store 获取用户信息
  userInfo.id = loginStore.username || '2021001'
  userInfo.username = loginStore.username || '张三'
  userInfo.college = '计算机学院'
  userInfo.grade = '大三'
  userInfo.phone = '13800138001'
  userInfo.bio = '热爱编程，喜欢运动，乐于助人 🚀'
  userInfo.role = 'student'
  userInfo.avatar = defaultAvatar
}

// ========== 保存个人资料 ==========
const saveProfile = () => {
  // 验证密码
  if (userInfo.newPassword || userInfo.confirmPassword || userInfo.oldPassword) {
    if (!userInfo.oldPassword) {
      ElMessage.warning('请输入当前密码')
      return
    }
    if (userInfo.newPassword.length < 6) {
      ElMessage.warning('新密码长度不能少于6位')
      return
    }
    if (userInfo.newPassword !== userInfo.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致')
      return
    }
  }

  saving.value = true
  setTimeout(() => {
    ElMessage.success('个人资料已更新！')
    saving.value = false
    // 清空密码字段
    userInfo.oldPassword = ''
    userInfo.newPassword = ''
    userInfo.confirmPassword = ''
  }, 1000)
}

// ========== 重置 ==========
const resetProfile = () => {
  loadUserInfo()
  userInfo.oldPassword = ''
  userInfo.newPassword = ''
  userInfo.confirmPassword = ''
  ElMessage.info('已重置')
}

// ========== 退出登录 ==========
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loginStore.logout()
    ElMessage.success('已退出登录')
    router.push('/Login')
  }).catch(() => {})
}

// ========== 更换头像 ==========
const handleChangeAvatar = () => {
  ElMessage.info('点击更换头像（功能开发中）')
}

// ========== 编辑/删除商品 ==========
const handleEditProduct = (row) => {
  ElMessage.info(`编辑商品: ${row.name}`)
}

const handleDeleteProduct = (row) => {
  ElMessageBox.confirm(`确定删除商品 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    myProducts.value = myProducts.value.filter(item => item.id !== row.id)
    myProductsTotal.value = myProducts.value.length
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// ========== 初始化 ==========
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
/* ===== 容器 ===== */
.page-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

/* ===== 左右布局 ===== */
.profile-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
}

/* ===== 左侧边栏 ===== */
.profile-sidebar {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  text-align: center;
  height: fit-content;
  border: 1px solid #f0f0f0;
}

.avatar-section {
  margin-bottom: 20px;
}

.info-section {
  text-align: left;
  font-size: 14px;
  line-height: 2;
  padding: 0 4px;
}

.info-section p {
  margin: 6px 0;
  color: #333;
}

.info-section strong {
  color: #666;
  font-weight: 500;
  display: inline-block;
  width: 72px;
}

/* ===== 右侧主内容 ===== */
.profile-main {
  background: #ffffff;
  border-radius: 12px;
  padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.profile-main :deep() {
  margin-bottom: 20px;
}

.profile-main :deep() {
  margin: 24px 0 20px 0;
}

/* ===== 底部 Tabs ===== */
.profile-tabs {
  margin-top: 24px;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }

  .profile-sidebar {
    display: grid;
    grid-template-columns: 120px 1fr;
    gap: 20px;
    align-items: start;
  }

  .avatar-section {
    margin-bottom: 0;
  }

  .profile-main {
    padding: 20px 16px;
  }

  .page-container {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .profile-sidebar {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .info-section {
    text-align: center;
  }

  .info-section strong {
    width: auto;
  }
}
</style>