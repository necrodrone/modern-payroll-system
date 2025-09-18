package com.payrollsystem.employee_service.model;

import com.payrollsystem.employee_service.validation.AtLeastOneNotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AtLeastOneNotNull(
        fieldNames = {"hourlyRate", "dailyRate", "weeklyRate", "monthlyRate", "yearlyRate"},
        message = "Either hourly rate or daily rate must be provided."
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

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
    @Digits(integer = 10, fraction = 2, message = "Hourly Rate must be a valid monetary amount with up to 10 digits and 2 decimals")
    @Positive(message = "Hourly Rate must be positive")
    private BigDecimal hourlyRate;

    @Column(precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2, message = "Daily Rate must be a valid monetary amount with up to 10 digits and 2 decimals")
    @Positive(message = "Daily Rate must be positive")
    private BigDecimal dailyRate;

    @Column(precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2, message = "Weekly Rate must be a valid monetary amount with up to 10 digits and 2 decimals")
    @Positive(message = "Weekly Rate must be positive")
    private BigDecimal weeklyRate;

    @Column(precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2, message = "Monthly Rate must be a valid monetary amount with up to 10 digits and 2 decimals")
    @Positive(message = "Monthly Rate must be positive")
    private BigDecimal monthlyRate;

    @Column(precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2, message = "Yearly Rate must be a valid monetary amount with up to 10 digits and 2 decimals")
    @Positive(message = "Yearly Rate must be positive")
    private BigDecimal yearlyRate;

    @NotNull(message = "Hired date is required")
    @PastOrPresent(message = "Hired date cannot be in the future")
    private LocalDate hiredDate;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    // --- fields for government contributions ---
    @Pattern(regexp = "^$|^[0-9]{10}$", message = "SSS number must be 10 digits")
    private String sssNumber;

    @Pattern(regexp = "^$|^[0-9]{12}$", message = "PhilHealth number must be 12 digits")
    private String philhealthNumber;

    @Pattern(regexp = "^$|^[0-9]{12}$", message = "Pag-IBIG number must be 12 digits")
    private String pagibigNumber;

    @Pattern(regexp = "^$|^[0-9]{11}$", message = "GSIS number must be 11 digits")
    private String gsisNumber;
}
