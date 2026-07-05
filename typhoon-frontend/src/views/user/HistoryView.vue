<template>
  <div class="history-container">
    <div class="bg-animation"></div>
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>

    <div class="page-header">
      <div class="header-left">
        <h2>📜 历史预测记录</h2>
        <p class="header-subtitle">查看历史台风预测任务及其轨迹分析</p>
      </div>
      <div class="header-actions">
        <button v-if="!isBatchMode" class="btn btn-primary" @click="enterBatchMode">批量管理</button>
        <template v-else>
          <button class="btn" @click="exitBatchMode">取消</button>
          <button class="btn btn-danger" @click="batchDelete" :disabled="selectedTasks.length === 0">
            批量删除 ({{ selectedTasks.length }})
          </button>
        </template>
        <button class="btn" @click="$router.push('/home')">← 返回首页</button>
      </div>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-value">{{ tasks.length }}</div>
          <div class="stat-label">总预测任务</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🔵</div>
        <div class="stat-content">
          <div class="stat-value">{{ unreadCount }}</div>
          <div class="stat-label">未读记录</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-value">{{ tasks.length - unreadCount }}</div>
          <div class="stat-label">已读记录</div>
        </div>
      </div>
    </div>

    <div class="glass-panel">
      <div class="table-container">
        <table class="modern-table">
          <thead>
            <tr>
              <th v-if="isBatchMode" class="checkbox-th">
                <input type="checkbox" :checked="isAllSelected" @change="toggleSelectAll" class="checkbox-all" />
              </th>
              <th class="status-th">状态</th>
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
              <td :colspan="isBatchMode ? 9 : 8" class="empty-state">
                <span class="empty-icon">📭</span>
                <p>暂无预测记录</p>
              </td>
            </tr>
            <tr v-for="task in tasks" :key="task.id" class="table-row" :class="{ selected: isBatchMode && selectedTasks.includes(task.id), unread: !readTasks.includes(task.id) }">
              <td v-if="isBatchMode">
                <input type="checkbox" :checked="selectedTasks.includes(task.id)" @change="toggleSelect(task.id)" class="checkbox-item" />
              </td>
              <td>
                <span v-if="!readTasks.includes(task.id)" class="status-dot unread"></span>
                <span v-else class="status-dot read"></span>
              </td>
              <td><span class="task-id">#{{ task.id }}</span></td>
              <td>{{ formatDateTime(task.createTime) }}</td>
              <td>{{ task.startLongitude.toFixed(2) }}°</td>
              <td>{{ task.startLatitude.toFixed(2) }}°</td>
              <td><span class="intensity-badge">{{ task.startIntensity }}</span></td>
              <td>{{ task.startPressure }} hPa</td>
              <td>
                <div class="action-buttons">
                  <button class="btn btn-small btn-primary" @click="viewDetails(task.id)">查看轨迹</button>
                  <button v-if="!isBatchMode" class="btn btn-small btn-danger" @click="deleteTask(task.id)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <div class="header-icon">📍</div>
          <h3>任务 #{{ selectedTaskId }} - 轨迹预测详情</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="loadingDetails" class="loading-state">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else class="details-table-container">
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
        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

const tasks = ref<any[]>([]);
const showModal = ref(false);
const selectedTaskId = ref<number | null>(null);
const selectedResults = ref<any[]>([]);
const loadingDetails = ref(false);
const isBatchMode = ref(false);
const selectedTasks = ref<number[]>([]);

const readTasks = ref<number[]>(JSON.parse(localStorage.getItem('history_read_tasks') || '[]'));

const unreadCount = computed(() => {
  return tasks.value.filter(task => !readTasks.value.includes(task.id)).length;
});

function formatDateTime(dateStr: string) {
  if (!dateStr) return '--';
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
}

const fetchTasks = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/history/tasks');
    tasks.value = res.data.data;
  } catch (error) {
    console.error("Failed to fetch tasks:", error);
  }
};

const viewDetails = async (taskId: number) => {
  if (!readTasks.value.includes(taskId)) {
    readTasks.value.push(taskId);
    localStorage.setItem('history_read_tasks', JSON.stringify(readTasks.value));
  }

  selectedTaskId.value = taskId;
  selectedResults.value = [];
  showModal.value = true;
  loadingDetails.value = true;
  
  try {
    const res = await axios.get(`http://localhost:8080/api/history/tasks/${taskId}/results`);
    selectedResults.value = res.data.data;
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

const deleteTask = async (taskId: number) => {
  if (!confirm(`确定要删除任务 #${taskId} 吗？此操作不可撤销。`)) {
    return;
  }
  
  try {
    await axios.delete(`http://localhost:8080/api/history/tasks/${taskId}`);
    tasks.value = tasks.value.filter(task => task.id !== taskId);
    alert(`任务 #${taskId} 删除成功！`);
  } catch (error) {
    console.error("Failed to delete task:", error);
    alert("删除失败，请稍后重试");
  }
};

const enterBatchMode = () => {
  isBatchMode.value = true;
  selectedTasks.value = [];
};

const exitBatchMode = () => {
  isBatchMode.value = false;
  selectedTasks.value = [];
};

const toggleSelect = (taskId: number) => {
  const index = selectedTasks.value.indexOf(taskId);
  if (index > -1) {
    selectedTasks.value.splice(index, 1);
  } else {
    selectedTasks.value.push(taskId);
  }
};

const isAllSelected = ref(false);

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedTasks.value = [];
  } else {
    selectedTasks.value = tasks.value.map(task => task.id);
  }
};

