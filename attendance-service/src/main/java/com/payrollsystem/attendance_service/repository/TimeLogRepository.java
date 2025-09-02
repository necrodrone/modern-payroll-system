package com.payrollsystem.attendance_service.repository;

import com.payrollsystem.attendance_service.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
    List<TimeLog> findByEmployeeId(Long employeeId);

    Optional<TimeLog> findTopByEmployeeIdOrderByTimeInDesc(Long employeeId);
}
