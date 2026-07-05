<template>
  <div class="alert-report">
    <div class="bg-animation"></div>
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>

    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
        <span>返回首页</span>
      </button>
      <h2>🌪 预警报告管理</h2>
    </div>

    <div class="stats-section">
      <div class="stat-card stat-alerts">
        <div class="stat-icon-wrap">
          <span class="stat-icon">🔔</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ reports.length }}</div>
          <div class="stat-label">预警记录</div>
          <div class="stat-sub">未读 {{ unreadCount }} 条</div>
        </div>
        <div class="stat-trend" :class="unreadCount > 0 ? 'positive' : 'neutral'">
          {{ unreadCount > 0 ? '⚠' : '✓' }}
        </div>
      </div>

      <div class="stat-card stat-subscriptions">
        <div class="stat-icon-wrap">
          <span class="stat-icon">📍</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ subscriptions.length }}</div>
          <div class="stat-label">已订阅位置</div>
          <div class="stat-sub">{{ activeSubscriptionCount }} 个启用中</div>
        </div>
        <div class="stat-trend neutral">—</div>
      </div>

      <div class="stat-card stat-risk">
        <div class="stat-icon-wrap">
          <span class="stat-icon">🌪</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ highRiskCount }}</div>
          <div class="stat-label">高风险预警</div>
          <div class="stat-sub">需要关注</div>
        </div>
        <div class="stat-trend" :class="highRiskCount > 0 ? 'danger' : 'positive'">
          {{ highRiskCount > 0 ? '!' : '-' }}
        </div>
      </div>

      <div class="stat-card stat-recent">
        <div class="stat-icon-wrap">
          <span class="stat-icon">⏱</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ recentHours }}</div>
          <div class="stat-label">最近预警</div>
          <div class="stat-sub">{{ recentHours > 0 ? `${recentHours}小时前` : '刚刚' }}</div>
        </div>
        <div class="stat-trend neutral">—</div>
      </div>
    </div>

    <div class="main-content">
      <div class="left-panel">
        <div class="panel-card clickable-card" @click="openAddSubscriptionModal">
          <div class="card-header">
            <h3 class="card-title">➕ 添加预警位置</h3>
          </div>
          <div class="add-card-content">
            <div class="add-circle">
              <span class="add-symbol">+</span>
            </div>
            <div class="add-info">
              <p class="add-title">订阅新位置</p>
              <p class="add-desc">选择城市或输入自定义坐标，设置预警时间范围</p>
            </div>
          </div>
        </div>

        <div class="panel-card">
          <div class="card-header">
            <h3 class="card-title">📍 已订阅列表</h3>
            <button class="btn btn-small" @click="openSubscriptionModal">查看全部</button>
          </div>
          <div v-if="subscriptions.length === 0" class="empty-state">
            <span class="empty-icon">📌</span>
            <p>暂无订阅位置</p>
            <button class="btn btn-primary" @click="openAddSubscriptionModal">立即添加</button>
          </div>
          <div v-else class="subscription-list">
            <div v-for="sub in subscriptions.slice(0, 4)" :key="sub.id" class="subscription-item">
              <div class="sub-left">
                <span class="location-icon">📍</span>
                <div class="sub-info">
                  <span class="sub-city">{{ sub.cityName || '自定义位置' }}</span>
                  <span class="sub-coords">{{ sub.latitude?.toFixed(2) }}, {{ sub.longitude?.toFixed(2) }}</span>
                </div>
              </div>
              <span class="status-badge" :class="sub.status === 1 ? 'status-active' : 'status-inactive'">
                {{ sub.status === 1 ? '已启用' : '已停用' }}
              </span>
            </div>
            <div v-if="subscriptions.length > 4" class="more-hint clickable" @click="openSubscriptionModal">
              查看全部 {{ subscriptions.length }} 个订阅 →
            </div>
          </div>
        </div>

        </div>

      <div class="right-panel">
        <div class="panel-card">
          <div class="card-header">
            <h3 class="card-title">🔔 预警记录</h3>
            <div class="header-actions">
              <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }} 条未读</span>
              <button class="btn btn-small" @click="openReportModal">查看全部</button>
            </div>
          </div>
          <div v-if="reports.length === 0" class="empty-state">
            <span class="empty-icon">🔕</span>
            <p>暂无预警记录</p>
            <p class="empty-sub">订阅位置后，当台风接近时会收到预警通知</p>
          </div>
          <div v-else class="report-list">
            <div v-for="report in reports.slice(0, 5)" :key="report.id" class="report-item" :class="{ unread: report.status === 0 }" @click="openReportModal">
              <div class="report-left">
                <span class="level-badge level-{{ report.level }}">{{ report.level }}级</span>
                <div class="report-info">
                  <div class="report-city">{{ report.cityName || '未知位置' }}</div>
                  <div class="report-meta">
                    <span>距离: {{ report.distance }} km</span>
                    <span class="report-time">{{ formatDateTime(report.createTime) }}</span>
                  </div>
                </div>
              </div>
              <span class="status-dot" :class="report.status === 0 ? 'unread' : 'read'"></span>
            </div>
            <div v-if="reports.length > 5" class="more-hint clickable" @click="openReportModal">
              查看全部 {{ reports.length }} 条记录 →
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预警详情弹窗 -->
    <div v-if="selectedReport" class="modal-overlay modal-overlay-top" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <span class="level-badge level-{{ selectedReport.level }}">{{ selectedReport.level }}级风圈预警</span>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="modal-info">
            <span>目标位置: {{ selectedReport.cityName }}</span>
            <span>距离: {{ selectedReport.distance }} km</span>
            <span v-if="selectedReport.triggerTime">警告时间: {{ formatWarningTime(selectedReport.triggerTime) }}</span>
            <span v-if="selectedReport.triggerTime">到达时间: {{ formatDateTime(selectedReport.triggerTime) }}</span>
          </div>
          <div class="message-content">
            <pre>{{ selectedReport.message }}</pre>
          </div>
        </div>
        <div class="modal-footer">
          <div class="modal-footer-left">
            <button class="btn btn-danger" @click="deleteReport(selectedReport.id); closeModal()">删除</button>
          </div>
          <div class="modal-footer-right">
            <button class="btn btn-primary btn-analysis" @click="goAnalysis(selectedReport)">详细分析</button>
            <button v-if="selectedReport.status === 0" class="btn btn-primary" 
                    @click="markAsRead(selectedReport.id)">标记为已读</button>
            <button class="btn" @click="closeModal">关闭</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改订阅弹窗 -->
    <div v-if="editModalVisible" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>修改预警订阅</h3>
          <button class="close-btn" @click="closeEditModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>选择城市</label>
            <select v-model="editForm.cityName" @change="onEditCityChange" class="city-select">
              <option value="">请选择城市</option>
              <option v-for="city in cities" :key="city.name" :value="city.name">
                {{ city.name }}
              </option>
              <option value="custom">自定义位置</option>
            </select>
          </div>

          <div v-if="editForm.cityName === 'custom'" class="custom-location">
            <div class="form-row">
              <div class="form-group half">
                <label>纬度</label>
                <input type="number" v-model="editForm.latitude" step="0.0001" />
              </div>
              <div class="form-group half">
                <label>经度</label>
                <input type="number" v-model="editForm.longitude" step="0.0001" />
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>时间范围</label>
            <div class="form-row">
              <div class="form-group half">
                <label class="small-label">开始时间</label>
                <input type="datetime-local" v-model="editForm.startTime" />
              </div>
              <div class="form-group half">
                <label class="small-label">结束时间</label>
                <input type="datetime-local" v-model="editForm.endTime" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveEdit">保存</button>
          <button class="btn" @click="closeEditModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 订阅列表弹窗 -->
    <SubscriptionModal
      :visible="subscriptionModalVisible"
      :subscriptions="subscriptions"
      @close="closeSubscriptionModal"
      @toggle="toggleSubscription"
      @edit="openEditModal"
      @delete="deleteSubscription"
    />

    <!-- 预警记录弹窗 -->
    <ReportModal
      :visible="reportModalVisible"
      :reports="reports"
      :unread-count="unreadCount"
      :is-batch-mode="isBatchMode"
      :selected-reports="selectedReports"
      @close="closeReportModal"
      @enter-batch-mode="enterBatchMode"
      @exit-batch-mode="exitBatchMode"
      @batch-delete="batchDelete"
      @toggle-select-all="toggleSelectAll"
      @toggle-select="toggleSelect"
      @go-analysis="goAnalysis"
      @view-report="viewReport"
      @delete-report="deleteReport"
    />

    <!-- 添加订阅弹窗 -->
    <AddSubscriptionModal
      :visible="addSubscriptionModalVisible"
      :cities="cities"
      @close="closeAddSubscriptionModal"
      @add="addSubscription"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import SubscriptionModal from '@/components/SubscriptionModal.vue';
