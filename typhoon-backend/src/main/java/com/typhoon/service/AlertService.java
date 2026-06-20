package com.typhoon.service;

import com.typhoon.dto.AlertSubscriptionDTO;
import com.typhoon.entity.AlertReport;
import com.typhoon.entity.AlertSubscription;

import java.util.List;

/**
 * 预警服务接口
 */
public interface AlertService {

    /**
     * 创建预警订阅
     */
    AlertSubscription createSubscription(AlertSubscription subscription);

    /**
     * 根据用户ID获取订阅列表
     */
    List<AlertSubscription> getSubscriptionsByUserId(Long userId);

    /**
     * 更新订阅状态
     */
    void updateSubscriptionStatus(Long id, Integer status);

    /**
     * 更新订阅信息
     */
    void updateSubscription(Long id, AlertSubscriptionDTO dto);

    /**
     * 删除订阅
     */
    void deleteSubscription(Long id);

    /**
     * 根据用户ID获取预警报告列表
     */
    List<AlertReport> getReportsByUserId(Long userId);

    /**
     * 检查预警并生成报告
     * @param userId 用户ID
     * @param typhoonId 台风ID（可选）
     * @param trajectory 台风轨迹数据
     */
    List<AlertReport> checkAlerts(Long userId, Long typhoonId, List<TrajectoryPoint> trajectory);

    /**
     * 标记预警为已读
     */
    void markAsRead(Long reportId);

    /**
     * 删除预警报告
     */
    void deleteReport(Long reportId);

    /**
     * 批量删除预警报告
     */
    void batchDeleteReports(List<Long> reportIds);

    /**
     * 获取未读预警数量
     */
    int getUnreadCount(Long userId);

    /**
     * 台风轨迹点
     */
    class TrajectoryPoint {
        private Double lat;
        private Double lon;
        private Integer grade;
        private Double windSpeed;
        private Double pressure;
        private Long time;

        public TrajectoryPoint() {}

        public TrajectoryPoint(Double lat, Double lon, Integer grade, Double windSpeed, Double pressure, Long time) {
            this.lat = lat;
            this.lon = lon;
            this.grade = grade;
            this.windSpeed = windSpeed;
            this.pressure = pressure;
            this.time = time;
        }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLon() { return lon; }
        public void setLon(Double lon) { this.lon = lon; }
        public Integer getGrade() { return grade; }
        public void setGrade(Integer grade) { this.grade = grade; }
        public Double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
        public Double getPressure() { return pressure; }
        public void setPressure(Double pressure) { this.pressure = pressure; }
        public Long getTime() { return time; }
        public void setTime(Long time) { this.time = time; }
    }
}