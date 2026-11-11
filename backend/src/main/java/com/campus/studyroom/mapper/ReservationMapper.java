package com.campus.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.studyroom.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 近 N 天预约趋势（按日期分组）
     */
    @Select("SELECT reserve_date AS reserveDate, COUNT(*) AS cnt " +
            "FROM reservation WHERE reserve_date >= #{start} AND status <> 3 " +
            "GROUP BY reserve_date ORDER BY reserve_date")
    List<Map<String, Object>> selectTrend(@Param("start") LocalDate start);

    /**
     * 自习室预约排行
     */
    @Select("SELECT r.name AS roomName, COUNT(*) AS cnt " +
            "FROM reservation rv LEFT JOIN study_room r ON rv.room_id = r.id " +
            "WHERE rv.status <> 3 GROUP BY rv.room_id, r.name ORDER BY cnt DESC")
    List<Map<String, Object>> selectRoomRanking();
}