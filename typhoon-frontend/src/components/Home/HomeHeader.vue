<template>
  <div class="header">
    <div class="logo-section">
      <div class="logo-icon">🌪</div>

      <div>
        <h1>台风路径分析系统</h1>
        <p>Typhoon Prediction Platform</p>
      </div>
    </div>

    <div class="user-section">
      <div class="user-info">
        <span class="user-icon">👤</span>
        <span class="user-name">{{ username }}</span>
      </div>

      <button
        class="logout-btn"
        @click="$emit('logout')"
      >
        <span>退出登录</span>
        <span class="logout-icon">🚪</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  username: string
}>()

defineEmits<{
  (e: 'logout'): void
}>()
</script>

<style scoped>
/* 不写任何 css */
/* 继续使用 home.css */
</style>