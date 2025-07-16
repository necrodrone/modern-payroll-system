package com.payrollsystem.employee_service.controller;

import com.payrollsystem.employee_service.model.Employee;
import com.payrollsystem.employee_service.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // Get all employees
    @GetMapping
    public List<Employee> getAll() {
        return service.getAll();
    }

    // Get employee by ID (throws NotFoundException if not found)
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        Employee employee = service.getById(id);
        return ResponseEntity.ok(employee);
    }

    // Create new employee
    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        Employee created = service.create(employee);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // Update employee by ID
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id,@Valid @RequestBody Employee employee) {
        Employee updated = service.update(id, employee);
        return ResponseEntity.ok(updated);
    }

    // Delete employee by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
