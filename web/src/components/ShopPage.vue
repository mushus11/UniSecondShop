<template>
  <div class="page-container">
    <h2 class="page-title">🛒 商品浏览</h2>

    <div class="category-filter">
      <el-button v-for="(label, key) in categoryMap" :key="key" :type="activeCategory === Number(key) ? 'primary' : ''" @click="activeCategory = Number(key); loadGoods()">
        {{ label }}
      </el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品名称" style="width: 300px" clearable @keyup.enter="currentPage = 1" />
      <el-button type="primary" @click="currentPage = 1">搜索</el-button>
      <el-button @click="keyword = ''; currentPage = 1">重置</el-button>
    </div>

    <div class="product-grid" v-loading="loading">
      <div v-if="filteredGoods.length === 0 && !loading" class="empty-hint">暂无商品</div>
      <div v-for="item in filteredGoods" :key="item.id" class="product-card">
        <div class="product-image">
          <el-image :src="getImageUrl(item.id)" fit="cover" style="width:100%;height:200px">
            <template #error><div class="image-placeholder">暂无图片</div></template>
          </el-image>
        </div>
        <div class="product-info">
          <div class="product-name">{{ item.name }}</div>
          <div class="product-price">¥{{ item.price?.toFixed(2) }}</div>
          <div class="product-meta">发布者: {{ item.userId }}</div>
          <div class="product-status">
            <el-tag :type="item.state ? 'info' : 'success'" size="small">{{ item.state ? '已售出' : '在售' }}</el-tag>
          </div>
          <el-button type="primary" size="small" style="width:100%;margin-top:8px" @click="handleBuy(item)" :disabled="item.state">
            {{ item.state ? '已售罄' : '立即购买' }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="pagination">
      <el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="total" :page-sizes="[8, 16, 24]" layout="total, sizes, prev, pager, next" />
    </div>

    <el-dialog v-model="buyDialogVisible" title="创建交易" width="500px">
      <el-form :model="buyForm" label-width="100px">
        <el-form-item label="商品名称">
          <el-input :model-value="buyForm.goodName" disabled />
        </el-form-item>
        <el-form-item label="交易价格">
          <el-input-number v-model="buyForm.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="交易地点">
          <el-input v-model="buyForm.tradingLocation" placeholder="如：图书馆门口" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="buyForm.note" type="textarea" :rows="2" placeholder="交易备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBuy" :loading="submitting">确认购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

const loginStore = useLoginStore()

const categoryMap: Record<number, string> = {
  0: '📚 教材书本',
  1: '💻 电子产品',
  2: '👔 服饰生活',
  3: '⚽ 运动器材',
  4: '🛏️ 宿舍家具',
  5: '💄 美妆文具'
}

// 默认选中第一个分类（教材书本）
const activeCategory = ref<number>(0)
const keyword = ref('')
const allGoods = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(8)

const total = computed(() => filteredByKeyword.value.length)

const filteredByKeyword = computed(() => {
  let list = allGoods.value
  if (keyword.value.trim()) {
    const kw = keyword.value.trim().toLowerCase()
    list = list.filter(g => g.name?.toLowerCase().includes(kw) || g.text?.toLowerCase().includes(kw))
  }
  return list
})

const filteredGoods = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredByKeyword.value.slice(start, start + pageSize.value)
})

const getImageUrl = (goodId: string) => `/api/image/getImage/placeholder`

const loadGoods = async () => {
  // 如果没有分类被选中，默认选中第一个
  if (activeCategory.value === null) {
    activeCategory.value = 0
  }

  loading.value = true
  try {
    const res = await api.get('/goods/getGoodsInfByType', {
      params: {
        type: activeCategory.value,
        goodID: ''
      }
    })
    allGoods.value = Array.isArray(res.data) ? res.data : []
    currentPage.value = 1
  } catch (e) {
    console.error('加载商品失败:', e)
    allGoods.value = []
  } finally {
    loading.value = false
  }
}

const buyDialogVisible = ref(false)
const submitting = ref(false)
const buyForm = ref({
  goodID: '',
  goodName: '',
  price: 0,
  sellerID: 0,
  tradingLocation: '',
  note: ''
})

const handleBuy = (item: any) => {
  buyForm.value = {
    goodID: item.id,
    goodName: item.name,
    price: item.price,
    sellerID: item.userId,
    tradingLocation: '',
    note: ''
  }
  buyDialogVisible.value = true
}

const submitBuy = async () => {
  if (!buyForm.value.tradingLocation.trim()) {
    ElMessage.warning('请输入交易地点')
    return
  }
  submitting.value = true
  try {
    const res = await api.post('/Records/createRecord', {
      sellerID: buyForm.value.sellerID,
      buyerID: parseInt(loginStore.id) || 0,
      goodID: buyForm.value.goodID,
      price: buyForm.value.price,
      tradingLocation: buyForm.value.tradingLocation,
      note: buyForm.value.note,
      state: false
    })
    if (res.data === 200) {
      ElMessage.success('交易创建成功！')
      buyDialogVisible.value = false
      loadGoods()
    } else {
      ElMessage.error('交易创建失败')
    }
  } catch (e) {
    console.error('创建交易失败:', e)
  } finally {
    submitting.value = false
  }
}

onMounted(loadGoods)
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.category-filter { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
.search-bar { display: flex; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 8px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 16px; }
.product-card { border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; background: #fff; }
.product-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.product-info { padding: 12px; }
.product-name { font-weight: 500; font-size: 14px; margin-bottom: 4px; }
.product-price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.product-meta { font-size: 12px; color: #909399; margin-top: 4px; }
.product-status { margin-top: 4px; }
.image-placeholder { width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; background: #f0f0f0; color: #999; }
.empty-hint { grid-column: 1 / -1; text-align: center; color: #909399; padding: 48px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>