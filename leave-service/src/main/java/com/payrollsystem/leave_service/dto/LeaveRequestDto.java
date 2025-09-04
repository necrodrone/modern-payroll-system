package com.payrollsystem.leave_service.dto;

import com.payrollsystem.leave_service.model.LeaveStatus;
import com.payrollsystem.leave_service.model.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LeaveRequestDto {

    @NotNull(message = "Employee ID is mandatory.")
    private Long employeeId;

    @NotNull(message = "Start date is mandatory.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;

    @NotNull(message = "End date is mandatory.")
    private LocalDate endDate;

    @NotNull(message = "Leave type is mandatory.")
    private LeaveType leaveType;

    // This field is included for potential use in update operations, but should not be set by the client on creation.
    private LeaveStatus status;
}
