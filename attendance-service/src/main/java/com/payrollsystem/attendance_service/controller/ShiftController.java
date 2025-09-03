package com.payrollsystem.attendance_service.controller;

import com.payrollsystem.attendance_service.model.Shift;
import com.payrollsystem.attendance_service.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    // CREATE: POST /v1/shifts
    @PostMapping
    public ResponseEntity<Shift> createShift(@Valid @RequestBody Shift shift) {
        Shift newShift = shiftService.createShift(shift);
        return new ResponseEntity<>(newShift, HttpStatus.CREATED);
    }

    // READ: GET /v1/shifts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Shift> getShiftById(@PathVariable Long id) {
        Shift shift = shiftService.getShiftById(id);
        return new ResponseEntity<>(shift, HttpStatus.OK);
    }

    // READ: GET /v1/shifts
    @GetMapping
    public ResponseEntity<List<Shift>> getAllShifts() {
        List<Shift> shifts = shiftService.getAllShifts();
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    // UPDATE: PUT /v1/shifts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable Long id, @Valid @RequestBody Shift shift) {
        Shift updatedShift = shiftService.updateShift(id, shift);
        return new ResponseEntity<>(updatedShift, HttpStatus.OK);
    }

    // DELETE: DELETE /v1/shifts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
