package com.typhoon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.typhoon.dto.AlertSubscriptionDTO;
import com.typhoon.entity.AlertReport;
import com.typhoon.entity.AlertSubscription;
import com.typhoon.entity.AlertTemplate;
import com.typhoon.mapper.AlertReportMapper;
import com.typhoon.mapper.AlertSubscriptionMapper;
import com.typhoon.mapper.AlertTemplateMapper;
import com.typhoon.service.AlertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 预警服务实现类
 */
@Service
public class AlertServiceImpl implements AlertService {

    private final AlertSubscriptionMapper subscriptionMapper;
    private final AlertReportMapper reportMapper;
    private final AlertTemplateMapper templateMapper;

    public AlertServiceImpl(AlertSubscriptionMapper subscriptionMapper,
                           AlertReportMapper reportMapper,
                           AlertTemplateMapper templateMapper) {
        this.subscriptionMapper = subscriptionMapper;
        this.reportMapper = reportMapper;
        this.templateMapper = templateMapper;
    }

    @Override
    @Transactional
    public AlertSubscription createSubscription(AlertSubscription subscription) {
        subscription.setStatus(1);
        subscription.setCreateTime(LocalDateTime.now());
        subscription.setUpdateTime(LocalDateTime.now());
        subscriptionMapper.insert(subscription);
        return subscription;
    }

