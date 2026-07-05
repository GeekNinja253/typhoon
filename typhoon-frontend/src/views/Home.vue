<template>
  <div class="home-page">
    <div class="bg-animation"></div>
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>
    <div class="bg-blob blob-3"></div>

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
        <button class="logout-btn" @click="handleLogout">
          <span>退出登录</span>
          <span class="logout-icon">🚪</span>
        </button>
      </div>
    </div>

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

    <div class="stats-section">
      <div class="stat-card stat-alerts clickable" @click="openAlertRecordModal">
        <div class="stat-icon">🔔</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.alertCount }}</div>
          <div class="stat-label">预警记录</div>
          <div class="stat-sub">未读 {{ stats.unreadCount }} 条</div>
        </div>
        <div class="stat-trend positive">↑ {{ stats.alertTrend }}%</div>
        <div class="stat-arrow">→</div>
      </div>

      <div class="stat-card stat-history clickable" @click="openAlertModal">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.historyCount }}</div>
          <div class="stat-label">历史任务</div>
          <div class="stat-sub">未读 {{ stats.historyUnreadCount }}</div>
        </div>
        <div class="stat-trend positive">↑ {{ stats.historyTrend }}%</div>
        <div class="stat-arrow">→</div>
      </div>

      <div class="stat-card stat-subscriptions clickable" @click="openSubscriptionModal">
        <div class="stat-icon">📍</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.activeCities.length }}</div>
          <div class="stat-label">已启用订阅</div>
          <div class="stat-sub">{{ stats.activeCities.join('、') || '暂无' }}</div>
        </div>
        <div class="stat-trend neutral">—</div>
        <div class="stat-arrow">→</div>
      </div>
    </div>

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
              <p>根据风速分为6个等级：热带低压、热带风暴、强热带风暴、台风、强台风、超强台风</p>
            </div>
          </div>
          <div class="knowledge-item">
            <div class="knowledge-icon">📏</div>
            <div class="knowledge-info">
              <h4>预警信号等级</h4>
              <p>蓝、黄、橙、红四级预警，分别对应一般、较重、严重、特别严重四个等级</p>
            </div>
          </div>
          <div class="knowledge-item">
            <div class="knowledge-icon">🏠</div>
            <div class="knowledge-info">
              <h4>防御措施</h4>
              <p>台风来临前检查门窗、准备应急物资、避免前往海边活动</p>
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
              <span class="update-content">优化预测算法，提升路径预测准确率</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="tips-section">
      <h3 class="section-title">
        <span class="title-icon">�</span>
        台风防御小贴士
      </h3>
      <div class="tips-grid">
        <div class="tip-card">
          <div class="tip-icon">🏠</div>
          <h4>居家防范</h4>
          <p>检查门窗加固，准备应急物资，关闭不必要电源</p>
        </div>
        <div class="tip-card">
          <div class="tip-icon">🚶</div>
          <h4>户外安全</h4>
          <p>减少外出，远离广告牌和高大树木，注意高空坠物</p>
        </div>
        <div class="tip-card">
          <div class="tip-icon">🚢</div>
          <h4>海上作业</h4>
          <p>小型船只及时回港避风，海上作业人员注意安全</p>
        </div>
      </div>
    </div>

    <div class="footer">
      <p>© 2026 台风路径分析系统 | 基于机器学习的智能预测平台</p>
    </div>

    <div v-if="showAlertModal" class="modal-overlay" @click.self="closeAlertModal">
      <div class="modal-content">
        <div class="modal-header">
          <div class="header-icon">📊</div>
          <h3>历史任务</h3>
          <button class="close-btn" @click="closeAlertModal">×</button>
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
                <tr v-for="task in alertTasks" :key="task.id" class="table-row" :class="{ unread: !alertReadTasks.includes(task.id) }">
                  <td>
                    <span v-if="!alertReadTasks.includes(task.id)" class="status-dot unread"></span>
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
                      <button class="btn btn-small btn-primary" @click="viewTaskDetails(task.id)">查看轨迹</button>
                      <button class="btn btn-small btn-danger" @click="deleteTask(task.id)">删除</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeAlertModal">关闭</button>
        </div>
      </div>
    </div>

    <ReportModal 
      :visible="showAlertRecordModal"
      :reports="alertRecords"
      :unread-count="stats.unreadCount"
      :is-batch-mode="isBatchMode"
      :selected-reports="selectedReports"
      @close="closeAlertRecordModal"
      @enter-batch-mode="enterBatchMode"
      @exit-batch-mode="exitBatchMode"
      @batch-delete="batchDelete"
      @toggle-select-all="toggleSelectAll"
      @toggle-select="toggleSelect"
      @go-analysis="goAnalysis"
      @view-report="viewReport"
      @delete-report="deleteAlertRecord"
    />

    <SubscriptionModal 
      :visible="showSubscriptionModal"
      :subscriptions="subscriptions"
      @close="closeSubscriptionModal"
      @toggle="toggleSubscription"
      @edit="editSubscription"
      @delete="deleteSubscription"
    />

    <div v-if="showTaskModal" class="modal-overlay" @click.self="closeTaskModal">
      <div class="modal-content">
        <div class="modal-header">
          <div class="header-icon">📍</div>
          <h3>任务 #{{ selectedTaskId }} - 轨迹预测详情</h3>
          <button class="close-btn" @click="closeTaskModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="loadingTaskDetails" class="loading-state">
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
                <tr v-for="result in selectedTaskResults" :key="result.id" class="table-row">
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
          <button class="btn btn-primary" @click="closeTaskModal">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import ReportModal from '../components/WarningBroadcast/ReportModal.vue';
