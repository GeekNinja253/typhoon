<script setup lang="ts">
import { ref } from "vue";
import { register } from "../../api/user";
import { useRouter } from "vue-router";
import { onMounted } from "vue";

const router = useRouter();

const form = ref({
  username: "",
  password: "",
  nickname: ""
});

onMounted(() => {
  console.log(form.value);
  form.value = {
    username: "",
    password: "",
    nickname: ""
  };
});

const loading = ref(false);

async function handleRegister() {
  if (!form.value.username.trim()) {
    alert("请输入用户名");
    return;
  }
  if (!form.value.password.trim()) {
    alert("请输入密码");
    return;
  }
  if (!form.value.nickname.trim()) {
    alert("请输入昵称");
    return;
  }
  loading.value = true;
  try {
    await register(form.value);
    alert("注册成功");
    router.push("/login");
  } catch (error: any) {
    alert(
      error.response?.data?.message ||
      error.message ||
      "注册失败"
    );
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="register-page">
    <div class="bg-circle circle1"></div>
    <div class="bg-circle circle2"></div>
    <div class="bg-circle circle3"></div>
    <div class="register-card">
      <div class="logo">
        🌪️
      </div>
      <h1>用户注册</h1>
      <p>
        加入台风路径预测平台
      </p>
      <input
        type="text"
        autocomplete="username"
        style="display:none"
      />

      <input
        type="password"
        autocomplete="current-password"
        style="display:none"
      />

      <div class="input-group">
        <input
          v-model="form.username"
          name="register_user_name"
          autocomplete="new-password"
          placeholder="请输入用户名"
        />
      </div>

      <div class="input-group">
        <input
          v-model="form.password"
          type="password"
          name="register_user_password"
          autocomplete="new-password"
          placeholder="请输入密码"
        />
      </div>

      <button
        class="register-btn"
        @click="handleRegister"
        :disabled="loading"
      >
        {{ loading ? "注册中..." : "立即注册" }}
      </button>
      <button
        class="login-btn"
        @click="$router.push('/login')"
      >
        返回登录
      </button>
    </div>
  </div>
</template>

<style scoped>

.register-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(
      135deg,
      #1e3c72,
      #2a5298,
      #4facfe
    );
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

.register-card {
  width: 420px;
  padding: 50px 40px;
  border-radius: 24px;
  backdrop-filter: blur(18px);
  background: rgba(255,255,255,0.15);
  border: 1px solid rgba(255,255,255,0.25);
  box-shadow:
    0 20px 50px rgba(0,0,0,0.25);
  text-align: center;
  z-index: 10;
}

.logo {
  font-size: 60px;
  margin-bottom: 10px;
}

h1 {
  color: white;
  margin-bottom: 10px;
}

p {
  color: rgba(255,255,255,0.85);
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
  background: rgba(255,255,255,0.9);
  transition: 0.3s;
}

.input-group input:focus {
  transform: translateY(-2px);
  box-shadow:
    0 0 0 3px rgba(79,172,254,0.4);
}

.register-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  background:
    linear-gradient(
      135deg,
      #00c6ff,
      #0072ff
    );
  transition: 0.3s;
}

.register-btn:hover {
  transform: translateY(-3px);
  box-shadow:
    0 10px 25px rgba(0,114,255,0.4);
}

.login-btn {
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

.login-btn:hover {
  background: rgba(255,255,255,0.15);
}

@media (max-width: 500px) {
  .register-card {
    width: 90%;
    padding: 35px 25px;
  }
}
</style>