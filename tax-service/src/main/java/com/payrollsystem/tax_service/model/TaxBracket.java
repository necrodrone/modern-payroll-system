package com.payrollsystem.tax_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entity representing a progressive tax bracket.
 * This entity will be used to store tax bracket data in the database,
 * making the tax calculation dynamic and configurable.
 */
@Entity
@Data
@NoArgsConstructor
public class TaxBracket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount from is mandatory.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount from must be a non-negative value.")
    private BigDecimal amountFrom;

    private BigDecimal amountTo;

    @NotNull(message = "Tax percentage is mandatory.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax percentage must be a non-negative value.")
    private BigDecimal taxPercentage;

    @NotNull(message = "Flat deduction is mandatory.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Flat deduction must be a non-negative value.")
    private BigDecimal flatDeduction;
}
