from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import joblib
import numpy as np

# 创建 FastAPI 应用实例，用于构建提供预测服务的 API 接口
app = FastAPI()

# 启用 CORS (跨域资源共享) 允许前端应用进行跨域请求
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 允许所有域名访问（生产环境中应指定具体的域名列表）
    allow_credentials=True,
    allow_methods=["*"],  # 允许所有 HTTP 方法 (GET, POST 等)
    allow_headers=["*"],  # 允许所有请求头
)

# 加载训练好的机器学习模型
# 注意: 假设 api.py 在 typhoon-ml 目录下运行，读取同目录下的 model.joblib
try:
    model = joblib.load('model.joblib')
    print("Model loaded successfully.")
except Exception as e:
    print("Warning: Could not load model.joblib. Make sure you trained it first.", e)
    model = None


# 使用 Pydantic 定义请求体数据结构及类型校验
# 这些字段对应了特征工程中的输入变量，即预测台风轨迹所需的当前状态
class PredictionRequest(BaseModel):
    lat: float  # 当前纬度
    lon: float  # 当前经度
    intensity: int  # 当前强度（0-6 的数值标识）
    grade: float  # 当前风力等级
    wind_speed: float  # 当前风速
    pressure: float  # 当前气压
    direction: float  # 当前移动方向（方位角）
    speed: float  # 当前移动速度


# 定义 POST 接口 "/predict" 以接收前端的预测请求
@app.post("/predict")
def predict_trajectory(req: PredictionRequest):
    if model is None:
        return {"error": "Model not loaded"}

    # 准备特征向量：将请求数据转换为模型所需的顺序与形状
    # 特征顺序：经度, 纬度, 强度(数值), 等级, 风速, 气压, 移动方向, 移动速度
    X = np.array([[req.lon, req.lat, req.intensity, req.grade, req.wind_speed, req.pressure, req.direction, req.speed]])

    # 预测未来 10 个时间步，每个时间步 5 个维度的变化量（Delta）
    y_pred = model.predict(X)[0]  # 输出形状为 (50,)，因为 10 个时间步 * 5 个变化量

    # 将模型输出的相对变化量（Delta）重构为未来的绝对预测值
    trajectory = []
    for i in range(10):
        dLat = y_pred[i * 5]  # 纬度变化量
        dLon = y_pred[i * 5 + 1]  # 经度变化量
        dGrade = y_pred[i * 5 + 2]  # 等级变化量
        dWind = y_pred[i * 5 + 3]  # 风速变化量
        dPress = y_pred[i * 5 + 4]  # 气压变化量

        trajectory.append({
            "lat": req.lat + dLat,
            "lon": req.lon + dLon,
            "grade": req.grade + dGrade,
            "wind_speed": req.wind_speed + dWind,
            "pressure": req.pressure + dPress
        })

    # 返回构建好的未来 10 步的台风预测轨迹
    return {"trajectory": trajectory}


# 使用 uvicorn 作为 ASGI 服务器运行应用
if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
