<template>
  <div class="page-container">
    <h2 class="page-title">🛒 商品浏览</h2>

    <!-- 分类筛选 -->
    <div class="category-filter">
      <el-button :type="activeCategory === '' ? 'primary' : ''" @click="activeCategory = ''; loadData()">全部</el-button>
      <el-button v-for="(label, key) in categoryMap" :key="key" :type="activeCategory === key ? 'primary' : ''" @click="activeCategory = key; loadData()">
        {{ label }}
      </el-button>
    </div>

    <!-- 搜索和排序 -->
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 300px" clearable @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px; margin-left: 12px" @change="loadData">
        <el-option label="最新发布" value="newest" />
        <el-option label="价格最低" value="price_asc" />
        <el-option label="价格最高" value="price_desc" />
        <el-option label="最热" value="hot" />
      </el-select>
    </div>

    <!-- 商品列表 -->
    <div class="product-grid">
      <div v-for="item in productList" :key="item.id" class="product-card">
        <div class="product-image"><el-image :src="defaultImage" fit="cover" style="width:100%;height:200px" /></div>
        <div class="product-info">
          <div class="product-name">{{ item.name }}</div>
          <div class="product-price">¥{{ item.price }}</div>
          <div class="product-meta">{{ item.seller }} · {{ item.time }}</div>
          <div class="product-status"><el-tag :type="item.status === '在售' ? 'success' : 'danger'" size="small">{{ item.status }}</el-tag></div>
          <el-button type="primary" size="small" style="width:100%;margin-top:8px" @click="handleBuy(item)">立即购买</el-button>
        </div>
      </div>
    </div>

    <div class="pagination"><el-pagination v-model:page-size="pageSize" v-model:current-page="currentPage" :total="total" layout="total, sizes, prev, pager, next" /></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const categoryMap = {
  books: '📚 教材书本',
  electronics: '💻 电子产品',
  clothing: '👔 服饰生活',
  sports: '⚽ 运动器材',
  furniture: '🛏️ 宿舍家具',
  beauty: '💄 美妆文具'
}

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"%3E%3Crect width="200" height="200" fill="%23f0f0f0"/%3E%3Ctext x="70" y="105" font-size="16" fill="%23999"%3E暂无图片%3C/text%3E%3C/svg%3E'

const activeCategory = ref('')
const keyword = ref('')
const sortBy = ref('newest')
const productList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

const loadData = () => {
  const mock = [
    { id: 1, name: '高等数学第六版', price: 25, seller: '张三', time: '10分钟前', status: '在售' },
    { id: 2, name: '蓝牙降噪耳机', price: 120, seller: '李四', time: '30分钟前', status: '在售' },
    { id: 3, name: '宿舍折叠桌', price: 45, seller: '王五', time: '1小时前', status: '在售' },
    { id: 4, name: '考研英语真题', price: 30, seller: '赵六', time: '2小时前', status: '已售' },
    { id: 5, name: '机械键盘', price: 89, seller: '钱七', time: '3小时前', status: '在售' },
    { id: 6, name: '宿舍收纳箱', price: 20, seller: '孙八', time: '4小时前', status: '在售' }
  ]
  productList.value = mock
  total.value = mock.length
}

const handleBuy = (item) => {
  ElMessage.success(`已加入购物车: ${item.name}`)
}

onMounted(loadData)
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }
.category-filter { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
.search-bar { display: flex; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 4px; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; }
.product-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.product-info { padding: 12px; }
.product-name { font-weight: 500; font-size: 14px; margin-bottom: 4px; }
.product-price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.product-meta { font-size: 12px; color: #909399; margin-top: 4px; }
.product-status { margin-top: 4px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>