package com.payrollsystem.employee_service.dto;

import com.payrollsystem.employee_service.model.DeductionFrequency;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DeductionRequestDto {

    private Long employeeId;

    @NotBlank(message = "Deduction name is required")
    private String name;

    @NotNull(message = "Deduction amount is required")
    @DecimalMin(value = "0.01", message = "Deduction amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Deduction frequency is required")
    private DeductionFrequency frequency;
}
