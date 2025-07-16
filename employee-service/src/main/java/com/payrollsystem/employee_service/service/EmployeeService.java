package com.payrollsystem.employee_service.service;

import com.payrollsystem.employee_service.exception.NotFoundException;
import com.payrollsystem.employee_service.model.Employee;
import com.payrollsystem.employee_service.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAll() {
        logger.info("Fetching all employees");
        return repository.findAll();
    }

    public Optional<Employee> getById(Long id) {
        logger.info("Fetching employee with id {}", id);
        return repository.findById(id);
    }

    public Employee create(Employee employee) {
        logger.info("Creating new employee: {}", employee.getFirstName() + " " + employee.getLastName());
        return repository.save(employee);
    }

    public Employee update(Long id, Employee employee) {
        logger.info("Updating employee with id {}", id);
        return repository.findById(id)
                .map(existing -> {
                    employee.setId(id);
                    return repository.save(employee);
                })
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));
    }

    public void delete(Long id) {
        logger.info("Deleting employee with id {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Employee not found with id " + id);
        }
        repository.deleteById(id);
    }
}
