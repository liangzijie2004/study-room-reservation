package com.campus.studyroom.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自习室
 */
@Data
@TableName("study_room")
public class StudyRoom {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 楼栋 */
    private String building;

    /** 楼层 */
    private String floor;

    /** 具体位置 */
    private String location;

    /** 开放时间 如 08:00 */
    private String openTime;

    /** 关闭时间 如 22:00 */
    private String closeTime;

    /** 座位容量 */
    private Integer capacity;

    private String description;

    /** 状态：1 开放 0 关闭 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 座位数量（非数据库字段，列表展示用） */
    @TableField(exist = false)
    private Integer seatCount;
}