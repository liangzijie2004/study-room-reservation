package com.campus.studyroom.common;

import java.util.Arrays;
import java.util.List;

/**
 * 业务常量
 */
public interface Constants {

    /** 角色 */
    String ROLE_STUDENT = "STUDENT";
    String ROLE_ADMIN = "ADMIN";

    /** 用户状态 */
    Integer STATUS_ENABLED = 1;
    Integer STATUS_DISABLED = 0;

    /** 自习室状态 */
    Integer ROOM_OPEN = 1;
    Integer ROOM_CLOSED = 0;

    /** 座位状态：1 可用 0 维护中 */
    Integer SEAT_AVAILABLE = 1;
    Integer SEAT_MAINTENANCE = 0;

    /** 预约状态：0 已预约 1 已签到 2 已完成 3 已取消 4 违约 */
    Integer RESERVE_BOOKED = 0;
    Integer RESERVE_CHECKED_IN = 1;
    Integer RESERVE_FINISHED = 2;
    Integer RESERVE_CANCELED = 3;
    Integer RESERVE_VIOLATED = 4;

    /** 固定预约时段 */
    List<String> TIME_SLOTS = Arrays.asList(
            "08:00-10:00",
            "10:00-12:00",
            "14:00-16:00",
            "16:00-18:00",
            "19:00-21:00");
}