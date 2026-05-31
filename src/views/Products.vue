<template>
  <div class="products">
    <header class="page-header">
      <h1>商品列表</h1>
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索商品" 
        class="search-input"
        @keyup.enter="search"
      />
    </header>
    
    <div class="filter-bar">
      <el-select v-model="selectedCategory" placeholder="选择分类" class="filter-select">
        <el-option label="全部" value="" />
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-select v-model="selectedSeason" placeholder="选择季节" class="filter-select">
        <el-option label="全部" value="" />
        <el-option label="春季" value="春季" />
        <el-option label="夏季" value="夏季" />
        <el-option label="秋季" value="秋季" />
        <el-option label="冬季" value="冬季" />
        <el-option label="四季" value="四季" />
      </el-select>
    </div>
    
    <div class="products-grid">
      <el-card 
        :body-style="{ padding: '0' }" 
        class="product-card" 
        v-for="product in products" 
        :key="product.id"
        @click="goToDetail(product.id)"
      >
        <img :src="product.image" class="product-img" />
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <p class="price">¥{{ product.salePrice }}</p>
          <div class="tags">
            <span class="tag">{{ product.category }}</span>
            <span class="tag">{{ product.season }}</span>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const products = ref([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedSeason = ref('')
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

const categories = ['上衣', '裤子', '裙子', '配饰', '套装']

const goToDetail = (id) => {
  window.location.href = `/product/${id}`
}

const loadProducts = async () => {
  try {
    let url = `/products?status=1`
    if (selectedCategory.value) url += `&category=${selectedCategory.value}`
    const res = await axios.get(url)
    products.value = res.data
    total.value = res.data.length
  } catch (e) {
    console.error(e)
  }
}

const search = () => {
  currentPage.value = 1
  loadProducts()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadProducts()
}

onMounted(() => {
  const params = new URLSearchParams(window.location.search)
  const category = params.get('category')
  if (category) {
    selectedCategory.value = category
  }
  loadProducts()
})
</script>

<style scoped>
.products {
  min-height: 100vh;
  background: #fafafa;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #333;
}

.search-input {
  width: 300px;
}

.filter-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.filter-select {
  width: 150px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-img {
  width: 100%;
  height: 250px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-info h3 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price {
  color: #e74c3c;
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 10px;
}

.tags {
  display: flex;
  gap: 5px;
}

.tag {
  background: #f0f0f0;
  color: #666;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}
</style>