<template>
  <div class="page-container">
    <h2 class="page-title">📦 商品档案管理</h2>

    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.total }}</span><span class="stat-label">总商品</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.active }}</span><span class="stat-label">在售</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.sold }}</span><span class="stat-label">已售</span></div>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品名称" style="width: 200px" clearable />
      <el-select v-model="filterType" placeholder="全部分类" style="width: 150px; margin-left: 12px">
        <el-option label="全部" :value="null" />
        <el-option v-for="(label, key) in categoryMap" :key="key" :label="label" :value="Number(key)" />
      </el-select>
      <el-button type="primary" style="margin-left: 12px" @click="currentPage = 1">搜索</el-button>
      <el-button style="margin-left: 8px" @click="resetFilters">重置</el-button>
      <el-button type="success" style="margin-left: 8px" @click="handleAdd">添加商品</el-button>
    </div>

    <el-table :data="pagedList" border stripe style="width: 100%; margin-top: 16px" v-loading="loading">
      <el-table-column prop="id" label="商品编号" width="240" show-overflow-tooltip />
      <el-table-column prop="name" label="商品名称" width="150" />
      <el-table-column label="分类" width="110">
        <template #default="{ row }"><el-tag>{{ categoryMap[row.type] || '未知' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="价格" width="90">
        <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.state ? 'info' : 'success'">{{ row.state ? '已售' : '在售' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="success" @click="handleViewImages(row)">图片</el-button>
          <el-button size="small" type="warning" @click="handleViewVouchers(row)">凭证</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="filteredList.length" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.type" style="width:100%" placeholder="请选择分类">
            <el-option v-for="(label, key) in categoryMap" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.text" type="textarea" :rows="3" placeholder="请描述商品信息" />
        </el-form-item>
        <el-form-item label="上架状态">
          <el-switch v-model="form.state" active-text="在售" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGoods" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="imageDialogVisible" title="商品图片管理" width="750px">
      <div v-if="currentGoodId" class="image-section">
        <div class="image-upload-row">
          <el-upload
            :action="`/api/image/commonImage`"
            :headers="uploadHeaders"
            :data="uploadData"
            name="images"
            multiple
            :on-success="onImageUploadSuccess"
            :before-upload="beforeImageUpload"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
        <el-divider>已上传图片</el-divider>
        <div class="image-list" v-loading="imageLoading">
          <div v-for="imgId in imageIds" :key="imgId" class="image-item">
            <el-image :src="`/api/image/getImage/${imgId}`" fit="cover" style="width:120px;height:120px;border-radius:4px" />
            <span class="image-id">{{ imgId?.substring(0, 8) }}...</span>
          </div>
          <div v-if="imageIds.length === 0 && !imageLoading" class="empty-hint">暂无图片，请上传</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="imageDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="voucherDialogVisible" title="购买凭证管理" width="650px">
      <div v-if="currentGoodId" class="image-section">
        <div class="image-upload-row">
          <el-upload
            :action="`/api/image/voucherImage`"
            :headers="uploadHeaders"
            :data="voucherUploadData"
            name="image"
            :on-success="onVoucherUploadSuccess"
            :before-upload="beforeImageUpload"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </div>
        <el-divider>已上传凭证</el-divider>
        <div class="image-list" v-loading="voucherLoading">
          <div v-for="imgId in voucherImageIds" :key="imgId" class="image-item">
            <el-image :src="`/api/image/getImage/${imgId}`" fit="cover" style="width:120px;height:120px;border-radius:4px" />
            <el-tag type="warning" size="small" style="margin-top:4px">凭证</el-tag>
            <span class="image-id">{{ imgId?.substring(0, 8) }}...</span>
          </div>
          <div v-if="voucherImageIds.length === 0 && !voucherLoading" class="empty-hint">暂无购买凭证</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="voucherDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

const loginStore = useLoginStore()

const categoryMap: Record<number, string> = {
  0: '教材书本', 1: '电子产品', 2: '服饰生活', 3: '运动器材', 4: '宿舍家具', 5: '美妆文具'
}

const stats = reactive({ total: 0, active: 0, sold: 0 })
const allList = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterType = ref<number | null>(null)

const filteredList = computed(() => {
  let list = allList.value
  if (keyword.value.trim()) {
    const kw = keyword.value.trim().toLowerCase()
    list = list.filter(g => g.name?.toLowerCase().includes(kw) || g.text?.toLowerCase().includes(kw))
  }
  if (filterType.value !== null) {
    list = list.filter(g => g.type === filterType.value)
  }
  return list
})

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

