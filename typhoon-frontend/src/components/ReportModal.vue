<template>
  <div v-if="visible" class="modal-overlay" @click="handleClose">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <div class="header-icon">📊</div>
        <h3 class="modal-title">预警记录</h3>
        <div class="header-actions">
          <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }} 条未读</span>
          <template v-if="!isBatchMode && reports.length > 0">
            <button class="btn btn-small" @click="$emit('enterBatchMode')">批量管理</button>
          </template>
          <template v-else-if="isBatchMode">
            <button class="btn btn-small" @click="$emit('exitBatchMode')">取消</button>
            <button class="btn btn-small btn-danger" @click="$emit('batchDelete')" :disabled="selectedReports.length === 0">
              批量删除 ({{ selectedReports.length }})
            </button>
          </template>
        </div>
        <button class="close-btn" @click="handleClose">×</button>
      </div>
      <div class="modal-body">
        <div v-if="reports.length === 0" class="empty-state">
          <span class="empty-icon">📭</span>
          <p>暂无预警记录</p>
        </div>
        <div v-else class="report-list">
          <div v-if="isBatchMode" class="batch-select-all">
            <label class="checkbox-label">
              <input type="checkbox" :checked="selectedReports.length === reports.length && reports.length > 0" 
                     :indeterminate="selectedReports.length > 0 && selectedReports.length < reports.length"
                     @change="$emit('toggleSelectAll')" />
              全选
            </label>
          </div>
          <div v-for="report in reports" :key="report.id" 
               class="report-card" :class="{ unread: report.status === 0, selected: isBatchMode && selectedReports.includes(report.id) }">
            <div v-if="isBatchMode" class="report-checkbox">
              <input type="checkbox" :checked="selectedReports.includes(report.id)" 
                     @click.stop="$emit('toggleSelect', report.id)" />
            </div>
            <div class="report-main">
              <div class="report-header">
                <span class="level-badge level-{{ report.level }}">{{ report.level }}级预警</span>
                <span class="report-time">{{ formatDateTime(report.createTime) }}</span>
              </div>
              <div class="report-city">{{ report.cityName || '未知位置' }}</div>
              <div class="report-preview">{{ report.message.substring(0, 80) }}...</div>
              <div class="report-meta">
                <span>距离: {{ report.distance }} km</span>
                <span v-if="report.triggerTime">预计到达: {{ formatDateTime(report.triggerTime) }}</span>
              </div>
            </div>
            <div class="report-actions" v-if="!isBatchMode">
              <button class="btn btn-small btn-analysis" @click.stop="$emit('goAnalysis', report)">详细分析</button>
              <button class="btn btn-small" @click.stop="$emit('viewReport', report)">记录预览</button>
              <button class="btn btn-small btn-danger" @click.stop="$emit('deleteReport', report.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-primary" @click="handleClose">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  visible: boolean;
  reports: any[];
  unreadCount: number;
  isBatchMode: boolean;
  selectedReports: number[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'enterBatchMode'): void;
  (e: 'exitBatchMode'): void;
  (e: 'batchDelete'): void;
  (e: 'toggleSelectAll'): void;
  (e: 'toggleSelect', id: number): void;
  (e: 'goAnalysis', report: any): void;
  (e: 'viewReport', report: any): void;
  (e: 'deleteReport', id: number): void;
}>();

function handleClose() {
  emit('close');
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
</script>

<style scoped>
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
  width: 90%;
  max-width: 900px;
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
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-icon {
  font-size: 24px;
  margin-right: 12px;
}

.modal-title {
  flex: 1;
  margin: 0;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-right: 16px;
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
  max-height: 550px;
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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  color: rgba(255, 255, 255, 0.4);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
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

.report-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-card {
  display: flex;
  align-items: stretch;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  transition: all 0.3s ease;
}

.report-card:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.1);
}

.report-card.unread {
  background: rgba(255, 197, 61, 0.1);
  border-left: 4px solid #ffc53d;
}

.report-card.selected {
  background: rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.4);
}

.report-checkbox {
  display: flex;
  align-items: center;
  padding-right: 16px;
}

.report-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

.report-main {
  flex: 1;
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
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 6px;
}

.report-preview {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 8px;
  line-height: 1.5;
}

.report-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.report-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 16px;
  justify-content: center;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: right;
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
}

.btn-primary:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
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
  padding: 6px 14px;
  font-size: 12px;
}
</style>