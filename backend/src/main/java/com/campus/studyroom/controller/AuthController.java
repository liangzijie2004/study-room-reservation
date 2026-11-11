package com.campus.studyroom.controller;

import com.campus.studyroom.common.Result;
import com.campus.studyroom.dto.LoginDTO;
import com.campus.studyroom.dto.RegisterDTO;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.service.UserService;
import com.campus.studyroom.util.UserContext;
import com.campus.studyroom.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证：注册 / 登录 / 当前用户
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated RegisterDTO dto) {
        userService.register(dto);
        return Result.ok("注册成功", null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Validated LoginDTO dto) {
        return Result.ok("登录成功", userService.login(dto));
    }

    @GetMapping("/info")
    public Result<User> info() {
        return Result.ok(userService.getCurrentUser(UserContext.getUserId()));
    }
}