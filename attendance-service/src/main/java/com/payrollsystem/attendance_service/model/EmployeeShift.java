package com.payrollsystem.attendance_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class EmployeeShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long shiftId;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    // Custom validation to ensure endDate is not before startDate when provided
    @AssertTrue(message = "End date cannot be before the start date.")
    public boolean isEndDateValid() {
        if (endDate != null) {
            return !endDate.isBefore(startDate);
        }
        return true;
    }
}
