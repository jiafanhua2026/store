<template>
  <div class="admin-settings">
    <h2>系统设置</h2>
    
    <el-card class="settings-card">
      <h3>公司信息</h3>
      <div class="setting-item">
        <label>公司名称</label>
        <div class="input-group">
          <el-input v-model="companyName" />
          <el-button @click="saveCompanyName" type="primary">保存</el-button>
        </div>
      </div>
    </el-card>
    
    <el-card class="settings-card">
      <h3>颜色主题</h3>
      <div class="theme-colors">
        <div 
          v-for="theme in themes" 
          :key="theme.name"
          class="theme-item"
          :class="{ active: currentTheme === theme.name }"
          @click="applyTheme(theme)"
        >
          <div class="theme-preview" :style="{ background: theme.gradient }"></div>
          <span>{{ theme.name }}</span>
        </div>
      </div>
    </el-card>
    
    <el-card class="settings-card">
      <h3>管理员账号</h3>
      <el-table :data="users" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'warning' : 'info'">
              {{ scope.row.role === 'admin' ? '管理员' : '销售员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-switch :value="scope.row.status === 1" />
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button @click="editUser(scope.row)" text>编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../../utils/axios'

const companyName = ref('')
const currentTheme = ref('优雅紫')
const users = ref([])

const themes = [
  { name: '优雅紫', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { name: '清新绿', gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' },
  { name: '活力橙', gradient: 'linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%)' },
  { name: '海洋蓝', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { name: '浪漫粉', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
]

const loadCompanyName = async () => {
  try {
    const res = await axios.get('/config/company-name')
    companyName.value = res.data.companyName
  } catch (e) {
    console.error(e)
  }
}

const saveCompanyName = async () => {
  try {
    await axios.put('/config/company-name', { companyName: companyName.value })
    alert('保存成功')
  } catch (e) {
    console.error(e)
  }
}

const applyTheme = (theme) => {
  currentTheme.value = theme.name
  document.documentElement.style.setProperty('--theme-gradient', theme.gradient)
}

const loadUsers = async () => {
  try {
    const res = await axios.get('/users')
    users.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const editUser = (user) => {
  console.log('Edit user:', user)
}

onMounted(() => {
  loadCompanyName()
  loadUsers()
})
</script>

<style scoped>
.admin-settings {
  padding: 20px;
}

.admin-settings h2 {
  margin: 0 0 20px;
}

.settings-card {
  margin-bottom: 20px;
  padding: 20px;
}

.settings-card h3 {
  margin: 0 0 20px;
  font-size: 16px;
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 20px;
}

.setting-item label {
  width: 100px;
  color: #666;
}

.input-group {
  flex: 1;
  display: flex;
  gap: 10px;
}

.input-group :deep(.el-input) {
  width: 300px;
}

.theme-colors {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.theme-item {
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  width: 140px;
  transition: all 0.3s;
}

.theme-item:hover,
.theme-item.active {
  background: #f0f0f0;
}

.theme-preview {
  height: 60px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.theme-item span {
  font-size: 14px;
}
</style>