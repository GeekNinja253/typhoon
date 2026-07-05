<template>
  <div
    v-if="visible"
    class="modal-overlay"
    @click.self="emit('close')"
  >
    <div class="modal-content">
      <div class="modal-header">
        <div class="header-icon">📊</div>
        <h3>历史任务</h3>
        <button class="close-btn" @click="emit('close')">×</button>
      </div>

      <div class="modal-body">
        <div v-if="alertTasks.length === 0" class="empty-state">
          <span class="empty-icon">📭</span>
          <p>暂无历史任务</p>
        </div>

        <div v-else class="details-table-container">
          <table class="modern-table details-table">
            <thead>
              <tr>
                <th>状态</th>
                <th>任务 ID</th>
                <th>预测时间</th>
                <th>初始经度</th>
                <th>初始纬度</th>
                <th>初始强度</th>
                <th>气压</th>
                <th>操作</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="task in alertTasks"
                :key="task.id"
                class="table-row"
                :class="{ unread: !alertReadTasks.includes(task.id) }"
              >
                <td>
                  <span
                    v-if="!alertReadTasks.includes(task.id)"
                    class="status-dot unread"
                  ></span>

                  <span
                    v-else
                    class="status-dot read"
                  ></span>
                </td>

                <td>
                  <span class="task-id">
                    #{{ task.id }}
                  </span>
                </td>

                <td>{{ formatDateTime(task.createTime) }}</td>

                <td>{{ task.startLongitude.toFixed(2) }}°</td>

                <td>{{ task.startLatitude.toFixed(2) }}°</td>

                <td>
                  <span class="intensity-badge">
                    {{ task.startIntensity }}
                  </span>
                </td>

                <td>{{ task.startPressure }} hPa</td>

                <td>
                  <div class="action-buttons">
                    <button
                      class="btn btn-small btn-primary"
                      @click="emit('view-task', task.id)"
                    >
                      查看轨迹
                    </button>

                    <button
                      class="btn btn-small btn-danger"
                      @click="emit('delete-task', task.id)"
                    >
                      删除
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>

          </table>
        </div>
      </div>

      <div class="modal-footer">
        <button
          class="btn btn-primary"
          @click="emit('close')"
        >
          关闭
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
interface Task {
  id: number
  createTime: string
  startLongitude: number
  startLatitude: number
  startIntensity: string | number
  startPressure: number
}

const props = defineProps<{
  visible: boolean
  alertTasks: Task[]
  alertReadTasks: number[]
  formatDateTime: (dateStr: string) => string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'view-task', id: number): void
  (e: 'delete-task', id: number): void
}>()
</script>
<style scoped>
</style>