package com.typhoon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预警记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("alert_report")
public class AlertReport {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订阅ID
     */
    @TableField("subscription_id")
    private Long subscriptionId;

    /**
     * 台风ID
     */
    @TableField("typhoon_id")
    private Long typhoonId;

    /**
     * 预警等级：7/10/12
     */
    @TableField("level")
    private Integer level;

    /**
     * 预警文本
     */
    @TableField("message")
    private String message;

    /**
     * 触发时间
     */
    @TableField("trigger_time")
    private LocalDateTime triggerTime;

    /**
     * 台风与目标点距离（km）
     */
    @TableField("distance")
    private Double distance;

    /**
     * 状态：0-未读，1-已读
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}