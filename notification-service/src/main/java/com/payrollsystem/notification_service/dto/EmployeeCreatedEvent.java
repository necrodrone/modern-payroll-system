package com.payrollsystem.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation for getters, setters, equals, hashCode, toString
@NoArgsConstructor // Lombok annotation for no-argument constructor
@AllArgsConstructor // Lombok annotation for all-argument constructor
public class EmployeeCreatedEvent {
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeePhoneNumber;
    // Add other relevant employee details as needed
}