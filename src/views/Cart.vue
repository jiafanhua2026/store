<template>
  <div class="cart">
    <header class="page-header">
      <h1>购物车</h1>
      <span class="item-count">共 {{ cartItems.length }} 件商品</span>
    </header>
    
    <div v-if="cartItems.length > 0" class="cart-content">
      <div class="cart-list">
        <el-card 
          v-for="(item, index) in cartItems" 
          :key="index"
          class="cart-item"
        >
          <div class="item-content">
            <div class="item-info">
              <h3>{{ item.name }}</h3>
              <div class="specs">
                <span>颜色: {{ item.color }}</span>
                <span>尺码: {{ item.size }}</span>
              </div>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-quantity">
              <el-input-number 
                v-model="item.quantity" 
                :min="1"
                @change="updateTotal"
              />
            </div>
            <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            <el-button @click="removeItem(index)" text type="danger">删除</el-button>
          </div>
        </el-card>
      </div>
      
      <div class="cart-summary">
        <div class="summary-card">
          <h3>订单汇总</h3>
          <div class="summary-row">
            <span>商品总数:</span>
            <span>{{ totalCount }} 件</span>
          </div>
          <div class="summary-row">
            <span>商品金额:</span>
            <span>¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span>应付金额:</span>
            <span>¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="checkout">去结算</el-button>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-cart">
      <el-empty description="购物车是空的" />
      <el-button @click="goShopping" type="primary">去购物</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const cartItems = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

const totalCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const updateTotal = () => {
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

const removeItem = (index) => {
  cartItems.value.splice(index, 1)
  localStorage.setItem('cart', JSON.stringify(cartItems.value))
}

const goShopping = () => {
  window.location.href = '/products'
}

const checkout = () => {
  alert('订单提交成功！')
  localStorage.removeItem('cart')
  cartItems.value = []
  window.location.href = '/'
}
</script>

<style scoped>
.cart {
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

.item-count {
  color: #999;
}

.cart-content {
  display: flex;
  gap: 30px;
}

.cart-list {
  flex: 1;
}

.cart-item {
  margin-bottom: 15px;
}

.item-content {
  display: flex;
  align-items: center;
  gap: 30px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 10px;
  font-size: 16px;
}

.specs {
  color: #999;
  font-size: 14px;
}

.specs span {
  margin-right: 20px;
}

.item-price {
  font-size: 18px;
  color: #e74c3c;
  font-weight: bold;
  width: 100px;
}

.item-quantity {
  width: 120px;
}

.item-total {
  font-size: 18px;
  font-weight: bold;
  width: 120px;
  text-align: right;
}

.cart-summary {
  width: 300px;
}

.summary-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.summary-card h3 {
  margin: 0 0 20px;
  font-size: 18px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.summary-row.total {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  padding-top: 15px;
  border-top: 1px dashed #ddd;
  margin-bottom: 20px;
}

.empty-cart {
  text-align: center;
  padding: 100px 0;
}

.empty-cart :deep(.el-empty) {
  margin-bottom: 30px;
}
</style>