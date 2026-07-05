<template>
  <div class="analysis-view">
    <div class="bg-animation"></div>
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>

    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
        <span>返回预警报告</span>
      </button>
      <h2>🌪 台风预警详细分析</h2>
    </div>

    <div class="analysis-container">
      <div class="section basic-info">
        <div class="section-header">
          <div class="section-icon">📋</div>
          <h3 class="section-title">基本信息</h3>
        </div>
        <div class="info-grid">
          <div class="info-card">
            <span class="info-icon">⚠️</span>
            <div class="info-content">
              <span class="info-label">预警等级</span>
              <span class="info-value level-badge level-{{ report.level }}">{{ report.level }}级</span>
            </div>
          </div>
          <div class="info-card">
            <span class="info-icon">🏙️</span>
            <div class="info-content">
              <span class="info-label">目标城市</span>
              <span class="info-value">{{ report.cityName || '自定义位置' }}</span>
            </div>
          </div>
          <div class="info-card">
            <span class="info-icon">📍</span>
            <div class="info-content">
              <span class="info-label">城市坐标</span>
              <span class="info-value">{{ formatCoord(report.latitude) }}, {{ formatCoord(report.longitude) }}</span>
            </div>
          </div>
          <div class="info-card">
            <span class="info-icon">📏</span>
            <div class="info-content">
              <span class="info-label">距离台风</span>
              <span class="info-value">{{ report.distance }} km</span>
            </div>
          </div>
          <div class="info-card">
            <span class="info-icon">🕐</span>
            <div class="info-content">
              <span class="info-label">预警时间</span>
              <span class="info-value">{{ formatDateTime(report.createTime) }}</span>
            </div>
          </div>
          <div class="info-card">
            <span class="info-icon">⏱️</span>
            <div class="info-content">
              <span class="info-label">预计到达</span>
              <span class="info-value">{{ formatDateTime(report.triggerTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section map-section">
        <div class="section-header">
          <div class="section-icon">🗺️</div>
          <h3 class="section-title">台风风圈影响地图</h3>
        </div>
        <div class="map-wrapper">
          <div ref="miniMap" class="mini-map"></div>
          <div class="map-info">
            <div class="wind-circle legend">
              <span class="legend-item circle-7">
                <span class="legend-dot" style="background: #32cd32;"></span>
                7级风圈 ({{ windCircles.level7.radius }} km)
              </span>
              <span class="legend-item circle-10">
                <span class="legend-dot" style="background: #ffa500;"></span>
                10级风圈 ({{ windCircles.level10.radius }} km)
              </span>
              <span class="legend-item circle-12">
                <span class="legend-dot" style="background: #ff0000;"></span>
                12级风圈 ({{ windCircles.level12.radius }} km)
              </span>
            </div>
            <div class="impact-status">
              <span v-if="isInCircle7" class="status-badge status-7">✓ 在7级风圈内</span>
              <span v-if="isInCircle10" class="status-badge status-10">✓ 在10级风圈内</span>
              <span v-if="isInCircle12" class="status-badge status-12">✓ 在12级风圈内</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section risk-card">
        <div class="section-header">
          <div class="section-icon">📊</div>
          <h3 class="section-title">综合风险评估</h3>
        </div>
        <div class="risk-level-card level-{{ overallRisk }}">
          <div class="risk-header">
            <div class="risk-icon">{{ getRiskIcon(overallRisk) }}</div>
            <div class="risk-info">
              <div class="risk-level">{{ getRiskText(overallRisk) }}</div>
              <div class="risk-score">{{ riskScore.toFixed(1) }}</div>
            </div>
          </div>
          <div class="risk-bar">
            <div class="risk-bar-fill" :style="{ width: riskScore + '%' }"></div>
          </div>
          <div class="risk-desc">{{ getRiskDesc(overallRisk) }}</div>
        </div>
      </div>

      <div class="section wind-table">
        <div class="section-header">
          <div class="section-icon">📈</div>
          <h3 class="section-title">风圈影响详细分析</h3>
        </div>
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
            <tr class="table-row">
              <td class="level-cell level-7">7级风圈</td>
              <td>{{ windCircles.level7.radius }}</td>
              <td>{{ windCircles.level7.area }}</td>
              <td>17.2-24.4 m/s</td>
              <td><span class="impact-tag mild">轻度影响</span></td>
              <td :class="isInCircle7 ? 'in-circle' : 'out-circle'">
                <span :class="isInCircle7 ? 'status-check' : 'status-cross'">
                  {{ isInCircle7 ? '✓' : '✗' }}
                </span>
              </td>
            </tr>
            <tr class="table-row">
              <td class="level-cell level-10">10级风圈</td>
              <td>{{ windCircles.level10.radius }}</td>
              <td>{{ windCircles.level10.area }}</td>
              <td>24.5-32.6 m/s</td>
              <td><span class="impact-tag moderate">中度影响</span></td>
              <td :class="isInCircle10 ? 'in-circle' : 'out-circle'">
                <span :class="isInCircle10 ? 'status-check' : 'status-cross'">
                  {{ isInCircle10 ? '✓' : '✗' }}
                </span>
              </td>
            </tr>
            <tr class="table-row">
              <td class="level-cell level-12">12级风圈</td>
              <td>{{ windCircles.level12.radius }}</td>
              <td>{{ windCircles.level12.area }}</td>
              <td>≥32.7 m/s</td>
              <td><span class="impact-tag severe">重度影响</span></td>
              <td :class="isInCircle12 ? 'in-circle' : 'out-circle'">
                <span :class="isInCircle12 ? 'status-check' : 'status-cross'">
                  {{ isInCircle12 ? '✓' : '✗' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="section charts-row">
        <div class="chart-box">
          <div class="section-header">
            <div class="section-icon">🎯</div>
            <h3 class="section-title">综合风险雷达图</h3>
          </div>
          <div ref="radarChart" class="radar-chart"></div>
        </div>
        <div class="metrics-box">
          <div class="section-header">
            <div class="section-icon">⚙️</div>
            <h3 class="section-title">关键指标</h3>
          </div>
          <div class="metrics-grid">
            <div class="metric-card">
              <div class="metric-icon blue">💨</div>
              <div class="metric-info">
                <div class="metric-label">风速</div>
                <div class="metric-value">{{ currentFrame.windSpeed?.toFixed(1) || '--' }}</div>
                <div class="metric-unit">m/s</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon purple">📊</div>
              <div class="metric-info">
                <div class="metric-label">风力等级</div>
                <div class="metric-value">{{ currentFrame.grade || '--' }}</div>
                <div class="metric-unit">级</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon orange">🌡️</div>
              <div class="metric-info">
                <div class="metric-label">中心气压</div>
                <div class="metric-value">{{ currentFrame.pressure?.toFixed(0) || '--' }}</div>
                <div class="metric-unit">hPa</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon green">📍</div>
              <div class="metric-info">
                <div class="metric-label">台风位置</div>
                <div class="metric-value">{{ typhoonPosition.lat?.toFixed(2) }}°N</div>
                <div class="metric-value small">{{ typhoonPosition.lng?.toFixed(2) }}°E</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="section trend-chart">
        <div class="section-header">
          <div class="section-icon">📉</div>
          <h3 class="section-title">风险等级趋势预测</h3>
        </div>
        <div ref="trendChart" class="trend-chart-container"></div>
      </div>

      <div class="section suggestions">
        <div class="section-header">
          <div class="section-icon">💡</div>
          <h3 class="section-title">系统分析建议</h3>
        </div>
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

const isInCircle7 = computed(() => {
  return report.value.distance <= windCircles.value.level7.radius;
});

const isInCircle10 = computed(() => {
  return report.value.distance <= windCircles.value.level10.radius;
});

const isInCircle12 = computed(() => {
  return report.value.distance <= windCircles.value.level12.radius;
});

const riskScore = computed(() => {
  let score = 0;
  if (isInCircle12.value) score += 50;
  else if (isInCircle10.value) score += 30;
  else if (isInCircle7.value) score += 15;
  
  score += (currentFrame.value.grade || 12) * 3;
  if (currentFrame.value.windSpeed > 40) score += 10;
  
  return Math.min(100, score);
});

const overallRisk = computed(() => {
  if (riskScore.value >= 80) return 'critical';
  if (riskScore.value >= 60) return 'high';
  if (riskScore.value >= 40) return 'medium';
  return 'low';
});

const radarChart = ref<HTMLElement | null>(null);
const trendChart = ref<HTMLElement | null>(null);
const miniMap = ref<HTMLElement | null>(null);

let radarChartInstance: echarts.ECharts | null = null;
let trendChartInstance: echarts.ECharts | null = null;
let cesiumViewer: Cesium.Viewer | null = null;

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

function initRadarChart() {
  if (!radarChart.value) return;
  
  radarChartInstance = echarts.init(radarChart.value);
  
  const option = {
    backgroundColor: 'transparent',
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
        color: '#fff',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      },
      splitArea: {
        areaStyle: {
          color: ['rgba(255,255,255,0.05)', 'rgba(255,255,255,0.1)']
        }
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.2)'
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
          color: overallRisk.value === 'critical' ? 'rgba(255, 0, 0, 0.4)' :
                  overallRisk.value === 'high' ? 'rgba(255, 165, 0, 0.4)' :
                  overallRisk.value === 'medium' ? 'rgba(255, 255, 0, 0.4)' :
                  'rgba(0, 255, 0, 0.4)'
        },
        lineStyle: {
          color: overallRisk.value === 'critical' ? '#ff0000' :
                 overallRisk.value === 'high' ? '#ffa500' :
                 overallRisk.value === 'medium' ? '#ffff00' : '#00ff00',
          width: 2
        },
        itemStyle: {
          color: overallRisk.value === 'critical' ? '#ff0000' :
                 overallRisk.value === 'high' ? '#ffa500' :
                 overallRisk.value === 'medium' ? '#ffff00' : '#00ff00'
        }
      }]
    }]
  };
  
  radarChartInstance.setOption(option);
}

