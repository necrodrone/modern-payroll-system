package com.payrollsystem.leave_service.service;

import com.payrollsystem.leave_service.dto.LeaveBalanceUpdateDto;
import com.payrollsystem.leave_service.exception.BadRequestException;
import com.payrollsystem.leave_service.exception.NotFoundException;
import com.payrollsystem.leave_service.model.LeaveBalance;
import com.payrollsystem.leave_service.model.LeaveType;
import com.payrollsystem.leave_service.repository.LeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    public LeaveBalance getLeaveBalance(Long employeeId, LeaveType leaveType) {
        return leaveBalanceRepository.findById_EmployeeIdAndId_LeaveType(employeeId, leaveType)
                .orElseThrow(() -> new NotFoundException("Leave balance not found for employeeId: " + employeeId + " and leaveType: " + leaveType));
    }

    public void initializeLeaveBalances(Long employeeId, Double initialBalance) {
        // Initialize default balances for all leave types for a new employee
        for (LeaveType leaveType : LeaveType.values()) {
            LeaveBalance leaveBalance = new LeaveBalance();
            leaveBalance.setId(new LeaveBalance.LeaveBalanceId(employeeId, leaveType));
            leaveBalance.setBalanceDays(initialBalance);
            leaveBalanceRepository.save(leaveBalance);
        }
    }

    public void updateLeaveBalance(LeaveBalanceUpdateDto updateDto) {
        LeaveBalance leaveBalance = getLeaveBalance(updateDto.getEmployeeId(), updateDto.getLeaveType());
        double newBalance = leaveBalance.getBalanceDays() + updateDto.getDays();
        if (newBalance < 0) {
            throw new BadRequestException("Not enough leave days available for this type.");
        }
        leaveBalance.setBalanceDays(newBalance);
        leaveBalanceRepository.save(leaveBalance);
    }
}
