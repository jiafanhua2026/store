<template>
  <div class="admin-purchase">
    <div class="header-row">
      <h2>采购入库</h2>
      <el-button type="primary" @click="showCreateModal = true">新建采购单</el-button>
    </div>
    
    <div class="filter-row">
      <el-input v-model="searchQuery" placeholder="搜索采购单号" @keyup.enter="search" />
      <el-select v-model="selectedStatus" placeholder="状态">
        <el-option label="全部" value="" />
        <el-option label="待入库" value="待入库" />
        <el-option label="已入库" value="已入库" />
      </el-select>
      <el-button @click="searchOrders">查询</el-button>
    </div>
    
    <el-table :data="purchaseOrders" border>
      <el-table-column prop="purchaseNo" label="采购单号" />
      <el-table-column prop="supplier" label="供应商" />
      <el-table-column prop="totalAmount" label="总金额">
        <template #default="scope">¥{{ scope.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === '已入库' ? 'success' : 'warning'">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="viewPurchase(scope.row)" text>查看</el-button>
          <el-button 
            v-if="scope.row.status === '待入库'" 
            @click="confirmPurchase(scope.row)" 
            text 
            type="success"
          >
            确认入库
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="采购单详情" v-model="showDetailModal">
      <div v-if="currentPurchase">
        <div class="detail-info">
          <div class="detail-row">
            <span class="label">采购单号:</span>
            <span>{{ currentPurchase.purchaseNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">供应商:</span>
            <span>{{ currentPurchase.supplier }}</span>
          </div>
          <div class="detail-row">
            <span class="label">总金额:</span>
            <span>¥{{ currentPurchase.totalAmount }}</span>
          </div>
          <div class="detail-row">
            <span class="label">状态:</span>
            <el-tag :type="currentPurchase.status === '已入库' ? 'success' : 'warning'">{{ currentPurchase.status }}</el-tag>
          </div>
          <div class="detail-row">
            <span class="label">备注:</span>
            <span>{{ currentPurchase.remark || '-' }}</span>
          </div>
        </div>
        
        <div class="items-section">
          <h4>采购商品</h4>
          <el-table :data="purchaseItems" border>
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="size" label="尺码" />
            <el-table-column prop="color" label="颜色" />
            <el-table-column prop="quantity" label="数量" />
            <el-table-column prop="unitCost" label="单价">
              <template #default="scope">¥{{ scope.row.unitCost }}</template>
            </el-table-column>
            <el-table-column prop="totalCost" label="小计">
              <template #default="scope">¥{{ scope.row.totalCost }}</template>
            </el-table-column>
            <el-table-column prop="status" label="入库状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
                  {{ scope.row.status === 1 ? '已入库' : '待入库' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog title="新建采购单" v-model="showCreateModal">
      <div class="create-purchase">
        <el-form-item label="供应商">
          <el-input v-model="purchaseForm.supplier" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="purchaseForm.remark" />
        </el-form-item>
        
        <div class="purchase-items">
          <h4>采购商品</h4>
          <el-table :data="purchaseForm.items" border>
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="size" label="尺码" />
            <el-table-column prop="color" label="颜色" />
            <el-table-column prop="quantity" label="数量">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" @change="updateTotal" />
              </template>
            </el-table-column>
            <el-table-column prop="unitCost" label="成本价">
              <template #default="scope">
                <el-input v-model.number="scope.row.unitCost" type="number" @change="updateTotal" />
              </template>
            </el-table-column>
            <el-table-column prop="totalCost" label="小计">
              <template #default="scope">¥{{ scope.row.totalCost }}</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button @click="removeItem(scope.$index)" text type="danger">删除</el-button>
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
            <el-select v-model="newItem.size" placeholder="尺码">
              <el-option v-for="s in sizes" :key="s" :label="s" :value="s" />
            </el-select>
            <el-select v-model="newItem.color" placeholder="颜色">
              <el-option v-for="c in colors" :key="c" :label="c" :value="c" />
            </el-select>
            <el-input-number v-model="newItem.quantity" :min="1" />
            <el-input v-model.number="newItem.unitCost" type="number" placeholder="成本价" />
            <el-button @click="addItem" type="primary">添加</el-button>
          </div>
        </div>
        
        <div class="purchase-total">
          <span>合计:</span>
          <span class="total">¥{{ totalAmount }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showCreateModal = false">取消</el-button>
        <el-button type="primary" @click="createPurchase">创建采购单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from '../../utils/axios'

const purchaseOrders = ref([])
const searchQuery = ref('')
const selectedStatus = ref('')
const showDetailModal = ref(false)
const showCreateModal = ref(false)
const currentPurchase = ref(null)
const purchaseItems = ref([])
const products = ref([])

const sizes = ['S', 'M', 'L', 'XL']
const colors = ['白色', '黑色', '粉色', '蓝色', '绿色']

const purchaseForm = reactive({
  supplier: '',
  remark: '',
  items: []
})

const newItem = reactive({
  productId: null,
  size: '',
  color: '',
  quantity: 1,
  unitCost: 0
})

const totalAmount = computed(() => {
  return purchaseForm.items.reduce((sum, item) => sum + item.totalCost, 0)
})

const updateTotal = () => {
  purchaseForm.items.forEach(item => {
    item.totalCost = item.quantity * item.unitCost
  })
}

const loadPurchaseOrders = async () => {
  try {
    let url = '/purchase'
    if (selectedStatus.value) {
      url += `?status=${selectedStatus.value}`
    }
    const res = await axios.get(url)
    purchaseOrders.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const searchOrders = () => {
  loadPurchaseOrders()
}

const viewPurchase = async (purchase) => {
  currentPurchase.value = purchase
  purchaseItems.value = []
  showDetailModal.value = true
}

const confirmPurchase = async (purchase) => {
  if (confirm('确认入库？')) {
    try {
      await axios.post(`/purchase/${purchase.id}/confirm`)
      purchase.status = '已入库'
    } catch (e) {
      console.error(e)
    }
  }
}

const onProductChange = () => {}

const addItem = () => {
  if (!newItem.productId || !newItem.size || !newItem.color) return
  
  const product = products.value.find(p => p.id === newItem.productId)
  
  purchaseForm.items.push({
    productId: newItem.productId,
    productName: product.name,
    size: newItem.size,
    color: newItem.color,
    quantity: newItem.quantity,
    unitCost: newItem.unitCost,
    totalCost: newItem.quantity * newItem.unitCost
  })
  
  newItem.productId = null
  newItem.size = ''
  newItem.color = ''
  newItem.quantity = 1
  newItem.unitCost = 0
}

const removeItem = (index) => {
  purchaseForm.items.splice(index, 1)
}

const createPurchase = async () => {
  try {
    await axios.post('/purchase', {
      supplier: purchaseForm.supplier,
      remark: purchaseForm.remark,
      items: purchaseForm.items
    })
    showCreateModal.value = false
    purchaseForm.supplier = ''
    purchaseForm.remark = ''
    purchaseForm.items = []
    loadPurchaseOrders()
  } catch (e) {
    console.error(e)
    alert('创建失败')
  }
}

onMounted(() => {
  loadPurchaseOrders()
  axios.get('/products').then(res => products.value = res.data)
})
</script>

<style scoped>
.admin-purchase {
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

.detail-row {
  display: flex;
  margin-bottom: 15px;
}

.label {
  width: 100px;
  color: #999;
}

.items-section {
  padding: 20px;
}

.items-section h4 {
  margin: 0 0 15px;
}

.create-purchase {
  padding: 20px;
}

.purchase-items {
  margin-bottom: 20px;
}

.purchase-items h4 {
  margin: 0 0 15px;
}

.add-item {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  align-items: center;
}

.add-item :deep(.el-select) {
  width: 100px;
}

.add-item :deep(.el-input) {
  width: 100px;
}

.purchase-total {
  text-align: right;
  padding: 15px;
  border-top: 1px solid #eee;
}

.purchase-total .total {
  font-size: 24px;
  font-weight: bold;
  color: #e74c3c;
}
</style>