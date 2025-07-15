package com.payrollsystem.employee_service.repository;

import com.payrollsystem.employee_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {}