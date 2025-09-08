package com.payrollsystem.attendance_service.repository;

import com.payrollsystem.attendance_service.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    List<TimeLog> findByEmployeeId(Long employeeId);

    Optional<TimeLog> findTopByEmployeeIdOrderByTimeInDesc(Long employeeId);

    /**
     * Finds all time logs for a given employee within a specific date and time range.
     * The range is inclusive of the start date and exclusive of the end date.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start of the date and time range.
     * @param endDate    The end of the date and time range.
     * @return A list of TimeLog entities.
     */
    List<TimeLog> findByEmployeeIdAndTimeInBetween(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);
}
