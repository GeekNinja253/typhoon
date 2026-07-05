<template>
  <div class="home-page">
    <div class="bg-animation"></div>
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>
    <div class="bg-blob blob-3"></div>

    <HomeHeader
        :username="username"
        @logout="handleLogout"
    />

    <WelcomeSection
        :username="username"
        :currentTime="currentTime"
    />

    <StatsSection
      :stats="stats"
      @openAlertRecordModal="openAlertRecordModal"
      @openAlertModal="openAlertModal"
      @openSubscriptionModal="openSubscriptionModal"
    />

    <QuickActions />

    <AnalysisSection />

    <TipsSection />

    <div class="footer">
      <p>© 2026 台风路径分析系统 | 基于机器学习的智能预测平台</p>
    </div>

    <HistoryModal
      :visible="showAlertModal"
      :alert-tasks="alertTasks"
      :alert-read-tasks="alertReadTasks"
      :format-date-time="formatDateTime"
      @close="closeAlertModal"
      @view-task="viewTaskDetails"
      @delete-task="deleteTask"
    />

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

    <TaskDetailModal
      :visible="showTaskModal"
      :task-id="selectedTaskId"
      :results="selectedTaskResults"
      :loading="loadingTaskDetails"
      @close="closeTaskModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import ReportModal from '../../components/WarningBroadcast/ReportModal.vue';
import SubscriptionModal from '../../components/WarningBroadcast/SubscriptionModal.vue';
import HomeHeader from '../../components/Home/HomeHeader.vue';
import WelcomeSection from '../../components/Home/WelcomeSection.vue';
import StatsSection from '../../components/Home/StatsSection.vue';
import QuickActions from '../../components/Home/QuickActions.vue';
import AnalysisSection from '../../components/Home/AnalysisSection.vue';
import TipsSection from '../../components/Home/TipsSection.vue';
import HistoryModal from '../../components/Home/HistoryModal.vue';
import TaskDetailModal from '../../components/Home/TaskDetailModal.vue';

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
