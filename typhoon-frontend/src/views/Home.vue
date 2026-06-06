<template>
  <div style="padding:40px;">
    <h2>台风路径分析系统 - 仪表盘</h2>

    <div style="display:flex; gap:20px; margin-top:20px;">
      
      <!-- GIS地图模块 -->
      <div class="card" @click="$router.push('/map')">
        🌪 台风GIS地图
      </div>

      <!-- 历史记录模块 -->
      <div class="card" @click="$router.push('/history')">
        📜 历史预测记录
      </div>

      <div class="card">
        👤 用户（待开发）
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.card {
  width: 200px;
  height: 120px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 10px;
  transition: 0.2s;
}
.card:hover {
  background: #e6f7ff;
}
</style>