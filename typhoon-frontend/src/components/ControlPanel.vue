<script setup lang="ts">
import { ref } from 'vue';

interface TyphoonItem {
  id: number;
  name: string;
  chineseName: string;
}

const formData = ref({
  startTime: '2026-05-20T08:00',
  longitude: 125.0,
  latitude: 20.0,
  pressure: 950,
  windSpeed: 45,
  direction: 315,
  speed: 25,
  intensity: 'STY',
  grade: 14,
  hours: 60
});

const emit = defineEmits(['simulate', 'clear']);

function handleSimulate() {
  const hours = formData.value.hours || 60;
  const steps = Math.round(hours / 6);
  
  const data = {
    ...formData.value,
    steps: steps
  };
  emit('simulate', data);
}

function handleClear() {
  emit('clear');
}

function setHoursMin() {
  formData.value.hours = 60;
}

function setHoursMax() {
  formData.value.hours = 240;
}
</script>

<template>
  <div class="control-panel">
    <div class="header">
      <h2>台风路径分析系统</h2>
      <p class="subtitle">Typhoon Tracking GIS</p>
    </div>

    <div class="panel-section">
      <h3>台风参数输入</h3>
      
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
          <input type="number" v-model="formData.grade" min="1" max="17" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label>移动方向 (度)</label>
          <input type="number" v-model="formData.direction" min="0" max="360" />
        </div>
        <div class="form-group half">
          <label>移动速度 (km/h)</label>
          <input type="number" v-model="formData.speed" min="0" />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label>预测时长 (小时)</label>
          <div class="duration-control">
          <input
            type="number"
            v-model="formData.hours"
            min="6"
            max="240"
            step="6"
          />

          <div class="quick-setting">
  <div class="quick-title">
    <span class="arrow">▶</span>
    快捷设置
  </div>

  <div class="quick-hours">
    <button
      class="quick-btn"
      @click="formData.hours = 60"
    >
      60小时
    </button>

    <button
      class="quick-btn"
      @click="formData.hours = 120"
    >
      120小时
    </button>

    <button
      class="quick-btn"
      @click="formData.hours = 180"
    >
      180小时
    </button>

    <button
      class="quick-btn"
      @click="formData.hours = 240"
    >
      240小时
    </button>
  </div>
</div>
        </div>
        </div>
        <div class="form-group half">
          <label>时间精度</label>
          <div class="input-info">每6小时一个预测点</div>
        </div>
      </div>
    </div>

    <div class="panel-actions">
      <button class="btn btn-primary" @click="handleSimulate">渲染轨迹与影响范围</button>
      <button class="btn btn-danger" @click="handleClear">清除地图</button>
    </div>
    
    <div class="legends-container">
      <div class="legend-section">
        <h3>台风强度图例</h3>
        <div class="legend-item"><span class="circle td"></span> 热带低压 (TD, <=7)</div>
        <div class="legend-item"><span class="circle ts"></span> 热带风暴 (TS, 8-9)</div>
        <div class="legend-item"><span class="circle sts"></span> 强热带风暴 (STS, 10-11)</div>
        <div class="legend-item"><span class="circle ty"></span> 台风 (TY, 12-13)</div>
        <div class="legend-item"><span class="circle sty"></span> 强台风 (STY, 14-15)</div>
        <div class="legend-item"><span class="circle superty"></span> 超强台风 (SuperTY, >=16)</div>
      </div>
      
      <div class="legend-section">
        <h3>风圈图例 (动态半径)</h3>
        <div class="legend-item"><span class="circle c7"></span> 7级风圈 (绿色)</div>
        <div class="legend-item"><span class="circle c10"></span> 10级风圈 (黄色)</div>
        <div class="legend-item"><span class="circle c12"></span> 12级风圈 (橙色)</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.control-panel {
  padding: 20px;
  overflow-y: auto;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
}

.header {
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
}

.header h2 {
  font-size: 18px;
  color: #667eea;
  margin-bottom: 4px;
  font-weight: 700;
}

.subtitle {
  font-size: 12px;
  color: #666;
}

.panel-section {
  margin-bottom: 24px;
}

.panel-section h3 {
  font-size: 14px;
  margin-bottom: 12px;
  color: #333;
  border-left: 3px solid #667eea;
  padding-left: 8px;
  font-weight: 600;
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
  color: #666;
}

input, select {
  padding: 10px 14px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  color: #333;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.input-info {
  padding: 10px 14px;
  font-size: 12px;
  color: #666;
  background: rgba(102, 126, 234, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;
}

input:focus, select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.typhoon-select {
  width: 100%;
  padding: 10px 32px 10px 12px;
  appearance: none;
  background: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  transition: all 0.2s ease;
  color: #333;
}

.typhoon-select:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.typhoon-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s;
  background: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.3);
}

.btn-small {
  padding: 4px 10px;
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
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 12px;
  font-size: 14px;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
}
.btn-primary:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-danger {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.3);
  font-weight: 600;
  transition: all 0.3s ease;
}
.btn-danger:hover {
  background: rgba(255, 107, 107, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.duration-control {
  display: flex;
  flex-direction: column;
  gap: 8px;
}



.quick-btn {
  width: 100%;
  text-align: center;
  padding: 6px 10px;
  font-size: 11px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.9);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
}

.quick-btn:hover {
  border-color: #667eea;
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.legends-container {
  display: flex;
  gap: 20px;
  margin-top: 30px;
}

.legend-section {
  flex: 1;
}

.legend-section h3 {
  font-size: 13px;
  margin-bottom: 10px;
  color: #333;
  border-left: 3px solid #667eea;
  padding-left: 8px;
  font-weight: 600;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  margin-bottom: 6px;
  color: #666;
}

.circle {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.quick-setting {
  position: relative;
  display: inline-block;
}

.quick-title {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  user-select: none;
}

.arrow {
  transition: transform 0.25s ease;
}

.quick-hours {
  position: absolute;

  top: 100%;
  left: 0;

  min-width: 140px;

  display: flex;
  flex-direction: column;

  gap: 6px;

  padding: 10px;

  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 8px;

  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);

  z-index: 9999;

  opacity: 0;
  visibility: hidden;

  transform: translateY(-5px);

  transition: all 0.2s ease;
}

.quick-setting:hover .quick-hours {
  opacity: 1;
  visibility: visible;
  transform: translateY(4px);
}

.quick-setting:hover .arrow {
  transform: rotate(90deg);
}

.c7 { background-color: rgba(16, 185, 129, 0.4); border: 1px solid #10b981; }
.c10 { background-color: rgba(234, 179, 8, 0.4); border: 1px solid #eab308; }
.c12 { background-color: rgba(249, 115, 16, 0.4); border: 1px solid #f97316; }

.td { background-color: #00D5CB; }
.ts { background-color: #FCFA00; }
.sts { background-color: #FDAA09; }
.ty { background-color: #FB3320; }
.sty { background-color: #F600A9; }
.superty { background-color: #AA0000; }
</style>