const batchDelete = async () => {
  if (selectedTasks.value.length === 0) return;
  
  if (!confirm(`确定要删除选中的 ${selectedTasks.value.length} 条记录吗？此操作不可撤销。`)) {
    return;
  }
  
  try {
    for (const taskId of selectedTasks.value) {
      await axios.delete(`http://localhost:8080/api/history/tasks/${taskId}`);
    }
    tasks.value = tasks.value.filter(task => !selectedTasks.value.includes(task.id));
    alert(`成功删除 ${selectedTasks.value.length} 条记录！`);
    exitBatchMode();
  } catch (error) {
    console.error("Failed to delete tasks:", error);
    alert("批量删除失败，请稍后重试");
  }
};

onMounted(() => {
  fetchTasks();
});
</script>

<style scoped>
.history-container {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #0c1929 0%, #1a365d 50%, #0f2027 100%);
  box-sizing: border-box;
  width: 100%;
  position: relative;
  overflow-x: hidden;
}

.bg-animation {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(0, 201, 255, 0.1) 0%, transparent 50%);
  animation: bgPulse 20s ease-in-out infinite;
  z-index: 0;
}

@keyframes bgPulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.bg-blob {
  position: fixed;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.2;
  z-index: 0;
}

.blob-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -200px;
  left: -100px;
  animation: blobMove1 15s ease-in-out infinite;
}

.blob-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #00C9FF, #92FE9D);
  bottom: -100px;
  right: -100px;
  animation: blobMove2 12s ease-in-out infinite;
}

@keyframes blobMove1 {
  0%, 100% { transform: translate(0, 0); }
  33% { transform: translate(100px, 50px); }
  66% { transform: translate(50px, 100px); }
}

@keyframes blobMove2 {
  0%, 100% { transform: translate(0, 0); }
  33% { transform: translate(-80px, -60px); }
  66% { transform: translate(-40px, -100px); }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.95);
  letter-spacing: 1px;
}

.header-subtitle {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.stat-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.stat-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.95);
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.glass-panel {
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.table-container {
  overflow-x: auto;
  max-height: calc(100vh - 400px);
  overflow-y: auto;
}

.modern-table {
  width: 100%;
  min-width: 800px;
  border-collapse: collapse;
  text-align: left;
}

.modern-table th, .modern-table td {
  padding: 14px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  white-space: nowrap;
}

.modern-table th {
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 1.5px;
  position: sticky;
  top: 0;
  background: rgba(12, 25, 41, 0.95);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.checkbox-th, .status-th {
  width: 60px;
}

.modern-table .table-row {
  transition: all 0.2s ease;
}

.modern-table .table-row:hover {
  background: rgba(255, 255, 255, 0.06);
}

.modern-table .table-row.selected {
  background: rgba(102, 126, 234, 0.15);
}

.modern-table .table-row.unread {
  background: rgba(255, 197, 61, 0.04);
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.unread {
  background: #ffc53d;
  box-shadow: 0 0 8px rgba(255, 197, 61, 0.5);
}

.status-dot.read {
  background: rgba(255, 255, 255, 0.3);
}

.task-id {
  color: #667eea;
  font-weight: 600;
}

.intensity-badge {
  display: inline-block;
  padding: 4px 10px;
  background: rgba(102, 126, 234, 0.2);
  color: #818cf8;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.checkbox-all,
.checkbox-item {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

.empty-state {
  text-align: center;
  padding: 60px 40px !important;
  color: rgba(255, 255, 255, 0.3);
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 10px 20px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.95);
}

.btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.3);
}

.btn-primary {
  background: rgba(102, 126, 234, 0.8);
  color: white;
  border-color: transparent;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-danger {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border-color: rgba(255, 107, 107, 0.4);
}

.btn-danger:hover:not(:disabled) {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.8), rgba(238, 82, 83, 0.8));
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.btn-danger:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-small {
  padding: 6px 14px;
  font-size: 12px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  width: 90%;
  max-width: 900px;
  max-height: 85vh;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-icon {
  font-size: 24px;
}

.modal-header h3 {
  margin: 0;
  flex: 1;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
}

.close-btn {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  font-size: 24px;
  padding: 5px 12px;
  line-height: 1;
  border-radius: 50%;
  transition: all 0.3s ease;
  border: none;
}

.close-btn:hover {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.2);
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px;
  max-height: calc(85vh - 140px);
  overflow-y: auto;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.details-table-container {
  margin-top: 10px;
}

.details-table {
  min-width: 700px;
}

.details-table th {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: right;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
