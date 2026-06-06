package com.typhoon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("prediction_result")
public class PredictionResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long taskId;
    private Integer stepIndex;
    
    private Double predLongitude;
    private Double predLatitude;
    private Double predGrade;
    private Double predWindSpeed;
    private Double predPressure;
}
