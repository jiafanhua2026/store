<template>
  <div class="home">
    <header class="header">
      <div class="header-content">
        <h1>{{ companyName }}</h1>
        <p>让力量与柔美共生，让华丽与内敛并存</p>
      </div>
      <div class="stats-badge">
        <el-badge :value="'实时浏览: ' + viewsCount" />
      </div>
    </header>
    
    <nav class="nav">
      <el-row :gutter="20">
        <el-col :span="4" v-for="cat in categories" :key="cat">
          <el-button 
            type="primary" 
            plain 
            @click="goToProducts(cat)"
            class="category-btn"
          >
            {{ cat }}
          </el-button>
        </el-col>
      </el-row>
    </nav>
    
    <section class="products-section">
      <h2>热门商品</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in products" :key="product.id">
          <el-card :body-style="{ padding: '0' }" class="product-card" @click="goToDetail(product.id)">
            <img :src="product.image" class="product-img" />
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">¥{{ product.salePrice }}</p>
              <p class="category">{{ product.category }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>
    
    <section class="banner-section">
      <el-carousel height="400px">
        <el-carousel-item v-for="i in 3" :key="i">
          <img :src="`https://neeko-copilot.bytedance.net/api/text_to_image?prompt=women%20fashion%20clothing%20banner%20spring%20collection%20${i}&image_size=landscape_16_9`" class="banner-img" />
        </el-carousel-item>
      </el-carousel>
    </section>
    
    <footer class="footer">
      <p>&copy; 2026 {{ companyName }}. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'

const companyName = ref('等你服饰')
const viewsCount = ref(0)
const products = ref([])
const categories = ['上衣', '裤子', '裙子', '配饰', '套装']

const goToProducts = (category) => {
  window.location.href = `/products?category=${category}`
}

const goToDetail = (id) => {
  window.location.href = `/product/${id}`
}

const loadData = async () => {
  try {
    const [productsRes, viewsRes, configRes] = await Promise.all([
      axios.get('/products?status=1'),
      axios.get('/stats/views'),
      axios.get('/config/company-name')
    ])
    
    products.value = productsRes.data.slice(0, 4)
    viewsCount.value = viewsRes.data
    companyName.value = configRes.data.companyName
    
    axios.post('/stats/views')
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadData()
  setInterval(() => {
    axios.get('/stats/views').then(res => {
      viewsCount.value = res.data
    })
  }, 5000)
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #fafafa;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  text-align: center;
  position: relative;
}

.header-content h1 {
  font-size: 48px;
  margin-bottom: 10px;
}

.header-content p {
  font-size: 18px;
  opacity: 0.9;
}

.stats-badge {
  position: absolute;
  top: 20px;
  right: 20px;
}

.nav {
  padding: 20px 40px;
  background: white;
  border-bottom: 1px solid #eee;
}

.category-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.products-section {
  padding: 40px;
}

.products-section h2 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
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
  margin: 0 0 5px;
}

.category {
  color: #999;
  font-size: 12px;
  margin: 0;
}

.banner-section {
  padding: 0 40px 40px;
}

.banner-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.footer {
  background: #333;
  color: white;
  text-align: center;
  padding: 30px;
}

.footer p {
  margin: 0;
}
</style>