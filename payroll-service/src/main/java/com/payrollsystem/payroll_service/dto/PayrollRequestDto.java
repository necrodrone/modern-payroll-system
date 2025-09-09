package com.payrollsystem.payroll_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PayrollRequestDto {

    @NotNull(message = "Employee ID is mandatory.")
    private Long employeeId;

    @NotNull(message = "Pay period start date is mandatory.")
    private LocalDate payPeriodStartDate;

    @NotNull(message = "Pay period end date is mandatory.")
    private LocalDate payPeriodEndDate;
}
