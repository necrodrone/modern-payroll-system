package com.payrollsystem.attendance_service.controller;

import com.payrollsystem.attendance_service.dto.EmployeeShiftAssignmentDto;
import com.payrollsystem.attendance_service.model.EmployeeShift;
import com.payrollsystem.attendance_service.service.EmployeeShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/employee-shifts")
public class EmployeeShiftController {

    private final EmployeeShiftService employeeShiftService;

    @Autowired
    public EmployeeShiftController(EmployeeShiftService employeeShiftService) {
        this.employeeShiftService = employeeShiftService;
    }

    // CREATE: POST /v1/employee-shifts
    @PostMapping
    public ResponseEntity<EmployeeShift> assignShiftToEmployee(
            @Valid @RequestBody EmployeeShiftAssignmentDto assignmentDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        EmployeeShift newAssignment = employeeShiftService.assignShiftToEmployee(assignmentDto, authorizationHeader);
        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED);
    }

    // READ: GET /v1/employee-shifts
    @GetMapping
    public ResponseEntity<List<EmployeeShift>> getAllEmployeeShifts() {
        List<EmployeeShift> shifts = employeeShiftService.getAllEmployeeShifts();
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    // READ: GET /v1/employee-shifts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeShift> getEmployeeShiftById(@PathVariable Long id) {
        EmployeeShift shift = employeeShiftService.getEmployeeShiftById(id);
        return new ResponseEntity<>(shift, HttpStatus.OK);
    }

    // UPDATE: PUT /v1/employee-shifts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeShift> updateEmployeeShift(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeShiftAssignmentDto assignmentDto,
            @RequestHeader("Authorization") String authorizationHeader) {
        EmployeeShift updatedAssignment = employeeShiftService.updateEmployeeShift(id, assignmentDto, authorizationHeader);
        return new ResponseEntity<>(updatedAssignment, HttpStatus.OK);
    }

    // DELETE: DELETE /v1/employee-shifts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeShift(@PathVariable Long id) {
        employeeShiftService.deleteEmployeeShift(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
