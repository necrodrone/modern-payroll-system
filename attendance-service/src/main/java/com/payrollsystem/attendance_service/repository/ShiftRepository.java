package com.payrollsystem.attendance_service.repository;

import com.payrollsystem.attendance_service.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
