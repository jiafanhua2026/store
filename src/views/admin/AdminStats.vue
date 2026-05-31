<template>
  <div class="admin-stats">
    <div class="header-row">
      <h2>数据统计</h2>
      <el-select v-model="period" @change="loadStats">
        <el-option label="今日" value="today" />
        <el-option label="本周" value="week" />
        <el-option label="本月" value="month" />
        <el-option label="本年" value="year" />
      </el-select>
    </div>
    
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-value">{{ salesStats.orderCount }}</div>
        <div class="stat-label">订单数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">¥{{ salesStats.totalAmount }}</div>
        <div class="stat-label">销售额</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ customerStats.totalCustomers }}</div>
        <div class="stat-label">客户总数</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ customerStats.vipCount }}</div>
        <div class="stat-label">VIP客户</div>
      </el-card>
      <el-card class="stat-card warning">
        <div class="stat-value">{{ customerStats.churnWarningCount }}</div>
        <div class="stat-label">流失预警</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-value">{{ stockStats.totalStock }}</div>
        <div class="stat-label">总库存</div>
      </el-card>
    </div>
    
    <div class="charts-row">
      <el-card class="chart-card">
        <h3>销售趋势</h3>
        <div ref="salesChart" class="chart"></div>
      </el-card>
      
      <el-card class="chart-card">
        <h3>客户等级分布</h3>
        <div ref="customerChart" class="chart"></div>
      </el-card>
    </div>
    
    <div class="charts-row">
      <el-card class="chart-card">
        <h3>库存分类占比</h3>
        <div ref="stockChart" class="chart"></div>
      </el-card>
      
      <el-card class="chart-card">
        <h3>订单状态分布</h3>
        <div ref="orderChart" class="chart"></div>
      </el-card>
    </div>
    
    <div class="bottom-section">
      <el-card>
        <h3>客户复购率统计</h3>
        <el-table :data="customerRepurchase" border>
          <el-table-column prop="name" label="客户" />
          <el-table-column prop="orderCount" label="订单数" />
          <el-table-column prop="totalAmount" label="消费总额">
            <template #default="scope">¥{{ scope.row.totalAmount }}</template>
          </el-table-column>
          <el-table-column prop="repurchaseRate" label="复购率">
            <template #default="scope">{{ scope.row.repurchaseRate }}%</template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import axios from '../../utils/axios'
import * as echarts from 'echarts'

const period = ref('today')
const salesStats = ref({ orderCount: 0, totalAmount: '0.00' })
const customerStats = ref({
  totalCustomers: 0,
  vipCount: 0,
  normalCount: 0,
  churnWarningCount: 0,
  avgOrderValue: '0.00'
})
const stockStats = ref({
  totalStock: 0,
  lowStockCount: 0,
  categoryStock: {}
})

const salesChart = ref(null)
const customerChart = ref(null)
const stockChart = ref(null)
const orderChart = ref(null)

const customerRepurchase = ref([
  { name: '张三', orderCount: 5, totalAmount: 1200, repurchaseRate: 80 },
  { name: '李四', orderCount: 3, totalAmount: 800, repurchaseRate: 60 },
  { name: '王五', orderCount: 8, totalAmount: 2500, repurchaseRate: 90 },
  { name: '赵六', orderCount: 2, totalAmount: 400, repurchaseRate: 40 },
  { name: '钱七', orderCount: 4, totalAmount: 1000, repurchaseRate: 70 }
])

const loadStats = async () => {
  try {
    const [salesRes, customerRes, stockRes] = await Promise.all([
      axios.get(`/stats/sales?period=${period.value}`),
      axios.get('/stats/customer'),
      axios.get('/stats/stock')
    ])
    
    salesStats.value = salesRes.data
    customerStats.value = customerRes.data
    stockStats.value = stockRes.data
    
    nextTick(() => {
      initCharts()
    })
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
        type: 'bar',
        itemStyle: { color: '#667eea' }
      }]
    })
  }
  
  if (customerChart.value) {
    const chart = echarts.init(customerChart.value)
    chart.setOption({
      series: [{
        type: 'pie',
        radius: '55%',
        data: [
          { value: customerStats.value.vipCount, name: 'VIP' },
          { value: customerStats.value.normalCount, name: '普通' }
        ],
        itemStyle: { colors: ['#f5576c', '#4facfe'] }
      }]
    })
  }
  
  if (stockChart.value) {
    const chart = echarts.init(stockChart.value)
    const categoryStock = stockStats.value.categoryStock || {}
    const data = Object.entries(categoryStock).map(([name, value]) => ({ name, value }))
    
    chart.setOption({
      series: [{
        type: 'pie',
        radius: '55%',
        data: data.length ? data : [
          { value: 335, name: '上衣' },
          { value: 278, name: '裤子' },
          { value: 445, name: '裙子' },
          { value: 198, name: '配饰' },
          { value: 146, name: '套装' }
        ],
        itemStyle: { colors: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe'] }
      }]
    })
  }
  
  if (orderChart.value) {
    const chart = echarts.init(orderChart.value)
    chart.setOption({
      xAxis: { type: 'category', data: ['待付款', '待发货', '已发货', '已完成', '售后'] },
      yAxis: { type: 'value' },
      series: [{
        data: [12, 8, 15, 45, 3],
        type: 'bar',
        itemStyle: {
          color: ['#f5576c', '#4facfe', '#667eea', '#43e97b', '#fa709a']
        }
      }]
    })
  }
}

watch(period, () => {
  loadStats()
})

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-stats {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-card.warning {
  border-color: #f5576c;
}

.stat-value {
  font-size: 32px;
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

.bottom-section {
  margin-top: 20px;
}

.bottom-section h3 {
  margin: 0 0 20px;
  font-size: 16px;
}
</style>