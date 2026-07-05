<template>
  <div class="welcome-section">
    <div class="welcome-content">
      <h2>
        <span class="greeting">欢迎回来，</span>
        <span class="username-highlight">{{ username }}</span>
      </h2>

      <p class="welcome-desc">
        基于机器学习的台风路径预测与分析平台
      </p>

      <div class="current-time">
        当前时间：{{ currentTime }}
      </div>
    </div>

    <div class="welcome-visual">
      <div class="typhoon-animation">
        <div class="typhoon-circle"></div>
        <div class="typhoon-ring ring-1"></div>
        <div class="typhoon-ring ring-2"></div>
        <div class="typhoon-ring ring-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  username: string
  currentTime: string
}>()
</script>

<style scoped>
</style>