<template>
  <div class="app">
    <router-view v-if="!isAdmin" />
    <AdminLayout v-else>
      <router-view />
    </AdminLayout>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import AdminLayout from './layouts/AdminLayout.vue'

const isAdmin = computed(() => {
  return window.location.pathname.startsWith('/admin')
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.app {
  min-height: 100vh;
}
</style>