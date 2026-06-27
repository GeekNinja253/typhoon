<template>
  <div class="analysis-view">
    <div class="page-header">
      <button class="back-btn" @click="goBack">← 返回预警报告</button>
      <h2>台风预警详细分析</h2>
    </div>

    <div class="analysis-container">
      <!-- ① 基本信息 -->
      <div class="section basic-info">
        <h3 class="section-title">① 基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">预警等级</span>
            <span class="value level-badge level-{{ report.level }}">{{ report.level }}级</span>
          </div>
          <div class="info-item">
            <span class="label">目标城市</span>
            <span class="value">{{ report.cityName || '自定义位置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">城市坐标</span>
            <span class="value">{{ formatCoord(report.latitude) }}, {{ formatCoord(report.longitude) }}</span>
          </div>
          <div class="info-item">
            <span class="label">距离台风</span>
            <span class="value">{{ report.distance }} km</span>
          </div>
          <div class="info-item">
            <span class="label">预警时间</span>
            <span class="value">{{ formatDateTime(report.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">预计到达</span>
            <span class="value">{{ formatDateTime(report.triggerTime) }}</span>
          </div>
        </div>
      </div>

      <!-- ② 地图（风圈+城市位置） -->
      <div class="section map-section">
        <h3 class="section-title">② 台风风圈影响地图</h3>
        <div class="map-wrapper">
          <div ref="miniMap" class="mini-map"></div>
          <div class="map-info">
            <div class="wind-circle legend">
              <span class="legend-item circle-7">7级风圈 ({{ windCircles.level7.radius }} km)</span>
              <span class="legend-item circle-10">10级风圈 ({{ windCircles.level10.radius }} km)</span>
              <span class="legend-item circle-12">12级风圈 ({{ windCircles.level12.radius }} km)</span>
            </div>
            <div class="impact-status">
              <span v-if="isInCircle7" class="status-7">✓ 在7级风圈内</span>
              <span v-if="isInCircle10" class="status-10">✓ 在10级风圈内</span>
              <span v-if="isInCircle12" class="status-12">✓ 在12级风圈内</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ③ 风险等级卡 -->
      <div class="section risk-card">
        <h3 class="section-title">③ 综合风险评估</h3>
        <div class="risk-level-card level-{{ overallRisk }}">
          <div class="risk-icon">{{ getRiskIcon(overallRisk) }}</div>
          <div class="risk-level">{{ getRiskText(overallRisk) }}</div>
          <div class="risk-score">{{ riskScore.toFixed(1) }}</div>
          <div class="risk-desc">{{ getRiskDesc(overallRisk) }}</div>
        </div>
      </div>

      <!-- ④ 风圈影响分析表格 -->
      <div class="section wind-table">
        <h3 class="section-title">④ 风圈影响详细分析</h3>
        <table class="analysis-table">
          <thead>
            <tr>
              <th>风圈等级</th>
              <th>半径 (km)</th>
              <th>影响面积 (km²)</th>
              <th>风速范围</th>
              <th>影响程度</th>
              <th>是否覆盖目标</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="level-cell level-7">7级风圈</td>
              <td>{{ windCircles.level7.radius }}</td>
              <td>{{ windCircles.level7.area }}</td>
              <td>17.2-24.4 m/s</td>
              <td>轻度影响</td>
              <td :class="isInCircle7 ? 'in-circle' : 'out-circle'">{{ isInCircle7 ? '是' : '否' }}</td>
            </tr>
            <tr>
              <td class="level-cell level-10">10级风圈</td>
              <td>{{ windCircles.level10.radius }}</td>
              <td>{{ windCircles.level10.area }}</td>
              <td>24.5-32.6 m/s</td>
              <td>中度影响</td>
              <td :class="isInCircle10 ? 'in-circle' : 'out-circle'">{{ isInCircle10 ? '是' : '否' }}</td>
            </tr>
            <tr>
              <td class="level-cell level-12">12级风圈</td>
              <td>{{ windCircles.level12.radius }}</td>
              <td>{{ windCircles.level12.area }}</td>
              <td>≥32.7 m/s</td>
              <td>重度影响</td>
              <td :class="isInCircle12 ? 'in-circle' : 'out-circle'">{{ isInCircle12 ? '是' : '否' }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ⑤⑥ 雷达图 + 指标卡 -->
      <div class="section charts-row">
        <div class="chart-box">
          <h3 class="section-title">⑤ 综合风险雷达图</h3>
          <div ref="radarChart" class="radar-chart"></div>
        </div>
        <div class="metrics-box">
          <h3 class="section-title">⑥ 关键指标</h3>
          <div class="metrics-grid">
            <div class="metric-card">
              <div class="metric-icon">💨</div>
              <div class="metric-label">风速</div>
              <div class="metric-value">{{ currentFrame.windSpeed?.toFixed(1) || '--' }}</div>
              <div class="metric-unit">m/s</div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">📊</div>
              <div class="metric-label">风力等级</div>
              <div class="metric-value">{{ currentFrame.grade || '--' }}</div>
              <div class="metric-unit">级</div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">🌡️</div>
              <div class="metric-label">中心气压</div>
              <div class="metric-value">{{ currentFrame.pressure?.toFixed(0) || '--' }}</div>
              <div class="metric-unit">hPa</div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">📍</div>
              <div class="metric-label">台风位置</div>
              <div class="metric-value">{{ typhoonPosition.lat?.toFixed(2) }}°N</div>
              <div class="metric-value">{{ typhoonPosition.lng?.toFixed(2) }}°E</div>
            </div>
          </div>
        </div>
      </div>

      <!-- ⑦ 风险趋势折线图 -->
      <div class="section trend-chart">
        <h3 class="section-title">⑦ 风险等级趋势预测</h3>
        <div ref="trendChart" class="trend-chart-container"></div>
      </div>

      <!-- ⑧ 系统分析建议 -->
      <div class="section suggestions">
        <h3 class="section-title">⑧ 系统分析建议</h3>
        <div class="suggestion-list">
          <div v-for="(suggestion, index) in suggestions" :key="index" class="suggestion-item">
            <span class="suggestion-icon">{{ suggestion.icon }}</span>
            <span class="suggestion-text">{{ suggestion.text }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import * as echarts from 'echarts';
import * as Cesium from 'cesium';
import 'cesium/Build/Cesium/Widgets/widgets.css';

const router = useRouter();
const route = useRoute();

const report = ref<any>({
  id: 0,
  level: 0,
  cityName: '',
  latitude: 0,
  longitude: 0,
  distance: 0,
  createTime: '',
  triggerTime: '',
  message: ''
});

const currentFrame = ref<any>({
  grade: 12,
  windSpeed: 35,
  pressure: 950
});

const typhoonPosition = ref({ lat: 20.5, lng: 125.3 });

// 风圈数据
const windCircles = computed(() => {
  const grade = currentFrame.value.grade || 12;
  return {
    level7: {
      radius: 150 + Math.max(0, grade - 7) * 15,
      area: Math.round(Math.PI * Math.pow(150 + Math.max(0, grade - 7) * 15, 2))
    },
    level10: {
      radius: 80 + Math.max(0, grade - 10) * 12,
      area: Math.round(Math.PI * Math.pow(80 + Math.max(0, grade - 10) * 12, 2))
    },
    level12: {
      radius: 40 + Math.max(0, grade - 12) * 10,
      area: Math.round(Math.PI * Math.pow(40 + Math.max(0, grade - 12) * 10, 2))
    }
  };
});

// 计算城市是否在风圈内
const isInCircle7 = computed(() => {
  return report.value.distance <= windCircles.value.level7.radius;
});

const isInCircle10 = computed(() => {
  return report.value.distance <= windCircles.value.level10.radius;
});

const isInCircle12 = computed(() => {
  return report.value.distance <= windCircles.value.level12.radius;
});

// 综合风险评分 (0-100)
const riskScore = computed(() => {
  let score = 0;
  if (isInCircle12.value) score += 50;
  else if (isInCircle10.value) score += 30;
  else if (isInCircle7.value) score += 15;
  
  score += (currentFrame.value.grade || 12) * 3;
  if (currentFrame.value.windSpeed > 40) score += 10;
  
  return Math.min(100, score);
});

// 综合风险等级
const overallRisk = computed(() => {
  if (riskScore.value >= 80) return 'critical';
  if (riskScore.value >= 60) return 'high';
  if (riskScore.value >= 40) return 'medium';
  return 'low';
});

// 图表引用
const radarChart = ref<HTMLElement | null>(null);
const trendChart = ref<HTMLElement | null>(null);
const miniMap = ref<HTMLElement | null>(null);

let radarChartInstance: echarts.ECharts | null = null;
let trendChartInstance: echarts.ECharts | null = null;
let cesiumViewer: Cesium.Viewer | null = null;

// 建议列表
const suggestions = computed(() => {
  const list = [];
  
  if (isInCircle12.value) {
    list.push({ icon: '🔴', text: '目标城市位于12级风圈范围内，建议立即启动紧急疏散预案，撤离所有居民至安全区域。' });
  }
  
  if (isInCircle10.value) {
    list.push({ icon: '🟠', text: '目标城市位于10级风圈范围内，建议加固建筑结构，停止户外活动，做好应急物资储备。' });
  }
  
  if (isInCircle7.value) {
    list.push({ icon: '🟡', text: '目标城市位于7级风圈范围内，建议关注台风动态，减少外出，检查门窗是否牢固。' });
  }
  
  if (currentFrame.value.grade >= 14) {
    list.push({ icon: '⚠️', text: '当前台风等级达到超强台风级别，破坏力极强，请高度重视并做好最充分准备。' });
  }
  
  list.push({ icon: '📡', text: '建议持续关注气象部门发布的最新预警信息，保持通讯畅通。' });
  list.push({ icon: '🏠', text: '建议储备至少3天的饮用水、食物和应急药品，确保基本生活需求。' });
  
  return list;
});

function getRiskText(level: string) {
  const texts = {
    critical: '极度危险',
    high: '高风险',
    medium: '中等风险',
    low: '低风险'
  };
  return texts[level as keyof typeof texts] || '未知';
}

function getRiskIcon(level: string) {
  const icons = {
    critical: '🔴',
    high: '🟠',
    medium: '🟡',
    low: '🟢'
  };
  return icons[level as keyof typeof icons] || '⚪';
}

function getRiskDesc(level: string) {
  const descs = {
    critical: '台风将严重影响目标区域，需立即采取紧急防护措施',
    high: '台风对目标区域有较大影响，需加强防范准备',
    medium: '台风对目标区域有一定影响，需保持警惕',
    low: '台风对目标区域影响较小，需关注动态'
  };
  return descs[level as keyof typeof descs] || '';
}

function formatDateTime(dateStr: string) {
  if (!dateStr) return '--';
  return new Date(dateStr).toLocaleString();
}

function formatCoord(value: number | undefined): string {
  if (value === undefined || value === null) return '--';
  return `${value.toFixed(4)}°`;
}

// 初始化雷达图
function initRadarChart() {
  if (!radarChart.value) return;
  
  radarChartInstance = echarts.init(radarChart.value);
  
  const option = {
    radar: {
      indicator: [
        { name: '风力等级', max: 17 },
        { name: '风速', max: 75 },
        { name: '距离风险', max: 100 },
        { name: '气压下降', max: 100 },
        { name: '风圈覆盖', max: 100 },
        { name: '综合影响', max: 100 }
      ],
      shape: 'polygon',
      splitNumber: 5,
      axisName: {
        color: '#333'
      },
      splitLine: {
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: [
          currentFrame.value.grade || 12,
          currentFrame.value.windSpeed || 35,
          isInCircle12.value ? 100 : isInCircle10.value ? 70 : isInCircle7.value ? 40 : 20,
          Math.max(0, 100 - (currentFrame.value.pressure || 950) / 10),
          isInCircle7.value ? 80 : 20,
          riskScore.value
        ],
        name: '风险指标',
        areaStyle: {
          color: overallRisk.value === 'critical' ? 'rgba(255, 0, 0, 0.3)' :
                  overallRisk.value === 'high' ? 'rgba(255, 165, 0, 0.3)' :
                  overallRisk.value === 'medium' ? 'rgba(255, 255, 0, 0.3)' :
                  'rgba(0, 255, 0, 0.3)'
        },
        lineStyle: {
          color: overallRisk.value === 'critical' ? '#ff0000' :
                 overallRisk.value === 'high' ? '#ffa500' :
                 overallRisk.value === 'medium' ? '#ffff00' : '#00ff00'
        }
      }]
    }]
  };
  
  radarChartInstance.setOption(option);
}

// 初始化趋势图
function initTrendChart() {
  if (!trendChart.value) return;
  
  trendChartInstance = echarts.init(trendChart.value);
  
  // 模拟未来趋势数据
  const hours = Array.from({ length: 24 }, (_, i) => i * 6);
  const grades = hours.map((_, i) => Math.max(8, Math.min(16, currentFrame.value.grade + Math.sin(i / 4) * 2)));
  const risks = hours.map((_, i) => {
    const grade = grades[i];
    return Math.min(100, 20 + grade * 4 + Math.random() * 10);
  });
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['风力等级', '风险评分']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: hours.map(h => `${h}h`),
      name: '预测时间'
    },
    yAxis: [
      {
        type: 'value',
        name: '风力等级',
        min: 0,
        max: 18
      },
      {
        type: 'value',
        name: '风险评分',
        min: 0,
        max: 100
      }
    ],
    series: [
      {
        name: '风力等级',
        type: 'line',
        data: grades,
        smooth: true,
        lineStyle: { color: '#1890ff' },
        areaStyle: { color: 'rgba(24, 144, 255, 0.2)' }
      },
      {
        name: '风险评分',
        type: 'line',
        yAxisIndex: 1,
        data: risks,
        smooth: true,
        lineStyle: { color: '#ff4d4f' },
        areaStyle: { color: 'rgba(255, 77, 79, 0.2)' }
      }
    ]
  };
  
  trendChartInstance.setOption(option);
}

// 初始化迷你地图
function initMiniMap() {
  if (!miniMap.value) return;
  
  cesiumViewer = new Cesium.Viewer(miniMap.value, {
    animation: false,
    timeline: false,
    geocoder: false,
    homeButton: false,
    sceneModePicker: false,
    navigationHelpButton: false,
    fullscreenButton: false,
    infoBox: false,
    selectionIndicator: false,
    baseLayerPicker: false,
    terrainProvider: new Cesium.EllipsoidTerrainProvider()
  });
  
  cesiumViewer.scene.globe.enableLighting = false;
  cesiumViewer.scene.fog.enabled = false;
  if (cesiumViewer.scene.skyAtmosphere) {
    cesiumViewer.scene.skyAtmosphere.show = false;
  }
  
  // 台风位置
  const typhoonPos = Cesium.Cartesian3.fromDegrees(
    typhoonPosition.value.lng,
    typhoonPosition.value.lat
  );
  
  // 台风中心点
  cesiumViewer.entities.add({
    position: typhoonPos,
    point: {
      pixelSize: 12,
      color: Cesium.Color.RED
    },
    label: {
      text: '台风中心',
      font: '14px sans-serif',
      fillColor: Cesium.Color.WHITE,
      outlineColor: Cesium.Color.BLACK,
      outlineWidth: 2
    }
  });
  
  // 7级风圈
  cesiumViewer.entities.add({
    position: typhoonPos,
    ellipse: {
      semiMajorAxis: windCircles.value.level7.radius * 1000,
      semiMinorAxis: windCircles.value.level7.radius * 1000,
      material: Cesium.Color.LIMEGREEN.withAlpha(0.25)
    }
  });
  
  // 10级风圈
  cesiumViewer.entities.add({
    position: typhoonPos,
    ellipse: {
      semiMajorAxis: windCircles.value.level10.radius * 1000,
      semiMinorAxis: windCircles.value.level10.radius * 1000,
      material: Cesium.Color.ORANGE.withAlpha(0.35)
    }
  });
  
  // 12级风圈
  cesiumViewer.entities.add({
    position: typhoonPos,
    ellipse: {
      semiMajorAxis: windCircles.value.level12.radius * 1000,
      semiMinorAxis: windCircles.value.level12.radius * 1000,
      material: Cesium.Color.RED.withAlpha(0.45)
    }
  });
  
  // 城市位置
  if (report.value.latitude && report.value.longitude) {
    const cityPos = Cesium.Cartesian3.fromDegrees(
      report.value.longitude,
      report.value.latitude
    );
    
    cesiumViewer.entities.add({
      position: cityPos,
      point: {
        pixelSize: 10,
        color: Cesium.Color.BLUE
      },
      label: {
        text: report.value.cityName || '目标城市',
        font: '14px sans-serif',
        fillColor: Cesium.Color.WHITE,
        outlineColor: Cesium.Color.BLACK,
        outlineWidth: 2
      }
    });
  }
  
  // 飞到台风位置
  cesiumViewer.camera.flyTo({
    destination: Cesium.Cartesian3.fromDegrees(
      typhoonPosition.value.lng,
      typhoonPosition.value.lat,
      500000
    ),
    duration: 2
  });
}

async function loadReportData() {
  // 优先从sessionStorage读取数据（从地图页或预警报告页传递过来的实时数据）
  const storedData = sessionStorage.getItem('currentAnalysis');
  if (storedData) {
    const data = JSON.parse(storedData);
    report.value = data;
    
    // 如果没有传递坐标，从消息中解析
    if ((data.latitude === undefined || data.latitude === null) && data.message) {
      const latMatch = data.message.match(/纬度[:\s]*([\d.]+)/);
      const lngMatch = data.message.match(/经度[:\s]*([\d.]+)/);
      if (latMatch) data.latitude = parseFloat(latMatch[1]);
      if (lngMatch) data.longitude = parseFloat(lngMatch[1]);
    }
    
    // 直接使用传递过来的实时数据
    if (data.typhoonLat !== undefined) {
      typhoonPosition.value.lat = data.typhoonLat;
    }
    if (data.typhoonLng !== undefined) {
      typhoonPosition.value.lng = data.typhoonLng;
    }
    if (data.grade !== undefined) {
      currentFrame.value.grade = data.grade;
    }
    if (data.windSpeed !== undefined) {
      currentFrame.value.windSpeed = data.windSpeed;
    }
    if (data.pressure !== undefined) {
      currentFrame.value.pressure = data.pressure;
    }
    
    // 清除sessionStorage中的数据
    sessionStorage.removeItem('currentAnalysis');
    return;
  }
  
  // 如果sessionStorage没有数据，从API获取
  const reportId = route.params.id;
  if (!reportId) return;
  
  try {
    const res = await axios.get(`http://localhost:8080/api/alert/report/detail/${reportId}`);
    report.value = res.data.data;
    
    // 从预警消息中解析台风数据
    parseTyphoonData(report.value.message);
    
  } catch (error) {
    console.error('加载预警详情失败', error);
  }
}

function parseTyphoonData(message: string) {
  if (!message) return;
  
  // 从消息中解析经纬度、等级等数据
  const latMatch = message.match(/纬度[:\s]*([\d.]+)/);
  const lngMatch = message.match(/经度[:\s]*([\d.]+)/);
  const gradeMatch = message.match(/等级[:\s]*(\d+)/);
  const speedMatch = message.match(/风速[:\s]*([\d.]+)/);
  const pressureMatch = message.match(/气压[:\s]*([\d.]+)/);
  
  if (latMatch) typhoonPosition.value.lat = parseFloat(latMatch[1]);
  if (lngMatch) typhoonPosition.value.lng = parseFloat(lngMatch[1]);
  if (gradeMatch) currentFrame.value.grade = parseInt(gradeMatch[1]);
  if (speedMatch) currentFrame.value.windSpeed = parseFloat(speedMatch[1]);
  if (pressureMatch) currentFrame.value.pressure = parseFloat(pressureMatch[1]);
}

function goBack() {
  router.push('/alert');
}

onMounted(async () => {
  await loadReportData();
  
  initRadarChart();
  initTrendChart();
  initMiniMap();
  
  // 窗口大小变化时重新渲染图表
  window.addEventListener('resize', () => {
    radarChartInstance?.resize();
    trendChartInstance?.resize();
  });
});

onBeforeUnmount(() => {
  radarChartInstance?.dispose();
  trendChartInstance?.dispose();
  cesiumViewer?.destroy();
  window.removeEventListener('resize', () => {});
});
</script>

<style scoped>
.analysis-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.back-btn {
  padding: 10px 20px;
  background: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #f0f0f0;
  transform: translateX(-3px);
}

.page-header h2 {
  color: white;
  font-size: 24px;
  margin: 0;
}

.analysis-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.section-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
  padding-left: 12px;
  border-left: 4px solid #1890ff;
}

/* 基本信息 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 13px;
  color: #666;
}

.info-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.level-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-weight: 600;
}

.level-1, .level-2 { background: #00d5cb; color: white; }
.level-3, .level-4 { background: #fcfa00; color: #333; }
.level-5, .level-6 { background: #fdaa09; color: white; }
.level-7, .level-8 { background: #fb3320; color: white; }
.level-9, .level-10 { background: #f600a9; color: white; }
.level-11, .level-12 { background: #aa0000; color: white; }

/* 地图 */
.map-wrapper {
  display: flex;
  gap: 20px;
}

.mini-map {
  width: 500px;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
}

.map-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
}

.circle-7 { background: rgba(50, 205, 50, 0.3); border: 2px solid #32cd32; }
.circle-10 { background: rgba(255, 165, 0, 0.3); border: 2px solid #ffa500; }
.circle-12 { background: rgba(255, 0, 0, 0.3); border: 2px solid #ff0000; }

.impact-status {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-7 { color: #32cd32; font-weight: 600; }
.status-10 { color: #ffa500; font-weight: 600; }
.status-12 { color: #ff0000; font-weight: 600; }

/* 风险等级卡 */
.risk-level-card {
  padding: 32px;
  text-align: center;
  border-radius: 12px;
}

.level-critical { background: linear-gradient(135deg, #ff0000, #ff4444); color: white; }
.level-high { background: linear-gradient(135deg, #ffa500, #ffcc00); color: white; }
.level-medium { background: linear-gradient(135deg, #ffff00, #ffdd00); color: #333; }
.level-low { background: linear-gradient(135deg, #00ff00, #44ff44); color: white; }

.risk-icon { font-size: 48px; margin-bottom: 8px; }
.risk-level { font-size: 28px; font-weight: 700; margin-bottom: 8px; }
.risk-score { font-size: 36px; font-weight: 800; margin-bottom: 8px; }
.risk-desc { font-size: 14px; opacity: 0.9; }

/* 表格 */
.analysis-table {
  width: 100%;
  border-collapse: collapse;
}

.analysis-table th, .analysis-table td {
  padding: 12px;
  border: 1px solid #eee;
  text-align: center;
}

.analysis-table th {
  background: #f5f5f5;
  font-weight: 600;
}

.level-cell {
  font-weight: 600;
}

.level-7 { background: rgba(50, 205, 50, 0.2); color: #32cd32; }
.level-10 { background: rgba(255, 165, 0, 0.2); color: #ffa500; }
.level-12 { background: rgba(255, 0, 0, 0.2); color: #ff0000; }

.in-circle { color: #ff4d4f; font-weight: 600; }
.out-circle { color: #52c41a; }

/* 图表行 */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-box, .metrics-box {
  background: white;
  border-radius: 12px;
  padding: 24px;
}

.radar-chart {
  height: 300px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.metric-card {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.metric-icon { font-size: 32px; margin-bottom: 8px; }
.metric-label { font-size: 13px; color: #666; margin-bottom: 4px; }
.metric-value { font-size: 24px; font-weight: 700; color: #1890ff; }
.metric-unit { font-size: 12px; color: #999; }

/* 趋势图 */
.trend-chart-container {
  height: 300px;
}

/* 建议 */
.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.suggestion-icon { font-size: 20px; }
.suggestion-text { font-size: 14px; color: #333; line-height: 1.5; }

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
  
  .map-wrapper {
    flex-direction: column;
  }
  
  .mini-map {
    width: 100%;
  }
}
</style>