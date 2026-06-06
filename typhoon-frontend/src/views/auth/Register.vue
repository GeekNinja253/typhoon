<script setup lang="ts">
import { ref } from "vue";
import { register } from "../../api/user";
import { useRouter } from "vue-router";

const router = useRouter();

const form = ref({
  username: "",
  password: "",
  nickname: ""
});

async function handleRegister() {
  await register(form.value);
  alert("注册成功");
  router.push("/login");
}
</script>

<template>
  <div style="padding:40px">
    <h2>注册</h2>

    <input v-model="form.username" placeholder="用户名" />
    <br />
    <input v-model="form.password" type="password" placeholder="密码" />
    <br />
    <input v-model="form.nickname" placeholder="昵称" />
    <br />

    <button @click="handleRegister">注册</button>
  </div>
</template>