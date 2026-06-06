<template>
  <div class="history-container">
    <div class="glass-panel">
      <div class="header">
        <h2>📜 历史预测记录</h2>
        <button class="back-btn" @click="$router.push('/home')">返回仪表盘</button>
      </div>

      <div class="table-container">
        <table class="modern-table">
          <thead>
            <tr>
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
            <tr v-if="tasks.length === 0">
              <td colspan="7" class="empty-state">暂无预测记录</td>
            </tr>
            <tr v-for="task in tasks" :key="task.id" class="table-row">
              <td>#{{ task.id }}</td>
              <td>{{ new Date(task.createTime).toLocaleString() }}</td>
              <td>{{ task.startLongitude.toFixed(2) }}°</td>
              <td>{{ task.startLatitude.toFixed(2) }}°</td>
              <td>{{ task.startIntensity }}</td>
              <td>{{ task.startPressure }} hPa</td>
              <td>
                <button class="view-btn" @click="viewDetails(task.id)">查看轨迹详情</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 轨迹详情模态框 (Glassmorphism) -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content glass-panel">
        <h3>任务 #{{ selectedTaskId }} - 轨迹预测详情</h3>
        <button class="close-btn" @click="closeModal">×</button>
        
        <div class="details-table-container">
          <table class="modern-table details-table">
            <thead>
              <tr>
                <th>步长 (Step)</th>
                <th>预测经度</th>
                <th>预测纬度</th>
                <th>预测等级</th>
                <th>风速 (m/s)</th>
                <th>气压 (hPa)</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loadingDetails">
                <td colspan="6" class="empty-state">加载中...</td>
              </tr>
              <tr v-for="result in selectedResults" :key="result.id" class="table-row">
                <td>{{ result.stepIndex }}</td>
                <td>{{ result.predLongitude.toFixed(4) }}°</td>
                <td>{{ result.predLatitude.toFixed(4) }}°</td>
                <td>{{ result.predGrade.toFixed(2) }}</td>
                <td>{{ result.predWindSpeed.toFixed(2) }}</td>
                <td>{{ result.predPressure.toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

const tasks = ref<any[]>([]);
const showModal = ref(false);
const selectedTaskId = ref<number | null>(null);
const selectedResults = ref<any[]>([]);
const loadingDetails = ref(false);

const fetchTasks = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/history/tasks');
    tasks.value = res.data;
  } catch (error) {
    console.error("Failed to fetch tasks:", error);
  }
};

const viewDetails = async (taskId: number) => {
  selectedTaskId.value = taskId;
  selectedResults.value = [];
  showModal.value = true;
  loadingDetails.value = true;
  
  try {
    const res = await axios.get(`http://localhost:8080/api/history/tasks/${taskId}/results`);
    selectedResults.value = res.data;
  } catch (error) {
    console.error("Failed to fetch results:", error);
  } finally {
    loadingDetails.value = false;
  }
};

const closeModal = () => {
  showModal.value = false;
  selectedTaskId.value = null;
};

onMounted(() => {
  fetchTasks();
});
</script>

<style scoped>
/* 渐变背景 */
.history-container {
  min-height: 100vh;
  padding: 40px;
  background: linear-gradient(135deg, #0f2027, #203a43, #2c5364);
  color: #fff;
  font-family: 'Inter', system-ui, sans-serif;
  box-sizing: border-box;
}

/* 玻璃拟物化面板 */
.glass-panel {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h2 {
  margin: 0;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 现代表格设计 */
.table-container {
  overflow-x: auto;
}

.modern-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.modern-table th, .modern-table td {
  padding: 15px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modern-table th {
  font-weight: 600;
  color: #a8b2d1;
  text-transform: uppercase;
  font-size: 0.85rem;
  letter-spacing: 1px;
}

.modern-table .table-row {
  transition: all 0.3s ease;
}

.modern-table .table-row:hover {
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-2px);
}

.empty-state {
  text-align: center;
  padding: 40px !important;
  color: #8892b0;
}

/* 按钮设计 */
button {
  cursor: pointer;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  padding: 10px 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.view-btn {
  background: linear-gradient(90deg, #00C9FF 0%, #92FE9D 100%);
  color: #000;
  padding: 8px 16px;
  font-size: 0.85rem;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(0, 201, 255, 0.3);
}

.view-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0, 201, 255, 0.5);
}

/* 模态框设计 */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  width: 80%;
  max-width: 900px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  animation: slideUp 0.4s ease;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: transparent;
  color: #fff;
  font-size: 24px;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #ff4d4f;
  transform: rotate(90deg);
}

.details-table-container {
  margin-top: 20px;
}

.details-table th {
  color: #00C9FF;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
