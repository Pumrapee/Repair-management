import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import OrderManagement from '../views/OrderManagement.vue';

const routes = [
  { path: '/', redirect: '/orders' },
  { path: '/login', component: Login },
  { path: '/orders', component: OrderManagement },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
