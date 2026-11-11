package com.campus.studyroom.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自习室预约排行
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRankVO {

    private String roomName;
    private Long count;
}