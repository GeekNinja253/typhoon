import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
import joblib
import os
import math

print("Loading dataset...")
df = pd.read_excel('../其它DATA20210000241945-2025.xlsx')

COL_ID = df.columns[0]
COL_TIME = df.columns[5]
COL_LON = df.columns[6]
COL_LAT = df.columns[7]
COL_INTENSITY = df.columns[8]
COL_GRADE = df.columns[9]
COL_WIND = df.columns[10]
COL_PRESS = df.columns[11]
COL_DIR = df.columns[12]
COL_SPEED = df.columns[13]

print(f"Total rows: {len(df)}")

df = df.dropna(subset=[COL_LON, COL_LAT])
df[COL_TIME] = pd.to_datetime(df[COL_TIME], errors='coerce')

# Convert numeric
df[COL_WIND] = pd.to_numeric(df[COL_WIND], errors='coerce')
df[COL_PRESS] = pd.to_numeric(df[COL_PRESS], errors='coerce')
df[COL_GRADE] = pd.to_numeric(df[COL_GRADE], errors='coerce')
df[COL_SPEED] = pd.to_numeric(df[COL_SPEED], errors='coerce')
df[COL_DIR] = pd.to_numeric(df[COL_DIR], errors='coerce')

# Map Intensity
def parse_intensity(val):
    val = str(val).upper()
    if 'SUPER' in val or '超强台风' in val: return 6
    if 'STY' in val or '强台风' in val: return 5
    if 'STS' in val or '强热带风暴' in val: return 3
    if 'TY' in val or '台风' in val: return 4
    if 'TS' in val or '热带风暴' in val: return 2
    if 'TD' in val or '热带低压' in val: return 1
    return 0

df['INTENSITY_NUM'] = df[COL_INTENSITY].apply(parse_intensity)

# Sort
df = df.sort_values(by=[COL_ID, COL_TIME])

def haversine(lon1, lat1, lon2, lat2):
    R = 6371.0
    lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
    c = 2 * math.asin(math.sqrt(a))
    return R * c

def bearing(lon1, lat1, lon2, lat2):
    lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
    dlon = lon2 - lon1
    y = math.sin(dlon) * math.cos(lat2)
    x = math.cos(lat1) * math.sin(lat2) - math.sin(lat1) * math.cos(lat2) * math.cos(dlon)
    brng = math.atan2(y, x)
    return (math.degrees(brng) + 360) % 360

# Impute and calculate
FUTURE_STEPS = 10
X = []
y = []

print("Building sequences and calculating missing metrics...")

for tid, group in df.groupby(COL_ID):
    group = group.reset_index(drop=True)
    # Interpolate wind, pressure, grade
    group[COL_WIND] = group[COL_WIND].interpolate(method='linear').bfill().ffill().fillna(0)
    group[COL_PRESS] = group[COL_PRESS].interpolate(method='linear').bfill().ffill().fillna(1000)
    group[COL_GRADE] = group[COL_GRADE].interpolate(method='linear').bfill().ffill().fillna(12)
    
    n = len(group)
    for i in range(n):
        # Calculate Dir and Speed if missing
        if pd.isna(group.loc[i, COL_DIR]) or pd.isna(group.loc[i, COL_SPEED]):
            if i > 0:
                dt_hours = (group.loc[i, COL_TIME] - group.loc[i-1, COL_TIME]).total_seconds() / 3600.0
                if dt_hours <= 0: dt_hours = 6.0
                dist = haversine(group.loc[i-1, COL_LON], group.loc[i-1, COL_LAT], 
                                 group.loc[i, COL_LON], group.loc[i, COL_LAT])
                spd = dist / dt_hours
                dir_ = bearing(group.loc[i-1, COL_LON], group.loc[i-1, COL_LAT], 
                               group.loc[i, COL_LON], group.loc[i, COL_LAT])
            else:
                spd, dir_ = 0, 0
                
            if pd.isna(group.loc[i, COL_SPEED]): group.loc[i, COL_SPEED] = spd
            if pd.isna(group.loc[i, COL_DIR]): group.loc[i, COL_DIR] = dir_
            
    # Now build dataset
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
        for j in range(1, FUTURE_STEPS + 1):
            target.append(group.loc[i+j, COL_LAT] - curr_lat)
            target.append(group.loc[i+j, COL_LON] - curr_lon)
            target.append(group.loc[i+j, COL_GRADE] - curr_grade)
            target.append(group.loc[i+j, COL_WIND] - curr_wind)
            target.append(group.loc[i+j, COL_PRESS] - curr_press)
            
        # Features: Lon, Lat, Intensity, Grade, Wind, Pressure, Dir, Speed
        X.append([curr_lon, curr_lat, curr_intens, curr_grade, curr_wind, curr_press, curr_dir, curr_spd])
        y.append(target)

X = np.array(X)
y = np.array(y)

print(f"Dataset shape: X={X.shape}, y={y.shape}")
print("Training LinearRegression model...")

model = LinearRegression()
model.fit(X, y)

print("Training completed. Saving model...")
joblib.dump(model, 'model.joblib')
print("Model saved to model.joblib!")
