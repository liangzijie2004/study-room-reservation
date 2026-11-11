package com.campus.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.studyroom.entity.Seat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeatMapper extends BaseMapper<Seat> {
}