<template>
  <div class="home-container">
    <el-card shadow="hover">
      <h1>欢迎来到商城首页</h1>
      <p>这里展示首页轮播、活动公告、热门商品推荐</p>
      <el-divider />
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card>活动专区1</el-card>
        </el-col>
        <el-col :span="8">
          <el-card>活动专区2</el-card>
        </el-col>
        <el-col :span="8">
          <el-card>活动专区3</el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
// 首页无需登录逻辑
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}
h1 {
  color: #304156;
}
</style>