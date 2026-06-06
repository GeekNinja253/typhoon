package com.typhoon.dto;

import lombok.Data;
import java.util.List;

@Data
public class MLPredictionResponseDTO {
    private List<TrajectoryPointDTO> trajectory;
    private String error;
}
