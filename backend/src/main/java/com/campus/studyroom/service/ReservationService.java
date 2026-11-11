package com.campus.studyroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.dto.ReservationDTO;
import com.campus.studyroom.entity.Reservation;
import com.campus.studyroom.vo.ReservationVO;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService extends IService<Reservation> {

    /** 固定时段列表 */
    List<String> slots();

    void create(Long userId, ReservationDTO dto);

    PageResult<ReservationVO> pageMy(Long userId, long page, long size, Integer status);

    PageResult<ReservationVO> pageAll(long page, long size, Integer status, String keyword, LocalDate date);

    void cancel(Long id, Long userId, boolean admin);

    void checkin(Long userId, Long id);

    void checkout(Long userId, Long id);

    /** 某自习室某时段已被占用的座位号列表 */
    List<String> occupiedSeatNos(Long roomId, LocalDate date, String startTime, String endTime);
}