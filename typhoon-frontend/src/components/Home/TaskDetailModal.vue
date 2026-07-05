<template>
  <div v-if="visible" class="modal-overlay" @click.self="emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <div class="header-icon">📍</div>
        <h3>任务 #{{ taskId }} - 轨迹预测详情</h3>
        <button class="close-btn" @click="emit('close')">×</button>
      </div>

      <div class="modal-body">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <div v-else class="details-table-container">
          <table class="modern-table details-table">
            <thead>
              <tr>
                <th>步长</th>
                <th>预测经度</th>
                <th>预测纬度</th>
                <th>预测等级</th>
                <th>风速</th>
                <th>气压</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="item in results" :key="item.id">
                <td>{{ item.stepIndex }}</td>
                <td>{{ item.predLongitude.toFixed(4) }}°</td>
                <td>{{ item.predLatitude.toFixed(4) }}°</td>
                <td>{{ item.predGrade.toFixed(2) }}</td>
                <td>{{ item.predWindSpeed.toFixed(2) }}</td>
                <td>{{ item.predPressure.toFixed(2) }}</td>
              </tr>
            </tbody>

          </table>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-primary" @click="emit('close')">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

interface TaskResult {
  id: number
  stepIndex: number
  predLongitude: number
  predLatitude: number
  predGrade: number
  predWindSpeed: number
  predPressure: number
}

defineProps<{
  visible: boolean
  taskId: number
  results: TaskResult[]
  loading: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()
</script>

<style scoped>
</style>