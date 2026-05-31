import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/products', name: 'Products', component: () => import('../views/Products.vue') },
  { path: '/product/:id', name: 'ProductDetail', component: () => import('../views/ProductDetail.vue') },
  { path: '/cart', name: 'Cart', component: () => import('../views/Cart.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  
  { path: '/admin', name: 'Admin', redirect: '/admin/dashboard' },
  { path: '/admin/dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue') },
  { path: '/admin/products', name: 'AdminProducts', component: () => import('../views/admin/AdminProducts.vue') },
  { path: '/admin/stock', name: 'AdminStock', component: () => import('../views/admin/AdminStock.vue') },
  { path: '/admin/orders', name: 'AdminOrders', component: () => import('../views/admin/AdminOrders.vue') },
  { path: '/admin/customers', name: 'AdminCustomers', component: () => import('../views/admin/AdminCustomers.vue') },
  { path: '/admin/purchase', name: 'AdminPurchase', component: () => import('../views/admin/AdminPurchase.vue') },
  { path: '/admin/stats', name: 'AdminStats', component: () => import('../views/admin/AdminStats.vue') },
  { path: '/admin/settings', name: 'AdminSettings', component: () => import('../views/admin/AdminSettings.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAdmin = to.path.startsWith('/admin')
  const loggedIn = localStorage.getItem('user')
  
  if (isAdmin && !loggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router