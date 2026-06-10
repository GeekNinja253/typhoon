import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
import joblib
import os
import math

print("Loading dataset...")
# 读取台风历史数据集，数据存储在Excel文件中
df = pd.read_excel('../其它DATA20210000241945-2025.xlsx')

# 定义各列名称变量，方便后续引用
COL_ID = df.columns[0]  # 台风编号
COL_TIME = df.columns[5]  # 时间
COL_LON = df.columns[6]  # 经度
COL_LAT = df.columns[7]  # 纬度
COL_INTENSITY = df.columns[8]  # 强度（中文描述/等级标识）
COL_GRADE = df.columns[9]  # 等级（风力级数）
COL_WIND = df.columns[10]  # 风速（米/秒）
COL_PRESS = df.columns[11]  # 中心最低气压（百帕）
COL_DIR = df.columns[12]  # 移动方向
COL_SPEED = df.columns[13]  # 移动速度

print(f"Total rows: {len(df)}")

# 数据清洗：移除经纬度为空的行
df = df.dropna(subset=[COL_LON, COL_LAT])
# 将时间列转换为 datetime 对象，无法解析的设为 NaT
df[COL_TIME] = pd.to_datetime(df[COL_TIME], errors='coerce')

# 数据类型转换：将相关气象特征列转换为数值型数据
df[COL_WIND] = pd.to_numeric(df[COL_WIND], errors='coerce')
df[COL_PRESS] = pd.to_numeric(df[COL_PRESS], errors='coerce')
df[COL_GRADE] = pd.to_numeric(df[COL_GRADE], errors='coerce')
df[COL_SPEED] = pd.to_numeric(df[COL_SPEED], errors='coerce')
df[COL_DIR] = pd.to_numeric(df[COL_DIR], errors='coerce')


# 强度映射函数：将台风的文本强度描述转换为数值型强度等级 (0-6)
def parse_intensity(val):
    val = str(val).upper()
    if 'SUPER' in val or '超强台风' in val: return 6
    if 'STY' in val or '强台风' in val: return 5
    if 'STS' in val or '强热带风暴' in val: return 3
    if 'TY' in val or '台风' in val: return 4
    if 'TS' in val or '热带风暴' in val: return 2
    if 'TD' in val or '热带低压' in val: return 1
    return 0


# 应用强度映射函数，生成新的数值型特征 'INTENSITY_NUM'
df['INTENSITY_NUM'] = df[COL_INTENSITY].apply(parse_intensity)

# 数据排序：按照台风编号和时间顺序进行排序，确保时序性正确
df = df.sort_values(by=[COL_ID, COL_TIME])


# 半正矢公式（Haversine formula）：根据地球表面两点经纬度计算两点之间的大圆距离（公里）
def haversine(lon1, lat1, lon2, lat2):
    R = 6371.0  # 地球平均半径（公里）
    lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = math.sin(dlat / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon / 2) ** 2
    c = 2 * math.asin(math.sqrt(a))
    return R * c


# 计算方位角（Bearing）：根据两点经纬度计算从点1移动到点2的初始方位角（度）
def bearing(lon1, lat1, lon2, lat2):
    lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
    dlon = lon2 - lon1
    y = math.sin(dlon) * math.cos(lat2)
    x = math.cos(lat1) * math.sin(lat2) - math.sin(lat1) * math.cos(lat2) * math.cos(dlon)
    brng = math.atan2(y, x)
    return (math.degrees(brng) + 360) % 360


# --- 特征工程与数据插补 ---
FUTURE_STEPS = 10  # 预测未来步长（例如未来10个时间步的轨迹和状态）
X = []  # 特征矩阵：存放当前时间步的台风状态
y = []  # 目标矩阵：存放未来10个时间步相对于当前的经纬度及气象指标变化量

print("Building sequences and calculating missing metrics...")

