package com.payrollsystem.employee_service.dto;

import com.payrollsystem.employee_service.model.DeductionFrequency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeductionDto {

    private Long id;
    private Long employeeId;
    private String name;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private DeductionFrequency frequency;
}
