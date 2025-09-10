package com.payrollsystem.leave_service.repository;

import com.payrollsystem.leave_service.model.Leave;
import com.payrollsystem.leave_service.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Custom query to find a leave request by its ID and the associated employee ID
    Optional<Leave> findByIdAndEmployeeId(Long id, Long employeeId);

    // Custom query to find all leave requests for a specific employee
    List<Leave> findByEmployeeId(Long employeeId);

    // Custom query to find leaves that overlap with a given date range for a specific employee
    List<Leave> findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long employeeId, LocalDate endDate, LocalDate startDate);

    // Custom query to find leaves that overlap with a given date range for a specific employee and status
    List<Leave> findByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long employeeId, LeaveStatus status, LocalDate endDate, LocalDate startDate);

    // Custom query to check for overlapping leave requests for an employee
    // It returns true if an existing leave request overlaps with the given date range.
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Leave l " +
            "WHERE l.employeeId = :employeeId " +
            "AND l.status IN ('PENDING', 'APPROVED') " +
            "AND ((:startDate BETWEEN l.startDate AND l.endDate) OR (:endDate BETWEEN l.startDate AND l.endDate) " +
            "OR (l.startDate BETWEEN :startDate AND :endDate) OR (l.endDate BETWEEN :startDate AND :endDate))")
    boolean existsByEmployeeIdAndDates(@Param("employeeId") Long employeeId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
}
