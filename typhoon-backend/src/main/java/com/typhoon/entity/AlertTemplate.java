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
 * 预警模板实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("alert_template")
public class AlertTemplate {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 预警等级：7/10/12
     */
    @TableField("level")
    private Integer level;

    /**
     * 模板标题
     */
    @TableField("title")
    private String title;

    /**
     * 个人与家庭防范措施
     */
    @TableField("personal_measures")
    private String personalMeasures;

    /**
     * 户外与作业安全措施
     */
    @TableField("outdoor_measures")
    private String outdoorMeasures;

    /**
     * 海上与水域人员措施
     */
    @TableField("marine_measures")
    private String marineMeasures;

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