import SubscriptionModal from '../components/WarningBroadcast/SubscriptionModal.vue';

const router = useRouter();

const user = JSON.parse(localStorage.getItem('user') || '{}');
const username = user.username || '用户';
const userId = user.id || 1;

const currentTime = ref('');
const stats = ref({
  alertCount: 3,
  unreadCount: 2,
  alertTrend: 15,
  historyCount: 10,
  historyUnreadCount: 5,
  historyTrend: 10,
  activeCities: ['高雄', '厦门']
});

const alertTasks = ref<any[]>([]);
const alertReadTasks = ref<number[]>(JSON.parse(localStorage.getItem('history_read_tasks') || '[]'));

const alertRecords = ref<any[]>([
  { id: 1, level: 3, createTime: new Date(Date.now() - 3600000).toISOString(), cityName: '高雄', distance: 120 },
  { id: 2, level: 2, createTime: new Date(Date.now() - 7200000).toISOString(), cityName: '厦门', distance: 200 },
  { id: 3, level: 4, createTime: new Date(Date.now() - 86400000).toISOString(), cityName: '福州', distance: 180 },
  { id: 4, level: 1, createTime: new Date(Date.now() - 172800000).toISOString(), cityName: '广州', distance: 300 },
  { id: 5, level: 5, createTime: new Date(Date.now() - 259200000).toISOString(), cityName: '深圳', distance: 280 },
  { id: 6, level: 2, createTime: new Date(Date.now() - 345600000).toISOString(), cityName: '珠海', distance: 250 },
  { id: 7, level: 3, createTime: new Date(Date.now() - 432000000).toISOString(), cityName: '汕头', distance: 150 },
]);
const isBatchMode = ref(false);
const selectedReports = ref<number[]>([]);

const subscriptions = ref<any[]>([]);



const showAlertModal = ref(false);
const showAlertRecordModal = ref(false);
const showSubscriptionModal = ref(false);
const showTaskModal = ref(false);
const selectedTaskId = ref(0);
const selectedTaskResults = ref<any[]>([]);
const loadingTaskDetails = ref(false);

let timeInterval: ReturnType<typeof setInterval>;

function updateTime() {
  const now = new Date();
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
}

