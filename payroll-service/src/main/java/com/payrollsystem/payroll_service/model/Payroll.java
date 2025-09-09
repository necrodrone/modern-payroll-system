package com.payrollsystem.payroll_service.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Table("payroll")
public class Payroll {

    // --- Getters and Setters ---
    @Id
    private Long id;

    @NotNull(message = "Employee ID cannot be null.")
    @Min(value = 1, message = "Employee ID must be a positive number.")
    @Column("employee_id")
    private Long employeeId;

    @NotNull(message = "Pay period start date cannot be null.")
    @PastOrPresent(message = "Pay period start date must be in the past or present.")
    @Column("pay_period_start_date")
    private LocalDate payPeriodStartDate;

    @NotNull(message = "Pay period end date cannot be null.")
    @PastOrPresent(message = "Pay period end date must be in the past or present.")
    @Column("pay_period_end_date")
    private LocalDate payPeriodEndDate;

    @NotNull(message = "Gross pay cannot be null.")
    @DecimalMin(value = "0.0", message = "Gross pay must be a non-negative value.")
    @Column("gross_pay")
    private BigDecimal grossPay;

    @NotNull(message = "Net pay cannot be null.")
    @DecimalMin(value = "0.0", message = "Net pay must be a non-negative value.")
    @Column("net_pay")
    private BigDecimal netPay;

    @NotNull(message = "Total taxes cannot be null.")
    @DecimalMin(value = "0.0", message = "Total taxes must be a non-negative value.")
    @Column("total_taxes")
    private BigDecimal totalTaxes;

    @NotNull(message = "Total deductions cannot be null.")
    @DecimalMin(value = "0.0", message = "Total deductions must be a non-negative value.")
    @Column("total_deductions")
    private BigDecimal totalDeductions;

    @NotNull(message = "Total hours worked cannot be null.")
    @Min(value = 0, message = "Total hours worked must be a non-negative value.")
    @Column("total_hours_worked")
    private Integer totalHoursWorked;

}
