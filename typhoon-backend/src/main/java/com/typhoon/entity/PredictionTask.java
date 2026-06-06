package com.typhoon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("prediction_task")
public class PredictionTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Double startLongitude;
    private Double startLatitude;
    private Integer startIntensity;
    private Double startGrade;
    private Double startWindSpeed;
    private Double startPressure;
    private Double startDirection;
    private Double startSpeed;
    
    private Date createTime;
}
