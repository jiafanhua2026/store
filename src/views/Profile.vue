<template>
  <div class="profile">
    <header class="page-header">
      <h1>个人中心</h1>
    </header>
    
    <div class="profile-content">
      <div class="user-info">
        <el-card>
          <h3>个人信息</h3>
          <div class="info-row">
            <span class="label">姓名:</span>
            <span>{{ customer.name }}</span>
          </div>
          <div class="info-row">
            <span class="label">手机号:</span>
            <span>{{ customer.phone }}</span>
          </div>
          <div class="info-row">
            <span class="label">微信:</span>
            <span>{{ customer.wechat }}</span>
          </div>
          <div class="info-row">
            <span class="label">地址:</span>
            <span>{{ customer.address }}</span>
          </div>
          <div class="info-row">
            <span class="label">会员等级:</span>
            <span :class="['level', customer.level]">{{ customer.level }}</span>
          </div>
        </el-card>
      </div>
      
      <div class="stats-info">
        <el-card>
          <h3>消费统计</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ customer.totalOrder }}</div>
              <div class="stat-label">订单总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ customer.totalAmount }}</div>
              <div class="stat-label">消费总额</div>
            </div>
          </div>
        </el-card>
      </div>
      
      <div class="orders-section">
        <el-card>
          <h3>我的订单</h3>
          <el-table :data="orders" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="amount" label="金额">
              <template #default="scope">¥{{ scope.row.amount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="下单时间" />
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const customer = ref({
  name: '访客',
  phone: '',
  wechat: '',
  address: '',
  level: '普通',
  totalOrder: 0,
  totalAmount: '0.00'
})

const orders = ref([])

const getStatusType = (status) => {
  const types = {
    '待付款': 'warning',
    '待发货': 'info',
    '已发货': 'primary',
    '已完成': 'success',
    '售后': 'danger'
  }
  return types[status] || 'default'
}

const loadData = async () => {
  try {
    const customerId = localStorage.getItem('customerId')
    if (customerId) {
      const [customerRes, ordersRes] = await Promise.all([
        axios.get(`/customers/${customerId}`),
        axios.get(`/orders?customerId=${customerId}`)
      ])
      customer.value = customerRes.data
      orders.value = ordersRes.data
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background: #fafafa;
  padding: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.profile-content {
  max-width: 800px;
}

.user-info {
  margin-bottom: 20px;
}

.user-info h3 {
  margin: 0 0 20px;
  font-size: 18px;
}

.info-row {
  display: flex;
  margin-bottom: 15px;
}

.label {
  width: 100px;
  color: #999;
}

.level {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.level.VIP {
  background: #fef3c7;
  color: #d97706;
}

.level.普通 {
  background: #dbeafe;
  color: #2563eb;
}

.stats-info {
  margin-bottom: 20px;
}

.stats-info h3 {
  margin: 0 0 20px;
  font-size: 18px;
}

.stats-grid {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.orders-section h3 {
  margin: 0 0 20px;
  font-size: 18px;
}
</style>