import ReportModal from '@/components/ReportModal.vue';
import AddSubscriptionModal from '@/components/AddSubscriptionModal.vue';

const router = useRouter();
const cities = ref<any[]>([]);
const subscriptions = ref<any[]>([]);
const reports = ref<any[]>([]);
const unreadCount = ref(0);
const selectedReport = ref<any>(null);

const subscriptionModalVisible = ref(false);
const reportModalVisible = ref(false);
const addSubscriptionModalVisible = ref(false);

const activeSubscriptionCount = computed(() => {
  return subscriptions.value.filter(s => s.status === 1).length;
});

const highRiskCount = computed(() => {
  return reports.value.filter(r => r.level >= 3).length;
});

const recentHours = computed(() => {
  if (reports.value.length === 0) return 0;
  const latest = new Date(Math.max(...reports.value.map(r => new Date(r.createTime).getTime())));
  const now = new Date();
  const diffHours = Math.floor((now.getTime() - latest.getTime()) / (1000 * 60 * 60));
  return diffHours;
});

// 修改订阅相关
const editModalVisible = ref(false);
const editForm = ref({
  id: null as number | null,
  cityName: '',
  latitude: 0,
  longitude: 0,
  startTime: '',
  endTime: ''
});

// 批量删除相关
const isBatchMode = ref(false);
const selectedReports = ref<number[]>([]);

