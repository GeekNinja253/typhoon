# 台风路径分析系统 (Typhoon Tracking System)

本项目包含三个主要部分：Vue 前端展示、Java 业务后端、Python 算法端。由于加入了完整的历史预测数据存储功能，启动流程需确保三个端均正常运行。

---

## 🚀 启动指南 (最新必读)

为了确保预测记录能成功保存到数据库中，请务必按照以下三个步骤依次启动项目：

### 第一步：准备 MySQL 数据库
1. 确保您的电脑上已安装并启动 **MySQL** 数据库服务。
2. 使用 Navicat 等可视化工具连接到本地 MySQL（账号密码默认配为 `root`/`root`）。
3. 新建一个名为 `typhoon_db` 的数据库。
4. 将 `typhoon-backend/src/main/resources/sql/typhoon.sql` 中的 SQL 脚本在 `typhoon_db` 数据库中执行，以完成数据表（如 `prediction_task` 等）的创建。

### 第二步：启动 Java 后端（核心存储协调服务）
由于 Java 代码在开发过程中经常变动，建议直接使用 IDE（推荐 **IntelliJ IDEA**）进行启动，这样不仅能保证运行的是最新代码，还能直观地查看报错信息。
1. 打开 IntelliJ IDEA，选择打开项目中的 `typhoon-backend` 文件夹。
2. 在左侧项目目录中找到 `src/main/java/com/typhoon/TyphoonBackendApplication.java`。
3. 点击行号旁的绿色箭头 **(Run)** 启动项目。
4. 观察下方的控制台输出，如果没有红色的数据库连接报错，且最后显示 `Started TyphoonBackendApplication`，说明后端的 8080 端口启动成功。

> *补充说明：如果您非要用 `start.bat` 命令启动 Java，请务必提前在 IDEA 右侧的 Maven 面板中，依次双击运行 `clean` 和 `package`，重新生成最新的 `.jar` 文件。*

### 第三步：一键启动前端和算法端
当 Java 后端在 IDEA 里平稳运行后，您可以进行最后一步：
1. 在项目根目录（即当前文件夹）双击运行 **`start.bat`** 文件。
2. 脚本会提示一些注意事项，按回车后它会帮您唤起黑色的命令行窗口：
   - **Python ML API 窗口**：自动激活虚拟环境并启动算法模型服务（端口 8000）。
   - **Vue Frontend 窗口**：自动编译并启动三维地球界面（端口 5173 或 5174）。
   - **Java Backend 窗口**：*(注：由于您已经在 IDEA 里启动了 Java，如果您看到这个多余的窗口，可以直接把它关掉 X 掉)*

### 最终访问
等待终端出现 `Local: http://localhost:5173/` 字样后，直接在浏览器中打开该地址即可开始体验渲染轨迹与查询历史记录！