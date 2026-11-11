package com.campus.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.studyroom.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}