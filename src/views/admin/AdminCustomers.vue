<template>
  <div class="admin-customers">
    <div class="header-row">
      <h2>客户管理</h2>
      <el-button type="primary" @click="showAddModal = true">添加客户</el-button>
    </div>
    
    <div class="filter-row">
      <el-input v-model="searchQuery" placeholder="搜索姓名或手机号" @keyup.enter="search" />
      <el-select v-model="selectedLevel" placeholder="会员等级">
        <el-option label="全部" value="" />
        <el-option label="VIP" value="VIP" />
        <el-option label="普通" value="普通" />
      </el-select>
      <el-button @click="searchCustomers">查询</el-button>
    </div>
    
    <el-table :data="customers" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="wechat" label="微信" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="level" label="等级">
        <template #default="scope">
          <el-tag :type="scope.row.level === 'VIP' ? 'warning' : 'info'">{{ scope.row.level }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalOrder" label="订单数" />
      <el-table-column prop="totalAmount" label="消费总额">
        <template #default="scope">¥{{ scope.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="lastPurchaseTime" label="最近消费" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="viewCustomer(scope.row)" text>查看详情</el-button>
          <el-button @click="editCustomer(scope.row)" text>编辑</el-button>
          <el-button @click="updateLevel(scope.row)" text type="primary">升级等级</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="客户详情" v-model="showDetailModal">
      <div v-if="currentCustomer">
        <div class="detail-info">
          <h3>{{ currentCustomer.name }}</h3>
          <div class="detail-row">
            <span class="label">手机号:</span>
            <span>{{ currentCustomer.phone }}</span>
          </div>
          <div class="detail-row">
            <span class="label">微信:</span>
            <span>{{ currentCustomer.wechat }}</span>
          </div>
          <div class="detail-row">
            <span class="label">地址:</span>
            <span>{{ currentCustomer.address }}</span>
          </div>
          <div class="detail-row">
            <span class="label">等级:</span>
            <el-tag :type="currentCustomer.level === 'VIP' ? 'warning' : 'info'">{{ currentCustomer.level }}</el-tag>
          </div>
          <div class="detail-row">
            <span class="label">订单数:</span>
            <span>{{ currentCustomer.totalOrder }}</span>
          </div>
          <div class="detail-row">
            <span class="label">消费总额:</span>
            <span>¥{{ currentCustomer.totalAmount }}</span>
          </div>
        </div>
        
        <div class="orders-section">
          <h4>历史订单</h4>
          <el-table :data="customerOrders" border>
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
        </div>
      </div>
    </el-dialog>
    
    <el-dialog title="客户信息" v-model="showAddModal">
      <el-form :model="customerForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="customerForm.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="customerForm.phone" />
        </el-form-item>
        <el-form-item label="微信">
          <el-input v-model="customerForm.wechat" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="customerForm.address" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="customerForm.level">
            <el-option label="普通" value="普通" />
            <el-option label="VIP" value="VIP" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveCustomer">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from '../../utils/axios'

const customers = ref([])
const searchQuery = ref('')
const selectedLevel = ref('')
const showDetailModal = ref(false)
const showAddModal = ref(false)
const currentCustomer = ref(null)
const customerOrders = ref([])

const customerForm = reactive({
  id: null,
  name: '',
  phone: '',
  wechat: '',
  address: '',
  level: '普通'
})

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

const loadCustomers = async () => {
  try {
    let url = '/customers'
    if (selectedLevel.value) {
      url += `?level=${selectedLevel.value}`
    }
    const res = await axios.get(url)
    customers.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const searchCustomers = () => {
  loadCustomers()
}

const viewCustomer = async (customer) => {
  currentCustomer.value = customer
  const res = await axios.get(`/orders?customerId=${customer.id}`)
  customerOrders.value = res.data
  showDetailModal.value = true
}

const editCustomer = (customer) => {
  Object.assign(customerForm, customer)
  showAddModal.value = true
}

const updateLevel = async (customer) => {
  const newLevel = customer.level === 'VIP' ? '普通' : 'VIP'
  try {
    await axios.post(`/customers/${customer.id}/level`, { level: newLevel })
    customer.level = newLevel
  } catch (e) {
    console.error(e)
  }
}

const saveCustomer = async () => {
  try {
    if (customerForm.id) {
      await axios.put(`/customers/${customerForm.id}`, customerForm)
    } else {
      await axios.post('/customers', customerForm)
    }
    showAddModal.value = false
    loadCustomers()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadCustomers()
})
</script>

<style scoped>
.admin-customers {
  padding: 20px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-row h2 {
  margin: 0;
}

.filter-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.filter-row :deep(.el-input) {
  width: 200px;
}

.detail-info {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.detail-info h3 {
  margin: 0 0 20px;
  font-size: 20px;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
}

.label {
  width: 100px;
  color: #999;
}

.orders-section {
  padding: 20px;
}

.orders-section h4 {
  margin: 0 0 15px;
}
</style>