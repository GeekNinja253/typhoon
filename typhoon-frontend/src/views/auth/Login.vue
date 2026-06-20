<template>
  <div class="login-page">
    <div class="bg-circle circle1"></div>
    <div class="bg-circle circle2"></div>
    <div class="bg-circle circle3"></div>
    <div class="login-card">
      <div class="logo">
        🌪️
      </div>
      <h1>台风预测系统</h1>
      <p>Typhoon Prediction System</p>
      <div class="input-group">
        <input
          v-model="form.username"
          placeholder="请输入用户名"
        />
      </div>
      <div class="input-group">
        <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
        />
      </div>
      <button
        class="login-btn"
        @click="handleLogin"
        :disabled="loading"
      >
        {{ loading ? "登录中..." : "立即登录" }}
      </button>
      <button
        class="register-btn"
        @click="$router.push('/register')"
      >
        注册账号
      </button>
    </div>
  </div>
</template>


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

    const user = res.data.data;

    const userData = {
    username: user.username,
    role: user.role || "USER",
    ...user
  };

    localStorage.setItem("user", JSON.stringify(userData));

    router.push("/home");
  } catch (error: any) {
    alert(
      error.response?.data?.message ||
      error.message ||
      "登录失败"
    );
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>

.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background:
    linear-gradient(
      135deg,
      #1e3c72,
      #2a5298,
      #4facfe
    );
  overflow: hidden;
}

.login-card {
  width: 420px;
  padding: 50px 40px;
  border-radius: 24px;
  backdrop-filter: blur(18px);
  background: rgba(255,255,255,0.15);
  border: 1px solid rgba(255,255,255,0.25);
  box-shadow:
    0 20px 50px rgba(0,0,0,0.25);
  text-align: center;
}

.logo {
  font-size: 60px;
  margin-bottom: 10px;
}

h1 {
  color: white;
  margin-bottom: 8px;
}

p {
  color: rgba(255,255,255,0.8);
  margin-bottom: 35px;
}

.input-group {
  margin-bottom: 18px;
}

.input-group input {
  width: 100%;
  box-sizing: border-box;
  padding: 14px 18px;
  border-radius: 12px;
  border: none;
  outline: none;
  font-size: 15px;
  background: rgba(255,255,255,0.85);
  transition: 0.3s;
}

.input-group input:focus {
  transform: translateY(-2px);
  box-shadow:
    0 0 0 3px rgba(79,172,254,0.4);
}

.login-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  cursor: pointer;
  background:
    linear-gradient(
      135deg,
      #00c6ff,
      #0072ff
    );
  transition: 0.3s;
  margin-top: 10px;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow:
    0 10px 25px rgba(0,114,255,0.45);
}

.register-btn {
  width: 100%;
  height: 48px;
  margin-top: 15px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.4);
  background: transparent;
  color: white;
  cursor: pointer;
  transition: 0.3s;
}

.register-btn:hover {
  background: rgba(255,255,255,0.15);
}
.bg-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
}

.circle1 {
  width: 400px;
  height: 400px;
  background: #00c6ff;

  top: -120px;
  left: -100px;
}

.circle2 {
  width: 350px;
  height: 350px;
  background: #6a11cb;

  right: -80px;
  top: 20%;
}

.circle3 {
  width: 300px;
  height: 300px;
  background: #43e97b;

  bottom: -100px;
  left: 30%;
}

@media (max-width: 500px) {
  .login-card {
    width: 90%;
    padding: 35px 25px;
  }
}

</style>