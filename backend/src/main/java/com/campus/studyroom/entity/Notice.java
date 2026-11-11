package com.campus.studyroom.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告
 */
@Data
@TableName("notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /** 发布人 id */
    private Long publisherId;

    /** 发布人姓名（非数据库字段） */
    @TableField(exist = false)
    private String publisherName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}