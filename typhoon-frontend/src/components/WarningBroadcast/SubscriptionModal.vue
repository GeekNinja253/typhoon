<template>
  <div v-if="visible" class="modal-overlay" @click="handleClose">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <div class="header-icon">📋</div>
        <h3 class="modal-title">已订阅列表</h3>
        <button class="close-btn" @click="handleClose">×</button>
      </div>
      <div class="modal-body">
        <div v-if="subscriptions.length === 0" class="empty-state">
          <span class="empty-icon">📭</span>
          <p>暂无订阅</p>
        </div>
        <div v-else class="subscription-grid">
          <div v-for="sub in subscriptions" :key="sub.id" class="subscription-card" :class="{ disabled: sub.status === 0 }">
            <div class="card-header">
              <div class="city-info">
                <span class="city-icon">📍</span>
                <span class="city-name">{{ sub.cityName || '自定义位置' }}</span>
              </div>
              <span class="status-badge" :class="sub.status === 1 ? 'status-active' : 'status-inactive'">
                {{ sub.status === 1 ? '已启用' : '已停用' }}
              </span>
            </div>
            <div class="card-body">
              <div class="info-row">
                <span class="info-label">坐标</span>
                <span class="info-value">{{ sub.latitude?.toFixed(4) }}, {{ sub.longitude?.toFixed(4) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">开始时间</span>
                <span class="info-value">{{ formatDateTime(sub.startTime) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">结束时间</span>
                <span class="info-value">{{ formatDateTime(sub.endTime) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">订阅时长</span>
                <span class="info-value">{{ getDuration(sub.startTime, sub.endTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <button class="btn btn-small" :class="sub.status === 1 ? 'btn-warning' : 'btn-primary'"
                      @click="$emit('toggle', sub.id, sub.status)">
                {{ sub.status === 1 ? '停用' : '启用' }}
              </button>
              <button class="btn btn-small btn-edit" @click="$emit('edit', sub)">修改</button>
              <button class="btn btn-small btn-danger" @click="$emit('delete', sub.id)">删除</button>
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
  subscriptions: any[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'toggle', id: number, status: number): void;
  (e: 'edit', sub: any): void;
  (e: 'delete', id: number): void;
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

function getDuration(startTime: string, endTime: string) {
  if (!startTime || !endTime) return '--';
  const start = new Date(startTime);
  const end = new Date(endTime);
  const diff = end.getTime() - start.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  if (days > 0) {
    return `${days}天 ${hours}小时`;
  }
  return `${hours}小时`;
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

.subscription-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.subscription-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
}

.subscription-card:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.subscription-card.disabled {
  opacity: 0.6;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.city-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.city-icon {
  font-size: 18px;
}

.city-name {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
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

.card-body {
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

.info-value {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.card-footer {
  display: flex;
  gap: 8px;
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

.btn-small {
  padding: 6px 14px;
  font-size: 12px;
}
</style>