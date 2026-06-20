<template>
  <div class="map-view">
    <div class="sidebar">
      <ControlPanel @simulate="onSimulate" @clear="onClear" />
    </div>

    <div class="main">
      <div class="map-box">
        <button class="back-btn" @click="goBack">← 返回</button>
        <MapContainer ref="mapRef" tk="3c848acf588261378c0dd00571a01fb5" />

        <div v-if="currentFrame" class="info-overlay">
          <h3 class="info-title">实时参数推演</h3>
          <div class="info-row"><span>时间:</span> <span>{{ new Date(currentFrame.time).toLocaleString() }}</span></div>
          <div class="info-row"><span>经度:</span> <span>{{ Number(currentFrame.longitude || currentFrame.lon).toFixed(2) }}°</span></div>
          <div class="info-row"><span>纬度:</span> <span>{{ Number(currentFrame.lat).toFixed(2) }}°</span></div>
          <div class="info-row"><span>风力等级:</span> <span>{{ currentFrame.grade }} 级</span></div>
          <div class="info-row" v-if="currentFrame.windSpeed !== undefined"><span>风速:</span> <span>{{ Number(currentFrame.windSpeed).toFixed(1) }} m/s</span></div>
          <div class="info-row" v-if="currentFrame.pressure !== undefined"><span>中心气压:</span> <span>{{ Number(currentFrame.pressure).toFixed(1) }} hPa</span></div>
          
          <!-- 台风范围数据 -->
          <div class="range-section">
            <div class="range-title">台风范围</div>
            <div v-if="currentFrame.grade >= 7" class="range-row">
              <span class="range-icon level7">7</span>
              <span class="range-label">7级风圈</span>
              <span class="range-value">{{ getWindRadius(7) }} km</span>
            </div>
            <div v-if="currentFrame.grade >= 10" class="range-row">
              <span class="range-icon level10">10</span>
              <span class="range-label">10级风圈</span>
              <span class="range-value">{{ getWindRadius(10) }} km</span>
            </div>
            <div v-if="currentFrame.grade >= 12" class="range-row">
              <span class="range-icon level12">12</span>
              <span class="range-label">12级风圈</span>
              <span class="range-value">{{ getWindRadius(12) }} km</span>
            </div>
            <div v-if="currentFrame.grade >= 7" class="area-row">
              <span class="area-label">影响面积</span>
              <span class="area-value">{{ affectedArea }} km²</span>
            </div>
          </div>
        </div>
      </div>

      <div class="timeline-box">
        <TimelinePlayer :frames="frames" @update:frameIndex="onFrameChange" />
      </div>
    </div>

    <!-- 预警结果弹窗 -->
    <div v-if="showAlertModal" class="alert-modal-overlay" @click="closeAlertModal">
      <div class="alert-modal-content" @click.stop>
        <div class="alert-modal-header">
          <h3>🌪 台风预警报告</h3>
          <button class="close-btn" @click="closeAlertModal">×</button>
        </div>
        <div class="alert-modal-body">
          <p class="alert-count">检测到 {{ alertResults.length }} 条预警信息</p>
          <div class="alert-list">
            <div v-for="alert in alertResults" :key="alert.id" class="alert-item">
              <div class="alert-level">
                <span class="level-badge level-{{ alert.level }}">{{ alert.level }}级预警</span>
                <span class="alert-city">{{ alert.cityName }}</span>
              </div>
              <div class="alert-info">
                <span>距离: {{ alert.distance }} km</span>
                <span>触发时间: {{ formatDateTime(alert.triggerTime) }}</span>
              </div>
              <div class="alert-message">
                <pre>{{ alert.message }}</pre>
              </div>
            </div>
          </div>
        </div>
        <div class="alert-modal-footer">
          <button class="btn btn-primary" @click="closeAlertModal">知道了</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import MapContainer from "../../components/MapContainer.vue";
import TimelinePlayer from "../../components/TimelinePlayer.vue";
import ControlPanel from "../../components/ControlPanel.vue";
import { getTyphoonPath } from "../../api/typhoon";
import axios from "axios";

const router = useRouter();
const mapRef = ref();

const frames = ref<any[]>([]);
const currentFrame = ref<any>(null);
const alertResults = ref<any[]>([]);
const showAlertModal = ref(false);

// 根据台风等级计算风圈半径（单位：km）
function getWindRadius(level: number): string {
  if (!currentFrame.value) return "-";
  const grade = currentFrame.value.grade || 0;
  
  let radius: number | null = null;
  if (level === 7 && grade >= 7) {
    radius = Math.round((150000 + Math.max(0, grade - 7) * 15000) / 1000);
  } else if (level === 10 && grade >= 10) {
    radius = Math.round((80000 + Math.max(0, grade - 10) * 12000) / 1000);
  } else if (level === 12 && grade >= 12) {
    radius = Math.round((40000 + Math.max(0, grade - 12) * 10000) / 1000);
  }
  
  return radius !== null ? radius.toString() : "-";
}

// 计算影响面积（单位：km²）
const affectedArea = computed(() => {
  if (!currentFrame.value) return "-";
  const grade = currentFrame.value.grade || 0;
  
  if (grade >= 7) {
    const radius = (150000 + Math.max(0, grade - 7) * 15000) / 1000;
    const area = Math.round(Math.PI * Math.pow(radius, 2));
    return area.toLocaleString();
  }
  return "-";
});

