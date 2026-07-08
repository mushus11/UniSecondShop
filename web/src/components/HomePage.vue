<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <h2 class="page-title">🏠 校园二手交易平台</h2>
    <!-- 欢迎banner -->
    <div class="welcome-banner">
      <h3>欢迎回来，{{ username }}！</h3>
      <p>今日有 {{ stats.orders }} 笔交易处理中，{{ stats.products }} 件商品在售</p>
    </div>
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <div class="quick-card" @click="$router.push('/Listings')">
        <span class="quick-icon">📝</span>
        <span class="quick-label">发布商品</span>
      </div>
      <div class="quick-card" @click="$router.push('/Orders')">
        <span class="quick-icon">📦</span>
        <span class="quick-label">管理交易</span>
      </div>
      <div class="quick-card" @click="$router.push('/Shop')">
        <span class="quick-icon">🔍</span>
        <span class="quick-label">浏览商品</span>
      </div>
      <div class="quick-card" @click="$router.push('/Profile')">
        <span class="quick-icon">👤</span>
        <span class="quick-label">个人中心</span>
      </div>
    </div>
    <!-- 置顶商品 -->
    <div class="section-header">
      <h3>🔥 置顶商品</h3>
      <el-button text @click="$router.push('/Shop')">查看全部 →</el-button>
    </div>
    <div class="product-grid" v-loading="loading.top">
      <div v-if="topGoods.length === 0" class="empty-hint">暂无置顶商品</div>
      <div v-for="item in topGoods" :key="item.id" class="product-card" @click="goDetail(item)">
        <div class="product-image">
          <el-image :src="getImageUrl(item.goodId)" fit="cover" style="width:100%;height:160px">
            <template #error><div class="image-placeholder">暂无图片</div></template>
          </el-image>
        </div>
        <div class="product-info">
          <div class="product-name">{{ getGoodName(item.goodId) }}</div>
          <div class="product-meta">发布ID: {{ item.id?.substring(0, 12) }}...</div>
          <div class="product-tags">
            <el-tag v-if="item.topMark" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="item.hurryMark" type="warning" size="small">急出</el-tag>
          </div>
        </div>
      </div>
    </div>
    <!-- 急出商品 -->

    <div class="section-header" style="margin-top:24px">
      <h3>⚡ 急出商品</h3>
      <el-button text @click="$router.push('/Shop')">查看全部 →</el-button>
    </div>
    <div class="product-grid" v-loading="loading.hurry">
      <div v-if="hurryGoods.length === 0" class="empty-hint">暂无急出商品</div>
      <div v-for="item in hurryGoods" :key="item.id" class="product-card" @click="goDetail(item)">
        <div class="product-image">
          <el-image :src="getImageUrl(item.goodId)" fit="cover" style="width:100%;height:160px">
            <template #error><div class="image-placeholder">暂无图片</div></template>
          </el-image>
        </div>
        <div class="product-info">
          <div class="product-name">{{ getGoodName(item.goodId) }}</div>
          <div class="product-meta">发布ID: {{ item.id?.substring(0, 12) }}...</div>
          <div class="product-tags">
            <el-tag v-if="item.hurryMark" type="warning" size="small">急出</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入依赖
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginStore } from '@/store/UseLogin'
import api from '@/api'
// 获取路由实例
const router = useRouter()
// 获取登录状态实例
const loginStore = useLoginStore()
// 用户名
const username = ref(loginStore.username || loginStore.id || '用户')
// 统计信息
const stats = reactive({ products: 0, orders: 0 })
// 加载状态
const loading = reactive({ top: false, hurry: false })
// 置顶商品列表
const topGoods = ref<any[]>([])
// 急出商品列表
const hurryGoods = ref<any[]>([])
// 商品缓存
const goodsCache = ref<Map<string, any>>(new Map())
// 获取商品图片url
const getImageUrl = (goodId: string) => {
  if (!goodId) return ''
  const cached = goodsCache.value.get(goodId)
  const imageId = cached?.imageId || ''
  return imageId ? `/api/image/getImage/${imageId}` : ''
}
// 获取商品名称
const getGoodName = (goodId: string) => {
  const cached = goodsCache.value.get(goodId)
  return cached?.name || '商品'
}
// 跳转商品详情
const goDetail = (item: any) => {
  router.push({ path: '/Shop', query: { goodId: item.goodId } })
}
// 加载置顶商品
const loadTopGoods = async () => {
  loading.top = true
  try {
    const res = await api.get('/goods/getGoodsInfIsTop')
    if (Array.isArray(res.data)) {
      topGoods.value = res.data
      for (const item of res.data) {
        if (item.goodsID && !goodsCache.value.has(item.goodsID)) {
          await fetchGoodDetail(item.goodsID)
        }
      }
    }
  } catch (e) {
    console.error('获取置顶商品失败:', e)
  } finally {
    loading.top = false
  }
}
// 加载急出商品
const loadHurryGoods = async () => {
  loading.hurry = true
  try {
    const res = await api.get('/goods/getGoodsInfIsHarry')
    if (Array.isArray(res.data)) {
      hurryGoods.value = res.data
      for (const item of res.data) {
        if (item.goodId && !goodsCache.value.has(item.goodId)) {
          await fetchGoodDetail(item.goodId)
        }
      }
    }
  } catch (e) {
    console.error('获取急出商品失败:', e)
  } finally {
    loading.hurry = false
  }
}
// 获取商品详情

const fetchGoodDetail = async (goodId: string) => {
  if (!goodId?.trim()) return
  try {
    const res = await api.get('/goods/getGoodInf', {
      params: { goodID: goodId }
    })
    if (res.data) {
      goodsCache.value.set(goodId, res.data)
    }
  } catch (e) {
    console.error('获取商品详情失败', e)
  }
}
// 获取统计信息

const loadStats = async () => {
  try {
    const upRes = await api.get('/Release/getUpGoods')
    if (Array.isArray(upRes.data)) {
      stats.products = upRes.data.length
    }
  } catch (e) {
    stats.products = 0
  }
  try {
    const recRes = await api.get('/Records/getRecordByUserID', {
      params: { ID: parseInt(loginStore.id) || 0 }
    })
    if (Array.isArray(recRes.data)) {
      stats.orders = recRes.data.filter((r: any) => !r.state).length
    }
  } catch (e) {
    stats.orders = 0
  }
}
// 页面加载时执行
onMounted(() => {
  username.value = loginStore.username || loginStore.id || '用户'
  loadTopGoods()
  loadHurryGoods()
  loadStats()
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

.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-header h3 { margin: 0; font-size: 18px; }

.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.product-card { border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; transition: box-shadow 0.3s; cursor: pointer; background: #fff; }
.product-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.product-info { padding: 12px; }
.product-name { font-weight: 500; font-size: 14px; margin-bottom: 4px; }
.product-meta { font-size: 12px; color: #909399; }
.product-tags { margin-top: 6px; display: flex; gap: 4px; }
.image-placeholder { width: 100%; height: 160px; display: flex; align-items: center; justify-content: center; background: #f0f0f0; color: #999; font-size: 14px; }
.empty-hint { grid-column: 1 / -1; text-align: center; color: #909399; padding: 32px; }
</style>