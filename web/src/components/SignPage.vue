<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from "axios";

const router = useRouter()

// 注册表单数据
const registerForm = reactive({
  username: 0,
  password: '',
})
const UserInformation = reactive({
  confirmPassword: '',
})

// 按钮文字
const btnText = ref('注册')


// 注册方法
async function handleRegister() {
  btnText.value = '注册中...'
  // 1. 验证账号是否为空
  if (!registerForm.username.trim()) {
    ElMessage.warning('请输入账号')
    return
  }

  // 2. 验证密码是否为空
  if (!registerForm.password) {
    ElMessage.warning('请输入密码')
    return
  }

  // 3. 验证密码长度
  if (registerForm.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }

  // 4. 验证两次密码是否一致
  if (registerForm.password !== UserInformation.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }


  const res = await axios({
    url:"/api/user/register",
    method:"post",
    data: {
      id: registerForm.username,
      password: registerForm.password
    }
  })

  if (res.data.code === 200) {
    ElMessage.success('注册成功！请登录')
    btnText.value = '注册'
    await router.push('/Login')
  }
}
</script>

<template>
  <div class="register-box">
    <h2 class="register-title">注册</h2>

    <!-- 账号 -->
    <div class="form-item">
      <label>账号：</label>
      <el-input
          v-model="registerForm.username"
          style="width: 240px"
          placeholder="请输入学号/账号"
          clearable
      />
    </div>

    <!-- 密码 -->
    <div class="form-item">
      <label>密码：</label>
      <el-input
          v-model="registerForm.password"
          style="width: 240px"
          type="password"
          placeholder="请输入密码（至少6位）"
          show-password
      />
    </div>

    <!-- 确认密码 -->
    <div class="form-item">
      <label>确认密码：</label>
      <el-input
          v-model="UserInformation.confirmPassword"
          style="width: 240px"
          type="password"
          placeholder="请再次输入密码"
          show-password
      />
    </div>

    <!-- 注册按钮 -->
    <button @click="handleRegister" class="register-btn">
      {{ btnText }}
    </button>

    <!-- 跳转到登录 -->
    <div class="login-link">
      <RouterLink to="/Login">已有账号？去登录</RouterLink>
    </div>
  </div>
</template>

<style scoped>
/* ========== 注册卡片容器（与登录样式一致） ========== */
.register-box {
  width: 420px;
  margin: 300px auto;
  padding: 35px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

/* ========== 注册标题 ========== */
.register-title {
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

/* ========== 注册按钮（与登录按钮风格一致） ========== */
.register-btn {
  width: 100%;
  height: 44px;
  background-color: #67c23a;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 10px;
}
.register-btn:hover {
  background-color: #5daf34;
}
.register-btn:active {
  background-color: #529b2e;
}

/* ========== 跳转到登录 ========== */
.login-link {
  text-align: center;
  margin-top: 18px;
  font-size: 14px;
}
.login-link a {
  color: #409eff;
  text-decoration: none;
}
.login-link a:hover {
  text-decoration: underline;
}
</style>