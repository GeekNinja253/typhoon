package com.typhoon.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 预警检查请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertCheckRequestDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 台风ID（可选）
     */
    private Long typhoonId;

    /**
     * 台风轨迹数据
     */
    @NotEmpty(message = "轨迹数据不能为空")
    @Valid
    private List<TrajectoryPointDTO> trajectory;
}