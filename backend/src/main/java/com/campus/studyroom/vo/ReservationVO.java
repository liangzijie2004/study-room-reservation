package com.campus.studyroom.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录展示对象（含用户名 / 自习室名 / 座位号）
 */
@Data
public class ReservationVO {

    private Long id;
    private Long userId;
    private String userName;
    private String studentNo;
    private Long roomId;
    private String roomName;
    private Long seatId;
    private String seatNo;
    private LocalDate reserveDate;
    private String startTime;
    private String endTime;
    private Integer status;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private LocalDateTime createTime;
}