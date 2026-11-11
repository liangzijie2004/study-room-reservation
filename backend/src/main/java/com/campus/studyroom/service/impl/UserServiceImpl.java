package com.campus.studyroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.studyroom.common.BusinessException;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.dto.LoginDTO;
import com.campus.studyroom.dto.PasswordDTO;
import com.campus.studyroom.dto.ProfileDTO;
import com.campus.studyroom.dto.RegisterDTO;
import com.campus.studyroom.entity.Reservation;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.mapper.ReservationMapper;
import com.campus.studyroom.mapper.UserMapper;
import com.campus.studyroom.service.UserService;
import com.campus.studyroom.util.JwtUtil;
import com.campus.studyroom.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (Constants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, user);
    }

    @Override
    public void register(RegisterDTO dto) {
        long exists = count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (exists > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setStudentNo(dto.getStudentNo());
        user.setPhone(dto.getPhone());
        user.setRole(Constants.ROLE_STUDENT);
        user.setStatus(Constants.STATUS_ENABLED);
        save(user);
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在或已注销");
        }
        return user;
    }

    @Override
    public void updateProfile(Long userId, ProfileDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (StringUtils.hasText(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        updateById(user);
    }

    @Override
    public void updatePassword(Long userId, PasswordDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        updateById(user);
    }

    @Override
    public PageResult<User> pageUsers(long page, long size, String keyword, String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getStudentNo, keyword));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = page(new Page<>(page, size), wrapper);
        return PageResult.of(result);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    public void updateUser(Long id, User user) {
        User db = getById(id);
        if (db == null) {
            throw new BusinessException("用户不存在");
        }
        db.setRealName(user.getRealName());
        db.setStudentNo(user.getStudentNo());
        db.setPhone(user.getPhone());
        db.setRole(user.getRole());
        updateById(db);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (Constants.ROLE_ADMIN.equals(user.getRole())) {
            throw new BusinessException("管理员账号不允许删除");
        }
        removeById(id);
        reservationMapper.delete(new LambdaQueryWrapper<Reservation>().eq(Reservation::getUserId, id));
    }
}