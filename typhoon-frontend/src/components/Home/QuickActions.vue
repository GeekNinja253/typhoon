<template>
  <div class="quick-actions">
    <h3 class="section-title">
      <span class="title-icon">⚡</span>
      快速操作
    </h3>

    <div class="action-grid">
      <div class="action-card" @click="$router.push('/map')">
        <div class="action-icon-wrapper blue">
          <span class="action-icon">🌪</span>
        </div>

        <div class="action-info">
          <h4>GIS地图分析</h4>
          <p>查看台风路径预测结果，实时追踪台风动态</p>
        </div>

        <div class="action-arrow">→</div>
      </div>

      <div class="action-card" @click="$router.push('/history')">
        <div class="action-icon-wrapper purple">
          <span class="action-icon">📜</span>
        </div>

        <div class="action-info">
          <h4>历史预测记录</h4>
          <p>查看历史任务与预测数据，分析预测趋势</p>
        </div>

        <div class="action-arrow">→</div>
      </div>

      <div class="action-card" @click="$router.push('/alert')">
        <div class="action-icon-wrapper orange">
          <span class="action-icon">🔔</span>
        </div>

        <div class="action-info">
          <h4>预警报告</h4>
          <p>订阅台风预警，获取实时通知与详细分析</p>
        </div>

        <div class="action-arrow">→</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>