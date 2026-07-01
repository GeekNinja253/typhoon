<template>
  <div class="alert-report">
    <div class="page-header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h2>预警报告管理</h2>
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
        <div class="panel-card">
          <h3 class="card-title">已订阅列表</h3>
          <div v-if="subscriptions.length === 0" class="empty-state">
            <p>暂无订阅</p>
          </div>
          <div v-else class="subscription-list">
            <div v-for="sub in subscriptions" :key="sub.id" class="subscription-item">
              <div class="sub-info">
                <span class="sub-city">{{ sub.cityName || '自定义位置' }}</span>
                <span class="sub-coord">{{ sub.latitude?.toFixed(4) }}, {{ sub.longitude?.toFixed(4) }}</span>
              </div>
              <div class="sub-time">{{ formatDateTime(sub.startTime) }} ~ {{ formatDateTime(sub.endTime) }}</div>
              <div class="sub-actions">
                <button class="btn btn-small" :class="sub.status === 1 ? 'btn-warning' : 'btn-primary'"
                        @click="toggleSubscription(sub.id, sub.status)">
                  {{ sub.status === 1 ? '停用' : '启用' }}
                </button>
                <button class="btn btn-small btn-edit" @click="openEditModal(sub)">修改</button>
                <button class="btn btn-small btn-danger" @click="deleteSubscription(sub.id)">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：预警记录 -->
      <div class="right-panel">
        <div class="panel-card">
          <div class="card-header">
            <h3 class="card-title">预警记录</h3>
            <div class="header-actions">
              <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }} 条未读</span>
              <button v-if="!isBatchMode && reports.length > 0" class="btn btn-small" @click="enterBatchMode">批量管理</button>
              <template v-else-if="isBatchMode">
                <button class="btn btn-small" @click="exitBatchMode">取消</button>
                <button class="btn btn-small btn-danger" @click="batchDelete" :disabled="selectedReports.length === 0">
                  批量删除 ({{ selectedReports.length }})
                </button>
              </template>
            </div>
          </div>
          
          <div v-if="reports.length === 0" class="empty-state">
            <p>暂无预警记录</p>
          </div>
          <div v-else class="report-list">
            <div v-if="isBatchMode" class="batch-select-all">
              <label class="checkbox-label">
                <input type="checkbox" :checked="selectedReports.length === reports.length && reports.length > 0" 
                       :indeterminate="selectedReports.length > 0 && selectedReports.length < reports.length"
                       @change="toggleSelectAll" />
                全选
              </label>
            </div>
            <div v-for="report in reports" :key="report.id" 
                 class="report-item" :class="{ unread: report.status === 0, selected: isBatchMode && selectedReports.includes(report.id) }"
                 @click="viewReport(report)">
              <div v-if="isBatchMode" class="report-checkbox">
                <input type="checkbox" :checked="selectedReports.includes(report.id)" 
                       @click.stop="toggleSelect(report.id)" />
              </div>
              <div class="report-content">
                <div class="report-header">
                  <span class="level-badge level-{{ report.level }}">{{ report.level }}级预警</span>
                  <span class="report-time">{{ formatDateTime(report.createTime) }}</span>
                </div>
                <div class="report-city">{{ report.cityName || '未知位置' }}</div>
                <div class="report-preview">{{ report.message.substring(0, 50) }}...</div>
                <div class="report-meta">
                  <span>距离: {{ report.distance }} km</span>
                </div>
              </div>
              <div v-if="!isBatchMode" class="report-actions">
                <button class="btn btn-small btn-analysis" @click.stop="goAnalysis(report)">详细分析</button>
                <button class="btn btn-small btn-danger" @click.stop="deleteReport(report.id)">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预警详情弹窗 -->
    <div v-if="selectedReport" class="modal-overlay" @click="closeModal">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const cities = ref<any[]>([]);
const subscriptions = ref<any[]>([]);
const reports = ref<any[]>([]);
const unreadCount = ref(0);
const selectedReport = ref<any>(null);

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
  background: #f5f5f5;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.back-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.back-btn:hover {
  background: #f5f5f5;
}

.page-header h2 {
  margin: 0;
  color: #1890ff;
}

.main-content {
  display: flex;
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.left-panel, .right-panel {
  flex: 1;
}

.panel-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  margin: 0 0 16px 0;
  color: #333;
  border-left: 3px solid #1890ff;
  padding-left: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.unread-badge {
  background: #ff4d4f;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}

.small-label {
  font-size: 12px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-group.half {
  flex: 1;
}

input, select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
}

.city-select {
  padding: 10px;
}

.btn {
  padding: 10px 20px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-primary {
  background: #1890ff;
  color: white;
}

.btn-primary:hover {
  background: #40a9ff;
  transform: translateY(-1px);
}

.btn-warning {
  background: #faad14;
  color: white;
}

.btn-warning:hover {
  background: #ffc53d;
  transform: translateY(-1px);
}

.btn-danger {
  background: linear-gradient(135deg, #ff6b6b, #ee5253);
  color: white;
  border: none;
}

.btn-danger:hover {
  background: linear-gradient(135deg, #ee5253, #ff6b6b);
  transform: translateY(-1px);
}

.btn-edit {
  background: #fff;
  color: #1890ff;
  border: 1px solid #1890ff;
}

.btn-edit:hover {
  background: #1890ff;
  color: white;
  transform: translateY(-1px);
}

.btn-analysis {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
}

.btn-analysis:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-1px);
}

.btn-small {
  padding: 4px 10px;
  font-size: 12px;
}

.empty-state {
  padding: 40px;
  text-align: center;
  color: #999;
}

.subscription-list {
  max-height: 300px;
  overflow-y: auto;
}

.subscription-item {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.subscription-item:last-child {
  border-bottom: none;
}

.sub-info {
  display: flex;
  gap: 12px;
  margin-bottom: 4px;
}

.sub-city {
  font-weight: 600;
  color: #333;
}

.sub-coord {
  font-size: 12px;
  color: #999;
}

.sub-time {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.sub-actions {
  display: flex;
  gap: 8px;
}

.report-list {
  max-height: 500px;
  overflow-y: auto;
}

.batch-select-all {
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 6px;
  margin-bottom: 10px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.report-item {
  padding: 14px;
  padding-bottom: 42px;
  padding-left: 42px;
  border-radius: 6px;
  background: #fafafa;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.report-item:hover {
  background: #f0f0f0;
}

.report-item.unread {
  background: #fff7e6;
  border-left: 3px solid #faad14;
}

.report-item.selected {
  background: #e6f7ff;
  border: 1px solid #1890ff;
}

.report-checkbox {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
}

.report-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.report-content {
  /* padding-left 由 report-item 提供 */
}

.report-actions {
  position: absolute;
  right: 14px;
  bottom: 14px;
  display: flex;
  gap: 16px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.level-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.level-badge.level-7 {
  background: #95de64;
  color: #1890ff;
}

.level-badge.level-10 {
  background: #ffc53d;
  color: #d46b08;
}

.level-badge.level-12 {
  background: #ff7875;
  color: #fff;
}

.report-time {
  font-size: 12px;
  color: #999;
}

.report-city {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.report-preview {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.report-meta {
  font-size: 12px;
  color: #999;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-height: 80vh;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.close-btn {
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.modal-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
  font-size: 13px;
  color: #666;
}

.message-content {
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.8;
  color: #333;
}

.message-content pre {
  margin: 0;
  white-space: pre-wrap;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid #eee;
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
