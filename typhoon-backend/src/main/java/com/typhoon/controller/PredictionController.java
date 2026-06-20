package com.typhoon.controller;

import com.typhoon.dto.MLPredictionResponseDTO;
import com.typhoon.dto.PredictionRequestDTO;
import com.typhoon.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.typhoon.common.Result;
import com.typhoon.entity.PredictionTask;
import com.typhoon.entity.PredictionResult;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow cross-origin requests from Vue frontend
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping("/predict")
    public Result<MLPredictionResponseDTO> predict(
            @RequestBody PredictionRequestDTO requestDTO) {

        MLPredictionResponseDTO result =
                predictionService.predictAndSave(requestDTO);

        return Result.success(
                "预测成功",
                result
        );
    }

    @GetMapping("/history/tasks")
    public Result<List<PredictionTask>> getAllTasks() {

        return Result.success(
                predictionService.getAllTasks()
        );
    }

    @GetMapping("/history/tasks/{taskId}/results")
    public Result<List<PredictionResult>> getTaskResults(
            @PathVariable Long taskId) {

        return Result.success(
                predictionService.getTaskResults(taskId)
        );
    }

    @DeleteMapping("/history/tasks/{taskId}")
    public Result<String> deleteTask(
            @PathVariable Long taskId) {

        predictionService.deleteTask(taskId);

        return Result.success(
                "删除成功",
                null
        );
    }
}
