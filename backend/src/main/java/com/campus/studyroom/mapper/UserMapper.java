package com.campus.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.studyroom.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}