<template>
  <div class="login-box">
    <h2 class="login-title">登录</h2>

    <!-- 账号（学号） -->
    <div class="form-item">
      <label>学号：</label>
      <el-input
          v-model="id"
          style="width: 240px"
          placeholder="请输入学号"
          clearable
          @keyup.enter="handleKeyEnter"
      />
    </div>

    <!-- 密码 -->
    <div class="form-item">
      <label>密码：</label>
      <el-input
          v-model="password"
          style="width: 240px"
          type="password"
          placeholder="请输入密码（至少6位）"
          show-password
          @keyup.enter="handleKeyEnter"
      />
    </div>

    <!-- 登录按钮 -->
    <button
        @click="handleLogin"
        class="login-btn"
        :disabled="btnText === '登录中...'"
    >
      {{ btnText }}
    </button>

    <!-- 跳转到注册 -->
    <div class="register-link">
      <RouterLink to="/Sign">还没有账号？立即注册</RouterLink>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useLoginStore } from "@/store/UseLogin"
import { ElMessage } from 'element-plus'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const loginStore = useLoginStore()

// 使用 storeToRefs 保持响应式
const { id, password, btnText } = storeToRefs(loginStore)

// 登录方法
const handleLogin = async () => {
  // 1. 验证账号是否为空
  if (!id.value.trim()) {
    ElMessage.warning('请输入学号')
    return
  }

  // 2. 验证学号是否为数字
  if (!/^\d+$/.test(id.value.trim())) {
    ElMessage.warning('学号必须为数字')
    return
  }

  // 3. 验证密码是否为空
  if (!password.value) {
    ElMessage.warning('请输入密码')
    return
  }

  // 4. 验证密码长度
  if (password.value.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }

  // 5. 调用 store 的 login 方法
  const success = await loginStore.login()

  // 6. 登录成功后跳转
  if (success) {
    const redirect = route.query.redirect || '/Home'
    router.push(redirect)
  }
}

// 回车键登录
const handleKeyEnter = () => {
  handleLogin()
}
</script>

<style scoped>
/* ========== 登录卡片容器 ========== */
.login-box {
  width: 420px;
  margin: 250px auto;
  padding: 35px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

/* ========== 登录标题 ========== */
.login-title {
  text-align: center;
  color: #333333;
  margin: 0 0 30px;
  font-size: 24px;
}

/* ========== 表单行统一布局 ========== */
.form-item {
  margin-bottom: 22px;
  display: flex;
  align-items: center;
}

/* ========== 统一标签样式 ========== */
.form-item label {
  width: 90px;
  font-size: 15px;
  color: #444444;
  flex-shrink: 0;
}

/* ========== 登录按钮 ========== */
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
.login-btn:hover:not(:disabled) {
  background-color: #337ecc;
}
.login-btn:active:not(:disabled) {
  background-color: #296cd0;
}
.login-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

/* ========== 跳转到注册 ========== */
.register-link {
  text-align: center;
  margin-top: 18px;
  font-size: 14px;
}
.register-link a {
  color: #409eff;
  text-decoration: none;
}
.register-link a:hover {
  text-decoration: underline;
}
</style>