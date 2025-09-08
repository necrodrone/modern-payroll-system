package com.payrollsystem.attendance_service.service;

import com.payrollsystem.attendance_service.exception.BadRequestException;
import com.payrollsystem.attendance_service.exception.NotFoundException;
import com.payrollsystem.attendance_service.model.TimeLog;
import com.payrollsystem.attendance_service.repository.TimeLogRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;

    public TimeLogService(TimeLogRepository timeLogRepository) {
        this.timeLogRepository = timeLogRepository;
    }

    /**
     * Smart punch detection:
     * - If no open log → clock-in.
     * - If open log exists → clock-out.
     * - Ignores duplicate punches within 1 minute.
     */
    public TimeLog punch(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();

        TimeLog lastLog = timeLogRepository
                .findTopByEmployeeIdOrderByTimeInDesc(employeeId)
                .orElse(null);

        if (lastLog == null || lastLog.getTimeOut() != null) {
            // No open log → clock-in
            TimeLog newTimeLog = new TimeLog();
            newTimeLog.setEmployeeId(employeeId);
            newTimeLog.setTimeIn(now);
            return timeLogRepository.save(newTimeLog);
        } else {
            // Open log exists → check if it's a duplicate punch
            if (Duration.between(lastLog.getTimeIn(), now).toSeconds() < 60) {
                // Ignore duplicate punch within 60 seconds
                return lastLog;
            }

            // Otherwise → clock-out
            lastLog.setTimeOut(now);
            return timeLogRepository.save(lastLog);
        }
    }

    public List<TimeLog> getAllTimeLogs() {
        return timeLogRepository.findAll();
    }

    public List<TimeLog> getLogsByEmployee(Long employeeId) {
        return timeLogRepository.findByEmployeeId(employeeId);
    }

    /**
     * Calculates the total hours worked by a specific employee
     * within a given date range.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start date of the period.
     * @param endDate    The end date of the period.
     * @return The total hours worked as a double.
     */
    public double getTotalHoursWorked(Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        // Validate date range
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before the start date.");
        }

        List<TimeLog> timeLogs = timeLogRepository.findByEmployeeIdAndTimeInBetween(employeeId, startDate, endDate);

        double totalHours = 0.0;
        for (TimeLog log : timeLogs) {
            if (log.getTimeIn() != null && log.getTimeOut() != null) {
                Duration duration = Duration.between(log.getTimeIn(), log.getTimeOut());
                totalHours += duration.toMinutes() / 60.0;
            }
        }
        return totalHours;
    }

    public void deleteTimeLog(Long timelogId) {
        if (!timeLogRepository.existsById(timelogId)) {
            throw new NotFoundException("TimeLog with id " + timelogId + " not found");
        }
        timeLogRepository.deleteById(timelogId);
    }
}
