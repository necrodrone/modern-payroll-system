package com.payrollsystem.payroll_service.repository;

import com.payrollsystem.payroll_service.model.Payroll;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository for managing Payroll entities.
 * The @Repository annotation tells Spring to create a concrete implementation of this interface
 * and make it available for dependency injection.
 */
@Repository
public interface PayrollRepository extends ReactiveCrudRepository<Payroll, Long> {

    /**
     * Finds all payroll records for a given employee.
     * Spring Data automatically generates the query based on the method name.
     *
     * @param employeeId the ID of the employee
     * @return a Flux of Payroll objects matching the employee ID
     */
    Flux<Payroll> findByEmployeeId(Long employeeId);
}
