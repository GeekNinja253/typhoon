CREATE TABLE typhoon_track (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,

                               typhoon_code VARCHAR(20),
                               chinese_name VARCHAR(50),
                               english_name VARCHAR(50),

                               start_time DATETIME,
                               end_time DATETIME,
                               `current_time` DATETIME,

                               longitude DOUBLE,
                               latitude DOUBLE,

                               intensity VARCHAR(50),
                               level INT,

                               wind_speed INT,
                               pressure INT,

                               direction VARCHAR(20),
                               move_speed INT
);

CREATE TABLE prediction_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    start_longitude DOUBLE,
    start_latitude DOUBLE,
    start_intensity INT,
    start_grade DOUBLE,
    start_wind_speed DOUBLE,
    start_pressure DOUBLE,
    start_direction DOUBLE,
    start_speed DOUBLE,
    create_time DATETIME
);

CREATE TABLE prediction_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT,
    step_index INT,
    pred_longitude DOUBLE,
    pred_latitude DOUBLE,
    pred_grade DOUBLE,
    pred_wind_speed DOUBLE,
    pred_pressure DOUBLE
);