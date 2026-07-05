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

    <div class="main-content">
      <!-- 左侧：添加预警位置 -->
      <div class="left-panel">
        <div class="panel-card">
          <h3 class="card-title">添加预警位置</h3>
          
          <div class="form-group">
            <label>选择城市</label>
            <select v-model="formData.cityName" @change="onCityChange" class="city-select">
              <option value="">请选择城市</option>
              <option v-for="city in cities" :key="city.name" :value="city.name">
                {{ city.name }}
              </option>
              <option value="custom">自定义位置</option>
            </select>
          </div>

          <div v-if="formData.cityName === 'custom'" class="custom-location">
            <div class="form-row">
              <div class="form-group half">
                <label>纬度</label>
                <input type="number" v-model="formData.latitude" step="0.0001" />
              </div>
              <div class="form-group half">
                <label>经度</label>
                <input type="number" v-model="formData.longitude" step="0.0001" />
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>时间范围</label>
            <div class="form-row">
              <div class="form-group half">
                <label class="small-label">开始时间</label>
                <input type="datetime-local" v-model="formData.startTime" />
              </div>
              <div class="form-group half">
                <label class="small-label">结束时间</label>
                <input type="datetime-local" v-model="formData.endTime" />
              </div>
            </div>
          </div>

          <button class="btn btn-primary" @click="addSubscription">添加预警订阅</button>
        </div>

        <!-- 已订阅列表 -->
        <div class="panel-card clickable-card" @click="openSubscriptionModal">
          <h3 class="card-title">已订阅列表</h3>
          <div class="card-summary">
            <span class="summary-count">{{ subscriptions.length }} 个订阅</span>
            <span class="summary-hint">点击查看详情 →</span>
          </div>
          <div v-if="subscriptions.length === 0" class="empty-state">
            <p>暂无订阅</p>
          </div>
          <div v-else class="subscription-list">
            <div v-for="sub in subscriptions.slice(0, 3)" :key="sub.id" class="subscription-item">
              <div class="sub-info">
                <span class="sub-city">{{ sub.cityName || '自定义位置' }}</span>
                <span class="status-dot" :class="sub.status === 1 ? 'active' : 'inactive'"></span>
              </div>
              <div class="sub-time">{{ formatDateTime(sub.startTime) }} ~ {{ formatDateTime(sub.endTime) }}</div>
            </div>
            <div v-if="subscriptions.length > 3" class="more-hint">
              还有 {{ subscriptions.length - 3 }} 个订阅...
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：预警记录 -->
      <div class="right-panel">
        <div class="panel-card clickable-card" @click="openReportModal">
          <div class="card-header">
            <h3 class="card-title">预警记录</h3>
            <div class="header-actions">
              <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }} 条未读</span>
            </div>
          </div>
          <div class="card-summary">
            <span class="summary-count">{{ reports.length }} 条记录</span>
            <span class="summary-hint">点击查看详情 →</span>
          </div>
          <div v-if="reports.length === 0" class="empty-state">
            <p>暂无预警记录</p>
          </div>
          <div v-else class="report-list">
            <div v-for="report in reports.slice(0, 3)" :key="report.id" class="report-item">
              <div class="report-content">
                <div class="report-header">
                  <span class="level-badge level-{{ report.level }}">{{ report.level }}级</span>
                  <span class="report-time">{{ formatDateTime(report.createTime) }}</span>
                </div>
                <div class="report-city">{{ report.cityName || '未知位置' }}</div>
                <div class="report-meta">
                  <span>距离: {{ report.distance }} km</span>
                  <span class="status-dot" :class="report.status === 0 ? 'unread' : 'read'"></span>
                </div>
              </div>
            </div>
            <div v-if="reports.length > 3" class="more-hint">
              还有 {{ reports.length - 3 }} 条记录...
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import SubscriptionModal from '@/components/SubscriptionModal.vue';
import ReportModal from '@/components/ReportModal.vue';

const router = useRouter();
const cities = ref<any[]>([]);
const subscriptions = ref<any[]>([]);
const reports = ref<any[]>([]);
const unreadCount = ref(0);
const selectedReport = ref<any>(null);

const subscriptionModalVisible = ref(false);
const reportModalVisible = ref(false);

const formData = ref({
  cityName: '',
  latitude: 22.5431,
  longitude: 114.0579,
  startTime: '',
  endTime: ''
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

function onCityChange() {
  if (formData.value.cityName && formData.value.cityName !== 'custom') {
    const city = cities.value.find(c => c.name === formData.value.cityName);
    if (city) {
      formData.value.latitude = city.latitude;
      formData.value.longitude = city.longitude;
    }
  }
}

async function addSubscription() {
  try {
    // 表单验证
    if (!formData.value.cityName && (!formData.value.latitude || !formData.value.longitude)) {
      alert('请选择城市或填写自定义位置');
      return;
    }
    
    if (!formData.value.startTime) {
      alert('请选择开始时间');
      return;
    }
    
    if (!formData.value.endTime) {
      alert('请选择结束时间');
      return;
    }
    
    const payload = {
      cityName: formData.value.cityName === 'custom' ? null : formData.value.cityName,
      latitude: formData.value.latitude,
      longitude: formData.value.longitude,
      startTime: new Date(formData.value.startTime).toISOString().slice(0, 19).replace('T', ' '),
      endTime: new Date(formData.value.endTime).toISOString().slice(0, 19).replace('T', ' ')
    };
    
    await axios.post('http://localhost:8080/api/alert/subscription', payload);
    alert('订阅成功');
    loadSubscriptions();
    
    // 重置表单
    formData.value = {
      cityName: '',
      latitude: 22.5431,
      longitude: 114.0579,
      startTime: '',
      endTime: ''
    };
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

.main-content {
  display: flex;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.left-panel, .right-panel {
  flex: 1;
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

.card-summary {
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
  max-height: 550px;
  overflow-y: auto;
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
</style>
