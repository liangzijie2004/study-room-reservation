package com.campus.studyroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.studyroom.common.BusinessException;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.dto.ReservationDTO;
import com.campus.studyroom.entity.Reservation;
import com.campus.studyroom.entity.Seat;
import com.campus.studyroom.entity.StudyRoom;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.mapper.ReservationMapper;
import com.campus.studyroom.mapper.SeatMapper;
import com.campus.studyroom.mapper.StudyRoomMapper;
import com.campus.studyroom.mapper.UserMapper;
import com.campus.studyroom.service.ReservationService;
import com.campus.studyroom.vo.ReservationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudyRoomMapper roomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Override
    public List<String> slots() {
        return Constants.TIME_SLOTS;
    }

    @Override
    public void create(Long userId, ReservationDTO dto) {
        String slot = dto.getStartTime() + "-" + dto.getEndTime();
        if (!Constants.TIME_SLOTS.contains(slot)) {
            throw new BusinessException("请选择有效的预约时段");
        }
        if (dto.getReserveDate().isBefore(LocalDate.now())) {
            throw new BusinessException("不能预约过去的日期");
        }
        StudyRoom room = roomMapper.selectById(dto.getRoomId());
        if (room == null || Constants.ROOM_CLOSED.equals(room.getStatus())) {
            throw new BusinessException("自习室不存在或已关闭");
        }
        Seat seat = seatMapper.selectById(dto.getSeatId());
        if (seat == null || !seat.getRoomId().equals(dto.getRoomId())) {
            throw new BusinessException("座位不存在");
        }
        if (Constants.SEAT_MAINTENANCE.equals(seat.getStatus())) {
            throw new BusinessException("该座位维护中，暂不可预约");
        }
        // 1. 座位时段冲突校验
        if (countSeatConflict(dto) > 0) {
            throw new BusinessException("该座位在所选时段已被预约，请更换座位或时段");
        }
        // 2. 用户时段冲突校验（同一时段只能预约一个座位）
        long userConflict = count(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .eq(Reservation::getReserveDate, dto.getReserveDate())
                .in(Reservation::getStatus, Constants.RESERVE_BOOKED, Constants.RESERVE_CHECKED_IN)
                .lt(Reservation::getStartTime, dto.getEndTime())
                .gt(Reservation::getEndTime, dto.getStartTime()));
        if (userConflict > 0) {
            throw new BusinessException("您在该时段已有预约，请勿重复预约");
        }
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setRoomId(dto.getRoomId());
        reservation.setSeatId(dto.getSeatId());
        reservation.setReserveDate(dto.getReserveDate());
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus(Constants.RESERVE_BOOKED);
        save(reservation);
    }

    @Override
    public PageResult<ReservationVO> pageMy(Long userId, long page, long size, Integer status) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getUserId, userId)
                .eq(status != null, Reservation::getStatus, status)
                .orderByDesc(Reservation::getReserveDate)
                .orderByDesc(Reservation::getId);
        Page<Reservation> result = page(new Page<>(page, size), wrapper);
        return toVoPage(result);
    }

    @Override
    public PageResult<ReservationVO> pageAll(long page, long size, Integer status, String keyword, LocalDate date) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Reservation::getStatus, status);
        wrapper.eq(date != null, Reservation::getReserveDate, date);
        if (StringUtils.hasText(keyword)) {
            List<Long> userIds = userMapper.selectList(new LambdaQueryWrapper<User>()
                            .like(User::getUsername, keyword)
                            .or().like(User::getRealName, keyword)
                            .or().like(User::getStudentNo, keyword))
                    .stream().map(User::getId).collect(Collectors.toList());
            if (userIds.isEmpty()) {
                return PageResult.of(Collections.emptyList(), 0, page, size);
            }
            wrapper.in(Reservation::getUserId, userIds);
        }
        wrapper.orderByDesc(Reservation::getReserveDate).orderByDesc(Reservation::getId);
        Page<Reservation> result = page(new Page<>(page, size), wrapper);
        return toVoPage(result);
    }

    @Override
    public void cancel(Long id, Long userId, boolean admin) {
        Reservation reservation = requireReservation(id);
        if (!admin && !reservation.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作他人预约");
        }
        if (!Constants.RESERVE_BOOKED.equals(reservation.getStatus())) {
            throw new BusinessException("当前状态不允许取消");
        }
        reservation.setStatus(Constants.RESERVE_CANCELED);
        updateById(reservation);
    }

    @Override
    public void checkin(Long userId, Long id) {
        Reservation reservation = requireReservation(id);
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作他人预约");
        }
        if (!Constants.RESERVE_BOOKED.equals(reservation.getStatus())) {
            throw new BusinessException("当前状态不允许签到");
        }
        if (!reservation.getReserveDate().equals(LocalDate.now())) {
            throw new BusinessException("仅可在预约当天签到");
        }
        reservation.setStatus(Constants.RESERVE_CHECKED_IN);
        reservation.setCheckinTime(LocalDateTime.now());
        updateById(reservation);
    }

    @Override
    public void checkout(Long userId, Long id) {
        Reservation reservation = requireReservation(id);
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作他人预约");
        }
        if (!Constants.RESERVE_CHECKED_IN.equals(reservation.getStatus())) {
            throw new BusinessException("请先签到后再签退");
        }
        reservation.setStatus(Constants.RESERVE_FINISHED);
        reservation.setCheckoutTime(LocalDateTime.now());
        updateById(reservation);
    }

    @Override
    public List<String> occupiedSeatNos(Long roomId, LocalDate date, String startTime, String endTime) {
        List<Reservation> reservations = list(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getRoomId, roomId)
                .eq(Reservation::getReserveDate, date)
                .in(Reservation::getStatus, Constants.RESERVE_BOOKED, Constants.RESERVE_CHECKED_IN)
                .lt(Reservation::getStartTime, endTime)
                .gt(Reservation::getEndTime, startTime));
        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> seatIds = reservations.stream().map(Reservation::getSeatId).collect(Collectors.toList());
        return seatMapper.selectBatchIds(seatIds).stream()
                .map(Seat::getSeatNo)
                .collect(Collectors.toList());
    }

    private long countSeatConflict(ReservationDTO dto) {
        return count(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getSeatId, dto.getSeatId())
                .eq(Reservation::getReserveDate, dto.getReserveDate())
                .in(Reservation::getStatus, Constants.RESERVE_BOOKED, Constants.RESERVE_CHECKED_IN)
                .lt(Reservation::getStartTime, dto.getEndTime())
                .gt(Reservation::getEndTime, dto.getStartTime()));
    }

    private Reservation requireReservation(Long id) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        return reservation;
    }

    private PageResult<ReservationVO> toVoPage(Page<Reservation> page) {
        List<Reservation> records = page.getRecords();
        if (records.isEmpty()) {
            return PageResult.of(Collections.emptyList(), page.getTotal(), page.getCurrent(), page.getSize());
        }
        List<Long> userIds = records.stream().map(Reservation::getUserId).distinct().collect(Collectors.toList());
        List<Long> roomIds = records.stream().map(Reservation::getRoomId).distinct().collect(Collectors.toList());
        List<Long> seatIds = records.stream().map(Reservation::getSeatId).distinct().collect(Collectors.toList());

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, StudyRoom> roomMap = roomMapper.selectBatchIds(roomIds).stream()
                .collect(Collectors.toMap(StudyRoom::getId, Function.identity()));
        Map<Long, Seat> seatMap = seatMapper.selectBatchIds(seatIds).stream()
                .collect(Collectors.toMap(Seat::getId, Function.identity()));

        List<ReservationVO> voList = new ArrayList<>();
        for (Reservation r : records) {
            ReservationVO vo = new ReservationVO();
            vo.setId(r.getId());
            vo.setUserId(r.getUserId());
            vo.setRoomId(r.getRoomId());
            vo.setSeatId(r.getSeatId());
            vo.setReserveDate(r.getReserveDate());
            vo.setStartTime(r.getStartTime());
            vo.setEndTime(r.getEndTime());
            vo.setStatus(r.getStatus());
            vo.setCheckinTime(r.getCheckinTime());
            vo.setCheckoutTime(r.getCheckoutTime());
            vo.setCreateTime(r.getCreateTime());
            User user = userMap.get(r.getUserId());
            if (user != null) {
                vo.setUserName(user.getRealName());
                vo.setStudentNo(user.getStudentNo());
            }
            StudyRoom room = roomMap.get(r.getRoomId());
            if (room != null) {
                vo.setRoomName(room.getName());
            }
            Seat seat = seatMap.get(r.getSeatId());
            if (seat != null) {
                vo.setSeatNo(seat.getSeatNo());
            }
            voList.add(vo);
        }
        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }
}