async function loadTyphoon(id: number) {
  try {
    const res = await getTyphoonPath(id);
    frames.value = res.data.data;
    if (frames.value.length > 0) currentFrame.value = frames.value[0];

    mapRef.value.loadRealPath(res.data.data);
    mapRef.value.renderFrame(0);
  } catch (error) {
    console.error("Failed to load predefined path", error);
  }
}

function onClear() {
  frames.value = [];
  currentFrame.value = null;
  if (mapRef.value) {
    mapRef.value.clearMap();
  }
}

async function onSimulate(data: any) {
  try {
    const intensityMap: Record<string, number> = {
      'TD': 1, 'TS': 2, 'STS': 3, 'TY': 4, 'STY': 5, 'SuperTY': 6
    };

    const payload = {
      lat: Number(data.latitude),
      lon: Number(data.longitude),
      intensity: intensityMap[data.intensity] || 4,
      grade: Number(data.grade),
      wind_speed: Number(data.windSpeed),
      pressure: Number(data.pressure),
      direction: Number(data.direction),
      speed: Number(data.speed),
      steps: Number(data.steps) || 10
    };
    
    const res = await axios.post('http://localhost:8080/api/predict', payload);
    const predictedTrajectory = res.data.data.trajectory;
    
    const startTime = new Date(data.startTime).getTime();
    
    const allPoints: any[] = [];
    allPoints.push({
      lat: data.latitude,
      longitude: data.longitude, 
      time: startTime,
      grade: parseInt(data.grade) || 12,
      windSpeed: parseFloat(data.windSpeed) || 0,
      pressure: parseFloat(data.pressure) || 1000,
      isFuture: false
    });
    
    predictedTrajectory.forEach((pt: any, index: number) => {
      allPoints.push({
        lat: pt.lat,
        longitude: pt.lon, 
        time: startTime + (index + 1) * 6 * 3600 * 1000,
        grade: Math.max(0, Math.round(pt.grade)),
        windSpeed: pt.wind_speed,
        pressure: pt.pressure,
        isFuture: true
      });
    });
    
    frames.value = allPoints;
    if (allPoints.length > 0) currentFrame.value = allPoints[0];
    mapRef.value.loadPredictedPath(data, allPoints);
    mapRef.value.renderFrame(0);
    
    // 触发预警检查
    await checkAlerts(allPoints);
  } catch (error) {
    console.error('预测失败', error);
    alert('请求预测数据失败！请检查 Java 后端服务 (端口 8080) 是否已启动。');
  }
}

// 检查预警
async function checkAlerts(trajectory: any[]) {
  try {
    const userId = 1; // 暂固定用户ID
    
    // 转换轨迹数据格式
    const trajectoryData = trajectory.map(pt => ({
      lat: pt.lat,
      lon: pt.longitude || pt.lon,
      grade: pt.grade,
      windSpeed: pt.windSpeed,
      pressure: pt.pressure,
      time: pt.time
    }));
    
    const res = await axios.post('http://localhost:8080/api/alert/check', {
      userId: userId,
      trajectory: trajectoryData
    });
    
    alertResults.value = res.data.data;
    
    if (alertResults.value.length > 0) {
      showAlertModal.value = true;
    }
  } catch (error) {
    console.error('预警检查失败', error);
  }
}

function onFrameChange(index: number) {
  if (frames.value && frames.value[index]) {
    currentFrame.value = frames.value[index];
  } else {
    currentFrame.value = null;
  }
  if (mapRef.value) {
    mapRef.value.renderFrame(index);
  }
}

function goBack() {
  router.push("/home");
}

function closeAlertModal() {
  showAlertModal.value = false;
}

function formatDateTime(dateStr: string) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString();
}
</script>
<style scoped>
.map-view {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 300px;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 1000;
  padding: 8px 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  pointer-events: auto;
}

.back-btn:hover {
  background: #f5f5f5;
  border-color: #1890ff;
  color: #1890ff;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.map-box {
  flex: 1;
  position: relative;
}

.info-overlay {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  z-index: 1000;
  width: 280px;
  pointer-events: none;
}

.info-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #1890ff;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.info-row span:last-child {
  font-weight: 600;
}

/* 台风范围模块样式 */
.range-section {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px dashed #ddd;
}

.range-title {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 10px;
}

.range-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
}

.range-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  font-size: 10px;
  font-weight: bold;
  color: white;
}

.range-icon.level7 {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.range-icon.level10 {
  background: linear-gradient(135deg, #facc15 0%, #eab308 100%);
}

.range-icon.level12 {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
}

.range-label {
  flex: 1;
  color: #64748b;
}

.range-value {
  font-weight: 600;
  color: #1e293b;
}

.area-row {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #eee;
  font-size: 13px;
}

.area-label {
  color: #64748b;
}

.area-value {
  font-weight: 600;
  color: #1890ff;
}

.timeline-box {
  height: 120px;
}

/* 预警弹窗样式 */
.alert-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.alert-modal-content {
  background: white;
  border-radius: 12px;
  width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.alert-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
}

.alert-modal-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  opacity: 0.8;
}

.close-btn:hover {
  opacity: 1;
}

.alert-modal-body {
  padding: 20px 24px;
  max-height: 400px;
  overflow-y: auto;
}

.alert-count {
  color: #ff7875;
  font-weight: 600;
  margin-bottom: 16px;
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.alert-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.alert-level {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.level-badge {
  padding: 2px 10px;
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

.alert-city {
  font-weight: 600;
  color: #333;
}

.alert-info {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
}

.alert-message {
  background: white;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}

.alert-message pre {
  margin: 0;
  white-space: pre-wrap;
}

.alert-modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
}
</style>
