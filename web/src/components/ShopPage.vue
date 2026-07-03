<template>
  <div class="shop-container">
    <el-card shadow="hover">
      <h2>商品商城</h2>
      <el-divider />
      <!-- 商品筛选 -->
      <el-space wrap>
        <el-button type="primary">全部商品</el-button>
        <el-button>数码产品</el-button>
        <el-button>服饰鞋帽</el-button>
        <el-button>生活用品</el-button>
      </el-space>
      <el-divider />
      <!-- 商品列表 -->
      <el-row :gutter="20">
        <el-col :span="6" v-for="i in 8" :key="i">
          <el-card shadow="hover">
            <div class="goods-img">商品{{i}}</div>
            <h4>精品商品{{i}}</h4>
            <p class="price">¥{{100 + i*20}}</p>
            <el-button size="small" type="primary">加入购物车</el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.shop-container {
  max-width: 1400px;
  margin: 0 auto;
}
.goods-img {
  height: 140px;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  margin-bottom: 10px;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
</style>