const userId = 1; // 暂固定用户ID

onMounted(() => {
  loadCities();
  loadSubscriptions();
  loadReports();
});

// 进入批量管理模式
function enterBatchMode() {
  isBatchMode.value = true;
  selectedReports.value = [];
}

// 退出批量管理模式
function exitBatchMode() {
  isBatchMode.value = false;
  selectedReports.value = [];
}

// 打开订阅列表弹窗
function openSubscriptionModal() {
  subscriptionModalVisible.value = true;
}

// 关闭订阅列表弹窗
function closeSubscriptionModal() {
  subscriptionModalVisible.value = false;
}

// 打开预警记录弹窗
function openReportModal() {
  reportModalVisible.value = true;
}

// 关闭预警记录弹窗
function closeReportModal() {
  reportModalVisible.value = false;
}

// 打开添加订阅弹窗
function openAddSubscriptionModal() {
  addSubscriptionModalVisible.value = true;
}

// 关闭添加订阅弹窗
function closeAddSubscriptionModal() {
  addSubscriptionModalVisible.value = false;
}

// 全选/取消全选
function toggleSelectAll() {
  if (selectedReports.value.length === reports.value.length) {
    selectedReports.value = [];
  } else {
    selectedReports.value = reports.value.map(r => r.id);
  }
}

// 切换单个选中
function toggleSelect(id: number) {
  const index = selectedReports.value.indexOf(id);
  if (index > -1) {
    selectedReports.value.splice(index, 1);
  } else {
    selectedReports.value.push(id);
  }
}

// 批量删除
async function batchDelete() {
  if (selectedReports.value.length === 0) return;
  if (!confirm(`确定要删除选中的 ${selectedReports.value.length} 条预警记录吗？`)) return;
  
  try {
    await axios.delete('http://localhost:8080/api/alert/report/batch', {
      data: { ids: selectedReports.value }
    });
    exitBatchMode();
    loadReports();
  } catch (error) {
    console.error('批量删除失败', error);
    alert('批量删除失败');
  }
}

async function loadCities() {
  try {
    const res = await axios.get('http://localhost:8080/api/alert/cities');
    cities.value = res.data.data;
  } catch (error) {
    console.error('加载城市列表失败', error);
  }
}

async function loadSubscriptions() {
  try {
    const res = await axios.get(`http://localhost:8080/api/alert/subscription/${userId}`);
    subscriptions.value = res.data.data;
  } catch (error) {
    console.error('加载订阅列表失败', error);
  }
}

async function loadReports() {
  try {
    const res = await axios.get(`http://localhost:8080/api/alert/report/${userId}`);
    reports.value = res.data.data;
    
    const countRes = await axios.get(`http://localhost:8080/api/alert/report/${userId}/unread`);
    unreadCount.value = countRes.data.data.unreadCount;
  } catch (error) {
    console.error('加载预警报告失败', error);
  }
}

