package com.typhoon.controller;

import com.typhoon.dto.MLPredictionResponseDTO;
import com.typhoon.dto.PredictionRequestDTO;
import com.typhoon.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow cross-origin requests from Vue frontend
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping("/predict")
    public MLPredictionResponseDTO predict(@RequestBody PredictionRequestDTO requestDTO) {
        return predictionService.predictAndSave(requestDTO);
    }

    @GetMapping("/history/tasks")
    public java.util.List<com.typhoon.entity.PredictionTask> getAllTasks() {
        return predictionService.getAllTasks();
    }

    @GetMapping("/history/tasks/{taskId}/results")
    public java.util.List<com.typhoon.entity.PredictionResult> getTaskResults(@PathVariable Long taskId) {
        return predictionService.getTaskResults(taskId);
    }
}
