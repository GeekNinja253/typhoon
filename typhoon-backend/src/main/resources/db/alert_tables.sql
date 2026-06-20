-- 预警订阅表
CREATE TABLE IF NOT EXISTS alert_subscription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    city_name VARCHAR(50),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    typhoon_id BIGINT,
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME
);

-- 预警报告表
CREATE TABLE IF NOT EXISTS alert_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    typhoon_id BIGINT,
    level INT NOT NULL,
    message TEXT NOT NULL,
    trigger_time DATETIME NOT NULL,
    distance DOUBLE,
    status INT DEFAULT 0,
    create_time DATETIME
);

-- 预警模板表
CREATE TABLE IF NOT EXISTS alert_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level INT NOT NULL,
    title VARCHAR(100),
    personal_measures TEXT,
    outdoor_measures TEXT,
    marine_measures TEXT,
    create_time DATETIME,
    update_time DATETIME
);

-- 插入预警模板数据
INSERT INTO alert_template (level, title, personal_measures, outdoor_measures, marine_measures) VALUES
(7, '7级风圈预警', 
 '- 关注台风动态，准备应急物资\n- 检查门窗，加固易吹落物品\n- 低洼地区居民做好防涝准备\n- 准备手电筒、电池等应急用品\n- 储备饮用水和食品',
 '- 减少户外活动，远离广告牌\n- 建筑工地做好防风措施\n- 高空作业暂停\n- 检查户外设施安全性\n- 避免在树下或临时建筑内停留',
 '- 小型船只回港避风\n- 海上作业人员注意安全\n- 避免出海捕鱼\n- 港口做好防浪准备\n- 水产养殖设施加固'),
(10, '10级风圈预警',
 '- 准备应急电源和食品\n- 检查并加固房屋设施\n- 低洼地区居民做好转移准备\n- 关闭不必要的电器设备\n- 准备急救包和常用药品',
 '- 停止户外作业，人员转移\n- 学校停课，商场停业\n- 避免在危险区域逗留\n- 交通管制区域遵守指引\n- 加固临时建筑和广告牌',
 '- 所有船只立即回港避风\n- 海上作业全部停止\n- 海上人员紧急撤离\n- 港口设施全面检查\n- 渔业养殖人员转移'),
(12, '12级风圈预警',
 '- 立即进入应急避难场所\n- 远离窗户和危险建筑\n- 准备自救工具\n- 切断非必要电源\n- 遵循政府疏散指示',
 '- 全面停工停课\n- 避免外出，就地避险\n- 电力通信设施重点防护\n- 交通全面管制\n- 危险区域全面封锁',
 '- 所有船舶进入紧急状态\n- 海上人员紧急转移\n- 港口设施全面加固\n- 海上搜救准备\n- 沿海防护设施检查')
ON DUPLICATE KEY UPDATE title = VALUES(title);