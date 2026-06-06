package com.typhoon.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Typhoon {

    private Long id;

    private String name;
    private Integer year;
    private String enName;
    private String intensity;
    private String landfall;

    private LocalDateTime createTime;
}