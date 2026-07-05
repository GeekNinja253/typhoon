<template>
  <div class="home-page">
    <div class="bg-animation"></div>
    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>
    <div class="bg-blob blob-3"></div>

    <div class="header">
      <div class="logo-section">
        <div class="logo-icon">🌪</div>
        <div>
          <h1>台风路径分析系统</h1>
          <p>Typhoon Prediction Platform</p>
        </div>
      </div>

      <div class="user-section">
        <div class="user-info">
          <span class="user-icon">👤</span>
          <span class="user-name">{{ username }}</span>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <span>退出登录</span>
          <span class="logout-icon">🚪</span>
        </button>
      </div>
    </div>

    <div class="welcome-section">
      <div class="welcome-content">
        <h2>
          <span class="greeting">欢迎回来，</span>
          <span class="username-highlight">{{ username }}</span>
        </h2>
        <p class="welcome-desc">
          基于机器学习的台风路径预测与分析平台
        </p>
        <div class="current-time">
          当前时间：{{ currentTime }}
        </div>
      </div>
      <div class="welcome-visual">
        <div class="typhoon-animation">
          <div class="typhoon-circle"></div>
          <div class="typhoon-ring ring-1"></div>
          <div class="typhoon-ring ring-2"></div>
          <div class="typhoon-ring ring-3"></div>
        </div>
      </div>
    </div>

    <div class="stats-section">
      <div class="stat-card stat-alerts">
        <div class="stat-icon">🔔</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.alertCount }}</div>
          <div class="stat-label">预警记录</div>
          <div class="stat-sub">未读 {{ stats.unreadCount }} 条</div>
        </div>
        <div class="stat-trend positive">↑ {{ stats.alertTrend }}%</div>
      </div>

      <div class="stat-card stat-history">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.historyCount }}</div>
          <div class="stat-label">历史任务</div>
          <div class="stat-sub">未读 {{ stats.historyUnreadCount }}</div>
        </div>
        <div class="stat-trend positive">↑ {{ stats.historyTrend }}%</div>
      </div>

      <div class="stat-card stat-subscriptions">
        <div class="stat-icon">📍</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.activeCities.length }}</div>
          <div class="stat-label">已启用订阅</div>
          <div class="stat-sub">{{ stats.activeCities.join('、') || '暂无' }}</div>
        </div>
        <div class="stat-trend neutral">—</div>
      </div>
    </div>

    <div class="quick-actions">
      <h3 class="section-title">
        <span class="title-icon">⚡</span>
        快速操作
      </h3>
      <div class="action-grid">
        <div class="action-card" @click="$router.push('/map')">
          <div class="action-icon-wrapper blue">
            <span class="action-icon">🌪</span>
          </div>
          <div class="action-info">
            <h4>GIS地图分析</h4>
            <p>查看台风路径预测结果，实时追踪台风动态</p>
          </div>
          <div class="action-arrow">→</div>
        </div>

        <div class="action-card" @click="$router.push('/history')">
          <div class="action-icon-wrapper purple">
            <span class="action-icon">📜</span>
          </div>
          <div class="action-info">
            <h4>历史预测记录</h4>
            <p>查看历史任务与预测数据，分析预测趋势</p>
          </div>
          <div class="action-arrow">→</div>
        </div>

        <div class="action-card" @click="$router.push('/alert')">
          <div class="action-icon-wrapper orange">
            <span class="action-icon">🔔</span>
          </div>
          <div class="action-info">
            <h4>预警报告</h4>
            <p>订阅台风预警，获取实时通知与详细分析</p>
          </div>
          <div class="action-arrow">→</div>
        </div>
      </div>
    </div>

    <div class="tips-section">
      <h3 class="section-title">
        <span class="title-icon">�</span>
        台风防御小贴士
      </h3>
      <div class="tips-grid">
        <div class="tip-card">
          <div class="tip-icon">🏠</div>
          <h4>居家防范</h4>
          <p>检查门窗加固，准备应急物资，关闭不必要电源</p>
        </div>
        <div class="tip-card">
          <div class="tip-icon">🚶</div>
          <h4>户外安全</h4>
          <p>减少外出，远离广告牌和高大树木，注意高空坠物</p>
        </div>
        <div class="tip-card">
          <div class="tip-icon">🚢</div>
          <h4>海上作业</h4>
          <p>小型船只及时回港避风，海上作业人员注意安全</p>
        </div>
      </div>
    </div>

    <div class="footer">
      <p>© 2026 台风路径分析系统 | 基于机器学习的智能预测平台</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

