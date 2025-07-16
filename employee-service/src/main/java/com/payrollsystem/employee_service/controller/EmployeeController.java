package com.payrollsystem.employee_service.controller;

import com.payrollsystem.employee_service.model.Employee;
import com.payrollsystem.employee_service.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    // Get employee by ID with explicit 404 handling
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new employee, return 201 Created with location header
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        Employee created = service.create(employee);
        return ResponseEntity
                .created( // builds 201 Created response with Location header
                        // e.g. /employees/{id}
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(created.getId())
                                .toUri()
                )
                .body(created);
    }

    // Update employee by ID, return 200 or 404 if not found
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        return service.getById(id)
                .map(existingEmployee -> {
                    Employee updated = service.update(id, employee);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete employee by ID, return 204 or 404 if not found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.getById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