function initTrendChart() {
  if (!trendChart.value) return;
  
  trendChartInstance = echarts.init(trendChart.value);
  
  const hours = Array.from({ length: 24 }, (_, i) => i * 6);
  const grades = hours.map((_, i) => Math.max(8, Math.min(16, currentFrame.value.grade + Math.sin(i / 4) * 2)));
  const risks = hours.map((_, i) => {
    const grade = grades[i];
    return Math.min(100, 20 + grade * 4 + Math.random() * 10);
  });
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'rgba(255, 255, 255, 0.1)',
      textStyle: { color: '#fff' }
    },
    legend: {
      data: ['风力等级', '风险评分'],
      textStyle: { color: '#fff' }
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
      name: '预测时间',
      nameTextStyle: { color: '#fff', fontSize: 12 },
      axisLabel: { color: 'rgba(255,255,255,0.7)', fontSize: 11 },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
      axisTick: { show: false }
    },
    yAxis: [
      {
        type: 'value',
        name: '风力等级',
        min: 0,
        max: 18,
        nameTextStyle: { color: '#fff', fontSize: 12 },
        axisLabel: { color: 'rgba(255,255,255,0.7)', fontSize: 11 },
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
      },
      {
        type: 'value',
        name: '风险评分',
        min: 0,
        max: 100,
        nameTextStyle: { color: '#fff', fontSize: 12 },
        axisLabel: { color: 'rgba(255,255,255,0.7)', fontSize: 11 },
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '风力等级',
        type: 'line',
        data: grades,
        smooth: true,
        lineStyle: { color: '#00C9FF', width: 2 },
        areaStyle: { color: 'rgba(0, 201, 255, 0.2)' },
        itemStyle: { color: '#00C9FF' }
      },
      {
        name: '风险评分',
        type: 'line',
        yAxisIndex: 1,
        data: risks,
        smooth: true,
        lineStyle: { color: '#ff6b6b', width: 2 },
        areaStyle: { color: 'rgba(255, 107, 107, 0.2)' },
        itemStyle: { color: '#ff6b6b' }
      }
    ]
  };
  
  trendChartInstance.setOption(option);
}

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
  
  const typhoonPos = Cesium.Cartesian3.fromDegrees(
    typhoonPosition.value.lng,
    typhoonPosition.value.lat
  );
  
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
  
  cesiumViewer.entities.add({
    position: typhoonPos,
    ellipse: {
      semiMajorAxis: windCircles.value.level7.radius * 1000,
      semiMinorAxis: windCircles.value.level7.radius * 1000,
      material: Cesium.Color.LIMEGREEN.withAlpha(0.25)
    }
  });
  
  cesiumViewer.entities.add({
    position: typhoonPos,
    ellipse: {
      semiMajorAxis: windCircles.value.level10.radius * 1000,
      semiMinorAxis: windCircles.value.level10.radius * 1000,
      material: Cesium.Color.ORANGE.withAlpha(0.35)
    }
  });
  
  cesiumViewer.entities.add({
    position: typhoonPos,
    ellipse: {
      semiMajorAxis: windCircles.value.level12.radius * 1000,
      semiMinorAxis: windCircles.value.level12.radius * 1000,
      material: Cesium.Color.RED.withAlpha(0.45)
    }
  });
  
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
  const storedData = sessionStorage.getItem('currentAnalysis');
  if (storedData) {
    const data = JSON.parse(storedData);
    report.value = data;
    
    if ((data.latitude === undefined || data.latitude === null) && data.message) {
      const latMatch = data.message.match(/纬度[:\s]*([\d.]+)/);
      const lngMatch = data.message.match(/经度[:\s]*([\d.]+)/);
      if (latMatch) data.latitude = parseFloat(latMatch[1]);
      if (lngMatch) data.longitude = parseFloat(lngMatch[1]);
    }
    
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
    
    sessionStorage.removeItem('currentAnalysis');
    return;
  }
  
  const reportId = route.params.id;
  if (!reportId) return;
  
  try {
    const res = await axios.get(`http://localhost:8080/api/alert/report/detail/${reportId}`);
    report.value = res.data.data;
    parseTyphoonData(report.value.message);
    
  } catch (error) {
    console.error('加载预警详情失败', error);
  }
}

