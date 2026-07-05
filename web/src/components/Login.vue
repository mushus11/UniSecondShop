<script setup>
import {useLoginStore } from "@/store/UseLogin"
const login = useLoginStore()
</script>

<template>
  <div class="login-box" v-if="!login.IfLogin">
    <h2 class="login-title">登录</h2>
    <div class="form-item">
      <label>账号：</label>
      <el-input
          v-model="login.username"
          style="width: 240px"
          placeholder="Please input"
          clearable
      />
    </div>
    <div class="form-item">
      <label>密码：</label>
      <el-input
          v-model="login.password"
          style="width: 240px"
          type="password"
          placeholder="Please input password"
          show-password
      />
    </div>
    <button @click="login.login()" class="login-btn">{{ login.btnText }}</button>
  </div>
</template>

<style scoped>
/* 登录卡片容器 */
.login-box {
  width: 420px;
  margin: 400px auto;
  padding: 35px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

/* 登录标题 */
.login-title {
  text-align: center;
  color: #333333;
  margin: 0 0 30px;
  font-size: 24px;
}

/* 表单行统一布局 */
.form-item {
  margin-bottom: 22px;
  display: flex;
  align-items: center;
}

/* 统一标签样式 */
.form-item label {
  width: 90px;
  font-size: 15px;
  color: #444444;
  flex-shrink: 0;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 44px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 10px;
}
.login-btn:hover {
  background-color: #337ecc;
}
.login-btn:active {
  background-color: #296cd0;
}
</style>