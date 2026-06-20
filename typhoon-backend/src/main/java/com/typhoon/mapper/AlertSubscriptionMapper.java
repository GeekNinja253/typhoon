package com.typhoon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.typhoon.entity.AlertSubscription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警订阅Mapper
 */
@Mapper
public interface AlertSubscriptionMapper extends BaseMapper<AlertSubscription> {

}