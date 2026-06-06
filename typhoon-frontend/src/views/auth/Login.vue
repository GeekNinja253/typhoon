<script setup lang="ts">
import { ref } from "vue";
import { login } from "../../api/user";
import { useRouter } from "vue-router";

const router = useRouter();

const form = ref({
  username: "",
  password: ""
});

const loading = ref(false);

async function handleLogin() {
  // 表单验证
  if (!form.value.username.trim()) {
    alert("请输入用户名");
    return;
  }
  if (!form.value.password.trim()) {
    alert("请输入密码");
    return;
  }

  loading.value = true;

  try {
    const res = await login(form.value);

    console.log("登录返回数据:", res);
    console.log("用户数据:", res.data);

    // 确保有 role 字段
    const userData = {
      username: res.data.username || form.value.username,
      role: res.data.role || "USER", // 默认角色
      ...res.data
    };

    localStorage.setItem("user", JSON.stringify(userData));
    alert("登录成功！用户：" + userData.username + ", 角色：" + userData.role);
    
    // 验证路由跳转
    console.log("准备跳转到 /home");
    await router.push("/home");
    console.log("跳转完成");
  } catch (error: any) {
    // 错误处理
    console.error("登录失败:", error);
    const errorMessage = error.response?.data?.message || 
                        error.message || 
                        "登录失败，请稍后重试";
    alert("登录失败: " + errorMessage);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div style="padding:40px">
    <h2>登录</h2>

    <input v-model="form.username" placeholder="用户名" />
    <br />
    <input v-model="form.password" type="password" placeholder="密码" />
    <br />

    <button @click="handleLogin" :disabled="loading">
  {{ loading ? '登录中...' : '登录' }}
</button>
  </div>
</template>