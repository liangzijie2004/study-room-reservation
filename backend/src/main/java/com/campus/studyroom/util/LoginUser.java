package com.campus.studyroom.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户信息（由 JWT 解析得到）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long id;
    private String username;
    private String role;
}