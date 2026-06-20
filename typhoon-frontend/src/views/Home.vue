<template>
  <div class="home-page">

    <div class="header">

      <div>
        <h1>🌪 台风路径分析系统</h1>
        <p>Typhoon Prediction Platform</p>
      </div>

      <button
        class="logout-btn"
        @click="handleLogout"
      >
        退出登录
      </button>

    </div>

    <div class="welcome">

      <h2>
        欢迎回来，
        {{ username }}
      </h2>

      <p>
        基于机器学习的台风路径预测与分析平台
      </p>

    </div>

    <div class="dashboard">

      <div
        class="card"
        @click="$router.push('/map')"
      >
        <div class="icon">
          🌪
        </div>

        <h3>GIS地图分析</h3>

        <p>
          查看台风路径预测结果
        </p>
      </div>

      <div
        class="card"
        @click="$router.push('/history')"
      >
        <div class="icon">
          📜
        </div>

        <h3>历史预测记录</h3>

        <p>
          查看历史任务与预测数据
        </p>
      </div>

      <div
        class="card"
        @click="$router.push('/alert')"
      >
        <div class="icon">
          🔔
        </div>

        <h3>预警报告</h3>

        <p>
          订阅台风预警，获取实时通知
        </p>
      </div>

    </div>

  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

const user =
  JSON.parse(
    localStorage.getItem("user") || "{}"
  );

const username =
  user.username || "用户";

function handleLogout() {

  localStorage.removeItem("user");

  router.push("/login");

}
</script>

<style scoped>

.home-page {
  min-height: 100vh;
  padding: 40px;
  background: linear-gradient(135deg, #eef5ff, #f8fbff);
  box-sizing: border-box;
  width: 100%;
  display: block;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.header h1 {
  margin: 0;
  color: #1677ff;
  font-size: 28px;
}

.header p {
  margin-top: 5px;
  color: #666;
  font-size: 14px;
}

.logout-btn {
  padding: 12px 24px;
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

.welcome {
  margin-bottom: 40px;
  padding: 30px;
  border-radius: 18px;
  background: white;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
}

.welcome h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
  color: #333;
}

.welcome p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
  max-width: 1200px;
}

.card {
  background: white;
  border-radius: 18px;
  padding: 35px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
}

.card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(22, 119, 255, 0.15);
}

.icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.card h3 {
  margin: 0 0 10px 0;
  color: #1677ff;
  font-size: 18px;
}

.card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

</style>