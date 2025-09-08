package com.payrollsystem.employee_service.controller;

import com.payrollsystem.employee_service.dto.DeductionDto;
import com.payrollsystem.employee_service.dto.DeductionRequestDto;
import com.payrollsystem.employee_service.service.DeductionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/deductions")
public class DeductionController {

    private final DeductionService deductionService;

    public DeductionController(DeductionService deductionService) {
        this.deductionService = deductionService;
    }

    @PostMapping
    public ResponseEntity<DeductionDto> createDeduction(@Valid @RequestBody DeductionRequestDto deductionRequestDto) {
        DeductionDto newDeduction = deductionService.createDeduction(deductionRequestDto);
        return new ResponseEntity<>(newDeduction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeductionDto>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @GetMapping("/{deductionId}")
    public ResponseEntity<DeductionDto> getDeductionById(@PathVariable Long deductionId) {
        return ResponseEntity.ok(deductionService.getDeductionById(deductionId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<DeductionDto>> getDeductionsByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(deductionService.getDeductionsByEmployeeId(employeeId));
    }

    @PutMapping("/{deductionId}")
    public ResponseEntity<DeductionDto> updateDeduction(
            @PathVariable Long deductionId,
            @Valid @RequestBody DeductionRequestDto updatedDeductionDto) {
        DeductionDto updatedDeduction = deductionService.updateDeduction(deductionId, updatedDeductionDto);
        return ResponseEntity.ok(updatedDeduction);
    }

    @DeleteMapping("/{deductionId}")
    public ResponseEntity<Void> deleteDeduction(@PathVariable Long deductionId) {
        deductionService.deleteDeduction(deductionId);
        return ResponseEntity.noContent().build();
    }
}
