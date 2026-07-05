<template>
  <div v-if="visible" class="modal-overlay" @click="handleClose">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <div class="header-icon">📍</div>
        <h3 class="modal-title">添加预警位置</h3>
        <button class="close-btn" @click="handleClose">×</button>
      </div>
      <div class="modal-body">
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
              <input type="number" v-model.number="formData.latitude" step="0.0001" placeholder="请输入纬度" />
            </div>
            <div class="form-group half">
              <label>经度</label>
              <input type="number" v-model.number="formData.longitude" step="0.0001" placeholder="请输入经度" />
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
      </div>
      <div class="modal-footer">
        <button class="btn" @click="handleClose">取消</button>
        <button class="btn btn-primary" @click="handleAdd">添加预警订阅</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps<{
  visible: boolean;
  cities: any[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'add', data: { cityName: string | null; latitude: number; longitude: number; startTime: string; endTime: string }): void;
}>();

const formData = ref({
  cityName: '',
  latitude: 22.5431,
  longitude: 114.0579,
  startTime: '',
  endTime: ''
});

watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm();
  }
});

function resetForm() {
  formData.value = {
    cityName: '',
    latitude: 22.5431,
    longitude: 114.0579,
    startTime: '',
    endTime: ''
  };
}

function onCityChange() {
  if (formData.value.cityName && formData.value.cityName !== 'custom') {
    const city = props.cities.find(c => c.name === formData.value.cityName);
    if (city) {
      formData.value.latitude = city.latitude;
      formData.value.longitude = city.longitude;
    }
  }
}

function handleClose() {
  emit('close');
}

function handleAdd() {
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
  
  emit('add', {
    cityName: formData.value.cityName === 'custom' ? null : formData.value.cityName,
    latitude: formData.value.latitude,
    longitude: formData.value.longitude,
    startTime: new Date(formData.value.startTime).toISOString().slice(0, 19).replace('T', ' '),
    endTime: new Date(formData.value.endTime).toISOString().slice(0, 19).replace('T', ' ')
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
  max-width: 500px;
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
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8px;
}

.form-group .small-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  font-size: 14px;
  transition: all 0.3s ease;
  outline: none;
}

.form-group input:focus,
.form-group select:focus {
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.08);
}

.form-group input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group.half {
  flex: 1;
}

.city-select {
  cursor: pointer;
}

.city-select option {
  background: #1a1a2e;
  color: #fff;
}

.custom-location {
  background: rgba(255, 255, 255, 0.03);
  padding: 16px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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
  border: none;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
</style>