async function addSubscription(data: { cityName: string | null; latitude: number; longitude: number; startTime: string; endTime: string }) {
  try {
    const payload = {
      cityName: data.cityName,
      latitude: data.latitude,
      longitude: data.longitude,
      startTime: data.startTime,
      endTime: data.endTime
    };
    
    await axios.post('http://localhost:8080/api/alert/subscription', payload);
    alert('订阅成功');
    loadSubscriptions();
    closeAddSubscriptionModal();
  } catch (error) {
    console.error('添加订阅失败', error);
    alert('添加订阅失败');
  }
}

async function toggleSubscription(id: number, status: number) {
  try {
    await axios.put(`http://localhost:8080/api/alert/subscription/${id}/status`, null, {
      params: { status: status === 1 ? 0 : 1 }
    });
    loadSubscriptions();
  } catch (error) {
    console.error('更新订阅状态失败', error);
  }
}

async function deleteSubscription(id: number) {
  if (!confirm('确定要删除这个订阅吗？')) return;
  
  try {
    await axios.delete(`http://localhost:8080/api/alert/subscription/${id}`);
    loadSubscriptions();
  } catch (error) {
    console.error('删除订阅失败', error);
  }
}

// 打开修改弹窗
function openEditModal(sub: any) {
  editForm.value = {
    id: sub.id,
    cityName: sub.cityName || 'custom',
    latitude: sub.latitude,
    longitude: sub.longitude,
    startTime: formatDateTimeToInput(sub.startTime),
    endTime: formatDateTimeToInput(sub.endTime)
  };
  editModalVisible.value = true;
}

// 关闭修改弹窗
function closeEditModal() {
  editModalVisible.value = false;
}

// 保存修改
async function saveEdit() {
  try {
    const payload = {
      cityName: editForm.value.cityName === 'custom' ? null : editForm.value.cityName,
      latitude: editForm.value.latitude,
      longitude: editForm.value.longitude,
      startTime: new Date(editForm.value.startTime).toISOString().slice(0, 19).replace('T', ' '),
      endTime: new Date(editForm.value.endTime).toISOString().slice(0, 19).replace('T', ' ')
    };
    
    await axios.put(`http://localhost:8080/api/alert/subscription/${editForm.value.id}`, payload);
    alert('修改成功');
    closeEditModal();
    loadSubscriptions();
  } catch (error) {
    console.error('修改订阅失败', error);
    alert('修改订阅失败');
  }
}

// 城市选择变化
function onEditCityChange() {
  if (editForm.value.cityName && editForm.value.cityName !== 'custom') {
    const city = cities.value.find(c => c.name === editForm.value.cityName);
    if (city) {
      editForm.value.latitude = city.latitude;
      editForm.value.longitude = city.longitude;
    }
  }
}

// 格式化日期时间为input datetime-local格式
function formatDateTimeToInput(dateStr: string): string {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function viewReport(report: any) {
  selectedReport.value = report;
}

async function markAsRead(id: number) {
  try {
    await axios.put(`http://localhost:8080/api/alert/report/${id}/read`);
    selectedReport.value.status = 1;
    unreadCount.value--;
  } catch (error) {
    console.error('标记已读失败', error);
  }
}

async function deleteReport(id: number) {
  if (!confirm('确定要删除这条预警记录吗？')) return;
  
  try {
    await axios.delete(`http://localhost:8080/api/alert/report/${id}`);
    loadReports();
  } catch (error) {
    console.error('删除预警记录失败', error);
    alert('删除失败');
  }
}

function closeModal() {
  selectedReport.value = null;
}

// 警告时间 = 触发时间 - 24小时
function formatWarningTime(dateStr: string) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  date.setHours(date.getHours() - 24);
  return date.toLocaleString();
}

function formatDateTime(dateStr: string) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString();
}

function goBack() {
  router.push('/home');
}

function goAnalysis(report: any) {
  console.log('goAnalysis report:', report);
  sessionStorage.setItem('currentAnalysis', JSON.stringify(report));
  router.push(`/analysis/${report.id}`);
}
</script>

<style scoped>
.alert-report {
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
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(-3px);
}

.back-icon {
  font-size: 16px;
}

