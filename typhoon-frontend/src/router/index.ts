import { createRouter, createWebHistory } from "vue-router";

import Login from "../views/auth/Login.vue";
import Register from "../views/auth/Register.vue";
import Home from "../views/Home.vue";
import MapView from "../views/user/MapView.vue";
import AdminHome from "../views/admin/AdminHome.vue";

const routes = [
  { path: "/", redirect: "/home" },

  { path: "/login", component: Login },
  { path: "/register", component: Register },

  { path: "/home", component: Home },

  { path: "/map", component: MapView },
  { path: "/history", component: () => import("../views/user/HistoryView.vue") },
  { path: "/admin", component: AdminHome }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 权限守卫
router.beforeEach((to, _, next) => {
  const user = JSON.parse(localStorage.getItem("user") || "{}");

  if (to.path.startsWith("/admin") && user.role !== "ADMIN") {
    next("/map");
  } else {
    next();
  }
});

export default router;