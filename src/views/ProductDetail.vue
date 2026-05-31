<template>
  <div class="product-detail">
    <el-row :gutter="40">
      <el-col :span="12">
        <el-carousel height="500px" class="image-carousel">
          <el-carousel-item>
            <img :src="product.image" class="main-img" />
          </el-carousel-item>
        </el-carousel>
      </el-col>
      
      <el-col :span="12">
        <div class="product-info">
          <h1>{{ product.name }}</h1>
          <div class="price-row">
            <span class="sale-price">¥{{ product.salePrice }}</span>
            <span class="cost-price">成本价: ¥{{ product.costPrice }}</span>
          </div>
          
          <div class="attributes">
            <div class="attr-item">
              <span class="label">分类:</span>
              <span>{{ product.category }}</span>
            </div>
            <div class="attr-item">
              <span class="label">款式:</span>
              <span>{{ product.style }}</span>
            </div>
            <div class="attr-item">
              <span class="label">面料:</span>
              <span>{{ product.fabric }}</span>
            </div>
            <div class="attr-item">
              <span class="label">季节:</span>
              <span>{{ product.season }}</span>
            </div>
          </div>
          
          <div class="sku-section">
            <div class="sku-row">
              <span class="label">颜色:</span>
              <div class="color-options">
                <span 
                  v-for="color in colors" 
                  :key="color" 
                  class="color-option"
                  :class="{ active: selectedColor === color }"
                  @click="selectedColor = color"
                >
                  {{ color }}
                </span>
              </div>
            </div>
            
            <div class="sku-row">
              <span class="label">尺码:</span>
              <div class="size-options">
                <span 
                  v-for="size in sizes" 
                  :key="size" 
                  class="size-option"
                  :class="{ active: selectedSize === size, disabled: !getStock(size) }"
                  @click="selectedSize = getStock(size) > 0 ? size : selectedSize"
                >
                  {{ size }} ({{ getStock(size) }})
                </span>
              </div>
            </div>
            
            <div class="sku-row">
              <span class="label">数量:</span>
              <el-input-number 
                v-model="quantity" 
                :min="1" 
                :max="getStock(selectedSize)"
              />
            </div>
          </div>
          
          <div class="actions">
            <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
            <el-button size="large" @click="buyNow">立即购买</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <div class="detail-section">
      <h2>商品详情</h2>
      <div class="detail-content" v-html="product.detail"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '../utils/axios'

const product = ref({})
const skus = ref([])
const selectedColor = ref('')
const selectedSize = ref('')
const quantity = ref(1)

const colors = computed(() => {
  return [...new Set(skus.value.map(s => s.color))]
})

const sizes = computed(() => {
  return [...new Set(skus.value.map(s => s.size))]
})

const getStock = (size) => {
  const sku = skus.value.find(s => s.size === size && s.color === selectedColor.value)
  return sku ? sku.stock : 0
}

const addToCart = () => {
  if (!selectedColor.value || !selectedSize.value) {
    alert('请选择颜色和尺码')
    return
  }
  
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const sku = skus.value.find(s => s.size === selectedSize.value && s.color === selectedColor.value)
  
  const item = {
    productId: product.value.id,
    skuId: sku.id,
    name: product.value.name,
    color: selectedColor.value,
    size: selectedSize.value,
    price: product.value.salePrice,
    quantity: quantity.value
  }
  
  cart.push(item)
  localStorage.setItem('cart', JSON.stringify(cart))
  alert('已加入购物车')
}

const buyNow = () => {
  addToCart()
  window.location.href = '/cart'
}

const loadProduct = async () => {
  const id = window.location.pathname.split('/').pop()
  try {
    const [productRes, skusRes] = await Promise.all([
      axios.get(`/products/${id}`),
      axios.get(`/products/${id}/skus`)
    ])
    
    product.value = productRes.data
    skus.value = skusRes.data
    
    if (skus.value.length > 0) {
      selectedColor.value = skus.value[0].color
      selectedSize.value = skus.value[0].size
    }
    
    axios.post('/stats/views')
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: #fafafa;
  padding: 40px;
}

.image-carousel :deep(.el-carousel__item) {
  background: white;
}

.main-img {
  width: 100%;
  height: 500px;
  object-fit: contain;
}

.product-info {
  background: white;
  padding: 30px;
}

.product-info h1 {
  font-size: 28px;
  margin-bottom: 20px;
}

.price-row {
  margin-bottom: 20px;
}

.sale-price {
  font-size: 36px;
  color: #e74c3c;
  font-weight: bold;
}

.cost-price {
  margin-left: 20px;
  color: #999;
  font-size: 14px;
}

.attributes {
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
}

.attr-item {
  display: flex;
  margin-bottom: 10px;
}

.attr-item:last-child {
  margin-bottom: 0;
}

.label {
  width: 80px;
  color: #999;
}

.sku-section {
  margin-bottom: 30px;
}

.sku-row {
  margin-bottom: 20px;
}

.sku-row .label {
  display: inline-block;
  width: 60px;
}

.color-options {
  display: inline-flex;
  gap: 10px;
}

.color-option {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.color-option:hover,
.color-option.active {
  border-color: #667eea;
  background: #667eea;
  color: white;
}

.size-options {
  display: inline-flex;
  gap: 10px;
}

.size-option {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.size-option:hover:not(.disabled),
.size-option.active {
  border-color: #667eea;
  background: #667eea;
  color: white;
}

.size-option.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.actions {
  display: flex;
  gap: 20px;
}

.detail-section {
  margin-top: 40px;
  background: white;
  padding: 30px;
}

.detail-section h2 {
  font-size: 20px;
  margin-bottom: 20px;
}

.detail-content {
  line-height: 1.8;
  color: #666;
}
</style>