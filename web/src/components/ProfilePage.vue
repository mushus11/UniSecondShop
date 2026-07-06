<template>
  <div class="page-container">
    <h2 class="page-title">👤 个人中心</h2>

    <div class="profile-layout">
      <div class="profile-sidebar">
        <div class="avatar-section">
          <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar" />
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleAvatarChange"
            accept="image/*"
            style="margin-top:12px"
          >
            <el-button size="small">更换头像</el-button>
          </el-upload>
        </div>
        <div class="info-section">
          <p><strong>姓名：</strong>{{ userInfo.name || '未设置' }}</p>
          <p><strong>学号：</strong>{{ userInfo.id }}</p>
          <p><strong>学院：</strong>{{ userInfo.college || '未设置' }}</p>
          <p><strong>年级：</strong>{{ userInfo.grade || '未设置' }}</p>
          <p><strong>联系方式：</strong>{{ userInfo.telephone || '未设置' }}</p>
          <p><strong>认证状态：</strong><el-tag :type="userInfo.certified ? 'success' : 'warning'">{{ userInfo.certified ? '已认证' : '未认证' }}</el-tag></p>
        </div>
      </div>

      <div class="profile-main">
        <el-form :model="editForm" label-width="100px" ref="formRef">
          <el-form-item label="姓名">
            <el-input v-model="editForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="学院">
            <el-input v-model="editForm.college" placeholder="请输入学院" />
          </el-form-item>
          <el-form-item label="年级">
            <el-select v-model="editForm.grade" style="width: 100%">
              <el-option label="大一" value="大一" />
              <el-option label="大二" value="大二" />
              <el-option label="大三" value="大三" />
              <el-option label="大四" value="大四" />
              <el-option label="研究生" value="研究生" />
              <el-option label="教职工" value="教职工" />
            </el-select>
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="editForm.telephone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="editForm.profile" type="textarea" :rows="4" placeholder="介绍一下你自己吧..." maxlength="200" show-word-limit />
          </el-form-item>

          <el-divider>修改密码</el-divider>

          <el-form-item label="新密码">
            <el-input v-model="newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            <el-button @click="resetProfile">重置</el-button>
            <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="profile-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="📦 我的发布" name="myGoods">
          <el-table :data="myGoods" border stripe style="width: 100%" v-loading="goodsLoading">
            <el-table-column prop="id" label="商品ID" width="280" show-overflow-tooltip />
            <el-table-column prop="name" label="商品名称" />
            <el-table-column label="价格" width="100">
              <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="分类" width="100">
              <template #default="{ row }"><el-tag size="small">{{ categoryMap[row.type] }}</el-tag></template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.state ? 'info' : 'success'" size="small">{{ row.state ? '已售' : '在售' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination v-model:current-page="goodsPage" :total="myGoodsTotal" :page-size="8" layout="prev, pager, next" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="📋 我的交易" name="myOrders">
          <el-table :data="myOrders" border stripe style="width: 100%" v-loading="ordersLoading">
            <el-table-column prop="id" label="交易单号" width="280" show-overflow-tooltip />
            <el-table-column prop="goodID" label="商品ID" width="280" show-overflow-tooltip />
            <el-table-column label="价格" width="100">
              <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="角色" width="80">
              <template #default="{ row }">
                <el-tag :type="row.sellerId === userIdNum ? 'primary' : 'success'" size="small">
                  {{ row.sellerId === userIdNum ? '卖家' : '买家' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.state ? 'success' : 'warning'" size="small">{{ row.state ? '已完成' : '处理中' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="transactionTime" label="交易时间" width="180" />
          </el-table>
          <div class="pagination">
            <el-pagination v-model:current-page="ordersPage" :total="myOrdersTotal" :page-size="8" layout="prev, pager, next" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="📢 我的求购" name="myPurchases">
          <el-table :data="myPurchases" border stripe style="width: 100%" v-loading="purchasesLoading">
            <el-table-column prop="id" label="需求单号" width="280" show-overflow-tooltip />
            <el-table-column prop="goodName" label="商品名" />
            <el-table-column label="期望价格" width="100">
              <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="分类" width="100">
              <template #default="{ row }"><el-tag size="small">{{ categoryMap[row.type] }}</el-tag></template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="handleDeletePurchase(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

const router = useRouter()
const loginStore = useLoginStore()
const formRef = ref()
const saving = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const userIdNum = computed(() => parseInt(loginStore.id) || 0)

const categoryMap: Record<number, string> = {
  0: '教材书本', 1: '电子产品', 2: '服饰生活', 3: '运动器材', 4: '宿舍家具', 5: '美妆文具'
}

const uploadHeaders = computed(() => ({ Authorization: `Bearer ${loginStore.jwt}` }))

const userInfo = reactive({
  id: '',
  name: '',
  college: '',
  grade: '',
  telephone: '',
  profile: '',
  certified: false,
  avatar: ''
})

const editForm = reactive({
  name: '',
  college: '',
  grade: '',
  telephone: '',
  profile: ''
})

const newPassword = ref('')
const confirmPassword = ref('')

const loadUserInfo = async () => {
  try {
    const res = await api.get('/user/getUserInf', {
      params: { id: userIdNum.value }
    })
    if (res.data) {
      userInfo.id = String(res.data.id || loginStore.id)
      userInfo.name = res.data.name || ''
      userInfo.college = res.data.college || ''
      userInfo.grade = res.data.grade || ''
      userInfo.telephone = res.data.telephone || ''
      userInfo.profile = res.data.profile || ''
      userInfo.certified = res.data.certified || false

      editForm.name = userInfo.name
      editForm.college = userInfo.college
      editForm.grade = userInfo.grade
      editForm.telephone = userInfo.telephone
      editForm.profile = userInfo.profile
    }
  } catch (e) {
    userInfo.id = loginStore.id
    editForm.name = loginStore.username || ''
  }
}

const saveProfile = async () => {
  if (newPassword.value) {
    if (newPassword.value.length < 6) {
      ElMessage.warning('新密码长度不能少于6位')
      return
    }
    if (newPassword.value !== confirmPassword.value) {
      ElMessage.warning('两次输入的密码不一致')
      return
    }
  }

  saving.value = true
  try {
    const res = await api.post('/user/reviseBaseInf', {
      id: userIdNum.value,
      name: editForm.name,
      college: editForm.college,
      grade: editForm.grade,
      telephone: editForm.telephone,
      profile: editForm.profile,
      certified: userInfo.certified
    })

    if (res.data === 200) {
      if (newPassword.value) {
        try {
          await api.post('/user/changePassword', {
            id: userIdNum.value,
            newPassword: newPassword.value
          })
        } catch (e) {}
      }
      ElMessage.success('个人资料已更新！')
      newPassword.value = ''
      confirmPassword.value = ''
    } else {
      ElMessage.error('修改失败')
    }
  } catch (e) {
    console.error('保存失败:', e)
  } finally {
    saving.value = false
  }
}

const resetProfile = () => {
  editForm.name = userInfo.name
  editForm.college = userInfo.college
  editForm.grade = userInfo.grade
  editForm.telephone = userInfo.telephone
  editForm.profile = userInfo.profile
  newPassword.value = ''
  confirmPassword.value = ''
  ElMessage.info('已重置')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    loginStore.logout()
    ElMessage.success('已退出登录')
    router.push('/Login')
  }).catch(() => {})
}

