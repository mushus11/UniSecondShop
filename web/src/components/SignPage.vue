<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from "axios";

const router = useRouter()

const registerForm = reactive({
  id: '',
  password: '',
})
const confirmPassword = ref('')

const btnText = ref('注册')

async function handleRegister() {
  btnText.value = '注册中...'

  if (!registerForm.id.trim()) {
    ElMessage.warning('请输入学号')
    btnText.value = '注册'
    return
  }

  const idNum = parseInt(registerForm.id)
  if (isNaN(idNum)) {
    ElMessage.warning('学号必须为数字')
    btnText.value = '注册'
    return
  }

  if (!registerForm.password) {
    ElMessage.warning('请输入密码')
    btnText.value = '注册'
    return
  }

  if (registerForm.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    btnText.value = '注册'
    return
  }

  if (registerForm.password !== confirmPassword.value) {
    ElMessage.warning('两次输入的密码不一致')
    btnText.value = '注册'
    return
  }

  try {
    const res = await axios({
      url: "/api/user/register",
      method: "post",
      data: {
        id: idNum,
        password: registerForm.password
      }
    })

    if (res.data.code === 200) {
      ElMessage.success('注册成功！请登录')
      btnText.value = '注册'
      await router.push('/Login')
    } else {
      ElMessage.error('注册失败，请重试')
      btnText.value = '注册'
    }
  } catch (error) {
    console.error('注册异常:', error)
    ElMessage.error('网络异常，请检查网络连接后重试')
    btnText.value = '注册'
  }
}
</script>

<template>
  <div class="register-box">
    <h2 class="register-title">注册</h2>

    <div class="form-item">
      <label>学号：</label>
      <el-input
          v-model="registerForm.id"
          style="width: 240px"
          placeholder="请输入学号"
          clearable
          @keyup.enter="handleRegister"
      />
    </div>

    <div class="form-item">
      <label>密码：</label>
      <el-input
          v-model="registerForm.password"
          style="width: 240px"
          type="password"
          placeholder="请输入密码（至少6位）"
          show-password
          @keyup.enter="handleRegister"
      />
    </div>

    <div class="form-item">
      <label>确认密码：</label>
      <el-input
          v-model="confirmPassword"
          style="width: 240px"
          type="password"
          placeholder="请再次输入密码"
          show-password
          @keyup.enter="handleRegister"
      />
    </div>

    <button @click="handleRegister" class="register-btn" :disabled="btnText === '注册中...'">
      {{ btnText }}
    </button>

    <div class="login-link">
      <RouterLink to="/Login">已有账号？去登录</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.register-box {
  width: 420px;
  margin: 300px auto;
  padding: 35px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  color: #333333;
  margin: 0 0 30px;
  font-size: 24px;
}

.form-item {
  margin-bottom: 22px;
  display: flex;
  align-items: center;
}

.form-item label {
  width: 90px;
  font-size: 15px;
  color: #444444;
  flex-shrink: 0;
}

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
.register-btn:hover:not(:disabled) {
  background-color: #5daf34;
}
.register-btn:active:not(:disabled) {
  background-color: #529b2e;
}
.register-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

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