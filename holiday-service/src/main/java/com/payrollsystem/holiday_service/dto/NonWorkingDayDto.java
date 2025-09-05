package com.payrollsystem.holiday_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class NonWorkingDayDto {

    private LocalDate date;
    private String name;
    private String type;

    public NonWorkingDayDto(LocalDate date, String name, String type) {
        this.date = date;
        this.name = name;
        this.type = type;
    }
}
