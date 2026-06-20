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
        // 设置默认步数
        if (requestDTO.getSteps() == null || requestDTO.getSteps() <= 0) {
            requestDTO.setSteps(10);
        }
        
        // 1. Call ML API with steps parameter
        String apiUrl = mlApiUrl + "?steps=" + requestDTO.getSteps();
        MLPredictionResponseDTO response = restTemplate.postForObject(apiUrl, requestDTO, MLPredictionResponseDTO.class);
        
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
            result.setPredWindSpeed(point.getWindSpeed());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long taskId) {
        // 先删除关联的预测结果
        predictionResultMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PredictionResult>().eq("task_id", taskId));
        // 再删除任务本身
        predictionTaskMapper.deleteById(taskId);
    }
}
