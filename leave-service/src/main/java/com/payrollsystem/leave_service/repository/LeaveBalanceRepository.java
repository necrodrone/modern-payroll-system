package com.payrollsystem.leave_service.repository;

import com.payrollsystem.leave_service.model.LeaveBalance;
import com.payrollsystem.leave_service.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, LeaveBalance.LeaveBalanceId> {

    Optional<LeaveBalance> findById_EmployeeIdAndId_LeaveType(Long employeeId, LeaveType leaveType);

    List<LeaveBalance> findById_EmployeeId(Long employeeId);
}
