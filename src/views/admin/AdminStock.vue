<template>
  <div class="admin-stock">
    <div class="header-row">
      <h2>库存管理</h2>
      <el-button type="primary" @click="showAdjustModal = true">库存调整</el-button>
    </div>
    
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-value">{{ stockStats.totalStock }}</div>
        <div class="stat-label">总库存</div>
      </el-card>
      <el-card class="stat-card warning">
        <div class="stat-value">{{ stockStats.lowStockCount }}</div>
        <div class="stat-label">低库存预警</div>
      </el-card>
    </div>
    
    <div class="filter-row">
      <el-input v-model="searchQuery" placeholder="搜索商品" />
      <el-select v-model="filterType" placeholder="出入库类型">
        <el-option label="全部" value="" />
        <el-option label="入库" value="入库" />
        <el-option label="出库" value="出库" />
      </el-select>
      <el-date-picker v-model="startDate" type="date" placeholder="开始日期" />
      <el-date-picker v-model="endDate" type="date" placeholder="结束日期" />
      <el-button @click="searchFlows">查询</el-button>
    </div>
    
    <el-card class="flow-card">
      <h3>库存流水</h3>
      <el-table :data="stockFlows" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="size" label="尺码" />
        <el-table-column prop="color" label="颜色" />
        <el-table-column prop="type" label="类型">
          <template #default="scope">
            <el-tag :type="scope.row.type === '入库' ? 'success' : 'warning'">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="costPrice" label="成本价">
          <template #default="scope">¥{{ scope.row.costPrice }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createdAt" label="时间" />
      </el-table>
    </el-card>
    
    <el-dialog title="库存调整" v-model="showAdjustModal">
      <el-form :model="adjustForm" label-width="80px">
        <el-form-item label="调整类型">
          <el-radio-group v-model="adjustForm.type">
            <el-radio label="入库" />
            <el-radio label="出库" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="SKU">
          <el-select v-model="adjustForm.skuId">
            <el-option 
              v-for="sku in skuOptions" 
              :key="sku.id" 
              :label="`${sku.productName} - ${sku.size} - ${sku.color}`" 
              :value="sku.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="adjustForm.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="成本价" v-if="adjustForm.type === '入库'">
          <el-input v-model.number="adjustForm.costPrice" type="number" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adjustForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdjustModal = false">取消</el-button>
        <el-button type="primary" @click="adjustStock">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from '../../utils/axios'

const stockStats = ref({
  totalStock: 0,
  lowStockCount: 0,
  categoryStock: {}
})

const stockFlows = ref([])
const searchQuery = ref('')
const filterType = ref('')
const startDate = ref('')
const endDate = ref('')
const showAdjustModal = ref(false)
const skuOptions = ref([])

const adjustForm = reactive({
  type: '入库',
  skuId: null,
  quantity: 1,
  costPrice: 0,
  remark: ''
})

const loadStockStats = async () => {
  try {
    const res = await axios.get('/stats/stock')
    stockStats.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadStockFlows = async () => {
  try {
    let url = '/stock/flows'
    if (startDate.value && endDate.value) {
      url += `?startDate=${startDate.value}&endDate=${endDate.value}`
    }
    const res = await axios.get(url)
    stockFlows.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadSkuOptions = async () => {
  try {
    const productsRes = await axios.get('/products')
    const products = productsRes.data
    
    const options = []
    for (const product of products) {
      const skusRes = await axios.get(`/products/${product.id}/skus`)
      skusRes.data.forEach(sku => {
        options.push({
          id: sku.id,
          productName: product.name,
          size: sku.size,
          color: sku.color
        })
      })
    }
    skuOptions.value = options
  } catch (e) {
    console.error(e)
  }
}

const searchFlows = () => {
  loadStockFlows()
}

const adjustStock = async () => {
  try {
    if (adjustForm.type === '入库') {
      await axios.post('/stock/add', {
        skuId: adjustForm.skuId,
        quantity: adjustForm.quantity,
        costPrice: adjustForm.costPrice,
        remark: adjustForm.remark || '库存调整'
      })
    } else {
      await axios.post('/stock/deduct', {
        skuId: adjustForm.skuId,
        quantity: adjustForm.quantity
      })
    }
    showAdjustModal.value = false
    loadStockStats()
    loadStockFlows()
  } catch (e) {
    console.error(e)
    alert('操作失败')
  }
}

onMounted(() => {
  loadStockStats()
  loadStockFlows()
  loadSkuOptions()
})
</script>

<style scoped>
.admin-stock {
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

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  text-align: center;
  padding: 20px;
}

.stat-card.warning {
  border-color: #f5576c;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
}

.stat-card.warning .stat-value {
  color: #f5576c;
}

.stat-label {
  color: #999;
  font-size: 14px;
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

.filter-row :deep(.el-select) {
  width: 150px;
}

.flow-card {
  padding: 20px;
}

.flow-card h3 {
  margin: 0 0 20px;
  font-size: 16px;
}
</style>