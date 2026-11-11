package com.campus.studyroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.studyroom.common.BusinessException;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.dto.SeatBatchDTO;
import com.campus.studyroom.entity.Seat;
import com.campus.studyroom.mapper.SeatMapper;
import com.campus.studyroom.service.SeatService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    @Override
    public List<Seat> listByRoom(Long roomId) {
        return list(new LambdaQueryWrapper<Seat>()
                .eq(Seat::getRoomId, roomId)
                .orderByAsc(Seat::getRowIndex)
                .orderByAsc(Seat::getColIndex));
    }

    @Override
    public void batchCreate(SeatBatchDTO dto) {
        if (dto.getRowCount() > 26) {
            throw new BusinessException("行数不能超过 26");
        }
        long exists = count(new LambdaQueryWrapper<Seat>().eq(Seat::getRoomId, dto.getRoomId()));
        if (exists > 0) {
            throw new BusinessException("该自习室已存在座位，请先删除原有座位后再生成");
        }
        String area = StringUtils.hasText(dto.getArea()) ? dto.getArea() : "普通区";
        List<Seat> seats = new ArrayList<>();
        for (int row = 0; row < dto.getRowCount(); row++) {
            char rowLabel = (char) ('A' + row);
            for (int col = 0; col < dto.getColCount(); col++) {
                Seat seat = new Seat();
                seat.setRoomId(dto.getRoomId());
                seat.setSeatNo(rowLabel + String.format("%02d", col + 1));
                seat.setArea(area);
                seat.setRowIndex(row);
                seat.setColIndex(col);
                seat.setStatus(Constants.SEAT_AVAILABLE);
                seats.add(seat);
            }
        }
        saveBatch(seats);
    }

    @Override
    public void updateSeat(Long id, Seat seat) {
        Seat db = getById(id);
        if (db == null) {
            throw new BusinessException("座位不存在");
        }
        db.setSeatNo(seat.getSeatNo());
        db.setArea(seat.getArea());
        db.setStatus(seat.getStatus());
        updateById(db);
    }

    @Override
    public void deleteSeat(Long id) {
        if (getById(id) == null) {
            throw new BusinessException("座位不存在");
        }
        removeById(id);
    }
}