const loadGoods = async () => {
  loading.value = true
  try {
    const promises = [0, 1, 2, 3, 4, 5].map(type =>
      api.get('/goods/getGoodsInfByType', {
        params: { type, userID: parseInt(loginStore.id) || 0, goodID: '' }
      }).then(r => (Array.isArray(r.data) ? r.data : [])).catch(() => [])
    )
    const results = await Promise.all(promises)
    allList.value = results.flat()
    stats.total = allList.value.length
    stats.active = allList.value.filter(g => !g.state).length
    stats.sold = allList.value.filter(g => g.state).length
  } catch (e) {
    console.error('加载商品失败:', e)
    allList.value = []
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  keyword.value = ''
  filterType.value = null
  currentPage.value = 1
}

const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const saving = ref(false)
const form = reactive({
  id: '',
  name: '',
  type: 0,
  price: 0,
  text: '',
  state: false
})

const handleAdd = () => {
  dialogTitle.value = '添加商品'
  form.id = ''
  form.name = ''
  form.type = 0
  form.price = 0
  form.text = ''
  form.state = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑商品'
  form.id = row.id
  form.name = row.name
  form.type = row.type
  form.price = row.price
  form.text = row.text || ''
  form.state = row.state
  dialogVisible.value = true
}

const saveGoods = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('请输入商品名称')
    return
  }
  saving.value = true
  try {
    const url = form.id ? '/goods/changeGoodInf' : '/goods/uploadGoodInf'
    const payload: any = {
      userId: parseInt(loginStore.id) || 0,
      name: form.name,
      type: form.type,
      price: form.price,
      text: form.text,
      state: form.state
    }
    const res = await api.post(url, payload)
    if (res.data === 200 || res.data.code === 200 || res.data.goodID) {
      ElMessage.success(form.id ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadGoods()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (e) {
    console.error('保存商品失败:', e)
  } finally {
    saving.value = false
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除商品 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const formData = new FormData()
      formData.append('goodID', row.id)
      const res = await api.delete('/goods/deleteGood', { data: formData })
      if (res.data === 200) {
        ElMessage.success('删除成功')
        loadGoods()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (e) { console.error('删除失败:', e) }
  }).catch(() => {})
}

const imageDialogVisible = ref(false)
const currentGoodId = ref('')
const imageIds = ref<string[]>([])
const imageLoading = ref(false)
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${loginStore.jwt}` }))
const uploadData = computed(() => ({
  goodID: currentGoodId.value,
  text: JSON.stringify([{ text: '', voucher: false }])
}))

const handleViewImages = async (row: any) => {
  currentGoodId.value = row.id
  imageDialogVisible.value = true
  imageLoading.value = true
  try {
    const res = await api.get('/image/getImagesID', { params: { goodID: row.id } })
    imageIds.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    imageIds.value = []
  } finally {
    imageLoading.value = false
  }
}

const beforeImageUpload = () => true
const onImageUploadSuccess = () => {
  ElMessage.success('图片上传成功')
  handleViewImages({ id: currentGoodId.value })
}

const voucherDialogVisible = ref(false)
const voucherImageIds = ref<string[]>([])
const voucherLoading = ref(false)
const voucherUploadData = computed(() => ({
  goodID: currentGoodId.value,
  text: JSON.stringify({ text: '购买凭证', voucher: true })
}))

const handleViewVouchers = async (row: any) => {
  currentGoodId.value = row.id
  voucherDialogVisible.value = true
  voucherLoading.value = true
  try {
    const res = await api.get('/image/getImagesID', { params: { goodID: row.id } })
    const allIds = Array.isArray(res.data) ? res.data : []
    const vouchers: string[] = []
    for (const imgId of allIds) {
      try {
        const textRes = await api.get(`/image/getImageText/${imgId}`)
        if (textRes.data?.voucher) {
          vouchers.push(imgId)
        }
      } catch { /* skip */ }
    }
    voucherImageIds.value = vouchers
  } catch (e) {
    voucherImageIds.value = []
  } finally {
    voucherLoading.value = false
  }
}

const onVoucherUploadSuccess = () => {
  ElMessage.success('购买凭证上传成功')
  handleViewVouchers({ id: currentGoodId.value })
}

onMounted(loadGoods)
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
.image-section { padding: 8px 0; }
.image-upload-row { margin-bottom: 8px; }
.image-list { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 8px; min-height: 60px; }
.image-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.image-id { font-size: 11px; color: #909399; }
.empty-hint { color: #909399; padding: 24px; text-align: center; }
</style>