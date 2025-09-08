package com.payrollsystem.attendance_service.controller;

import com.payrollsystem.attendance_service.model.TimeLog;
import com.payrollsystem.attendance_service.service.TimeLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/timelogs")
public class TimeLogController {

    private final TimeLogService timeLogService;

    public TimeLogController(TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    /**
     * Single punch endpoint.
     * System decides if it's a clock-in or clock-out.
     */
    @PostMapping("/punch/{employeeId}")
    public ResponseEntity<TimeLog> punch(@PathVariable Long employeeId) {
        TimeLog punchedLog = timeLogService.punch(employeeId);
        return new ResponseEntity<>(punchedLog, HttpStatus.CREATED);
    }

    /**
     * Returns the total hours worked by an employee in a given date range.
     */
    @GetMapping("/hours-worked")
    public ResponseEntity<Double> getTotalHoursWorked(
            @RequestParam Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        double totalHours = timeLogService.getTotalHoursWorked(employeeId, start, end);
        return ResponseEntity.ok(totalHours);
    }

    @GetMapping
    public ResponseEntity<List<TimeLog>> getAllTimeLogs() {
        return ResponseEntity.ok(timeLogService.getAllTimeLogs());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TimeLog>> getLogsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(timeLogService.getLogsByEmployee(employeeId));
    }

    @DeleteMapping("/{timelogId}")
    public ResponseEntity<Void> deleteTimeLog(@PathVariable Long timelogId) {
        timeLogService.deleteTimeLog(timelogId);
        return ResponseEntity.noContent().build();
    }
}
