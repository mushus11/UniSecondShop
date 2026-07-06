<script setup lang="ts">
import { useLoginStore } from "@/store/UseLogin"
import { useRouter } from "vue-router"
const login = useLoginStore()
const router = useRouter()

const handleLogout = () => {
  login.logout()
  router.push('/Login')
}
</script>

<template>
  <div class="nav-wrap">
    <div class="nav" v-if="login.isLoggedIn">
      <RouterLink to="/Home" class="nav-item">首页</RouterLink>
      <RouterLink to="/Shop" class="nav-item">商城</RouterLink>
      <RouterLink to="/Users" class="nav-item">用户管理</RouterLink>
      <RouterLink to="/Catalog" class="nav-item">商品档案</RouterLink>
      <RouterLink to="/Listings" class="nav-item">发布管理</RouterLink>
      <RouterLink to="/Categories" class="nav-item">分类专区</RouterLink>
      <RouterLink to="/Orders" class="nav-item">交易管理</RouterLink>
      <RouterLink to="/Reports" class="nav-item">统计报表</RouterLink>
      <RouterLink to="/Profile" class="nav-item">个人中心</RouterLink>
      <span class="nav-item nav-logout" @click="handleLogout">注销</span>
    </div>
    <div class="page-view">
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
.nav-wrap {
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  overflow-y: auto;
  background-image: url(/bg.png);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.nav {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  padding: 0;
  height: 72px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(16px) saturate(1.2);
  -webkit-backdrop-filter: blur(16px) saturate(1.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 28px rgba(0, 0, 0, 0.05), 0 1px 4px rgba(0, 0, 0, 0.02);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: box-shadow 0.3s ease;
}

.nav-item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 26px;
  font-size: 15px;
  font-weight: 600;
  color: #4a5568;
  text-decoration: none;
  border-radius: 40px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  background: transparent;
  cursor: pointer;
}

.nav-item:hover {
  color: #1a2332;
  background: rgba(79, 140, 247, 0.08);
  transform: translateY(-1px);
}

.nav-logout {
  color: #f56c6c;
  margin-left: auto;
}
.nav-logout:hover {
  color: #e04040;
  background: rgba(245, 108, 108, 0.08);
}

.page-view {
  flex: 1;
  padding: 32px 0 48px;
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  box-shadow: 0 -2px 32px rgba(0, 0, 0, 0.02);
  overflow-y: auto;
}

.page-view > * {
  animation: fadeUp 0.5s cubic-bezier(0.4, 0, 0.2, 1) both;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(18px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 680px) {
  .nav { padding: 0; height: 64px; gap: 6px; justify-content: flex-start; overflow-x: auto; -webkit-overflow-scrolling: touch; flex-wrap: nowrap; scrollbar-width: none; }
  .nav::-webkit-scrollbar { display: none; }
  .nav-item { padding: 6px 18px; font-size: 14px; flex-shrink: 0; }
  .page-view { padding: 20px 0 32px; border-radius: 20px 20px 0 0; margin-top: 4px; }
}

@media (max-width: 420px) {
  .nav { height: 56px; padding: 0; gap: 4px; }
  .nav-item { padding: 5px 14px; font-size: 13px; }
  .page-view { padding: 16px 0 24px; border-radius: 16px 16px 0 0; }
}
</style>