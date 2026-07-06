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
      <el-button type="primary" @click="handleAddCategory">➕ 新增分类</el-button>
      <el-button type="warning" @click="handleTopItems">⭐ 置顶商品</el-button>
      <el-button type="danger" @click="handleUrgent">🔥 急出专区</el-button>
      <el-button type="info" @click="handleWanted">📢 求购板块</el-button>
    </div>

    <el-tabs v-model="activeCategory" @tab-click="loadCategoryItems">
      <el-tab-pane v-for="(label, key) in categoryMap" :key="key" :label="label" :name="key">
        <div class="category-items">
          <div v-for="item in categoryItems" :key="item.id" class="item-card">
            <div class="item-image"><el-image :src="defaultImage" fit="cover" style="width:100%;height:140px" /></div>
            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-price">¥{{ item.price }}</div>
              <div class="item-status"><el-tag :type="item.status === '在售' ? 'success' : 'info'" size="small">{{ item.status }}</el-tag></div>
              <div class="item-actions">
                <el-button size="small" type="primary" @click="handleEditItem(item)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteItem(item)">删除</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination"><el-pagination :total="categoryTotal" layout="prev, pager, next" /></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="140" viewBox="0 0 200 140"%3E%3Crect width="200" height="140" fill="%23f0f0f0"/%3E%3Ctext x="70" y="75" font-size="16" fill="%23999"%3E暂无图片%3C/text%3E%3C/svg%3E'

const categoryMap = {
  books: '教材书本',
  electronics: '电子产品',
  clothing: '服饰生活',
  sports: '运动器材',
  furniture: '宿舍家具',
  beauty: '美妆文具'
}

const icons = {
  books: '📚', electronics: '💻', clothing: '👔', sports: '⚽', furniture: '🛏️', beauty: '💄'
}

const colors = {
  books: '#e8f4f8', electronics: '#e8f5e9', clothing: '#fce4ec', sports: '#fff3e0', furniture: '#f3e5f5', beauty: '#fce4ec'
}

const categoryCounts = reactive({
  books: 12, electronics: 8, clothing: 6, sports: 4, furniture: 5, beauty: 3
})

const activeCategory = ref('books')
const categoryItems = ref([])
const categoryTotal = ref(0)

const loadCategoryItems = () => {
  const mock = [
    { id: 'C001', name: '高等数学上册', price: 25, status: '在售' },
    { id: 'C002', name: '线性代数辅导', price: 18, status: '在售' },
    { id: 'C003', name: '英语四级真题', price: 30, status: '已售' }
  ]
  categoryItems.value = mock
  categoryTotal.value = mock.length
}

const handleAddCategory = () => { ElMessage.info('新增分类') }
const handleTopItems = () => { ElMessage.info('置顶商品管理') }
const handleUrgent = () => { ElMessage.info('急出专区管理') }
const handleWanted = () => { ElMessage.info('求购板块管理') }
const handleEditItem = (item) => { ElMessage.info(`编辑: ${item.name}`) }
const handleDeleteItem = (item) => { ElMessage.success(`已删除: ${item.name}`) }

onMounted(loadCategoryItems)
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
.category-items { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; margin: 16px 0; }
.item-card { border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; }
.item-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.item-info { padding: 12px; }
.item-name { font-weight: 500; font-size: 14px; margin-bottom: 4px; }
.item-price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.item-status { margin: 4px 0; }
.item-actions { display: flex; gap: 8px; margin-top: 8px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>