const user = JSON.parse(localStorage.getItem('user') || '{}');
const username = user.username || '用户';
const userId = user.id || 1;

const currentTime = ref('');
const stats = ref({
  alertCount: 3,
  unreadCount: 2,
  alertTrend: 15,
  historyCount: 10,
  historyUnreadCount: 5,
  historyTrend: 10,
  activeCities: ['高雄', '厦门']
});

let timeInterval: ReturnType<typeof setInterval>;

function updateTime() {
  const now = new Date();
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
}

async function loadStats() {
  let alertCount = 3;
  let unreadCount = 2;
  let historyCount = 10;
  let historyUnreadCount = 5;
  let activeCities: string[] = ['高雄', '厦门'];

  try {
    const historyRes = await axios.get('http://localhost:8080/api/history/tasks');
    const tasks = historyRes.data.data || [];
    historyCount = tasks.length;
    
    const readTasks = JSON.parse(localStorage.getItem('history_read_tasks') || '[]');
    historyUnreadCount = tasks.filter((task: any) => !readTasks.includes(task.id)).length;
  } catch (error) {
    console.warn('加载历史任务失败，使用默认数据', error);
  }

  try {
    const alertRes = await axios.get(`http://localhost:8080/api/alert/report/${userId}`);
    const alerts = alertRes.data.data || [];
    alertCount = alerts.length;
    unreadCount = alerts.filter((a: any) => a.status === 0).length;
  } catch (error) {
    console.warn('加载预警记录失败，使用默认数据', error);
  }

  try {
    const subRes = await axios.get(`http://localhost:8080/api/alert/subscription/${userId}`);
    const subs = subRes.data.data || [];
    activeCities = subs
      .filter((s: any) => s.status === 1)
      .map((s: any) => s.cityName || '自定义位置');
  } catch (error) {
    console.warn('加载订阅信息失败，使用默认数据', error);
  }

  stats.value = {
    alertCount,
    unreadCount,
    alertTrend: Math.min(100, Math.round((alertCount / (alertCount + 10)) * 50)),
    historyCount,
    historyUnreadCount,
    historyTrend: Math.min(100, Math.round((historyCount / (historyCount + 15)) * 40)),
    activeCities
  };
}

function handleLogout() {
  localStorage.removeItem('user');
  router.push('/login');
}

onMounted(() => {
  updateTime();
  timeInterval = setInterval(updateTime, 1000);
  loadStats();
});

onUnmounted(() => {
  clearInterval(timeInterval);
});
</script>

<style scoped>
.home-page {
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
    radial-gradient(circle at 80% 20%, rgba(0, 201, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(95, 222, 100, 0.05) 0%, transparent 50%);
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
  opacity: 0.3;
  z-index: 0;
}

.blob-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -100px;
  left: -100px;
  animation: blobMove1 15s ease-in-out infinite;
}

.blob-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #00C9FF, #92FE9D);
  bottom: -50px;
  right: -50px;
  animation: blobMove2 12s ease-in-out infinite;
}

.blob-3 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #ff6b6b, #ee5253);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: blobMove3 18s ease-in-out infinite;
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

@keyframes blobMove3 {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.2); }
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  font-size: 48px;
  animation: logoSpin 8s linear infinite;
}

