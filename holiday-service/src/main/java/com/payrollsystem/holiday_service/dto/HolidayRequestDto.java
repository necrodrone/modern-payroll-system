package com.payrollsystem.holiday_service.dto;

import com.payrollsystem.holiday_service.model.HolidayType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class HolidayRequestDto {

    @NotBlank(message = "Holiday name is mandatory.")
    private String name;

    @NotNull(message = "Date is mandatory.")
    @FutureOrPresent(message = "Holiday date cannot be in the past.")
    private LocalDate date;

    @NotNull(message = "Holiday type is mandatory.")
    private HolidayType type;
}