const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) ElMessage.warning('只能上传图片文件')
  return isImage
}

const handleAvatarChange = async (file: any) => {
  if (!beforeAvatarUpload(file.raw)) return

  const formData = new FormData()
  formData.append('image', file.raw)
  formData.append('id', String(userInfo.id || loginStore.id))

  try {
    const res = await api.post('/image/postImage', formData)
    if (res.data) {
      userInfo.avatar = res.data
      ElMessage.success('头像上传成功')
    }
  } catch (e) {
    console.error('上传失败:', e)
    ElMessage.error('头像上传失败')
  }
}

const onAvatarSuccess = (response: any) => {
  if (response) {
    userInfo.avatar = response
    ElMessage.success('头像上传成功')
  }
}

const activeTab = ref('myGoods')

const myGoods = ref<any[]>([])
const myGoodsTotal = ref(0)
const goodsLoading = ref(false)
const goodsPage = ref(1)

const loadMyGoods = async () => {
  goodsLoading.value = true
  try {
    const res = await api.get('/goods/getGoodsInfByUser', {
      params: { userID: userIdNum.value, goodID: '', type: 0 }
    })
    myGoods.value = Array.isArray(res.data) ? res.data : []
    myGoodsTotal.value = myGoods.value.length
  } catch (e) {
    myGoods.value = []
  } finally {
    goodsLoading.value = false
  }
}

