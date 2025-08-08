package com.payrollsystem.employee_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatedEvent {
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeePhoneNumber;
    // Add other relevant employee details as needed
}