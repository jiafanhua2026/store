<template>
  <div class="login">
    <div class="login-container">
      <div class="login-card">
        <div class="logo">
          <h1>{{ companyName }}</h1>
          <p>女装进销存管理系统</p>
        </div>
        
        <el-form :model="loginForm" ref="loginForm" class="login-form">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码" 
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="login" class="login-btn">登录</el-button>
          </el-form-item>
        </el-form>
        
        <div class="tips">
          <p>管理员: admin / admin123</p>
          <p>销售员: sales / sales123</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const companyName = ref('等你服饰')
const loginForm = ref({
  username: '',
  password: ''
})

const login = async () => {
  try {
    const res = await axios.post('/users/login', loginForm.value)
    localStorage.setItem('user', JSON.stringify(res.data))
    window.location.href = '/admin/dashboard'
  } catch (e) {
    alert('登录失败，请检查用户名和密码')
  }
}

const loadCompanyName = async () => {
  try {
    const res = await axios.get('/config/company-name')
    companyName.value = res.data.companyName
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadCompanyName()
})
</script>

<style scoped>
.login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.1);
}

.logo {
  text-align: center;
  margin-bottom: 30px;
}

.logo h1 {
  margin: 0 0 10px;
  font-size: 28px;
  color: #667eea;
}

.logo p {
  margin: 0;
  color: #999;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.tips {
  text-align: center;
  color: #999;
  font-size: 12px;
}

.tips p {
  margin: 5px 0;
}
</style>