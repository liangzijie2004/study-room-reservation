package com.campus.studyroom.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户（学生 / 管理员）
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** 密码：仅允许写入，不返回给前端 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String realName;

    /** 学号 */
    private String studentNo;

    private String phone;

    private String avatar;

    /** 角色：STUDENT / ADMIN */
    private String role;

    /** 状态：1 正常 0 禁用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}