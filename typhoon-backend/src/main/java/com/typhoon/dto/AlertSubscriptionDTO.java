package com.typhoon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预警订阅请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscriptionDTO {

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度最小值为-90")
    @DecimalMax(value = "90", message = "纬度最大值为90")
    private Double latitude;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180", message = "经度最小值为-180")
    @DecimalMax(value = "180", message = "经度最大值为180")
    private Double longitude;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 台风ID（可选）
     */
    private Long typhoonId;
}