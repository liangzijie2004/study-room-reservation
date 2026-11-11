package com.campus.studyroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.entity.Reservation;
import com.campus.studyroom.entity.Seat;
import com.campus.studyroom.entity.StudyRoom;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.mapper.ReservationMapper;
import com.campus.studyroom.mapper.SeatMapper;
import com.campus.studyroom.mapper.StudyRoomMapper;
import com.campus.studyroom.mapper.UserMapper;
import com.campus.studyroom.service.StatsService;
import com.campus.studyroom.vo.RoomRankVO;
import com.campus.studyroom.vo.StatsOverviewVO;
import com.campus.studyroom.vo.TrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudyRoomMapper roomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public StatsOverviewVO overview() {
        StatsOverviewVO vo = new StatsOverviewVO();
        long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>());
        long roomCount = roomMapper.selectCount(new LambdaQueryWrapper<StudyRoom>());
        long seatCount = seatMapper.selectCount(new LambdaQueryWrapper<Seat>());
        long todayCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getReserveDate, LocalDate.now())
                .in(Reservation::getStatus, Constants.RESERVE_BOOKED,
                        Constants.RESERVE_CHECKED_IN, Constants.RESERVE_FINISHED));

        vo.setUserCount(userCount);
        vo.setRoomCount(roomCount);
        vo.setSeatCount(seatCount);
        vo.setTodayReservationCount(todayCount);

        double rate = 0D;
        if (seatCount > 0) {
            rate = Math.min(100D, todayCount * 100.0 / seatCount);
        }
        vo.setOccupancyRate(BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP).doubleValue());
        return vo;
    }

    @Override
    public List<TrendVO> trend(int days) {
        if (days <= 0) {
            days = 7;
        }
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days - 1L);
        List<Map<String, Object>> rows = reservationMapper.selectTrend(start);

        Map<String, Long> countMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> lower = lowerKeys(row);
            countMap.put(toDateString(lower.get("reservedate")), toLong(lower.get("cnt")));
        }
        List<TrendVO> list = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            String date = start.plusDays(i).format(DATE_FMT);
            list.add(new TrendVO(date, countMap.getOrDefault(date, 0L)));
        }
        return list;
    }

    @Override
    public List<RoomRankVO> roomRanking() {
        List<Map<String, Object>> rows = reservationMapper.selectRoomRanking();
        List<RoomRankVO> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> lower = lowerKeys(row);
            Object name = lower.get("roomname");
            list.add(new RoomRankVO(name == null ? "未知自习室" : name.toString(), toLong(lower.get("cnt"))));
        }
        return list;
    }

    /** 将 map 的 key 统一转为小写，规避不同数据库列标签大小写差异 */
    private Map<String, Object> lowerKeys(Map<String, Object> row) {
        Map<String, Object> result = new HashMap<>();
        row.forEach((k, v) -> result.put(k.toLowerCase(), v));
        return result;
    }

    private String toDateString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().format(DATE_FMT);
        }
        return value.toString();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }
}