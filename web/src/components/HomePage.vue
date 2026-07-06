<template>
  <div class="page-container">
    <h2 class="page-title">🏠 校园二手交易平台</h2>

    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <h3>欢迎回来，{{ username }}！</h3>
      <p>今日有 {{ todayOrders }} 笔交易待处理，{{ todayProducts }} 件商品新发布</p>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <div class="quick-card" @click="$router.push('/Listings')">
        <span class="quick-icon">📝</span>
        <span class="quick-label">发布商品</span>
      </div>
      <div class="quick-card" @click="$router.push('/Orders')">
        <span class="quick-icon">📦</span>
        <span class="quick-label">管理订单</span>
      </div>
      <div class="quick-card" @click="$router.push('/Catalog')">
        <span class="quick-icon">🔍</span>
        <span class="quick-label">浏览商品</span>
      </div>
      <div class="quick-card" @click="$router.push('/Profile')">
        <span class="quick-icon">👤</span>
        <span class="quick-label">个人中心</span>
      </div>
    </div>

    <!-- 数据概览 -->
    <div class="stats-cards">
      <div class="stat-card"><span class="stat-number">{{ stats.products }}</span><span class="stat-label">在售商品</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.orders }}</span><span class="stat-label">今日订单</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.users }}</span><span class="stat-label">在线用户</span></div>
      <div class="stat-card"><span class="stat-number">{{ stats.messages }}</span><span class="stat-label">未读消息</span></div>
    </div>

    <!-- 最新商品 -->
    <div class="section-header">
      <h3>🔥 最新发布</h3>
      <el-button type="text" @click="$router.push('/Shop')">查看全部 →</el-button>
    </div>
    <div class="product-grid">
      <div v-for="item in latestProducts" :key="item.id" class="product-card">
        <div class="product-image"><el-image :src="defaultImage" fit="cover" style="width:100%;height:160px" /></div>
        <div class="product-info">
          <div class="product-name">{{ item.name }}</div>
          <div class="product-price">¥{{ item.price }}</div>
          <div class="product-meta">{{ item.seller }} · {{ item.time }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useLoginStore } from '@/store/UseLogin'

const loginStore = useLoginStore()
const username = ref(loginStore.id || '用户')

const todayOrders = ref(12)
const todayProducts = ref(8)

const stats = reactive({
  products: 156,
  orders: 12,
  users: 89,
  messages: 5
})

const defaultImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="160" viewBox="0 0 200 160"%3E%3Crect width="200" height="160" fill="%23f0f0f0"/%3E%3Ctext x="70" y="85" font-size="16" fill="%23999"%3E暂无图片%3C/text%3E%3C/svg%3E'

const latestProducts = ref([
  { id: 1, name: '高等数学第六版', price: 25, seller: '张三', time: '10分钟前' },
  { id: 2, name: '蓝牙降噪耳机', price: 120, seller: '李四', time: '30分钟前' },
  { id: 3, name: '宿舍折叠桌', price: 45, seller: '王五', time: '1小时前' },
  { id: 4, name: '考研英语真题', price: 30, seller: '赵六', time: '2小时前' }
])

onMounted(() => {
  // 从 store 获取用户名
  username.value = loginStore.id || '用户'
})
</script>

<style scoped>
.page-container { padding: 24px; }
.page-title { font-size: 22px; margin-bottom: 24px; }

.welcome-banner { background: linear-gradient(135deg, #409eff, #79bbff); color: #fff; padding: 24px 32px; border-radius: 12px; margin-bottom: 24px; }
.welcome-banner h3 { margin: 0 0 8px 0; font-size: 20px; }
.welcome-banner p { margin: 0; opacity: 0.9; }

.quick-actions { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.quick-card { background: #f5f7fa; border-radius: 12px; padding: 24px; text-align: center; cursor: pointer; transition: all 0.3s; border: 2px solid transparent; }
.quick-card:hover { border-color: #409eff; transform: translateY(-4px); box-shadow: 0 4px 12px rgba(64,158,255,0.2); }
.quick-icon { font-size: 32px; display: block; margin-bottom: 8px; }
.quick-label { font-size: 14px; color: #333; font-weight: 500; }

.stats-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #f5f7fa; padding: 20px; border-radius: 8px; text-align: center; }
.stat-number { font-size: 28px; font-weight: bold; color: #409eff; display: block; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }

.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-header h3 { margin: 0; font-size: 18px; }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; }
.product-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.product-info { padding: 12px; }
.product-name { font-weight: 500; font-size: 14px; margin-bottom: 4px; }
.product-price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.product-meta { font-size: 12px; color: #909399; margin-top: 4px; }
</style>