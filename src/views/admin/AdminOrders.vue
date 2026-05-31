<template>
  <div class="admin-orders">
    <div class="header-row">
      <h2>订单管理</h2>
      <el-button type="primary" @click="showCreateModal = true">快速开单</el-button>
    </div>
    
    <div class="filter-row">
      <el-input v-model="searchQuery" placeholder="搜索订单号" @keyup.enter="search" />
      <el-select v-model="selectedStatus" placeholder="订单状态">
        <el-option label="全部" value="" />
        <el-option label="待付款" value="待付款" />
        <el-option label="待发货" value="待发货" />
        <el-option label="已发货" value="已发货" />
        <el-option label="已完成" value="已完成" />
        <el-option label="售后" value="售后" />
      </el-select>
      <el-date-picker v-model="startDate" type="date" placeholder="开始日期" />
      <el-date-picker v-model="endDate" type="date" placeholder="结束日期" />
      <el-button @click="searchOrders">查询</el-button>
    </div>
    
    <el-table :data="orders" border>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="customerName" label="客户" />
      <el-table-column prop="amount" label="金额">
        <template #default="scope">¥{{ scope.row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="salesName" label="销售员" />
      <el-table-column prop="createdAt" label="下单时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="viewOrder(scope.row)" text>查看</el-button>
          <el-button @click="updateStatus(scope.row)" text type="primary">更新状态</el-button>
          <el-button @click="refundOrder(scope.row)" text type="danger">退款</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="订单详情" v-model="showDetailModal">
      <div v-if="currentOrder">
        <div class="detail-info">
          <div class="detail-row">
            <span class="label">订单号:</span>
            <span>{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">金额:</span>
            <span>¥{{ currentOrder.amount }}</span>
          </div>
          <div class="detail-row">
            <span class="label">状态:</span>
            <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.status }}</el-tag>
          </div>
          <div class="detail-row">
            <span class="label">创建时间:</span>
            <span>{{ currentOrder.createdAt }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog title="快速开单" v-model="showCreateModal">
      <div class="create-order">
        <el-form-item label="客户">
          <el-select v-model="orderForm.customerId">
            <el-option label="散客" :value="null" />
            <el-option 
              v-for="c in customers" 
              :key="c.id" 
              :label="`${c.name} - ${c.phone}`" 
              :value="c.id" 
            />
          </el-select>
        </el-form-item>
        
        <div class="order-items">
          <h4>订单商品</h4>
          <el-table :data="orderForm.items" border>
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="size" label="尺码" />
            <el-table-column prop="color" label="颜色" />
            <el-table-column prop="quantity" label="数量">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" />
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价">
              <template #default="scope">¥{{ scope.row.unitPrice }}</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button @click="removeOrderItem(scope.$index)" text type="danger">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="add-item">
            <el-select v-model="newItem.productId" placeholder="选择商品" @change="onProductChange">
              <el-option 
                v-for="p in products" 
                :key="p.id" 
                :label="p.name" 
                :value="p.id" 
              />
            </el-select>
            <el-select v-model="newItem.skuId" placeholder="选择SKU">
              <el-option 
                v-for="s in currentSkus" 
                :key="s.id" 
                :label="`${s.size} - ${s.color}`" 
                :value="s.id" 
              />
            </el-select>
            <el-input-number v-model="newItem.quantity" :min="1" />
            <el-button @click="addOrderItem" type="primary">添加</el-button>
          </div>
        </div>
        
        <div class="order-total">
          <span>合计:</span>
          <span class="total">¥{{ totalAmount }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showCreateModal = false">取消</el-button>
        <el-button type="primary" @click="createOrder">创建订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from '../../utils/axios'

const orders = ref([])
const searchQuery = ref('')
const selectedStatus = ref('')
const startDate = ref('')
const endDate = ref('')
const showDetailModal = ref(false)
const showCreateModal = ref(false)
const currentOrder = ref(null)
const customers = ref([])
const products = ref([])
const currentSkus = ref([])

const orderForm = reactive({
  customerId: null,
  salesId: 1,
  items: []
})

const newItem = reactive({
  productId: null,
  skuId: null,
  quantity: 1
})

const totalAmount = computed(() => {
  return orderForm.items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0)
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

const loadOrders = async () => {
  try {
    let url = '/orders'
    if (selectedStatus.value) {
      url += `?status=${selectedStatus.value}`
    }
    if (startDate.value && endDate.value) {
      url += `${selectedStatus.value ? '&' : '?'}startDate=${startDate.value}&endDate=${endDate.value}`
    }
    const res = await axios.get(url)
    orders.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const searchOrders = () => {
  loadOrders()
}

const viewOrder = (order) => {
  currentOrder.value = order
  showDetailModal.value = true
}

const updateStatus = async (order) => {
  const statuses = ['待付款', '待发货', '已发货', '已完成']
  const currentIndex = statuses.indexOf(order.status)
  if (currentIndex < statuses.length - 1) {
    try {
      await axios.put(`/orders/${order.id}/status`, { status: statuses[currentIndex + 1] })
      order.status = statuses[currentIndex + 1]
    } catch (e) {
      console.error(e)
    }
  }
}

const refundOrder = async (order) => {
  if (confirm('确定退款？')) {
    try {
      await axios.put(`/orders/${order.id}/status`, { status: '售后' })
      order.status = '售后'
    } catch (e) {
      console.error(e)
    }
  }
}

const onProductChange = async (productId) => {
  if (productId) {
    const res = await axios.get(`/products/${productId}/skus`)
    currentSkus.value = res.data
  }
}

const addOrderItem = async () => {
  if (!newItem.productId || !newItem.skuId) return
  
  const product = products.value.find(p => p.id === newItem.productId)
  const sku = currentSkus.value.find(s => s.id === newItem.skuId)
  
  orderForm.items.push({
    productId: newItem.productId,
    skuId: newItem.skuId,
    productName: product.name,
    size: sku.size,
    color: sku.color,
    quantity: newItem.quantity,
    unitPrice: product.salePrice,
    totalPrice: product.salePrice * newItem.quantity
  })
  
  newItem.productId = null
  newItem.skuId = null
  newItem.quantity = 1
  currentSkus.value = []
}

const removeOrderItem = (index) => {
  orderForm.items.splice(index, 1)
}

const createOrder = async () => {
  try {
    await axios.post('/orders', {
      customerId: orderForm.customerId,
      salesId: orderForm.salesId,
      items: orderForm.items
    })
    showCreateModal.value = false
    orderForm.customerId = null
    orderForm.items = []
    loadOrders()
  } catch (e) {
    console.error(e)
    alert('创建订单失败')
  }
}

onMounted(() => {
  loadOrders()
  axios.get('/customers').then(res => customers.value = res.data)
  axios.get('/products?status=1').then(res => products.value = res.data)
})
</script>

<style scoped>
.admin-orders {
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
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.filter-row :deep(.el-input) {
  width: 200px;
}

.detail-info {
  padding: 20px;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
}

.label {
  width: 100px;
  color: #999;
}

.create-order {
  padding: 20px;
}

.order-items {
  margin-bottom: 20px;
}

.order-items h4 {
  margin: 0 0 15px;
}

.add-item {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  align-items: center;
}

.add-item :deep(.el-select) {
  width: 150px;
}

.order-total {
  text-align: right;
  padding: 15px;
  border-top: 1px solid #eee;
}

.order-total .total {
  font-size: 24px;
  font-weight: bold;
  color: #e74c3c;
}
</style>