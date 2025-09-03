package com.payrollsystem.attendance_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EmployeeShiftAssignmentDto {

    @NotNull(message = "Employee ID is required.")
    private Long employeeId;

    @NotNull(message = "Shift ID is required.")
    private Long shiftId;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    private LocalDate endDate;
}
