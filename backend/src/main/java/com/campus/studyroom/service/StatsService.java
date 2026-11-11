package com.campus.studyroom.service;

import com.campus.studyroom.vo.RoomRankVO;
import com.campus.studyroom.vo.StatsOverviewVO;
import com.campus.studyroom.vo.TrendVO;

import java.util.List;

public interface StatsService {

    StatsOverviewVO overview();

    List<TrendVO> trend(int days);

    List<RoomRankVO> roomRanking();
}