async function loadStats() {
  let alertCount = 3;
  let unreadCount = 2;
  let historyCount = 10;
  let historyUnreadCount = 5;
  let activeCities: string[] = ['高雄', '厦门'];

  try {
    const historyRes = await axios.get('http://localhost:8080/api/history/tasks');
    const tasks = historyRes.data.data || [];
    historyCount = tasks.length;
    
    const readTasks = JSON.parse(localStorage.getItem('history_read_tasks') || '[]');
    historyUnreadCount = tasks.filter((task: any) => !readTasks.includes(task.id)).length;
  } catch (error) {
    console.warn('加载历史任务失败，使用默认数据', error);
  }

  try {
    const alertRes = await axios.get(`http://localhost:8080/api/alert/report/${userId}`);
    const alerts = alertRes.data.data || alertRes.data || [];
    alertRecords.value = alerts;
    alertCount = alerts.length;
    unreadCount = alerts.filter((a: any) => a.status === 0).length;
    
    if (alerts.length === 0) {
      alertRecords.value = [
        { id: 1, level: 3, createTime: new Date(Date.now() - 3600000).toISOString(), cityName: '高雄', distance: 120 },
        { id: 2, level: 2, createTime: new Date(Date.now() - 7200000).toISOString(), cityName: '厦门', distance: 200 },
        { id: 3, level: 4, createTime: new Date(Date.now() - 86400000).toISOString(), cityName: '福州', distance: 180 },
        { id: 4, level: 1, createTime: new Date(Date.now() - 172800000).toISOString(), cityName: '广州', distance: 300 },
        { id: 5, level: 5, createTime: new Date(Date.now() - 259200000).toISOString(), cityName: '深圳', distance: 280 },
        { id: 6, level: 2, createTime: new Date(Date.now() - 345600000).toISOString(), cityName: '珠海', distance: 250 },
        { id: 7, level: 3, createTime: new Date(Date.now() - 432000000).toISOString(), cityName: '汕头', distance: 150 },
      ];
    }
  } catch (error) {
    console.warn('加载预警记录失败，使用默认数据', error);
    alertRecords.value = [
      { id: 1, level: 3, createTime: new Date(Date.now() - 3600000).toISOString(), cityName: '高雄', distance: 120 },
      { id: 2, level: 2, createTime: new Date(Date.now() - 7200000).toISOString(), cityName: '厦门', distance: 200 },
      { id: 3, level: 4, createTime: new Date(Date.now() - 86400000).toISOString(), cityName: '福州', distance: 180 },
      { id: 4, level: 1, createTime: new Date(Date.now() - 172800000).toISOString(), cityName: '广州', distance: 300 },
      { id: 5, level: 5, createTime: new Date(Date.now() - 259200000).toISOString(), cityName: '深圳', distance: 280 },
      { id: 6, level: 2, createTime: new Date(Date.now() - 345600000).toISOString(), cityName: '珠海', distance: 250 },
      { id: 7, level: 3, createTime: new Date(Date.now() - 432000000).toISOString(), cityName: '汕头', distance: 150 },
    ];
  }

  try {
    const subRes = await axios.get(`http://localhost:8080/api/alert/subscription/${userId}`);
    const subs = subRes.data.data || [];
    activeCities = subs
      .filter((s: any) => s.status === 1)
      .map((s: any) => s.cityName || '自定义位置');
  } catch (error) {
    console.warn('加载订阅信息失败，使用默认数据', error);
  }

  stats.value = {
    alertCount,
    unreadCount,
    alertTrend: Math.min(100, Math.round((alertCount / (alertCount + 10)) * 50)),
    historyCount,
    historyUnreadCount,
    historyTrend: Math.min(100, Math.round((historyCount / (historyCount + 15)) * 40)),
    activeCities
  };
}

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

async function loadAlertTasks() {
  try {
    const res = await axios.get('http://localhost:8080/api/history/tasks');
    alertTasks.value = res.data.data;
  } catch (error) {
    console.warn('加载历史任务失败', error);
  }
}

function openAlertModal() {
  loadAlertTasks();
  showAlertModal.value = true;
}

function closeAlertModal() {
  showAlertModal.value = false;
}

async function loadAlertRecords() {
  try {
    const res = await axios.get(`http://localhost:8080/api/alert/report/${userId}`);
    alertRecords.value = res.data.data;
  } catch (error) {
    console.warn('加载预警记录失败', error);
  }
}

async function openAlertRecordModal() {
  await loadAlertRecords();
  showAlertRecordModal.value = true;
}

function closeAlertRecordModal() {
  showAlertRecordModal.value = false;
}

async function loadSubscriptions() {
  try {
    const res = await axios.get(`http://localhost:8080/api/alert/subscription/${userId}`);
    subscriptions.value = res.data.data;
  } catch (error) {
    console.warn('加载订阅列表失败', error);
  }
}

async function openSubscriptionModal() {
  await loadSubscriptions();
  showSubscriptionModal.value = true;
}

function closeSubscriptionModal() {
  showSubscriptionModal.value = false;
}

async function toggleSubscription(id: number, status: number) {
  try {
    const newStatus = status === 1 ? 0 : 1;
    await axios.put(`http://localhost:8080/api/alert/subscription/${id}/status`, null, {
      params: { status: newStatus }
    });
    subscriptions.value = subscriptions.value.map(s => 
      s.id === id ? { ...s, status: newStatus } : s
    );
    loadStats();
  } catch (error) {
    console.error("Failed to toggle subscription:", error);
  }
}

