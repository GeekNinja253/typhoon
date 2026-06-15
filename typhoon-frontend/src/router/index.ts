import { createRouter, createWebHistory } from "vue-router";

import Login from "../views/auth/Login.vue";
import Register from "../views/auth/Register.vue";
import Home from "../views/Home.vue";
import MapView from "../views/user/MapView.vue";
import AdminHome from "../views/admin/AdminHome.vue";

const routes = [
  { path: "/", redirect: "/login" },

  { path: "/login", component: Login },
  { path: "/register", component: Register },

  { path: "/home", component: Home, meta: { requiresAuth: true } },

  { path: "/map", component: MapView, meta: { requiresAuth: true } },
  { path: "/history", component: () => import("../views/user/HistoryView.vue"), meta: { requiresAuth: true } },
  { path: "/admin", component: AdminHome, meta: { requiresAuth: true, requiresAdmin: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 权限守卫
router.beforeEach((to, _, next) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isLoggedIn = !!user.username;

  // 如果页面需要登录且用户未登录，重定向到登录页
  if (to.meta.requiresAuth && !isLoggedIn) {
    next("/login");
    return;
  }

  // 如果页面需要管理员权限且用户不是管理员
  if (to.meta.requiresAdmin && user.role !== "ADMIN") {
    next("/map");
    return;
  }

  next();
});

export default router;