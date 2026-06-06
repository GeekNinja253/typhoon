package com.typhoon.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class PredictionRequestDTO {
    private Double lat;
    private Double lon;
    private Integer intensity;
    private Double grade;
    
    @JsonProperty("wind_speed")
    private Double wind_speed;
    
    private Double pressure;
    private Double direction;
    private Double speed;
}