# 按台风编号分组，处理每个台风的轨迹数据
for tid, group in df.groupby(COL_ID):
    group = group.reset_index(drop=True)

    # 对风速、气压和等级进行线性插值，填补缺失值。
    # 采用线性插值后，利用前后向填充（bfill, ffill），最后使用默认值填充
    group[COL_WIND] = group[COL_WIND].interpolate(method='linear').bfill().ffill().fillna(0)
    group[COL_PRESS] = group[COL_PRESS].interpolate(method='linear').bfill().ffill().fillna(1000)
    group[COL_GRADE] = group[COL_GRADE].interpolate(method='linear').bfill().ffill().fillna(12)

    n = len(group)
    for i in range(n):
        # 如果方向或速度有缺失，则根据前后轨迹点的时间差和距离进行估算
        if pd.isna(group.loc[i, COL_DIR]) or pd.isna(group.loc[i, COL_SPEED]):
            if i > 0:
                dt_hours = (group.loc[i, COL_TIME] - group.loc[i - 1, COL_TIME]).total_seconds() / 3600.0
                if dt_hours <= 0: dt_hours = 6.0  # 若时间异常，默认时间间隔为6小时
                # 计算当前点与上一个点之间的距离（公里）
                dist = haversine(group.loc[i - 1, COL_LON], group.loc[i - 1, COL_LAT],
                                 group.loc[i, COL_LON], group.loc[i, COL_LAT])
                # 计算移动速度（公里/小时）
                spd = dist / dt_hours
                # 计算移动方位角
                dir_ = bearing(group.loc[i - 1, COL_LON], group.loc[i - 1, COL_LAT],
                               group.loc[i, COL_LON], group.loc[i, COL_LAT])
            else:
                spd, dir_ = 0, 0  # 轨迹起点缺失值默认设为0

            # 填补缺失值
            if pd.isna(group.loc[i, COL_SPEED]): group.loc[i, COL_SPEED] = spd
            if pd.isna(group.loc[i, COL_DIR]): group.loc[i, COL_DIR] = dir_

    # 构建监督学习的数据集（X和y）
    for i in range(n - FUTURE_STEPS):
        curr_lon = group.loc[i, COL_LON]
        curr_lat = group.loc[i, COL_LAT]
        curr_wind = group.loc[i, COL_WIND]
        curr_press = group.loc[i, COL_PRESS]
        curr_grade = group.loc[i, COL_GRADE]
        curr_intens = group.loc[i, 'INTENSITY_NUM']
        curr_dir = group.loc[i, COL_DIR]
        curr_spd = group.loc[i, COL_SPEED]

        target = []
        # 构建未来FUTURE_STEPS步的目标输出变量（预测变化量Delta而非绝对值，这能提高模型学习的稳定性）
        for j in range(1, FUTURE_STEPS + 1):
            target.append(group.loc[i + j, COL_LAT] - curr_lat)  # 纬度变化量
            target.append(group.loc[i + j, COL_LON] - curr_lon)  # 经度变化量
            target.append(group.loc[i + j, COL_GRADE] - curr_grade)  # 等级变化量
            target.append(group.loc[i + j, COL_WIND] - curr_wind)  # 风速变化量
            target.append(group.loc[i + j, COL_PRESS] - curr_press)  # 气压变化量

        # 记录当前状态特征（8个维度的输入）
        # 特征：经度, 纬度, 强度(数值), 等级, 风速, 气压, 移动方向, 移动速度
        X.append([curr_lon, curr_lat, curr_intens, curr_grade, curr_wind, curr_press, curr_dir, curr_spd])
        y.append(target)  # 对应未来的变化量序列

# 转换为 numpy 数组，供机器学习模型使用
X = np.array(X)
y = np.array(y)

print(f"Dataset shape: X={X.shape}, y={y.shape}")
print("Training LinearRegression model...")

# --- 模型训练 ---
# 使用 sklearn 的线性回归模型 (Linear Regression)
# 这是一个多输出回归模型 (Multi-output Regression)，能将8维特征映射到50维(10步x5指标)的变化量上
model = LinearRegression()
model.fit(X, y)  # 在整理好的数据集上拟合模型

print("Training completed. Saving model...")
# 使用 joblib 序列化并保存训练好的模型，供后续推理/API接口使用
joblib.dump(model, 'model.joblib')
print("Model saved to model.joblib!")
