package com.payrollsystem.holiday_service.controller;

import com.payrollsystem.holiday_service.dto.SuspensionRequestDto;
import com.payrollsystem.holiday_service.model.Suspension;
import com.payrollsystem.holiday_service.service.SuspensionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/suspensions")
public class SuspensionController {

    private final SuspensionService suspensionService;

    @Autowired
    public SuspensionController(SuspensionService suspensionService) {
        this.suspensionService = suspensionService;
    }

    @PostMapping
    public ResponseEntity<Suspension> createSuspension(@Valid @RequestBody SuspensionRequestDto suspensionDto) {
        Suspension newSuspension = suspensionService.createSuspension(suspensionDto);
        return new ResponseEntity<>(newSuspension, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Suspension>> getAllSuspensions() {
        List<Suspension> suspensions = suspensionService.getAllSuspensions();
        return new ResponseEntity<>(suspensions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Suspension> getSuspensionById(@PathVariable Long id) {
        Suspension suspension = suspensionService.getSuspensionById(id);
        return new ResponseEntity<>(suspension, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Suspension> updateSuspension(@PathVariable Long id, @Valid @RequestBody SuspensionRequestDto suspensionDto) {
        Suspension updatedSuspension = suspensionService.updateSuspension(id, suspensionDto);
        return new ResponseEntity<>(updatedSuspension, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuspension(@PathVariable Long id) {
        suspensionService.deleteSuspension(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
