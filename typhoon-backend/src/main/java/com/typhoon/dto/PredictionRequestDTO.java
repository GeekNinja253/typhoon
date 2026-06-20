package com.typhoon.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 台风预测请求DTO
 * 包含台风参数的输入校验规则
 */
@Data
public class PredictionRequestDTO {

    @NotNull(message = "纬度不能为空")
    @Min(value = -90, message = "纬度最小值为-90")
    @Max(value = 90, message = "纬度最大值为90")
    private Double lat;

    @NotNull(message = "经度不能为空")
    @Min(value = -180, message = "经度最小值为-180")
    @Max(value = 180, message = "经度最大值为180")
    private Double lon;

    @NotNull(message = "强度不能为空")
    @Min(value = 1, message = "强度最小值为1")
    @Max(value = 6, message = "强度最大值为6")
    private Integer intensity;

    @NotNull(message = "等级不能为空")
    @Min(value = 1, message = "等级最小值为1")
    @Max(value = 17, message = "等级最大值为17")
    private Double grade;

    @JsonProperty("wind_speed")
    @NotNull(message = "风速不能为空")
    @Min(value = 0, message = "风速不能为负")
    private Double wind_speed;

    @NotNull(message = "气压不能为空")
    @Min(value = 850, message = "气压最小值为850 hPa")
    @Max(value = 1050, message = "气压最大值为1050 hPa")
    private Double pressure;

    @NotNull(message = "方向不能为空")
    @Min(value = 0, message = "方向最小值为0")
    @Max(value = 360, message = "方向最大值为360")
    private Double direction;

    @NotNull(message = "速度不能为空")
    @Min(value = 0, message = "速度不能为负")
    private Double speed;

    @Min(value = 1, message = "预测步数最小值为1")
    @Max(value = 40, message = "预测步数最大值为40")
    private Integer steps;  // 预测步数，默认10步
}
