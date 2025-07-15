package com.payrollsystem.employee_service.service;

import com.payrollsystem.employee_service.model.Employee;
import com.payrollsystem.employee_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public List<Employee> getAll() {
        return repository.findAll();
    }

    public Optional<Employee> getById(Long id) {
        return repository.findById(id);
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public Employee update(Long id, Employee employee) {
        employee.setId(id);
        return repository.save(employee);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}