package com.typhoon.service.impl;

import com.typhoon.dto.MLPredictionResponseDTO;
import com.typhoon.dto.PredictionRequestDTO;
import com.typhoon.dto.TrajectoryPointDTO;
import com.typhoon.entity.PredictionResult;
import com.typhoon.entity.PredictionTask;
import com.typhoon.mapper.PredictionResultMapper;
import com.typhoon.mapper.PredictionTaskMapper;
import com.typhoon.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PredictionTaskMapper predictionTaskMapper;

    @Autowired
    private PredictionResultMapper predictionResultMapper;

    // Use property if possible, default to localhost:8000
    private String mlApiUrl = "http://localhost:8000/predict";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MLPredictionResponseDTO predictAndSave(PredictionRequestDTO requestDTO) {
        // 1. Call ML API
        MLPredictionResponseDTO response = restTemplate.postForObject(mlApiUrl, requestDTO, MLPredictionResponseDTO.class);
        
        if (response == null || response.getError() != null || response.getTrajectory() == null) {
            return response; // Return error or null
        }

        // 2. Save Task
        PredictionTask task = new PredictionTask();
        task.setStartLongitude(requestDTO.getLon());
        task.setStartLatitude(requestDTO.getLat());
        task.setStartIntensity(requestDTO.getIntensity());
        task.setStartGrade(requestDTO.getGrade());
        task.setStartWindSpeed(requestDTO.getWind_speed());
        task.setStartPressure(requestDTO.getPressure());
        task.setStartDirection(requestDTO.getDirection());
        task.setStartSpeed(requestDTO.getSpeed());
        task.setCreateTime(new Date());
        
        predictionTaskMapper.insert(task); // Task ID is auto-generated and mapped back

        // 3. Save Results
        List<TrajectoryPointDTO> trajectory = response.getTrajectory();
        for (int i = 0; i < trajectory.size(); i++) {
            TrajectoryPointDTO point = trajectory.get(i);
            PredictionResult result = new PredictionResult();
            result.setTaskId(task.getId());
            result.setStepIndex(i + 1);
            result.setPredLongitude(point.getLon());
            result.setPredLatitude(point.getLat());
            result.setPredGrade(point.getGrade());
            result.setPredWindSpeed(point.getWind_speed());
            result.setPredPressure(point.getPressure());
            
            predictionResultMapper.insert(result);
        }

        return response;
    }

    @Override
    public List<PredictionTask> getAllTasks() {
        return predictionTaskMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PredictionTask>().orderByDesc("create_time"));
    }

    @Override
    public List<PredictionResult> getTaskResults(Long taskId) {
        return predictionResultMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PredictionResult>().eq("task_id", taskId).orderByAsc("step_index"));
    }
}
