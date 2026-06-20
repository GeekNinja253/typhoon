package com.typhoon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.typhoon.entity.AlertReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 预警报告Mapper
 */
@Mapper
public interface AlertReportMapper extends BaseMapper<AlertReport> {

    /**
     * 查询未读预警数量
     */
    @Select("SELECT COUNT(*) FROM alert_report ar " +
            "JOIN alert_subscription asub ON ar.subscription_id = asub.id " +
            "WHERE asub.user_id = #{userId} AND ar.status = 0")
    int countUnread(@Param("userId") Long userId);
}