function parseTyphoonData(message: string) {
  if (!message) return;
  
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
  color: #fff;
  font-size: 28px;
  margin: 0;
  font-weight: 700;
  letter-spacing: 1px;
}

.analysis-container {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.section {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 28px;
  margin-bottom: 24px;
  transition: all 0.3s ease;
}

.section:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.section-icon {
  font-size: 24px;
}

.section-title {
  font-size: 20px;
  color: #fff;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.info-card:hover {
  background: rgba(255, 255, 255, 0.1);
}

.info-icon {
  font-size: 28px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.level-badge {
  padding: 4px 14px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 14px;
}

.level-1, .level-2 { background: linear-gradient(135deg, #00d5cb, #00b894); color: white; }
.level-3, .level-4 { background: linear-gradient(135deg, #fcfa00, #ffe000); color: #333; }
.level-5, .level-6 { background: linear-gradient(135deg, #fdaa09, #fdcb6e); color: white; }
.level-7, .level-8 { background: linear-gradient(135deg, #fb3320, #e17055); color: white; }
.level-9, .level-10 { background: linear-gradient(135deg, #f600a9, #fd79a8); color: white; }
.level-11, .level-12 { background: linear-gradient(135deg, #aa0000, #d63031); color: white; }

.map-wrapper {
  display: flex;
  gap: 24px;
}

.mini-map {
  flex: 1;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.map-info {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.impact-status {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.status-badge {
  display: inline-block;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
}

.status-7 { background: rgba(50, 205, 50, 0.2); color: #32cd32; border: 1px solid rgba(50, 205, 50, 0.3); }
.status-10 { background: rgba(255, 165, 0, 0.2); color: #ffa500; border: 1px solid rgba(255, 165, 0, 0.3); }
.status-12 { background: rgba(255, 0, 0, 0.2); color: #ff0000; border: 1px solid rgba(255, 0, 0, 0.3); }

.risk-level-card {
  padding: 32px;
  border-radius: 16px;
  text-align: center;
}

.level-critical { background: linear-gradient(135deg, rgba(255, 0, 0, 0.3), rgba(255, 68, 68, 0.2)); border: 1px solid rgba(255, 0, 0, 0.3); }
.level-high { background: linear-gradient(135deg, rgba(255, 165, 0, 0.3), rgba(255, 204, 0, 0.2)); border: 1px solid rgba(255, 165, 0, 0.3); }
.level-medium { background: linear-gradient(135deg, rgba(255, 255, 0, 0.3), rgba(255, 221, 0, 0.2)); border: 1px solid rgba(255, 255, 0, 0.3); }
.level-low { background: linear-gradient(135deg, rgba(0, 255, 0, 0.3), rgba(68, 255, 68, 0.2)); border: 1px solid rgba(0, 255, 0, 0.3); }

.risk-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.risk-icon {
  font-size: 56px;
}

.risk-info {
  text-align: left;
}

.risk-level {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.risk-score {
  font-size: 40px;
  font-weight: 800;
  color: #fff;
}

.risk-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.risk-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #00C9FF, #92FE9D);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.risk-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.analysis-table {
  width: 100%;
  border-collapse: collapse;
}

.analysis-table th, .analysis-table td {
  padding: 14px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.analysis-table th {
  background: rgba(255, 255, 255, 0.05);
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.table-row {
  transition: all 0.3s ease;
}

.table-row:hover {
  background: rgba(255, 255, 255, 0.05);
}

.level-cell {
  font-weight: 700;
  font-size: 15px;
}

.level-7 { background: rgba(50, 205, 50, 0.15); color: #32cd32; }
.level-10 { background: rgba(255, 165, 0, 0.15); color: #ffa500; }
.level-12 { background: rgba(255, 0, 0, 0.15); color: #ff0000; }

.impact-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.impact-tag.mild { background: rgba(50, 205, 50, 0.2); color: #32cd32; }
.impact-tag.moderate { background: rgba(255, 165, 0, 0.2); color: #ffa500; }
.impact-tag.severe { background: rgba(255, 0, 0, 0.2); color: #ff0000; }

.status-check {
  color: #52c41a;
  font-weight: 700;
  font-size: 18px;
}

.status-cross {
  color: rgba(255, 255, 255, 0.3);
  font-size: 18px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.chart-box, .metrics-box {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 28px;
}

.radar-chart {
  height: 320px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.metric-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.metric-card:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-3px);
}

.metric-icon {
  font-size: 36px;
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 12px;
}

.metric-icon.blue { background: rgba(0, 201, 255, 0.2); }
.metric-icon.purple { background: rgba(102, 126, 234, 0.2); }
.metric-icon.orange { background: rgba(255, 197, 61, 0.2); }
.metric-icon.green { background: rgba(149, 222, 100, 0.2); }

.metric-info {
  text-align: center;
}

.metric-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 4px;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.metric-value.small {
  font-size: 18px;
}

.metric-unit {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 2px;
}

.trend-chart-container {
  height: 320px;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.suggestion-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(5px);
}

.suggestion-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.suggestion-text {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
}

@media (max-width: 1024px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .map-wrapper {
    flex-direction: column;
  }
  
  .mini-map {
    width: 100%;
  }
  
  .map-info {
    width: 100%;
  }
}
</style>
