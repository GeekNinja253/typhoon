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
import ReportModal from '../../components/WarningBroadcast/ReportModal.vue';
import SubscriptionModal from '../../components/WarningBroadcast/SubscriptionModal.vue';

import '@/styles/home.css'

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

</style>