function editSubscription(sub: any) {
  console.log("Edit subscription:", sub);
}

async function deleteSubscription(id: number) {
  if (!confirm('确定要删除这个订阅吗？')) return;

  try {
    await axios.delete(`http://localhost:8080/api/alert/subscription/${id}`);
    subscriptions.value = subscriptions.value.filter(s => s.id !== id);
    loadStats();
  } catch (error) {
    console.error("Failed to delete subscription:", error);
  }
}

async function deleteAlertRecord(id: number) {
  if (!confirm('确定要删除这条预警记录吗？')) return;

  try {
    await axios.delete(`http://localhost:8080/api/alert/report/${id}`);
    alertRecords.value = alertRecords.value.filter(r => r.id !== id);
    loadStats();
  } catch (error) {
    console.error("Failed to delete alert record:", error);
  }
}

function enterBatchMode() {
  isBatchMode.value = true;
}

function exitBatchMode() {
  isBatchMode.value = false;
  selectedReports.value = [];
}

async function batchDelete() {
  if (selectedReports.value.length === 0) return;
  if (!confirm(`确定要删除选中的 ${selectedReports.value.length} 条预警记录吗？`)) return;

  try {
    for (const id of selectedReports.value) {
      await axios.delete(`http://localhost:8080/api/alert/report/${id}`);
    }
    alertRecords.value = alertRecords.value.filter(r => !selectedReports.value.includes(r.id));
    selectedReports.value = [];
    isBatchMode.value = false;
    loadStats();
  } catch (error) {
    console.error("Failed to batch delete alert records:", error);
  }
}

function toggleSelectAll() {
  if (selectedReports.value.length === alertRecords.value.length) {
    selectedReports.value = [];
  } else {
    selectedReports.value = alertRecords.value.map(r => r.id);
  }
}

function toggleSelect(id: number) {
  const index = selectedReports.value.indexOf(id);
  if (index === -1) {
    selectedReports.value.push(id);
  } else {
    selectedReports.value.splice(index, 1);
  }
}

function goAnalysis(report: any) {
  router.push(`/analysis/${report.taskId}`);
}

function viewReport(report: any) {
  console.log("View report:", report);
}

function closeTaskModal() {
  showTaskModal.value = false;
}

async function viewTaskDetails(taskId: number) {
  if (!alertReadTasks.value.includes(taskId)) {
    alertReadTasks.value.push(taskId);
    localStorage.setItem('history_read_tasks', JSON.stringify(alertReadTasks.value));
  }

  selectedTaskId.value = taskId;
  selectedTaskResults.value = [];
  showTaskModal.value = true;
  loadingTaskDetails.value = true;

  try {
    const res = await axios.get(`http://localhost:8080/api/history/tasks/${taskId}/results`);
    selectedTaskResults.value = res.data.data;
  } catch (error) {
    console.error("Failed to fetch results:", error);
  } finally {
    loadingTaskDetails.value = false;
  }
}

async function deleteTask(taskId: number) {
  if (!confirm('确定要删除这条记录吗？')) return;

  try {
    await axios.delete(`http://localhost:8080/api/history/tasks/${taskId}`);
    alertTasks.value = alertTasks.value.filter(t => t.id !== taskId);
    loadStats();
  } catch (error) {
    console.error("Failed to delete task:", error);
  }
}

function handleLogout() {
  localStorage.removeItem('user');
  router.push('/login');
}

onMounted(() => {
  updateTime();
  timeInterval = setInterval(updateTime, 1000);
  loadStats();
});

onUnmounted(() => {
  clearInterval(timeInterval);
});
</script>

<style scoped>
.home-page {
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
    radial-gradient(circle at 80% 20%, rgba(0, 201, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(95, 222, 100, 0.05) 0%, transparent 50%);
  animation: bgPulse 20s ease-in-out infinite;
  z-index: 0;
  pointer-events: none;
}

@keyframes bgPulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.bg-blob {
  position: fixed;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  z-index: 0;
  pointer-events: none;
}

.blob-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -100px;
  left: -100px;
  animation: blobMove1 15s ease-in-out infinite;
}

