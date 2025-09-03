package com.payrollsystem.attendance_service.controller;

import com.payrollsystem.attendance_service.model.Shift;
import com.payrollsystem.attendance_service.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    // CREATE: POST /v1/shifts
    @PostMapping
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        Shift newShift = shiftService.createShift(shift);
        return new ResponseEntity<>(newShift, HttpStatus.CREATED);
    }

    // READ: GET /v1/shifts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Shift> getShiftById(@PathVariable Long id) {
        Optional<Shift> shift = shiftService.getShiftById(id);
        return shift.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // READ: GET /v1/shifts
    @GetMapping
    public ResponseEntity<List<Shift>> getAllShifts() {
        List<Shift> shifts = shiftService.getAllShifts();
        return new ResponseEntity<>(shifts, HttpStatus.OK);
    }

    // UPDATE: PUT /v1/shifts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable Long id, @RequestBody Shift shift) {
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
