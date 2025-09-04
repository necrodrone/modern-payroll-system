package com.payrollsystem.attendance_service.controller;

import com.payrollsystem.attendance_service.dto.LeaveRequestDto;
import com.payrollsystem.attendance_service.model.Leave;
import com.payrollsystem.attendance_service.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    public ResponseEntity<Leave> createLeaveRequest(@Valid @RequestBody LeaveRequestDto leaveRequestDto,
                                                    @RequestHeader("Authorization") String authorizationHeader) {
        Leave newLeave = leaveService.createLeaveRequest(leaveRequestDto, authorizationHeader);
        return new ResponseEntity<>(newLeave, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Leave>> getAllLeaves() {
        List<Leave> leaves = leaveService.getAllLeaves();
        return new ResponseEntity<>(leaves, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Leave> getLeaveById(@PathVariable Long id) {
        Leave leave = leaveService.getLeaveById(id);
        return new ResponseEntity<>(leave, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Leave> updateLeaveRequest(@PathVariable Long id,
                                                    @Valid @RequestBody LeaveRequestDto leaveRequestDto,
                                                    @RequestHeader("Authorization") String authorizationHeader) {
        Leave updatedLeave = leaveService.updateLeaveRequest(id, leaveRequestDto, authorizationHeader);
        return new ResponseEntity<>(updatedLeave, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
