<script setup lang="ts">
import { ref, onMounted } from 'vue';

interface TyphoonItem {
  id: number;
  name: string;
  chineseName: string;
}

// Form data
const formData = ref({
  startTime: '2026-05-20T08:00',
  longitude: 125.0,
  latitude: 20.0,
  pressure: 950,
  windSpeed: 45,
  direction: 315,
  speed: 25,
  intensity: 'STY',
  grade: 14
});

const emit = defineEmits(['simulate', 'clear']);

function handleSimulate() {
  emit('simulate', formData.value);
}

function handleClear() {
  emit('clear');
}


</script>

<template>
  <div class="control-panel">
    <div class="header">
      <h2>台风路径分析系统</h2>
      <p class="subtitle">Typhoon Tracking GIS</p>
    </div>

    <div class="panel-section">
      <h3>台风参数输入 (UI 展示)</h3>
      
      <div class="form-row">
        <div class="form-group">
          <label>起始时间</label>
          <input type="datetime-local" v-model="formData.startTime" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label>经度 (°E)</label>
          <input type="number" v-model="formData.longitude" step="0.1" />
        </div>
        <div class="form-group half">
          <label>纬度 (°N)</label>
          <input type="number" v-model="formData.latitude" step="0.1" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label>中心气压 (hPa)</label>
          <input type="number" v-model="formData.pressure" />
        </div>
        <div class="form-group half">
          <label>最大风速 (m/s)</label>
          <input type="number" v-model="formData.windSpeed" />
        </div>
      </div>
      
      <div class="form-row">
        <div class="form-group half">
          <label>台风强度</label>
          <select v-model="formData.intensity" class="typhoon-select">
            <option value="TD">热带低压</option>
            <option value="TS">热带风暴</option>
            <option value="STS">强热带风暴</option>
            <option value="TY">台风</option>
            <option value="STY">强台风</option>
            <option value="SuperTY">超强台风</option>
          </select>
        </div>
        <div class="form-group half">
          <label>风力等级</label>
          <input type="number" v-model="formData.grade" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label>移动方向 (度)</label>
          <input type="number" v-model="formData.direction" />
        </div>
        <div class="form-group half">
          <label>移动速度 (km/h)</label>
          <input type="number" v-model="formData.speed" />
        </div>
      </div>

    </div>

    <div class="panel-actions">
      <button class="btn btn-primary" @click="handleSimulate">渲染轨迹与影响范围</button>
      <button class="btn btn-danger" @click="handleClear">清除地图</button>
    </div>
    
    <div class="legend-section">
      <h3>风圈图例</h3>
      <div class="legend-item"><span class="circle c7"></span> 7级风圈 (绿色)</div>
      <div class="legend-item"><span class="circle c10"></span> 10级风圈 (橙色)</div>
      <div class="legend-item"><span class="circle c12"></span> 12级风圈 (红色)</div>
    </div>
  </div>
</template>

<style scoped>
.control-panel {
  padding: 20px;
  overflow-y: auto;
  height: 100%;
}

.header {
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
}

.header h2 {
  font-size: 18px;
  color: var(--primary-color);
  margin-bottom: 4px;
}

.subtitle {
  font-size: 12px;
  color: var(--text-secondary);
}

.panel-section {
  margin-bottom: 24px;
}

.panel-section h3 {
  font-size: 14px;
  margin-bottom: 12px;
  color: #111;
  border-left: 3px solid var(--primary-color);
  padding-left: 8px;
}

.form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.form-group.half {
  flex: 0 0 calc(50% - 6px);
}

label {
  font-size: 12px;
  color: var(--text-secondary);
}

input, select {
  padding: 8px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 13px;
  outline: none;
}

input:focus, select:focus {
  border-color: var(--primary-color);
}

.select-wrapper {
  position: relative;
  display: inline-block;
  width: 100%;
}

.typhoon-select {
  width: 100%;
  padding: 10px 32px 10px 12px;
  appearance: none;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.typhoon-select:hover {
  border-color: var(--primary-color);
  background-color: #f8fafc;
}

.typhoon-select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.15);
}

.select-arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: var(--text-secondary);
  font-size: 10px;
  transition: transform 0.2s ease;
}

.select-wrapper:hover .select-arrow {
  color: var(--primary-color);
}

.select-wrapper:focus-within .select-arrow {
  transform: translateY(-50%) rotate(180deg);
  color: var(--primary-color);
}

.typhoon-select:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.input-row {
  display: flex;
  gap: 8px;
}
.input-row input {
  flex: 1;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-small {
  padding: 4px 10px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
}
.btn-small:hover {
  background-color: #e6e6e6;
}

.panel-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
  padding: 12px;
  font-size: 14px;
}
.btn-primary:hover {
  background-color: #40a9ff;
}

.btn-danger {
  background-color: #fff;
  color: var(--color-superty);
  border: 1px solid var(--color-superty);
}
.btn-danger:hover {
  background-color: #fff1f0;
}

.legend-section {
  margin-top: 30px;
}
.legend-section h3 {
  font-size: 13px;
  margin-bottom: 10px;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  margin-bottom: 6px;
  color: var(--text-secondary);
}
.circle {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
.c7 { background-color: rgba(16, 185, 129, 0.4); border: 1px solid #10b981; }
.c10 { background-color: rgba(249, 115, 22, 0.4); border: 1px solid #f97316; }
.c12 { background-color: rgba(239, 68, 68, 0.4); border: 1px solid #ef4444; }
</style>
