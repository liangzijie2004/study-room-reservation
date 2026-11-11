package com.campus.studyroom.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SeatBatchDTO {

    @NotNull(message = "请选择自习室")
    private Long roomId;

    @NotNull(message = "请填写行数")
    @Min(value = 1, message = "行数至少为 1")
    private Integer rowCount;

    @NotNull(message = "请填写列数")
    @Min(value = 1, message = "列数至少为 1")
    private Integer colCount;

    /** 区域名称，如 靠窗区 / 中央区，可为空 */
    private String area;
}