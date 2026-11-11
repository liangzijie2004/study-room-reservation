package com.campus.studyroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.studyroom.dto.SeatBatchDTO;
import com.campus.studyroom.entity.Seat;

import java.util.List;

public interface SeatService extends IService<Seat> {

    List<Seat> listByRoom(Long roomId);

    void batchCreate(SeatBatchDTO dto);

    void updateSeat(Long id, Seat seat);

    void deleteSeat(Long id);
}