    @Override
    public List<AlertSubscription> getSubscriptionsByUserId(Long userId) {
        QueryWrapper<AlertSubscription> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return subscriptionMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void updateSubscriptionStatus(Long id, Integer status) {
        AlertSubscription subscription = subscriptionMapper.selectById(id);
        if (subscription != null) {
            subscription.setStatus(status);
            subscription.setUpdateTime(LocalDateTime.now());
            subscriptionMapper.updateById(subscription);
        }
    }

    @Override
    @Transactional
    public void deleteSubscription(Long id) {
        subscriptionMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateSubscription(Long id, AlertSubscriptionDTO dto) {
        AlertSubscription subscription = subscriptionMapper.selectById(id);
        if (subscription != null) {
            subscription.setCityName(dto.getCityName());
            subscription.setLatitude(dto.getLatitude());
            subscription.setLongitude(dto.getLongitude());
            subscription.setStartTime(dto.getStartTime());
            subscription.setEndTime(dto.getEndTime());
            subscription.setUpdateTime(LocalDateTime.now());
            subscriptionMapper.updateById(subscription);
        }
    }

    @Override
    public List<AlertReport> getReportsByUserId(Long userId) {
        // 查询用户所有订阅
        QueryWrapper<AlertSubscription> subWrapper = new QueryWrapper<>();
        subWrapper.eq("user_id", userId);
        List<AlertSubscription> subscriptions = subscriptionMapper.selectList(subWrapper);
        
        // 查询所有预警报告
        List<AlertReport> allReports = new ArrayList<>();
        for (AlertSubscription sub : subscriptions) {
            QueryWrapper<AlertReport> reportWrapper = new QueryWrapper<>();
            reportWrapper.eq("subscription_id", sub.getId());
            reportWrapper.orderByDesc("create_time");
            allReports.addAll(reportMapper.selectList(reportWrapper));
        }
        
        // 按创建时间排序
        allReports.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return allReports;
    }

    @Override
    @Transactional
    public List<AlertReport> checkAlerts(Long userId, Long typhoonId, List<TrajectoryPoint> trajectory) {
        List<AlertReport> generatedReports = new ArrayList<>();
        
        // 查询用户启用的订阅
        QueryWrapper<AlertSubscription> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        List<AlertSubscription> subscriptions = subscriptionMapper.selectList(wrapper);
        
        for (AlertSubscription subscription : subscriptions) {
            List<AlertReport> reports = checkSingleSubscription(subscription, typhoonId, trajectory);
            generatedReports.addAll(reports);
            
            // 逐条插入
            for (AlertReport report : reports) {
                reportMapper.insert(report);
            }
        }
        
        return generatedReports;
    }

    /**
     * 检查单个订阅
     */
    private List<AlertReport> checkSingleSubscription(AlertSubscription subscription, 
                                                     Long typhoonId, 
                                                     List<TrajectoryPoint> trajectory) {
        List<AlertReport> reports = new ArrayList<>();
        
        Double targetLat = subscription.getLatitude();
        Double targetLon = subscription.getLongitude();
        LocalDateTime startTime = subscription.getStartTime();
        LocalDateTime endTime = subscription.getEndTime();
        
        // 记录已触发的最高等级（同一时间点只播报最高等级）
        Map<Long, Integer> triggeredLevels = new HashMap<>();
        
        for (TrajectoryPoint point : trajectory) {
            if (point.getGrade() == null || point.getGrade() < 7) {
                continue; // 只处理7级以上
            }
            
            // Step 1: 时间判断
            LocalDateTime pointTime = Instant.ofEpochMilli(point.getTime())
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            
            if (pointTime.isBefore(startTime) || pointTime.isAfter(endTime)) {
                continue; // 不在时间范围内
            }
            
            // Step 2: 空间判断 - 计算距离
            double distance = calculateDistance(targetLat, targetLon, point.getLat(), point.getLon());
            
            // Step 3: 等级判断 - 检查是否进入各级风圈
            int highestLevel = getHighestWindLevel(point.getGrade(), distance);
            
            if (highestLevel > 0) {
                // 检查是否已触发更高等级
                Long timeKey = point.getTime() / (6 * 3600 * 1000); // 按6小时间隔分组
                Integer existingLevel = triggeredLevels.get(timeKey);
                
                if (existingLevel == null || highestLevel > existingLevel) {
                    triggeredLevels.put(timeKey, highestLevel);
                    
                    AlertReport report = createAlertReport(subscription.getId(), typhoonId, 
                            highestLevel, distance, pointTime);
                    reports.add(report);
                }
            }
        }
        
        return reports;
    }

    /**
     * 计算两点之间的距离（Haversine公式）
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // 地球半径（km）
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return R * c;
    }

    /**
     * 根据台风等级和距离判断最高风圈等级
     */
    private int getHighestWindLevel(Integer grade, double distance) {
        // 12级风圈
        double radius12 = calculateWindRadius(grade, 12);
        if (radius12 > 0 && distance <= radius12) {
            return 12;
        }
        
        // 10级风圈
        double radius10 = calculateWindRadius(grade, 10);
        if (radius10 > 0 && distance <= radius10) {
            return 10;
        }
        
        // 7级风圈
        double radius7 = calculateWindRadius(grade, 7);
        if (radius7 > 0 && distance <= radius7) {
            return 7;
        }
        
        return 0;
    }

    /**
     * 计算风圈半径（km）
     * 7级半径 = 150km + (level-7)*15km
     * 10级半径 = 80km + (level-10)*12km  
     * 12级半径 = 40km + (level-12)*10km
     */
    private double calculateWindRadius(Integer typhoonGrade, Integer targetLevel) {
        if (typhoonGrade < targetLevel) {
            return -1;
        }
        
        switch (targetLevel) {
            case 7:
                return 150 + Math.max(0, typhoonGrade - 7) * 15;
            case 10:
                return 80 + Math.max(0, typhoonGrade - 10) * 12;
            case 12:
                return 40 + Math.max(0, typhoonGrade - 12) * 10;
            default:
                return -1;
        }
    }

    /**
     * 创建预警报告
     */
    private AlertReport createAlertReport(Long subscriptionId, Long typhoonId, 
                                          Integer level, Double distance, 
                                          LocalDateTime triggerTime) {
        AlertTemplate template = templateMapper.selectByLevel(level);
        
        StringBuilder message = new StringBuilder();
        message.append("【台风预警 - ").append(level).append("级风圈】\n\n");
        
        if (template != null) {
            message.append("【个人与家庭防范措施】\n");
            message.append(template.getPersonalMeasures()).append("\n\n");
            
            message.append("【户外与作业安全措施】\n");
            message.append(template.getOutdoorMeasures()).append("\n\n");
            
            message.append("【海上与水域人员措施】\n");
            message.append(template.getMarineMeasures());
        } else {
            // 默认模板
            message.append(getDefaultMessage(level));
        }
        
        AlertReport report = new AlertReport();
        report.setSubscriptionId(subscriptionId);
        report.setTyphoonId(typhoonId);
        report.setLevel(level);
        report.setMessage(message.toString());
        report.setTriggerTime(triggerTime);
        report.setDistance(Math.round(distance * 100.0) / 100.0);
        report.setStatus(0); // 未读
        report.setCreateTime(LocalDateTime.now());
        
        return report;
    }

    /**
     * 获取默认预警消息
     */
    private String getDefaultMessage(Integer level) {
        if (level == 7) {
            return "【个人与家庭防范措施】\n" +
                   "- 关注台风动态，准备应急物资\n" +
                   "- 检查门窗，加固易吹落物品\n" +
                   "- 低洼地区居民做好防涝准备\n\n" +
                   "【户外与作业安全措施】\n" +
                   "- 减少户外活动，远离广告牌\n" +
                   "- 建筑工地做好防风措施\n" +
                   "- 高空作业暂停\n\n" +
                   "【海上与水域人员措施】\n" +
                   "- 小型船只回港避风\n" +
                   "- 海上作业人员注意安全\n" +
                   "- 避免出海捕鱼";
        } else if (level == 10) {
            return "【个人与家庭防范措施】\n" +
                   "- 准备应急电源和食品\n" +
                   "- 检查并加固房屋设施\n" +
                   "- 低洼地区居民做好转移准备\n\n" +
                   "【户外与作业安全措施】\n" +
                   "- 停止户外作业，人员转移\n" +
                   "- 学校停课，商场停业\n" +
                   "- 避免在危险区域逗留\n\n" +
                   "【海上与水域人员措施】\n" +
                   "- 所有船只立即回港避风\n" +
                   "- 海上作业全部停止\n" +
                   "- 海上人员紧急撤离";
        } else if (level == 12) {
            return "【个人与家庭防范措施】\n" +
                   "- 立即进入应急避难场所\n" +
                   "- 远离窗户和危险建筑\n" +
                   "- 准备自救工具\n\n" +
                   "【户外与作业安全措施】\n" +
                   "- 全面停工停课\n" +
                   "- 避免外出，就地避险\n" +
                   "- 电力通信设施重点防护\n\n" +
                   "【海上与水域人员措施】\n" +
                   "- 所有船舶进入紧急状态\n" +
                   "- 海上人员紧急转移\n" +
                   "- 港口设施全面加固";
        }
        return "";
    }

    @Override
    @Transactional
    public void markAsRead(Long reportId) {
        AlertReport report = reportMapper.selectById(reportId);
        if (report != null) {
            report.setStatus(1);
            reportMapper.updateById(report);
        }
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        reportMapper.deleteById(reportId);
    }

    @Override
    @Transactional
    public void batchDeleteReports(List<Long> reportIds) {
        reportMapper.deleteBatchIds(reportIds);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return reportMapper.countUnread(userId);
    }
}