package com.campus.studyroom.vo;

import lombok.Data;

/**
 * 数据看板总览
 */
@Data
public class StatsOverviewVO {

    private Long userCount;
    private Long roomCount;
    private Long seatCount;
    private Long todayReservationCount;
    /** 今日座位使用率（百分比，保留 1 位小数） */
    private Double occupancyRate;
}