.blob-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #00C9FF, #92FE9D);
  bottom: -50px;
  right: -50px;
  animation: blobMove2 12s ease-in-out infinite;
}

.blob-3 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #ff6b6b, #ee5253);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: blobMove3 18s ease-in-out infinite;
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

@keyframes blobMove3 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.2); }
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  font-size: 48px;
  animation: logoSpin 8s linear infinite;
}

@keyframes logoSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.logo-section h1 {
  margin: 0;
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
}

.logo-section p {
  margin: 4px 0 0 0;
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.user-icon {
  font-size: 18px;
}

.user-name {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 42px;
  padding: 0 22px;
  border-radius: 12px;
  border: 1px solid rgba(255, 107, 107, 0.35);
  background: rgba(255, 107, 107, 0.12);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all .25s ease;
  box-shadow: 0 6px 18px rgba(255, 107, 107, .15);
}

.logout-btn:hover {
  background: linear-gradient(135deg, #ff6b6b, #ff8f70);
  border-color: transparent;
  transform: translateY(-3px);
  box-shadow:
    0 10px 24px rgba(255, 107, 107, .35),
    0 0 16px rgba(255, 107, 107, .25);
}

.logout-btn:active {
  transform: translateY(0);
  box-shadow: 0 3px 10px rgba(255, 107, 107, .2);
}

.logout-btn span {
  transition: all .25s ease;
}

.logout-btn:hover .logout-icon {
  transform: translateX(4px);
}

.logout-btn:focus-visible {
  outline: 2px solid rgba(255,255,255,.5);
  outline-offset: 2px;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  margin-bottom: 40px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 1;
}

.welcome-content h2 {
  margin: 0;
  font-size: 32px;
  color: #fff;
  line-height: 1.4;
}

.greeting {
  color: rgba(255, 255, 255, 0.8);
}

.username-highlight {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-desc {
  margin: 12px 0 0 0;
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
}

.current-time {
  margin: 20px 0 0 0;
  padding: 10px 20px;
  background: rgba(0, 201, 255, 0.1);
  border-radius: 8px;
  color: #00C9FF;
  font-size: 14px;
  font-family: 'Courier New', monospace;
}

.welcome-visual {
  flex-shrink: 0;
}

.typhoon-animation {
  position: relative;
  width: 150px;
  height: 150px;
}

.typhoon-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, #fff 0%, #00C9FF 50%, transparent 100%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: typhoonPulse 2s ease-in-out infinite;
}

@keyframes typhoonPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  50% { transform: translate(-50%, -50%) scale(1.3); opacity: 0.7; }
}

.typhoon-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 2px solid rgba(0, 201, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.ring-1 {
  width: 80px;
  height: 80px;
  animation: ringRotate1 3s linear infinite;
}

.ring-2 {
  width: 110px;
  height: 110px;
  animation: ringRotate2 4s linear infinite;
  border-color: rgba(102, 126, 234, 0.3);
}

.ring-3 {
  width: 140px;
  height: 140px;
  animation: ringRotate1 5s linear infinite;
  border-color: rgba(95, 222, 100, 0.3);
}

@keyframes ringRotate1 {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes ringRotate2 {
  0% { transform: translate(-50%, -50%) rotate(360deg); }
  100% { transform: translate(-50%, -50%) rotate(0deg); }
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.stat-alerts {
  border-left: 4px solid #ff6b6b;
}

.stat-history {
  border-left: 4px solid #667eea;
}

.stat-subscriptions {
  border-left: 4px solid #00C9FF;
}

.stat-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

.stat-label {
  margin-top: 4px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.stat-sub {
  margin-top: 2px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.stat-trend {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
}

.stat-trend.positive {
  color: #95de64;
  background: rgba(149, 222, 100, 0.1);
}

.stat-trend.neutral {
  color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 24px 0;
  font-size: 20px;
  color: #fff;
  font-weight: 600;
}

.title-icon {
  font-size: 20px;
}

.quick-actions {
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.action-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.action-card:hover {
  transform: translateX(10px);
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.action-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 16px;
  flex-shrink: 0;
}

.action-icon-wrapper.blue {
  background: linear-gradient(135deg, rgba(0, 201, 255, 0.2), rgba(0, 201, 255, 0.05));
}

.action-icon-wrapper.purple {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(102, 126, 234, 0.05));
}

.action-icon-wrapper.orange {
  background: linear-gradient(135deg, rgba(255, 197, 61, 0.2), rgba(255, 197, 61, 0.05));
}

.action-icon {
  font-size: 32px;
}

.action-info {
  flex: 1;
}

.action-info h4 {
  margin: 0;
  font-size: 18px;
  color: #fff;
}

.action-info p {
  margin: 6px 0 0 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.action-arrow {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.action-card:hover .action-arrow {
  color: #fff;
  transform: translateX(5px);
}

.tips-section {
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.tip-card {
  padding: 24px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.tip-card:hover {
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-3px);
}

.tip-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.tip-card h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #fff;
}

.tip-card p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.6;
}

.footer {
  text-align: center;
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
  z-index: 1;
}

.footer p {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  border-color: rgba(102, 126, 234, 0.4);
}

.stat-arrow {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.stat-card.clickable:hover .stat-arrow {
  color: #fff;
  transform: translateX(5px);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  width: 90%;
  max-width: 1000px;
  max-height: 85vh;
  background: rgba(12, 25, 41, 0.95);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  animation: slideUp 0.3s ease;
  overflow: hidden;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 24px;
}

.close-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 107, 107, 0.3);
  color: #ff6b6b;
}

.modal-body {
  padding: 24px;
  max-height: calc(85vh - 140px);
  overflow-y: auto;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: rgba(255, 255, 255, 0.5);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0;
  font-size: 16px;
}

.details-table-container {
  margin-top: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.modern-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 700px;
}

.modern-table th {
  color: #667eea;
  background: rgba(12, 25, 41, 0.95);
  padding: 12px 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.modern-table td {
  padding: 12px 16px;
  color: rgba(255, 255, 255, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 14px;
}

.table-row:hover td {
  background: rgba(255, 255, 255, 0.03);
}

.table-row.unread td {
  background: rgba(102, 126, 234, 0.05);
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.unread {
  background: #667eea;
  box-shadow: 0 0 8px rgba(102, 126, 234, 0.5);
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
  color: #667eea;
  border-radius: 20px;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-danger {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.3);
}

.btn-danger:hover {
  background: rgba(255, 107, 107, 0.3);
}

.btn-small {
  padding: 6px 12px;
  font-size: 12px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: rgba(255, 255, 255, 0.5);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.stat-card.stat-alerts.clickable:hover {
  border-color: rgba(255, 107, 107, 0.4);
}

.alert-record-modal .modern-table th {
  color: #ff6b6b;
}

.alert-record-modal .table-row.unread td {
  background: rgba(255, 107, 107, 0.05);
}

.alert-record-modal .status-dot.unread {
  background: #ff6b6b;
  box-shadow: 0 0 8px rgba(255, 107, 107, 0.5);
}

.alert-record-modal .task-id {
  color: #ff6b6b;
}

.alert-record-modal .intensity-badge {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.alert-record-modal .btn-primary {
  background: linear-gradient(135deg, #ff6b6b, #ee5253);
}

.alert-record-modal .btn-primary:hover {
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.alert-record-modal .loading-spinner {
  border-top-color: #ff6b6b;
}

.analysis-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  width: 100%;
  max-width: none;
  margin: 0 auto 32px;
  padding: 0 24px;
  position: relative;
  z-index: 1;
  box-sizing: border-box;
}

.analysis-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 28px;
  transition: all 0.3s ease;
}

.analysis-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.card-title {
  font-size: 18px;
  margin: 0;
  color: #fff;
  font-weight: 600;
}

.knowledge-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.knowledge-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.knowledge-item:hover {
  background: rgba(255, 255, 255, 0.06);
}

.knowledge-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.knowledge-info {
  flex: 1;
}

.knowledge-info h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #fff;
  font-weight: 600;
}

.knowledge-info p {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  line-height: 1.5;
}

.system-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: rgba(50, 205, 50, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(50, 205, 50, 0.2);
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.status-dot.green {
  background: #32cd32;
  box-shadow: 0 0 8px rgba(50, 205, 50, 0.5);
}

.system-status span {
  font-size: 14px;
  color: #32cd32;
  font-weight: 600;
}

.system-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

.system-icon {
  font-size: 22px;
  flex-shrink: 0;
}

.system-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.system-value {
  font-size: 14px;
  color: #fff;
  font-weight: 600;
}

.system-update {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.update-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.update-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.update-title {
  font-size: 12px;
  color: rgba(102, 126, 234, 0.8);
  font-weight: 600;
}

.update-content {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}
</style>
