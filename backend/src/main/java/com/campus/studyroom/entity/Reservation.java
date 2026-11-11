package com.campus.studyroom.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录
 */
@Data
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roomId;

    private Long seatId;

    /** 预约日期 */
    private LocalDate reserveDate;

    /** 开始时间 如 08:00 */
    private String startTime;

    /** 结束时间 如 10:00 */
    private String endTime;

    /** 状态：0 已预约 1 已签到 2 已完成 3 已取消 4 违约 */
    private Integer status;

    private LocalDateTime checkinTime;

    private LocalDateTime checkoutTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}