<template>
  <div class="analysis-section">
    <div class="analysis-card knowledge-card">
      <div class="card-header">
        <h3 class="card-title">🌪 台风科普知识</h3>
      </div>

      <div class="knowledge-content">
        <div class="knowledge-item">
          <div class="knowledge-icon">🌀</div>
          <div class="knowledge-info">
            <h4>台风等级划分</h4>
            <p>
              根据风速分为6个等级：热带低压、热带风暴、强热带风暴、台风、强台风、超强台风
            </p>
          </div>
        </div>

        <div class="knowledge-item">
          <div class="knowledge-icon">📏</div>
          <div class="knowledge-info">
            <h4>预警信号等级</h4>
            <p>
              蓝、黄、橙、红四级预警，分别对应一般、较重、严重、特别严重四个等级
            </p>
          </div>
        </div>

        <div class="knowledge-item">
          <div class="knowledge-icon">🏠</div>
          <div class="knowledge-info">
            <h4>防御措施</h4>
            <p>
              台风来临前检查门窗、准备应急物资、避免前往海边活动
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="analysis-card system-card">
      <div class="card-header">
        <h3 class="card-title">💡 系统动态</h3>
      </div>

      <div class="system-content">
        <div class="system-status">
          <div class="status-dot green"></div>
          <span>系统运行正常</span>
        </div>

        <div class="system-item">
          <div class="system-icon">🔄</div>
          <div class="system-info">
            <span class="system-label">数据更新</span>
            <span class="system-value">实时同步</span>
          </div>
        </div>

        <div class="system-item">
          <div class="system-icon">🌐</div>
          <div class="system-info">
            <span class="system-label">气象数据源</span>
            <span class="system-value">国家气象局</span>
          </div>
        </div>

        <div class="system-item">
          <div class="system-icon">⚡</div>
          <div class="system-info">
            <span class="system-label">预测模型</span>
            <span class="system-value">机器学习v2.0</span>
          </div>
        </div>

        <div class="system-update">
          <span class="update-icon">📝</span>

          <div class="update-info">
            <span class="update-title">最近更新</span>
            <span class="update-content">
              优化预测算法，提升路径预测准确率
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>