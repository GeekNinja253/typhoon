package com.typhoon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预警报告响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertReportDTO {

    /**
     * 预警ID
     */
    private Long id;

    /**
     * 订阅ID
     */
    private Long subscriptionId;

    /**
     * 台风ID
     */
    private Long typhoonId;

    /**
     * 预警等级：7/10/12
     */
    private Integer level;

    /**
     * 预警文本
     */
    private String message;

    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;

    /**
     * 台风与目标点距离（km）
     */
    private Double distance;

    /**
     * 状态：0-未读，1-已读
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 城市名称
     */
    private String cityName;
}