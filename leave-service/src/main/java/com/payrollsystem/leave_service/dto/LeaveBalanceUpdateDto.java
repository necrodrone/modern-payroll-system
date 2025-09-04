package com.payrollsystem.leave_service.dto;

import com.payrollsystem.leave_service.model.LeaveType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveBalanceUpdateDto {

    @NotNull(message = "Employee ID is mandatory.")
    private Long employeeId;

    @NotNull(message = "Leave type is mandatory.")
    private LeaveType leaveType;

    @NotNull(message = "Days to update is mandatory.")
    private Double days;
}