@keyframes logoSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.logo-section h1 {
  margin: 0;
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
}

.logo-section p {
  margin: 4px 0 0 0;
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.user-icon {
  font-size: 18px;
}

.user-name {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: white;
  background: linear-gradient(135deg, #ff6b6b, #ee5253);
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(238, 82, 83, 0.3);
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  margin-bottom: 40px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 1;
}

.welcome-content h2 {
  margin: 0;
  font-size: 32px;
  color: #fff;
  line-height: 1.4;
}

.greeting {
  color: rgba(255, 255, 255, 0.8);
}

.username-highlight {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-desc {
  margin: 12px 0 0 0;
  color: rgba(255, 255, 255, 0.6);
  font-size: 16px;
}

.current-time {
  margin: 20px 0 0 0;
  padding: 10px 20px;
  background: rgba(0, 201, 255, 0.1);
  border-radius: 8px;
  color: #00C9FF;
  font-size: 14px;
  font-family: 'Courier New', monospace;
}

.welcome-visual {
  flex-shrink: 0;
}

.typhoon-animation {
  position: relative;
  width: 150px;
  height: 150px;
}

.typhoon-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, #fff 0%, #00C9FF 50%, transparent 100%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: typhoonPulse 2s ease-in-out infinite;
}

@keyframes typhoonPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  50% { transform: translate(-50%, -50%) scale(1.3); opacity: 0.7; }
}

.typhoon-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 2px solid rgba(0, 201, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.ring-1 {
  width: 80px;
  height: 80px;
  animation: ringRotate1 3s linear infinite;
}

.ring-2 {
  width: 110px;
  height: 110px;
  animation: ringRotate2 4s linear infinite;
  border-color: rgba(102, 126, 234, 0.3);
}

.ring-3 {
  width: 140px;
  height: 140px;
  animation: ringRotate1 5s linear infinite;
  border-color: rgba(95, 222, 100, 0.3);
}

@keyframes ringRotate1 {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes ringRotate2 {
  0% { transform: translate(-50%, -50%) rotate(360deg); }
  100% { transform: translate(-50%, -50%) rotate(0deg); }
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.stat-alerts {
  border-left: 4px solid #ff6b6b;
}

.stat-history {
  border-left: 4px solid #667eea;
}

.stat-subscriptions {
  border-left: 4px solid #00C9FF;
}

.stat-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

.stat-label {
  margin-top: 4px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.stat-sub {
  margin-top: 2px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.stat-trend {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
}

.stat-trend.positive {
  color: #95de64;
  background: rgba(149, 222, 100, 0.1);
}

.stat-trend.neutral {
  color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 24px 0;
  font-size: 20px;
  color: #fff;
  font-weight: 600;
}

.title-icon {
  font-size: 20px;
}

.quick-actions {
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.action-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.action-card:hover {
  transform: translateX(10px);
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.action-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 16px;
  flex-shrink: 0;
}

.action-icon-wrapper.blue {
  background: linear-gradient(135deg, rgba(0, 201, 255, 0.2), rgba(0, 201, 255, 0.05));
}

.action-icon-wrapper.purple {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(102, 126, 234, 0.05));
}

.action-icon-wrapper.orange {
  background: linear-gradient(135deg, rgba(255, 197, 61, 0.2), rgba(255, 197, 61, 0.05));
}

.action-icon {
  font-size: 32px;
}

.action-info {
  flex: 1;
}

.action-info h4 {
  margin: 0;
  font-size: 18px;
  color: #fff;
}

.action-info p {
  margin: 6px 0 0 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.action-arrow {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.action-card:hover .action-arrow {
  color: #fff;
  transform: translateX(5px);
}

.tips-section {
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.tip-card {
  padding: 24px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.tip-card:hover {
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-3px);
}

.tip-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.tip-card h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #fff;
}

.tip-card p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.6;
}

.footer {
  text-align: center;
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
  z-index: 1;
}

.footer p {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
}
</style>
