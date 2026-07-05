<template>
  <div class="stats-section">
    <div
      class="stat-card stat-alerts clickable"
      @click="$emit('openAlertRecordModal')"
    >
      <div class="stat-icon">🔔</div>

      <div class="stat-content">
        <div class="stat-value">{{ stats.alertCount }}</div>
        <div class="stat-label">预警记录</div>
        <div class="stat-sub">
          未读 {{ stats.unreadCount }} 条
        </div>
      </div>

      <div class="stat-trend positive">
        ↑ {{ stats.alertTrend }}%
      </div>

      <div class="stat-arrow">→</div>
    </div>

    <div
      class="stat-card stat-history clickable"
      @click="$emit('openAlertModal')"
    >
      <div class="stat-icon">📊</div>

      <div class="stat-content">
        <div class="stat-value">{{ stats.historyCount }}</div>
        <div class="stat-label">历史任务</div>
        <div class="stat-sub">
          未读 {{ stats.historyUnreadCount }}
        </div>
      </div>

      <div class="stat-trend positive">
        ↑ {{ stats.historyTrend }}%
      </div>

      <div class="stat-arrow">→</div>
    </div>

    <div
      class="stat-card stat-subscriptions clickable"
      @click="$emit('openSubscriptionModal')"
    >
      <div class="stat-icon">📍</div>

      <div class="stat-content">
        <div class="stat-value">
          {{ stats.activeCities.length }}
        </div>

        <div class="stat-label">
          已启用订阅
        </div>

        <div class="stat-sub">
          {{ stats.activeCities.join('、') || '暂无' }}
        </div>
      </div>

      <div class="stat-trend neutral">—</div>

      <div class="stat-arrow">→</div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  stats: {
    alertCount: number
    unreadCount: number
    alertTrend: number
    historyCount: number
    historyUnreadCount: number
    historyTrend: number
    activeCities: string[]
  }
}>()

defineEmits([
  'openAlertRecordModal',
  'openAlertModal',
  'openSubscriptionModal'
])
</script>

<style scoped>
</style>