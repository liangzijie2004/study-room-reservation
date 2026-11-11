-- ============================================================
-- 校园共享自习室座位预约系统  数据库结构（H2 内存库 / MySQL 兼容模式）
-- 说明：H2 建表脚本不写注释语法，字段含义见 db/mysql/schema.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(100) NOT NULL,
    real_name   VARCHAR(50),
    student_no  VARCHAR(30),
    phone       VARCHAR(20),
    avatar      VARCHAR(255),
    role        VARCHAR(20) DEFAULT 'STUDENT',
    status      INT         DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS study_room
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    building    VARCHAR(50),
    floor       VARCHAR(20),
    location    VARCHAR(100),
    open_time   VARCHAR(10),
    close_time  VARCHAR(10),
    capacity    INT,
    description VARCHAR(255),
    status      INT DEFAULT 1,
    create_time DATETIME
);

CREATE TABLE IF NOT EXISTS seat
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id     BIGINT      NOT NULL,
    seat_no     VARCHAR(20) NOT NULL,
    area        VARCHAR(30),
    row_index   INT,
    col_index   INT,
    status      INT DEFAULT 1,
    create_time DATETIME
);

CREATE TABLE IF NOT EXISTS reservation
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    room_id       BIGINT      NOT NULL,
    seat_id       BIGINT      NOT NULL,
    reserve_date  DATE        NOT NULL,
    start_time    VARCHAR(10) NOT NULL,
    end_time      VARCHAR(10) NOT NULL,
    status        INT DEFAULT 0,
    checkin_time  DATETIME,
    checkout_time DATETIME,
    create_time   DATETIME
);

CREATE TABLE IF NOT EXISTS notice
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(100) NOT NULL,
    content      VARCHAR(2000),
    publisher_id BIGINT,
    create_time  DATETIME
);