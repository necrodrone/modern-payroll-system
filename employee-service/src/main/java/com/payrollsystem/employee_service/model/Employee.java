package com.payrollsystem.employee_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data; // Import Lombok's @Data annotation

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data // This single annotation generates all getters, setters, equals, hashCode, and toString methods
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Position is required")
    private String position;

    @NotBlank(message = "Contact info is required")
    private String phoneNumber;

    @Column(precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2, message = "Salary must be a valid monetary amount with up to 10 digits and 2 decimals")
    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    @NotNull(message = "Hired date is required")
    @PastOrPresent(message = "Hired date cannot be in the future")
    private LocalDate hiredDate;
}