package com.typhoon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TyphoonPath {

    private Long id;

    private Long typhoonId;

    private Double latitude;
    private Double longitude;

    private Integer windSpeed;
    private Integer pressure;

    private LocalDateTime recordTime;
}