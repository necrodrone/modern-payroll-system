package com.payrollsystem.holiday_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class SuspensionRequestDto {

    @NotBlank(message = "Suspension name is mandatory.")
    private String name;

    @NotNull(message = "Start date is mandatory.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;

    @NotNull(message = "End date is mandatory.")
    private LocalDate endDate;

    private String reason;
}
