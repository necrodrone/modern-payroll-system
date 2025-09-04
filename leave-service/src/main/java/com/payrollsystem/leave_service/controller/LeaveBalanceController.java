package com.payrollsystem.leave_service.controller;

import com.payrollsystem.leave_service.dto.LeaveBalanceUpdateDto;
import com.payrollsystem.leave_service.model.LeaveBalance;
import com.payrollsystem.leave_service.model.LeaveType;
import com.payrollsystem.leave_service.service.LeaveBalanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-balances")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @Autowired
    public LeaveBalanceController(LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping("/{employeeId}/{leaveType}")
    public ResponseEntity<LeaveBalance> getLeaveBalance(@PathVariable Long employeeId, @PathVariable LeaveType leaveType) {
        LeaveBalance leaveBalance = leaveBalanceService.getLeaveBalance(employeeId, leaveType);
        return new ResponseEntity<>(leaveBalance, HttpStatus.OK);
    }

    // Endpoint to initialize balances for a new employee
    @PostMapping("/initialize/{employeeId}")
    public ResponseEntity<Void> initializeLeaveBalances(@PathVariable Long employeeId, @RequestParam Double initialBalance) {
        leaveBalanceService.initializeLeaveBalances(employeeId, initialBalance);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // A more flexible endpoint for updating balances
    @PutMapping
    public ResponseEntity<Void> updateLeaveBalance(@Valid @RequestBody LeaveBalanceUpdateDto updateDto) {
        leaveBalanceService.updateLeaveBalance(updateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
