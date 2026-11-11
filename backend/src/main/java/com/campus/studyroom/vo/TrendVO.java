package com.campus.studyroom.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预约趋势
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendVO {

    private String date;
    private Long count;
}