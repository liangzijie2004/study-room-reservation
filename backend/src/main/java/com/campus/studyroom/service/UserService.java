package com.campus.studyroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.dto.LoginDTO;
import com.campus.studyroom.dto.PasswordDTO;
import com.campus.studyroom.dto.ProfileDTO;
import com.campus.studyroom.dto.RegisterDTO;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.vo.LoginVO;

public interface UserService extends IService<User> {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    User getCurrentUser(Long userId);

    void updateProfile(Long userId, ProfileDTO dto);

    void updatePassword(Long userId, PasswordDTO dto);

    PageResult<User> pageUsers(long page, long size, String keyword, String role);

    void changeStatus(Long id, Integer status);

    void updateUser(Long id, User user);

    void deleteUser(Long id);
}