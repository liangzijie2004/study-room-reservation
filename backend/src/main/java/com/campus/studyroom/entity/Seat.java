package com.campus.studyroom.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 座位
 */
@Data
@TableName("seat")
public class Seat {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    /** 座位号 如 A01 */
    private String seatNo;

    /** 区域 如 靠窗区 */
    private String area;

    /** 座位图行索引（从 0 开始） */
    private Integer rowIndex;

    /** 座位图列索引（从 0 开始） */
    private Integer colIndex;

    /** 状态：1 可用 0 维护中 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}