<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <h2>{{ companyName }}</h2>
      </div>
      <el-menu :default-active="activeMenu" class="menu" router>
        <el-menu-item index="/admin/dashboard">
          <el-icon><component :is="icons.Dashboard" /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <el-icon><component :is="icons.ShoppingBag" /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/purchase">
          <el-icon><component :is="icons.Download" /></el-icon>
          <span>采购入库</span>
        </el-menu-item>
        <el-menu-item index="/admin/stock">
          <el-icon><component :is="icons.Package" /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><component :is="icons.Document" /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/customers">
          <el-icon><component :is="icons.User" /></el-icon>
          <span>客户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/stats">
          <el-icon><component :is="icons.BarChart" /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/settings">
          <el-icon><component :is="icons.Setting" /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <span>{{ currentUser?.name }}</span>
          <el-button @click="logout" text>退出</el-button>
        </div>
      </el-header>
      
      <el-main class="main">
        <slot />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Dashboard, ShoppingBag, Download, Package, 
  Document, User, BarChart, Setting 
} from '@element-plus/icons-vue'
import axios from '../utils/axios'

const icons = { Dashboard, ShoppingBag, Download, Package, Document, User, BarChart, Setting }

const companyName = ref('等你服饰')
const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))

const activeMenu = computed(() => {
  return window.location.pathname
})

const logout = () => {
  localStorage.removeItem('user')
  window.location.href = '/login'
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
.admin-layout {
  height: 100vh;
}

.aside {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.menu {
  border: none;
  color: white;
}

.menu :deep(.el-menu-item) {
  color: rgba(255,255,255,0.8);
}

.menu :deep(.el-menu-item:hover),
.menu :deep(.el-menu-item.is-active) {
  background: rgba(255,255,255,0.1);
  color: white;
}

.header {
  background: white;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.main {
  padding: 20px;
  background: #f5f5f5;
  overflow-y: auto;
}
</style>