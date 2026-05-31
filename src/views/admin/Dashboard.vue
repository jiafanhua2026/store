<template>
  <div class="dashboard">
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-icon products">
          <el-icon><component :is="icons.ShoppingBag" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overviewStats.totalProducts }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-icon customers">
          <el-icon><component :is="icons.User" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overviewStats.totalCustomers }}</div>
          <div class="stat-label">客户总数</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-icon orders">
          <el-icon><component :is="icons.Document" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overviewStats.todayOrders }}</div>
          <div class="stat-label">今日订单</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-icon sales">
          <el-icon><component :is="icons.Wallet" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ overviewStats.todaySales }}</div>
          <div class="stat-label">今日销售额</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-icon views">
          <el-icon><component :is="icons.Eye" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overviewStats.realtimeViews }}</div>
          <div class="stat-label">实时浏览</div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-icon totalOrders">
          <el-icon><component :is="icons.List" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overviewStats.totalOrders }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </el-card>
    </div>
    
    <div class="charts-row">
      <el-card class="chart-card">
        <h3>销售趋势</h3>
        <div ref="salesChart" class="chart"></div>
      </el-card>
      
      <el-card class="chart-card">
        <h3>库存分布</h3>
        <div ref="stockChart" class="chart"></div>
      </el-card>
    </div>
    
    <div class="bottom-row">
      <el-card class="recent-orders">
        <h3>待处理订单</h3>
        <el-table :data="pendingOrders" border>
          <el-table-column prop="orderNo" label="订单号" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">¥{{ scope.row.amount }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="时间" />
        </el-table>
      </el-card>
      
      <el-card class="low-stock">
        <h3>库存预警</h3>
        <el-table :data="lowStockItems" border>
          <el-table-column prop="productName" label="商品" />
          <el-table-column prop="size" label="尺码" />
          <el-table-column prop="color" label="颜色" />
          <el-table-column prop="stock" label="库存">
            <template #default="scope">
              <span v-if="scope.row.stock < 5" class="low">库存不足</span>
              <span v-else>{{ scope.row.stock }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ShoppingBag, User, Document, Wallet, Eye, List } from '@element-plus/icons-vue'
import axios from '../../utils/axios'
import * as echarts from 'echarts'

const icons = { ShoppingBag, User, Document, Wallet, Eye, List }

const overviewStats = ref({
  totalProducts: 0,
  totalCustomers: 0,
  totalOrders: 0,
  todayOrders: 0,
  todaySales: '0.00',
  realtimeViews: 0
})

const pendingOrders = ref([])
const lowStockItems = ref([])

const salesChart = ref(null)
const stockChart = ref(null)

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

const loadOverviewStats = async () => {
  try {
    const res = await axios.get('/stats/overview')
    overviewStats.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadPendingOrders = async () => {
  try {
    const res = await axios.get('/orders?status=待付款')
    pendingOrders.value = res.data.slice(0, 5)
  } catch (e) {
    console.error(e)
  }
}

const loadLowStockItems = async () => {
  try {
    const res = await axios.get('/stock/flows')
    lowStockItems.value = res.data.slice(0, 5)
  } catch (e) {
    console.error(e)
  }
}

const initCharts = () => {
  if (salesChart.value) {
    const chart = echarts.init(salesChart.value)
    chart.setOption({
      xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
      yAxis: { type: 'value' },
      series: [{
        data: [1200, 1320, 1010, 1340, 1900, 2300, 2100],
        type: 'line',
        smooth: true,
        areaStyle: { color: 'rgba(102, 126, 234, 0.3)' },
        lineStyle: { color: '#667eea' }
      }]
    })
  }
  
  if (stockChart.value) {
    const chart = echarts.init(stockChart.value)
    chart.setOption({
      series: [{
        type: 'pie',
        radius: '55%',
        data: [
          { value: 335, name: '上衣' },
          { value: 278, name: '裤子' },
          { value: 445, name: '裙子' },
          { value: 198, name: '配饰' },
          { value: 146, name: '套装' }
        ],
        itemStyle: {
          colors: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe']
        }
      }]
    })
  }
}

onMounted(() => {
  loadOverviewStats()
  loadPendingOrders()
  loadLowStockItems()
  
  nextTick(() => {
    initCharts()
  })
  
  setInterval(() => {
    axios.get('/stats/views').then(res => {
      overviewStats.value.realtimeViews = res.data
    })
  }, 5000)
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.products { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.customers { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.orders { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.sales { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-icon.views { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-icon.totalOrders { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }

.stat-icon :deep(.el-icon) {
  font-size: 24px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card h3 {
  margin: 0 0 20px;
  font-size: 16px;
}

.chart {
  height: 250px;
}

.bottom-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.recent-orders h3,
.low-stock h3 {
  margin: 0 0 20px;
  font-size: 16px;
}

.low {
  color: #f5576c;
  font-weight: bold;
}
</style>