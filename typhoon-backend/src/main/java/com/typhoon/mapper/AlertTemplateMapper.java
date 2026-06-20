package com.typhoon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.typhoon.entity.AlertTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 预警模板Mapper
 */
@Mapper
public interface AlertTemplateMapper extends BaseMapper<AlertTemplate> {

    /**
     * 根据等级查询模板
     */
    @Select("SELECT * FROM alert_template WHERE level = #{level}")
    AlertTemplate selectByLevel(@Param("level") Integer level);
}