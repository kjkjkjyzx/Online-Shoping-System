<template>
  <div class="layout">
    <Header />
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-links">
          <router-link to="/product">全部商品</router-link>
          <router-link to="/cart">购物车</router-link>
          <router-link to="/order">我的订单</router-link>
          <router-link to="/user">个人中心</router-link>
        </div>
        <p class="footer-copy">© 2026 网上商城 · 品质购物平台</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main {
  flex: 1;
  background: var(--color-bg-page);
}

.footer {
  background: #2c2c2c;
  color: var(--color-text-inverse);
  padding: 36px 20px 24px;
  margin-top: auto;
}

.footer-inner {
  max-width: var(--max-width);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.footer-links {
  display: flex;
  gap: 32px;
}

.footer-links a {
  color: #ccc;
  font-size: var(--font-size-base);
  text-decoration: none;
  transition: var(--transition);
  cursor: pointer;
}

.footer-links a:hover {
  color: var(--color-primary-lighter);
}

.footer-copy {
  font-size: var(--font-size-sm);
  color: #666;
  margin: 0;
}
</style>
