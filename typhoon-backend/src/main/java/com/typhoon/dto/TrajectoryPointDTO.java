package com.typhoon.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class TrajectoryPointDTO {
    private Double lat;
    private Double lon;
    private Double grade;
    
    @JsonProperty("wind_speed")
    private Double windSpeed;
    
    private Double pressure;
    
    private Long time;
}
