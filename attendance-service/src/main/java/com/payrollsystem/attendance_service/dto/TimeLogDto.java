package com.payrollsystem.attendance_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimeLogDto {
    private Long employeeId;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
}