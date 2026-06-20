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
 * 预警订阅实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("alert_subscription")
public class AlertSubscription {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 城市名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 纬度
     */
    @TableField("latitude")
    private Double latitude;

    /**
     * 经度
     */
    @TableField("longitude")
    private Double longitude;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 绑定的台风ID（可选）
     */
    @TableField("typhoon_id")
    private Long typhoonId;

    /**
     * 状态：0-停用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}