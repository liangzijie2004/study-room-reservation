package com.campus.studyroom.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ReservationDTO {

    @NotNull(message = "请选择自习室")
    private Long roomId;

    @NotNull(message = "请选择座位")
    private Long seatId;

    @NotNull(message = "请选择预约日期")
    private LocalDate reserveDate;

    @NotBlank(message = "请选择开始时间")
    private String startTime;

    @NotBlank(message = "请选择结束时间")
    private String endTime;
}