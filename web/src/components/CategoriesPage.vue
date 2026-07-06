<template>
  <div class="page-container">
    <h2 class="page-title">📂 分类专区管理</h2>

    <div class="category-stats">
      <div v-for="(count, cat) in categoryCounts" :key="cat" class="category-stat-item" :style="{ background: colors[cat] }">
        <span class="cat-icon">{{ icons[cat] }}</span>
        <span class="cat-name">{{ categoryMap[cat] }}</span>
        <span class="cat-count">{{ count }}件</span>
      </div>
    </div>

    <div class="action-bar">
      <el-button type="warning" @click="showTopDialog = true">⭐ 置顶商品管理</el-button>
      <el-button type="danger" @click="showHurryDialog = true">🔥 急出专区</el-button>
      <el-button type="info" @click="showPurchaseDialog = true">📢 求购板块</el-button>
    </div>

    <el-tabs v-model="activeCategory" @tab-click="loadCategoryGoods">
      <el-tab-pane v-for="(label, key) in categoryMap" :key="key" :label="label" :name="String(key)">
        <div class="category-items" v-loading="catLoading">
          <div v-if="categoryGoods.length === 0 && !catLoading" class="empty-hint">该分类暂无商品</div>
          <div v-for="item in categoryGoods" :key="item.id" class="item-card">
            <div class="item-image">
              <el-image :src="`/api/image/getImage/placeholder`" fit="cover" style="width:100%;height:140px">
                <template #error><div class="image-placeholder">暂无图片</div></template>
              </el-image>
            </div>
            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-price">¥{{ item.price?.toFixed(2) }}</div>
              <div class="item-status">
                <el-tag :type="item.state ? 'info' : 'success'" size="small">{{ item.state ? '已售' : '在售' }}</el-tag>
              </div>
              <div class="item-actions">
                <el-button size="small" type="primary" @click="handleTopItem(item)">置顶</el-button>
                <el-button size="small" type="warning" @click="handleHurryItem(item)">急出</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination">
          <el-pagination v-model:current-page="catPage" :total="categoryGoodsTotal" :page-size="catPageSize" layout="prev, pager, next" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="showTopDialog" title="⭐ 置顶商品管理" width="700px">
      <el-table :data="topGoods" border stripe v-loading="topLoading">
        <el-table-column prop="id" label="发布编号" width="280" show-overflow-tooltip />
        <el-table-column prop="goodId" label="商品ID" width="280" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag>{{ stateMap[row.state] }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleCancelTop(row)">取消置顶</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="showHurryDialog" title="🔥 急出商品专区" width="700px">
      <el-table :data="hurryGoods" border stripe v-loading="hurryLoading">
        <el-table-column prop="id" label="发布编号" width="280" show-overflow-tooltip />
        <el-table-column prop="goodId" label="商品ID" width="280" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag>{{ stateMap[row.state] }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="warning" @click="handleCancelHurry(row)">取消急出</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="showPurchaseDialog" title="📢 求购信息" width="700px">
      <el-select v-model="purchaseType" placeholder="选择分类" style="width:200px;margin-bottom:16px" @change="loadPurchases">
        <el-option v-for="(label, key) in categoryMap" :key="key" :label="label" :value="Number(key)" />
      </el-select>
      <el-table :data="purchases" border stripe v-loading="purchaseLoading">
        <el-table-column prop="id" label="需求单号" width="280" show-overflow-tooltip />
        <el-table-column prop="goodName" label="商品名" width="150" />
        <el-table-column label="期望价格" width="100">
          <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="text" label="描述" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'

const loginStore = useLoginStore()

const categoryMap: Record<number, string> = {
  0: '教材书本', 1: '电子产品', 2: '服饰生活', 3: '运动器材', 4: '宿舍家具', 5: '美妆文具'
}
const icons: Record<number, string> = { 0: '📚', 1: '💻', 2: '👔', 3: '⚽', 4: '🛏️', 5: '💄' }
const colors: Record<number, string> = { 0: '#e8f4f8', 1: '#e8f5e9', 2: '#fce4ec', 3: '#fff3e0', 4: '#f3e5f5', 5: '#fce4ec' }
const stateMap: Record<number, string> = { 0: '未处理', 1: '上架', 2: '下架', 3: '售出' }

const categoryCounts = reactive<Record<number, number>>({ 0: 0, 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 })
const activeCategory = ref('0')
const categoryGoods = ref<any[]>([])
const categoryGoodsTotal = ref(0)
const catLoading = ref(false)
const catPage = ref(1)
const catPageSize = ref(8)

const loadCategoryCounts = async () => {
  for (const type of [0, 1, 2, 3, 4, 5]) {
    try {
      const res = await api.get('/goods/getGoodsInfByType', {
        params: { type, userID: parseInt(loginStore.id) || 0, goodID: '' }
      })
      categoryCounts[type] = Array.isArray(res.data) ? res.data.length : 0
    } catch (e) {
      categoryCounts[type] = 0
    }
  }
}

const loadCategoryGoods = async () => {
  catLoading.value = true
  const type = Number(activeCategory.value)
  try {
    const res = await api.get('/goods/getGoodsInfByType', {
      params: { type, userID: parseInt(loginStore.id) || 0, goodID: '' }
    })
    categoryGoods.value = Array.isArray(res.data) ? res.data : []
    categoryGoodsTotal.value = categoryGoods.value.length
  } catch (e) {
    categoryGoods.value = []
    categoryGoodsTotal.value = 0
  } finally {
    catLoading.value = false
  }
}

const handleTopItem = async (item: any) => {
  try {
    const formData = new FormData()
    formData.append('ID', item.id)
    const res = await api.post('/Release/changeTop', formData)
    ElMessage.success(res.data === 200 ? '置顶成功' : '操作失败')
    loadTopGoods()
  } catch (e) {
    console.error('置顶失败:', e)
  }
}

const handleHurryItem = async (item: any) => {
  try {
    const formData = new FormData()
    formData.append('ID', item.id)
    const res = await api.post('/Release/changeHurry', formData)
    ElMessage.success(res.data === 200 ? '急出标记成功' : '操作失败')
    loadHurryGoods()
  } catch (e) {
    console.error('急出标记失败:', e)
  }
}

const showTopDialog = ref(false)
const topGoods = ref<any[]>([])
const topLoading = ref(false)
const loadTopGoods = async () => {
  topLoading.value = true
  try {
    const res = await api.get('/goods/getGoodsInfIsTop')
    topGoods.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    topGoods.value = []
  } finally {
    topLoading.value = false
  }
}

const handleCancelTop = async (row: any) => {
  try {
    const formData = new FormData()
    formData.append('ID', row.goodId || row.id)
    await api.post('/Release/changeTop', formData)
    ElMessage.success('已取消置顶')
    loadTopGoods()
  } catch (e) {}
}

const showHurryDialog = ref(false)
const hurryGoods = ref<any[]>([])
const hurryLoading = ref(false)
const loadHurryGoods = async () => {
  hurryLoading.value = true
  try {
    const res = await api.get('/goods/getGoodsInfIsHarry')
    hurryGoods.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    hurryGoods.value = []
  } finally {
    hurryLoading.value = false
  }
}

const handleCancelHurry = async (row: any) => {
  try {
    const formData = new FormData()
    formData.append('ID', row.goodId || row.id)
    await api.post('/Release/changeHurry', formData)
    ElMessage.success('已取消急出')
    loadHurryGoods()
  } catch (e) {}
}

const showPurchaseDialog = ref(false)
const purchaseType = ref(0)
const purchases = ref<any[]>([])
const purchaseLoading = ref(false)
const loadPurchases = async () => {
  purchaseLoading.value = true
  try {
    const res = await api.get('/Purchase/getPurchaseByType', {
      params: { type: purchaseType.value }
    })
    purchases.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    purchases.value = []
  } finally {
    purchaseLoading.value = false
  }
}

onMounted(() => {
  loadCategoryCounts()
  loadCategoryGoods()
  loadTopGoods()
  loadHurryGoods()
})
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.category-stats { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 12px; margin-bottom: 20px; }
.category-stat-item { padding: 16px; border-radius: 8px; text-align: center; }
.cat-icon { font-size: 28px; display: block; }
.cat-name { display: block; font-size: 14px; font-weight: 500; margin: 4px 0; }
.cat-count { font-size: 20px; font-weight: bold; color: #409eff; }
.action-bar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.category-items { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; margin: 16px 0; min-height: 120px; }
.item-card { border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; background: #fff; }
.item-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.item-info { padding: 12px; }
.item-name { font-weight: 500; font-size: 14px; margin-bottom: 4px; }
.item-price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.item-status { margin: 4px 0; }
.item-actions { display: flex; gap: 8px; margin-top: 8px; }
.image-placeholder { width: 100%; height: 140px; display: flex; align-items: center; justify-content: center; background: #f0f0f0; color: #999; }
.empty-hint { grid-column: 1 / -1; color: #909399; padding: 32px; text-align: center; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>