package com.payrollsystem.employee_service.service;

import com.payrollsystem.employee_service.exception.NotFoundException;
import com.payrollsystem.employee_service.model.Employee;
import com.payrollsystem.employee_service.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Page<Employee> getAll(String search, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching employees (search={}, page={}, size={}, sortBy={}, sortDir={})", search, page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search == null || search.trim().isEmpty()) {
            return repository.findAll(pageable);
        } else {
            return repository.search(search.trim(), pageable);
        }
    }

    public Employee getById(Long id) {
        logger.info("Fetching employee with id {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));
    }

    public Employee create(Employee employee) {
        logger.info("Creating new employee: {} {}", employee.getFirstName(), employee.getLastName());
        return repository.save(employee);
    }

    public Employee update(Long id, Employee updatedEmployee) {
        logger.info("Updating employee with id {}", id);
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));

        // Update only the fields
        existing.setFirstName(updatedEmployee.getFirstName());
        existing.setLastName(updatedEmployee.getLastName());
        existing.setEmail(updatedEmployee.getEmail());
        existing.setPosition(updatedEmployee.getPosition());
        existing.setSalary(updatedEmployee.getSalary());
        existing.setHiredDate(updatedEmployee.getHiredDate());

        return repository.save(existing);
    }

    public void delete(Long id) {
        logger.info("Deleting employee with id {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Employee not found with id " + id);
        }
        repository.deleteById(id);
    }
}
