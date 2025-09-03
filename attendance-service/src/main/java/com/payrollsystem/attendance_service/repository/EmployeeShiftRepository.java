package com.payrollsystem.attendance_service.repository;

import com.payrollsystem.attendance_service.model.EmployeeShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {

    @Query("SELECT CASE WHEN COUNT(es) > 0 THEN TRUE ELSE FALSE END FROM EmployeeShift es " +
            "WHERE es.employeeId = :employeeId " +
            "AND ((:endDate IS NULL AND es.endDate IS NOT NULL AND :startDate <= es.endDate) OR " +
            "(:endDate IS NOT NULL AND es.endDate IS NULL AND es.startDate <= :endDate) OR " +
            "(:endDate IS NOT NULL AND es.endDate IS NOT NULL AND es.startDate < :endDate AND es.endDate > :startDate))")
    boolean existsByEmployeeIdAndDates(@Param("employeeId") Long employeeId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT es FROM EmployeeShift es " +
            "WHERE es.employeeId = :employeeId " +
            "AND ((:endDate IS NULL AND es.endDate IS NOT NULL AND :startDate <= es.endDate) OR " +
            "(:endDate IS NOT NULL AND es.endDate IS NULL AND es.startDate <= :endDate) OR " +
            "(:endDate IS NOT NULL AND es.endDate IS NOT NULL AND es.startDate < :endDate AND es.endDate > :startDate))")
    Optional<EmployeeShift> findByEmployeeIdAndDates(@Param("employeeId") Long employeeId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    // Checks for a permanent shift assignment for a given employee
    boolean existsByEmployeeIdAndEndDateIsNull(Long employeeId);

    // This method is used to find a permanent assignment if it exists
    Optional<EmployeeShift> findByEmployeeIdAndEndDateIsNull(Long employeeId);
}
