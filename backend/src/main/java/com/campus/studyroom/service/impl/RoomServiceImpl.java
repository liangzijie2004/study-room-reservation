package com.campus.studyroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.studyroom.common.BusinessException;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.entity.Reservation;
import com.campus.studyroom.entity.Seat;
import com.campus.studyroom.entity.StudyRoom;
import com.campus.studyroom.mapper.ReservationMapper;
import com.campus.studyroom.mapper.SeatMapper;
import com.campus.studyroom.mapper.StudyRoomMapper;
import com.campus.studyroom.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RoomServiceImpl extends ServiceImpl<StudyRoomMapper, StudyRoom> implements RoomService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public PageResult<StudyRoom> pageRooms(long page, long size, String keyword) {
        LambdaQueryWrapper<StudyRoom> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(StudyRoom::getName, keyword)
                    .or().like(StudyRoom::getBuilding, keyword)
                    .or().like(StudyRoom::getLocation, keyword));
        }
        wrapper.orderByDesc(StudyRoom::getCreateTime);
        Page<StudyRoom> result = page(new Page<>(page, size), wrapper);
        fillSeatCount(result.getRecords());
        return PageResult.of(result);
    }

    @Override
    public List<StudyRoom> listAll() {
        List<StudyRoom> rooms = list(new LambdaQueryWrapper<StudyRoom>()
                .eq(StudyRoom::getStatus, Constants.ROOM_OPEN)
                .orderByAsc(StudyRoom::getId));
        fillSeatCount(rooms);
        return rooms;
    }

    @Override
    public StudyRoom detail(Long id) {
        StudyRoom room = getById(id);
        if (room == null) {
            throw new BusinessException("自习室不存在");
        }
        room.setSeatCount(seatMapper.selectCount(
                new LambdaQueryWrapper<Seat>().eq(Seat::getRoomId, id)).intValue());
        return room;
    }

    @Override
    public void createRoom(StudyRoom room) {
        room.setId(null);
        if (room.getStatus() == null) {
            room.setStatus(Constants.ROOM_OPEN);
        }
        save(room);
    }

    @Override
    public void updateRoom(Long id, StudyRoom room) {
        if (getById(id) == null) {
            throw new BusinessException("自习室不存在");
        }
        room.setId(id);
        updateById(room);
    }

    @Override
    public void deleteRoom(Long id) {
        if (getById(id) == null) {
            throw new BusinessException("自习室不存在");
        }
        seatMapper.delete(new LambdaQueryWrapper<Seat>().eq(Seat::getRoomId, id));
        reservationMapper.delete(new LambdaQueryWrapper<Reservation>().eq(Reservation::getRoomId, id));
        removeById(id);
    }

    /**
     * 批量填充自习室座位数量
     */
    private void fillSeatCount(List<StudyRoom> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return;
        }
        for (StudyRoom room : rooms) {
            long count = seatMapper.selectCount(
                    new LambdaQueryWrapper<Seat>().eq(Seat::getRoomId, room.getId()));
            room.setSeatCount((int) count);
        }
    }
}