const myOrders = ref<any[]>([])
const myOrdersTotal = ref(0)
const ordersLoading = ref(false)
const ordersPage = ref(1)

const loadMyOrders = async () => {
  ordersLoading.value = true
  try {
    const res = await api.get('/Records/getRecordByUserID', {
      params: { ID: userIdNum.value }
    })
    myOrders.value = Array.isArray(res.data) ? res.data : []
    myOrdersTotal.value = myOrders.value.length
  } catch (e) {
    myOrders.value = []
  } finally {
    ordersLoading.value = false
  }
}

const myPurchases = ref<any[]>([])
const purchasesLoading = ref(false)

const loadMyPurchases = async () => {
  purchasesLoading.value = true
  try {
    const res = await api.get('/Purchase/getPurchaseByUserID', {
      params: { userID: userIdNum.value }
    })
    myPurchases.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    myPurchases.value = []
  } finally {
    purchasesLoading.value = false
  }
}

const handleDeletePurchase = async (row: any) => {
  ElMessageBox.confirm('确定删除此求购需求吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const res = await api.delete('/Purchase/delete', { params: { id: row.id } })
      if (res.data === 200) {
        ElMessage.success('删除成功')
        loadMyPurchases()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (e) {}
  }).catch(() => {})
}

onMounted(() => {
  loadUserInfo()
  loadMyGoods()
  loadMyOrders()
  loadMyPurchases()
})
</script>

<style scoped>
.page-container { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 24px; font-weight: 600; color: #1a1a2e; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid #f0f0f0; }

.profile-layout { display: grid; grid-template-columns: 280px 1fr; gap: 24px; }

.profile-sidebar { background: #ffffff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); text-align: center; height: fit-content; border: 1px solid #f0f0f0; }
.avatar-section { margin-bottom: 20px; }
.info-section { text-align: left; font-size: 14px; line-height: 2; padding: 0 4px; }
.info-section p { margin: 6px 0; color: #333; }
.info-section strong { color: #666; font-weight: 500; display: inline-block; width: 80px; }

.profile-main { background: #ffffff; border-radius: 12px; padding: 28px 32px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); border: 1px solid #f0f0f0; }

.profile-tabs { margin-top: 24px; background: #ffffff; border-radius: 12px; padding: 20px 24px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); border: 1px solid #f0f0f0; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }

@media (max-width: 768px) {
  .profile-layout { grid-template-columns: 1fr; }
  .profile-sidebar { display: grid; grid-template-columns: 120px 1fr; gap: 20px; align-items: start; }
  .profile-main { padding: 20px 16px; }
  .page-container { padding: 16px; }
}

@media (max-width: 480px) {
  .profile-sidebar { grid-template-columns: 1fr; text-align: center; }
  .info-section { text-align: center; }
  .info-section strong { width: auto; }
}
</style>