package com.campus.studyroom.controller;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.Result;
import com.campus.studyroom.service.StatsService;
import com.campus.studyroom.vo.RoomRankVO;
import com.campus.studyroom.vo.StatsOverviewVO;
import com.campus.studyroom.vo.TrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计（管理端看板）
 */
@RestController
@RequestMapping("/api/stats")
@AdminOnly
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/overview")
    public Result<StatsOverviewVO> overview() {
        return Result.ok(statsService.overview());
    }

    @GetMapping("/trend")
    public Result<List<TrendVO>> trend(@RequestParam(defaultValue = "7") int days) {
        return Result.ok(statsService.trend(days));
    }

    @GetMapping("/room-ranking")
    public Result<List<RoomRankVO>> roomRanking() {
        return Result.ok(statsService.roomRanking());
    }
}