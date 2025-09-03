package com.payrollsystem.attendance_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank
    private String name;

    private LocalTime flexStartTime;

    private LocalTime flexEndTime;

    @NotNull
    @Min(0)
    private Integer workHours;

    @NotNull
    @Min(0)
    private Integer breakMinutes;

    private LocalTime computedShiftEndTime;
}
