package com.payrollsystem.employee_service.repository;

import com.payrollsystem.employee_service.model.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    /**
     * Finds all deductions for a given employee ID.
     * @param employeeId The ID of the employee.
     * @return A list of deductions for the specified employee.
     */
    List<Deduction> findByEmployeeId(Long employeeId);
}
