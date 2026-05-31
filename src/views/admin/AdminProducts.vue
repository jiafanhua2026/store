<template>
  <div class="admin-products">
    <div class="header-row">
      <h2>商品管理</h2>
      <el-button type="primary" @click="showAddModal = true">添加商品</el-button>
    </div>
    
    <div class="filter-row">
      <el-input v-model="searchQuery" placeholder="搜索商品" @keyup.enter="search" />
      <el-select v-model="selectedCategory" placeholder="分类">
        <el-option label="全部" value="" />
        <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
      </el-select>
      <el-select v-model="selectedStatus" placeholder="状态">
        <el-option label="全部" :value="" />
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
    </div>
    
    <el-table :data="products" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="costPrice" label="成本价">
        <template #default="scope">¥{{ scope.row.costPrice }}</template>
      </el-table-column>
      <el-table-column prop="salePrice" label="售价">
        <template #default="scope">¥{{ scope.row.salePrice }}</template>
      </el-table-column>
      <el-table-column prop="style" label="款式" />
      <el-table-column prop="season" label="季节" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-switch 
            :value="scope.row.status === 1" 
            @change="toggleStatus(scope.row.id, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="editProduct(scope.row)" text>编辑</el-button>
          <el-button @click="deleteProduct(scope.row.id)" text type="danger">删除</el-button>
          <el-button @click="manageSkus(scope.row)" text>管理SKU</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="商品信息" v-model="showAddModal">
      <el-form :model="productForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="productForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="productForm.category">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="productForm.image" placeholder="输入图片URL" />
        </el-form-item>
        <el-form-item label="成本价">
          <el-input v-model.number="productForm.costPrice" type="number" />
        </el-form-item>
        <el-form-item label="售价">
          <el-input v-model.number="productForm.salePrice" type="number" />
        </el-form-item>
        <el-form-item label="款式">
          <el-input v-model="productForm.style" />
        </el-form-item>
        <el-form-item label="面料">
          <el-input v-model="productForm.fabric" />
        </el-form-item>
        <el-form-item label="季节">
          <el-select v-model="productForm.season">
            <el-option label="春季" value="春季" />
            <el-option label="夏季" value="夏季" />
            <el-option label="秋季" value="秋季" />
            <el-option label="冬季" value="冬季" />
            <el-option label="四季" value="四季" />
          </el-select>
        </el-form-item>
        <el-form-item label="详情">
          <el-input type="textarea" v-model="productForm.detail" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="SKU管理" v-model="showSkuModal">
      <div class="sku-form">
        <el-table :data="currentSkus" border>
          <el-table-column prop="size" label="尺码" />
          <el-table-column prop="color" label="颜色" />
          <el-table-column prop="stock" label="库存">
            <template #default="scope">
              <el-input-number v-model="scope.row.stock" @change="updateSku(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button @click="deleteSku(scope.row.id)" text type="danger">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="add-sku">
          <el-select v-model="newSku.size" placeholder="尺码">
            <el-option v-for="s in sizes" :key="s" :label="s" :value="s" />
          </el-select>
          <el-select v-model="newSku.color" placeholder="颜色">
            <el-option v-for="c in colors" :key="c" :label="c" :value="c" />
          </el-select>
          <el-input-number v-model="newSku.stock" :min="0" />
          <el-button @click="addSku" type="primary">添加</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from '../../utils/axios'

const products = ref([])
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedStatus = ref('')
const showAddModal = ref(false)
const showSkuModal = ref(false)

const categories = ['上衣', '裤子', '裙子', '配饰', '套装']
const sizes = ['S', 'M', 'L', 'XL']
const colors = ['白色', '黑色', '粉色', '蓝色', '绿色']

const productForm = reactive({
  id: null,
  name: '',
  category: '',
  image: '',
  costPrice: 0,
  salePrice: 0,
  style: '',
  fabric: '',
  season: '',
  detail: '',
  status: 1
})

const currentProduct = ref(null)
const currentSkus = ref([])

const newSku = reactive({
  size: '',
  color: '',
  stock: 0
})

const loadProducts = async () => {
  try {
    let url = '/products'
    if (selectedStatus.value !== '') {
      url += `?status=${selectedStatus.value}`
    }
    const res = await axios.get(url)
    products.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const search = () => {
  loadProducts()
}

const toggleStatus = async (id, value) => {
  try {
    await axios.post(`/products/${id}/status`, { status: value ? 1 : 0 })
  } catch (e) {
    console.error(e)
  }
}

const editProduct = (product) => {
  Object.assign(productForm, product)
  showAddModal.value = true
}

const deleteProduct = async (id) => {
  if (confirm('确定删除该商品？')) {
    try {
      await axios.delete(`/products/${id}`)
      loadProducts()
    } catch (e) {
      console.error(e)
    }
  }
}

const saveProduct = async () => {
  try {
    if (productForm.id) {
      await axios.put(`/products/${productForm.id}`, productForm)
    } else {
      await axios.post('/products', productForm)
    }
    showAddModal.value = false
    loadProducts()
  } catch (e) {
    console.error(e)
  }
}

const manageSkus = async (product) => {
  currentProduct.value = product
  const res = await axios.get(`/products/${product.id}/skus`)
  currentSkus.value = res.data
  showSkuModal.value = true
}

const addSku = async () => {
  try {
    const sku = {
      productId: currentProduct.value.id,
      size: newSku.size,
      color: newSku.color,
      stock: newSku.stock
    }
    await axios.post('/products/skus', sku)
    currentSkus.value = await axios.get(`/products/${currentProduct.value.id}/skus`).then(r => r.data)
    newSku.size = ''
    newSku.color = ''
    newSku.stock = 0
  } catch (e) {
    console.error(e)
  }
}

const updateSku = async (sku) => {
  try {
    await axios.put(`/products/skus/${sku.id}`, sku)
  } catch (e) {
    console.error(e)
  }
}

const deleteSku = async (id) => {
  try {
    await axios.delete(`/products/skus/${id}`)
    currentSkus.value = await axios.get(`/products/${currentProduct.value.id}/skus`).then(r => r.data)
  } catch (e) {
    console.error(e)
  }
}

loadProducts()
</script>

<style scoped>
.admin-products {
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

.filter-row :deep(.el-select) {
  width: 150px;
}

.sku-form {
  padding: 20px;
}

.add-sku {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  align-items: center;
}

.add-sku :deep(.el-select) {
  width: 100px;
}
</style>