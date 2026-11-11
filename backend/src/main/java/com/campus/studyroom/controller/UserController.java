package com.campus.studyroom.controller;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.common.Result;
import com.campus.studyroom.dto.PasswordDTO;
import com.campus.studyroom.dto.ProfileDTO;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.service.UserService;
import com.campus.studyroom.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @AdminOnly
    public Result<PageResult<User>> page(@RequestParam(defaultValue = "1") long page,
                                         @RequestParam(defaultValue = "10") long size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String role) {
        return Result.ok(userService.pageUsers(page, size, keyword, role));
    }

    @PutMapping("/{id}/status")
    @AdminOnly
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.changeStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @AdminOnly
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody ProfileDTO dto) {
        userService.updateProfile(UserContext.getUserId(), dto);
        return Result.ok("资料已更新", null);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Validated PasswordDTO dto) {
        userService.updatePassword(UserContext.getUserId(), dto);
        return Result.ok("密码修改成功", null);
    }
}