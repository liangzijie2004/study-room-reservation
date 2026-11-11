package com.campus.studyroom.controller;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.common.Result;
import com.campus.studyroom.dto.ReservationDTO;
import com.campus.studyroom.service.ReservationService;
import com.campus.studyroom.util.UserContext;
import com.campus.studyroom.vo.ReservationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
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

/**
 * 预约
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/slots")
    public Result<List<String>> slots() {
        return Result.ok(reservationService.slots());
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Validated ReservationDTO dto) {
        reservationService.create(UserContext.getUserId(), dto);
        return Result.ok("预约成功", null);
    }

    @GetMapping("/my")
    public Result<PageResult<ReservationVO>> my(@RequestParam(defaultValue = "1") long page,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) Integer status) {
        return Result.ok(reservationService.pageMy(UserContext.getUserId(), page, size, status));
    }

    @GetMapping
    @AdminOnly
    public Result<PageResult<ReservationVO>> page(@RequestParam(defaultValue = "1") long page,
                                                  @RequestParam(defaultValue = "10") long size,
                                                  @RequestParam(required = false) Integer status,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.ok(reservationService.pageAll(page, size, status, keyword, date));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id, UserContext.getUserId(), UserContext.isAdmin());
        return Result.ok("已取消预约", null);
    }

    @PutMapping("/{id}/checkin")
    public Result<Void> checkin(@PathVariable Long id) {
        reservationService.checkin(UserContext.getUserId(), id);
        return Result.ok("签到成功", null);
    }

    @PutMapping("/{id}/checkout")
    public Result<Void> checkout(@PathVariable Long id) {
        reservationService.checkout(UserContext.getUserId(), id);
        return Result.ok("签退成功", null);
    }

    @GetMapping("/seat-status")
    public Result<List<String>> seatStatus(@RequestParam Long roomId,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                           @RequestParam String startTime,
                                           @RequestParam String endTime) {
        return Result.ok(reservationService.occupiedSeatNos(roomId, date, startTime, endTime));
    }
}