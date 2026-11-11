-- ============================================================
-- 校园共享自习室座位预约系统  数据库结构（MySQL 8.0）
-- 使用方式：
--   1. 执行本脚本创建库与表；
--   2. 启动后端时指定 --spring.profiles.active=mysql；
--   3. 若表为空，程序会自动写入演示数据（DataInitializer）。
-- ============================================================

CREATE DATABASE IF NOT EXISTS study_room DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE study_room;

DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS study_room;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    real_name   VARCHAR(50) COMMENT '真实姓名',
    student_no  VARCHAR(30) COMMENT '学号',
    phone       VARCHAR(20) COMMENT '手机号',
    avatar      VARCHAR(255) COMMENT '头像',
    role        VARCHAR(20) DEFAULT 'STUDENT' COMMENT '角色 STUDENT/ADMIN',
    status      INT         DEFAULT 1 COMMENT '状态 1正常 0禁用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    KEY idx_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '用户表';

CREATE TABLE study_room
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '自习室名称',
    building    VARCHAR(50) COMMENT '楼栋',
    floor       VARCHAR(20) COMMENT '楼层',
    location    VARCHAR(100) COMMENT '位置',
    open_time   VARCHAR(10) COMMENT '开放时间',
    close_time  VARCHAR(10) COMMENT '关闭时间',
    capacity    INT COMMENT '容量',
    description VARCHAR(255) COMMENT '简介',
    status      INT DEFAULT 1 COMMENT '状态 1开放 0关闭',
    create_time DATETIME COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '自习室表';

CREATE TABLE seat
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    room_id     BIGINT      NOT NULL COMMENT '所属自习室',
    seat_no     VARCHAR(20) NOT NULL COMMENT '座位号',
    area        VARCHAR(30) COMMENT '区域',
    row_index   INT COMMENT '座位图行索引',
    col_index   INT COMMENT '座位图列索引',
    status      INT DEFAULT 1 COMMENT '状态 1可用 0维护中',
    create_time DATETIME COMMENT '创建时间',
    KEY idx_seat_room (room_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位表';

CREATE TABLE reservation
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id       BIGINT      NOT NULL COMMENT '用户id',
    room_id       BIGINT      NOT NULL COMMENT '自习室id',
    seat_id       BIGINT      NOT NULL COMMENT '座位id',
    reserve_date  DATE        NOT NULL COMMENT '预约日期',
    start_time    VARCHAR(10) NOT NULL COMMENT '开始时间',
    end_time      VARCHAR(10) NOT NULL COMMENT '结束时间',
    status        INT DEFAULT 0 COMMENT '0已预约 1已签到 2已完成 3已取消 4违约',
    checkin_time  DATETIME COMMENT '签到时间',
    checkout_time DATETIME COMMENT '签退时间',
    create_time   DATETIME COMMENT '创建时间',
    KEY idx_reservation_seat (seat_id, reserve_date, status),
    KEY idx_reservation_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '预约记录表';

CREATE TABLE notice
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title        VARCHAR(100) NOT NULL COMMENT '标题',
    content      VARCHAR(2000) COMMENT '内容',
    publisher_id BIGINT COMMENT '发布人id',
    create_time  DATETIME COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '公告表';