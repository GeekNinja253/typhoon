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
          
          <!-- 详细分析按钮 -->
          <!-- <button class="btn-analysis-overlay" @click="goToAnalysis">📊 详细分析</button> -->
          
          <!-- 台风范围数据 -->
          <div class="range-section">
            <div class="range-title">台风范围</div>
            <div v-if="currentFrame.grade >= 7" class="range-row">
              <span class="range-icon level7">7</span>
              <span class="range-label">7级风圈半径</span>
              <span class="range-value">{{ getWindRadius(7) }} km</span>
            </div>
            <div v-if="currentFrame.grade >= 10" class="range-row">
              <span class="range-icon level10">10</span>
              <span class="range-label">10级风圈半径</span>
              <span class="range-value">{{ getWindRadius(10) }} km</span>
            </div>
            <div v-if="currentFrame.grade >= 12" class="range-row">
              <span class="range-icon level12">12</span>
              <span class="range-label">12级风圈半径</span>
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
              <button class="btn-analysis-alert" @click="goToAnalysisFromAlert(alert)">📊 详细分析</button>
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
import MapContainer from "../../components/GISMap/MapContainer.vue";
import TimelinePlayer from "../../components/GISMap/TimelinePlayer.vue";
import ControlPanel from "../../components/GISMap/ControlPanel.vue";
import axios from "axios";
import '@/styles/mapview.css'

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

function goToAnalysisFromAlert(alert: any) {
  const analysisData = {
    // 基本信息（使用预警数据中的订阅地址信息）
    id: alert.id || 0,
    level: alert.level || 12,
    cityName: alert.cityName || '订阅位置',
    latitude: alert.latitude || 0,
    longitude: alert.longitude || 0,
    distance: alert.distance || 0,
    createTime: alert.createTime || new Date().toISOString(),
    triggerTime: alert.triggerTime || null,
    // 从预警消息中解析台风实时数据
    typhoonLat: currentFrame.value?.lat || 0,
    typhoonLng: currentFrame.value?.longitude || currentFrame.value?.lon || 0,
    grade: currentFrame.value?.grade || 12,
    windSpeed: currentFrame.value?.windSpeed || 0,
    pressure: currentFrame.value?.pressure || 1000,
    // 风圈数据
    windRadius7: currentFrame.value?.grade >= 7 ? Math.round((150000 + Math.max(0, (currentFrame.value?.grade || 0) - 7) * 15000) / 1000) : 0,
    windRadius10: currentFrame.value?.grade >= 10 ? Math.round((80000 + Math.max(0, (currentFrame.value?.grade || 0) - 10) * 12000) / 1000) : 0,
    windRadius12: currentFrame.value?.grade >= 12 ? Math.round((40000 + Math.max(0, (currentFrame.value?.grade || 0) - 12) * 10000) / 1000) : 0,
    affectedArea: currentFrame.value?.grade >= 7 ? Math.round(Math.PI * Math.pow((150000 + Math.max(0, (currentFrame.value?.grade || 0) - 7) * 15000) / 1000, 2)) : 0,
    // 完整帧数据
    frames: frames.value,
    message: alert.message || ''
  };
  
  sessionStorage.setItem('currentAnalysis', JSON.stringify(analysisData));
  router.push(`/analysis/${alert.id || 0}`);
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

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.map-box {
  flex: 1;
  position: relative;
}
</style>
