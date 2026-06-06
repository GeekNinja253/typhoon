package com.typhoon.service;

import com.typhoon.dto.PredictionRequestDTO;
import com.typhoon.dto.MLPredictionResponseDTO;

import com.typhoon.entity.PredictionTask;
import com.typhoon.entity.PredictionResult;
import java.util.List;

public interface PredictionService {
    MLPredictionResponseDTO predictAndSave(PredictionRequestDTO requestDTO);
    List<PredictionTask> getAllTasks();
    List<PredictionResult> getTaskResults(Long taskId);
}
