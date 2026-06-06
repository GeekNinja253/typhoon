from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import joblib
import numpy as np

app = FastAPI()

# Enable CORS for the frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Adjust in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load the trained model
# Note: we assume api.py is running in typhoon-ml directory
try:
    model = joblib.load('model.joblib')
    print("Model loaded successfully.")
except Exception as e:
    print("Warning: Could not load model.joblib. Make sure you trained it first.", e)
    model = None

class PredictionRequest(BaseModel):
    lat: float
    lon: float
    intensity: int
    grade: float
    wind_speed: float
    pressure: float
    direction: float
    speed: float

@app.post("/predict")
def predict_trajectory(req: PredictionRequest):
    if model is None:
        return {"error": "Model not loaded"}

    # Prepare features: Lon, Lat, Intensity, Grade, Wind, Pressure, Dir, Speed
    X = np.array([[req.lon, req.lat, req.intensity, req.grade, req.wind_speed, req.pressure, req.direction, req.speed]])
    
    # Predict 5 delta metrics per step
    y_pred = model.predict(X)[0] # shape (50,) because 10 steps * 5
    
    # Reconstruct absolute values
    trajectory = []
    for i in range(10):
        dLat = y_pred[i*5]
        dLon = y_pred[i*5 + 1]
        dGrade = y_pred[i*5 + 2]
        dWind = y_pred[i*5 + 3]
        dPress = y_pred[i*5 + 4]
        
        trajectory.append({
            "lat": req.lat + dLat,
            "lon": req.lon + dLon,
            "grade": req.grade + dGrade,
            "wind_speed": req.wind_speed + dWind,
            "pressure": req.pressure + dPress
        })
        
    return {"trajectory": trajectory}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
