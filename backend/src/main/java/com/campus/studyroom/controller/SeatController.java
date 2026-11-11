package com.campus.studyroom.controller;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.Constants;
import com.campus.studyroom.common.Result;
import com.campus.studyroom.dto.SeatBatchDTO;
import com.campus.studyroom.entity.Seat;
import com.campus.studyroom.service.ReservationService;
import com.campus.studyroom.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 座位
 */
@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/room/{roomId}")
    public Result<List<Seat>> listByRoom(@PathVariable Long roomId) {
        return Result.ok(seatService.listByRoom(roomId));
    }

    @GetMapping("/available")
    public Result<List<Long>> available(@RequestParam Long roomId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                        @RequestParam String startTime,
                                        @RequestParam String endTime) {
        List<String> occupied = reservationService.occupiedSeatNos(roomId, date, startTime, endTime);
        List<Long> ids = seatService.listByRoom(roomId).stream()
                .filter(seat -> Constants.SEAT_AVAILABLE.equals(seat.getStatus()))
                .filter(seat -> !occupied.contains(seat.getSeatNo()))
                .map(Seat::getId)
                .collect(Collectors.toList());
        return Result.ok(ids);
    }

    @PostMapping("/batch")
    @AdminOnly
    public Result<Void> batchCreate(@RequestBody @Validated SeatBatchDTO dto) {
        seatService.batchCreate(dto);
        return Result.ok("座位生成成功", null);
    }

    @PutMapping("/{id}")
    @AdminOnly
    public Result<Void> update(@PathVariable Long id, @RequestBody Seat seat) {
        seatService.updateSeat(id, seat);
        return Result.ok("修改成功", null);
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    public Result<Void> delete(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return Result.ok();
    }
}