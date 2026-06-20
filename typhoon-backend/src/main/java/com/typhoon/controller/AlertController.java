package com.typhoon.controller;

import com.typhoon.common.Result;
import com.typhoon.dto.AlertCheckRequestDTO;
import com.typhoon.dto.AlertReportDTO;
import com.typhoon.dto.AlertSubscriptionDTO;
import com.typhoon.dto.TrajectoryPointDTO;
import com.typhoon.entity.AlertReport;
import com.typhoon.entity.AlertSubscription;
import com.typhoon.mapper.AlertSubscriptionMapper;
import com.typhoon.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警控制器
 */
@RestController
@RequestMapping("/api/alert")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;
    private final AlertSubscriptionMapper subscriptionMapper;

    public AlertController(AlertService alertService, AlertSubscriptionMapper subscriptionMapper) {
        this.alertService = alertService;
        this.subscriptionMapper = subscriptionMapper;
    }

    /**
     * 创建预警订阅
     */
    @PostMapping("/subscription")
    public Result<AlertSubscription> createSubscription(@Valid @RequestBody AlertSubscriptionDTO dto) {
        AlertSubscription subscription = new AlertSubscription();
        subscription.setUserId(1L); // 暂固定用户ID
        subscription.setCityName(dto.getCityName());
        subscription.setLatitude(dto.getLatitude());
        subscription.setLongitude(dto.getLongitude());
        subscription.setStartTime(dto.getStartTime());
        subscription.setEndTime(dto.getEndTime());
        subscription.setTyphoonId(dto.getTyphoonId());
        
        AlertSubscription result = alertService.createSubscription(subscription);
        return Result.success(result);
    }

    /**
     * 获取用户订阅列表
     */
    @GetMapping("/subscription/{userId}")
    public Result<List<AlertSubscription>> getSubscriptions(@PathVariable Long userId) {
        List<AlertSubscription> subscriptions = alertService.getSubscriptionsByUserId(userId);
        return Result.success(subscriptions);
    }

    /**
     * 更新订阅状态
     */
    @PutMapping("/subscription/{id}/status")
    public Result<Void> updateSubscriptionStatus(@PathVariable Long id, @RequestParam Integer status) {
        alertService.updateSubscriptionStatus(id, status);
        return Result.success();
    }

    /**
     * 删除订阅
     */
    @DeleteMapping("/subscription/{id}")
    public Result<Void> deleteSubscription(@PathVariable Long id) {
        alertService.deleteSubscription(id);
        return Result.success();
    }

    /**
     * 更新订阅
     */
    @PutMapping("/subscription/{id}")
    public Result<Void> updateSubscription(@PathVariable Long id, @Valid @RequestBody AlertSubscriptionDTO dto) {
        alertService.updateSubscription(id, dto);
        return Result.success();
    }

    /**
     * 触发预警检查
     */
    @PostMapping("/check")
    public Result<List<AlertReportDTO>> checkAlerts(@Valid @RequestBody AlertCheckRequestDTO request) {
        // 转换轨迹数据
        List<AlertService.TrajectoryPoint> trajectory = request.getTrajectory().stream()
                .map(this::convertToTrajectoryPoint)
                .collect(Collectors.toList());
        
        List<AlertReport> reports = alertService.checkAlerts(request.getUserId(), request.getTyphoonId(), trajectory);
        
        List<AlertReportDTO> dtoList = reports.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
        
        return Result.success(dtoList);
    }

    /**
     * 获取用户预警报告列表
     */
    @GetMapping("/report/{userId}")
    public Result<List<AlertReportDTO>> getReports(@PathVariable Long userId) {
        List<AlertReport> reports = alertService.getReportsByUserId(userId);
        
        List<AlertReportDTO> dtoList = reports.stream()
                .map(this::convertToReportDTO)
                .collect(Collectors.toList());
        
        return Result.success(dtoList);
    }

    /**
     * 标记预警为已读
     */
    @PutMapping("/report/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        alertService.markAsRead(id);
        return Result.success();
    }

    /**
     * 删除预警报告
     */
    @DeleteMapping("/report/{id}")
    public Result<Void> deleteReport(@PathVariable Long id) {
        alertService.deleteReport(id);
        return Result.success();
    }

    /**
     * 批量删除预警报告
     */
    @DeleteMapping("/report/batch")
    public Result<Void> batchDeleteReports(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        if (ids != null && !ids.isEmpty()) {
            alertService.batchDeleteReports(ids);
        }
        return Result.success();
    }

    /**
     * 获取未读预警数量
     */
    @GetMapping("/report/{userId}/unread")
    public Result<Map<String, Integer>> getUnreadCount(@PathVariable Long userId) {
        int count = alertService.getUnreadCount(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("unreadCount", count);
        return Result.success(result);
    }

    /**
     * 获取推荐城市列表
     */
    @GetMapping("/cities")
    public Result<List<Map<String, Object>>> getRecommendedCities() {
        List<Map<String, Object>> cities = List.of(
            createCity("上海", 31.2304, 121.4737),
            createCity("深圳", 22.5431, 114.0579),
            createCity("广州", 23.1291, 113.2644),
            createCity("厦门", 24.4798, 118.0894),
            createCity("香港", 22.3193, 114.1694),
            createCity("台北", 25.0330, 121.5654),
            createCity("高雄", 22.6273, 120.3014),
            createCity("海口", 20.0231, 110.3398),
            createCity("宁波", 29.8739, 121.5408),
            createCity("舟山", 30.0176, 122.2078)
        );
        return Result.success(cities);
    }

    private Map<String, Object> createCity(String name, double lat, double lon) {
        Map<String, Object> city = new HashMap<>();
        city.put("name", name);
        city.put("latitude", lat);
        city.put("longitude", lon);
        return city;
    }

    private AlertService.TrajectoryPoint convertToTrajectoryPoint(TrajectoryPointDTO dto) {
        Integer grade = dto.getGrade() != null ? dto.getGrade().intValue() : null;
        return new AlertService.TrajectoryPoint(
                dto.getLat(),
                dto.getLon(),
                grade,
                dto.getWindSpeed(),
                dto.getPressure(),
                dto.getTime()
        );
    }

    private AlertReportDTO convertToReportDTO(AlertReport report) {
        AlertReportDTO dto = new AlertReportDTO();
        dto.setId(report.getId());
        dto.setSubscriptionId(report.getSubscriptionId());
        dto.setTyphoonId(report.getTyphoonId());
        dto.setLevel(report.getLevel());
        dto.setMessage(report.getMessage());
        dto.setTriggerTime(report.getTriggerTime());
        dto.setDistance(report.getDistance());
        dto.setStatus(report.getStatus());
        dto.setCreateTime(report.getCreateTime());
        
        // 获取城市名称
        AlertSubscription subscription = subscriptionMapper.selectById(report.getSubscriptionId());
        if (subscription != null) {
            dto.setCityName(subscription.getCityName());
        }
        
        return dto;
    }
}