.page-header h2 {
  margin: 0;
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto 32px;
  position: relative;
  z-index: 1;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.stat-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-alerts .stat-icon-wrap {
  background: rgba(255, 107, 107, 0.15);
}

.stat-subscriptions .stat-icon-wrap {
  background: rgba(102, 126, 234, 0.15);
}

.stat-risk .stat-icon-wrap {
  background: rgba(255, 197, 61, 0.15);
}

.stat-recent .stat-icon-wrap {
  background: rgba(0, 201, 255, 0.15);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 2px;
}

.stat-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.stat-trend {
  font-size: 20px;
  font-weight: 700;
}

.stat-trend.positive {
  color: #32cd32;
}

.stat-trend.danger {
  color: #ff6b6b;
}

.stat-trend.neutral {
  color: rgba(255, 255, 255, 0.3);
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.left-panel, .right-panel {
  display: flex;
  flex-direction: column;
}

.left-panel {
  gap: 24px;
}

.right-panel {
  min-height: 100%;
}

.panel-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 28px;
  margin-bottom: 24px;
  transition: all 0.3s ease;
}

.right-panel .panel-card {
  flex-grow: 1;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
}

.panel-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.clickable-card {
  cursor: pointer;
}

.clickable-card:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.add-icon {
  font-size: 48px;
  color: rgba(255, 255, 255, 0.1);
  text-align: center;
  margin-top: 16px;
  transition: all 0.3s ease;
}

.clickable-card:hover .add-icon {
  color: rgba(102, 126, 234, 0.5);
  transform: scale(1.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.summary-count {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
}

.summary-hint {
  font-size: 13px;
  color: rgba(102, 126, 234, 0.8);
  transition: all 0.3s ease;
}

.clickable-card:hover .summary-hint {
  transform: translateX(5px);
  color: #667eea;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-dot.active {
  background: #32cd32;
  box-shadow: 0 0 8px rgba(50, 205, 50, 0.5);
}

.status-dot.inactive {
  background: rgba(255, 255, 255, 0.3);
}

.more-hint {
  padding: 12px;
  text-align: center;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: 8px;
}

.card-title {
  font-size: 20px;
  margin: 0 0 20px 0;
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.unread-badge {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid rgba(255, 107, 107, 0.3);
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 8px;
  font-weight: 500;
}

.small-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group.half {
  flex: 1;
}

input, select {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  transition: all 0.3s ease;
}

input:focus, select:focus {
  outline: none;
  border-color: rgba(102, 126, 234, 0.6);
  background: rgba(255, 255, 255, 0.1);
}

input::placeholder, select::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.city-select {
  padding: 14px;
}

.btn {
  padding: 12px 24px;
  border-radius: 12px;
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
}

.btn-primary:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-warning {
  background: rgba(255, 197, 61, 0.2);
  color: #ffc53d;
  border: 1px solid rgba(255, 197, 61, 0.4);
}

.btn-warning:hover {
  background: rgba(255, 197, 61, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 197, 61, 0.3);
}

.btn-danger {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.4);
}

.btn-danger:hover {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.8), rgba(238, 82, 83, 0.8));
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.btn-edit {
  background: rgba(0, 201, 255, 0.1);
  color: #00C9FF;
  border: 1px solid rgba(0, 201, 255, 0.3);
}

.btn-edit:hover {
  background: rgba(0, 201, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 201, 255, 0.3);
}

.btn-analysis {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
}

.btn-analysis:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
}

.btn-small {
  padding: 8px 16px;
  font-size: 13px;
}

.empty-state {
  padding: 60px 40px;
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
}

.empty-state p {
  font-size: 15px;
}

.subscription-list {
  max-height: 350px;
  overflow-y: auto;
}

.subscription-item {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.subscription-item:last-child {
  border-bottom: none;
}

.subscription-item:hover {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  margin: 0 -8px;
  padding: 16px 24px;
}

.sub-info {
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
}

.sub-city {
  font-weight: 600;
  color: #fff;
  font-size: 16px;
}

.sub-coord {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.sub-time {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 12px;
}

.sub-actions {
  display: flex;
  gap: 10px;
}

.report-list {
  flex-grow: 1;
  overflow-y: auto;
  min-height: 0;
}

.report-list::-webkit-scrollbar,
.subscription-list::-webkit-scrollbar {
  width: 6px;
}

.report-list::-webkit-scrollbar-track,
.subscription-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.report-list::-webkit-scrollbar-thumb,
.subscription-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.report-list::-webkit-scrollbar-thumb:hover,
.subscription-list::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

.batch-select-all {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  margin-bottom: 16px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.7);
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.report-item {
  padding: 18px;
  padding-bottom: 48px;
  padding-left: 48px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.report-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.1);
}

.report-item.unread {
  background: rgba(255, 197, 61, 0.1);
  border-left: 4px solid #ffc53d;
}

.report-item.selected {
  background: rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(102, 126, 234, 0.4);
}

.report-checkbox {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
}

.report-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

.report-content {
  /* padding-left 由 report-item 提供 */
}

.report-actions {
  position: absolute;
  right: 18px;
  bottom: 18px;
  display: flex;
  gap: 16px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.level-badge {
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.level-badge.level-7 {
  background: rgba(50, 205, 50, 0.2);
  color: #32cd32;
  border: 1px solid rgba(50, 205, 50, 0.3);
}

.level-badge.level-10 {
  background: rgba(255, 197, 61, 0.2);
  color: #ffc53d;
  border: 1px solid rgba(255, 197, 61, 0.3);
}

.level-badge.level-12 {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.3);
}

.report-time {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.report-city {
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #fff;
}

.report-preview {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 6px;
  line-height: 1.5;
}

.report-meta {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

/* 弹窗样式 */
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

.modal-overlay-top {
  z-index: 2000;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  width: 550px;
  max-height: 85vh;
  overflow: hidden;
  animation: slideUp 0.3s ease;
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

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  font-size: 28px;
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.modal-body {
  padding: 24px;
  max-height: 450px;
  overflow-y: auto;
}

.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.modal-body::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.modal-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.message-content {
  white-space: pre-wrap;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.9);
}

.message-content pre {
  margin: 0;
  white-space: pre-wrap;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-footer-left {
  display: flex;
  gap: 12px;
}

.modal-footer-right {
  display: flex;
  gap: 12px;
}

.add-card-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.add-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
  border: 2px dashed rgba(102, 126, 234, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.clickable-card:hover .add-circle {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.5);
  transform: scale(1.1);
}

.add-symbol {
  font-size: 32px;
  color: rgba(102, 126, 234, 0.6);
  font-weight: 300;
}

.clickable-card:hover .add-symbol {
  color: rgba(102, 126, 234, 1);
}

.add-info {
  flex: 1;
}

.add-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px 0;
}

.add-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.sub-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.location-icon {
  font-size: 18px;
}

.sub-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sub-coords {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-active {
  background: rgba(50, 205, 50, 0.2);
  color: #32cd32;
  border: 1px solid rgba(50, 205, 50, 0.3);
}

.status-inactive {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-sub {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 8px;
}

.more-hint.clickable {
  cursor: pointer;
  color: rgba(102, 126, 234, 0.8);
}

.more-hint.clickable:hover {
  color: #667eea;
}

.level-distribution {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.level-bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.level-bar-label {
  width: 40px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.level-bar-track {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 4px;
  overflow: hidden;
}

.level-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.level-bar-fill.level-1 {
  background: linear-gradient(90deg, #95de64, #32cd32);
}

.level-bar-fill.level-2 {
  background: linear-gradient(90deg, #ffc107, #ff9800);
}

.level-bar-fill.level-3 {
  background: linear-gradient(90deg, #ff9800, #ff6b6b);
}

.level-bar-fill.level-4 {
  background: linear-gradient(90deg, #ff6b6b, #e91e63);
}

.level-bar-fill.level-5 {
  background: linear-gradient(90deg, #e91e63, #9c27b0);
}

.level-bar-count {
  width: 30px;
  text-align: right;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.trend-chart {
  padding: 20px 0;
}

.chart-bars {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 150px;
  padding: 0 10px;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.chart-bar {
  width: 32px;
  background: rgba(102, 126, 234, 0.3);
  border-radius: 6px 6px 0 0;
  transition: all 0.3s ease;
  min-height: 4px;
}

.chart-bar.current {
  background: linear-gradient(180deg, #667eea, #764ba2);
}

.chart-bar:hover {
  background: rgba(102, 126, 234, 0.6);
}

.chart-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.chart-legend {
  display: flex;
  justify-content: center;
  margin-top: 16px;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.legend-dot {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
}

.report-left {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
}

.report-info {
  flex: 1;
}

.report-item .report-header {
  margin-bottom: 8px;
}

.report-item .report-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.report-item.unread {
  background: rgba(255, 107, 107, 0.05);
  border-color: rgba(255, 107, 107, 0.15);
}

.report-item.unread .report-city {
  color: #ff6b6b;
}
</style>
