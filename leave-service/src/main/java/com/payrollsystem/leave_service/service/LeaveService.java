package com.payrollsystem.leave_service.service;

import com.payrollsystem.leave_service.dto.LeaveRequestDto;
import com.payrollsystem.leave_service.exception.BadRequestException;
import com.payrollsystem.leave_service.exception.ConflictException;
import com.payrollsystem.leave_service.exception.NotFoundException;
import com.payrollsystem.leave_service.model.Leave;
import com.payrollsystem.leave_service.model.LeaveStatus;
import com.payrollsystem.leave_service.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class LeaveService {

    private final WebClient.Builder webClientBuilder;
    private final LeaveRepository leaveRepository;
    private final String employeeServiceUri;

    @Autowired
    public LeaveService(WebClient.Builder webClientBuilder,
                        LeaveRepository leaveRepository,
                        @Value("${service.employee.uri}") String employeeServiceUri) {
        this.webClientBuilder = webClientBuilder;
        this.leaveRepository = leaveRepository;
        this.employeeServiceUri = employeeServiceUri;
    }

    public Leave createLeaveRequest(LeaveRequestDto leaveRequestDto, String authorizationHeader) {
        // 1. Validate employee existence via employee-service
        verifyEmployeeExists(leaveRequestDto.getEmployeeId(), authorizationHeader);

        // 2. Validate dates
        if (leaveRequestDto.getEndDate().isBefore(leaveRequestDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before the start date.");
        }

        // 3. Check for existing leave requests that would cause a conflict
        if (leaveRepository.existsByEmployeeIdAndDates(
                leaveRequestDto.getEmployeeId(),
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate())) {
            throw new ConflictException("A leave request already exists for this date range.");
        }

        // 4. Create and save the new leave request
        Leave newLeave = new Leave();
        newLeave.setEmployeeId(leaveRequestDto.getEmployeeId());
        newLeave.setStartDate(leaveRequestDto.getStartDate());
        newLeave.setEndDate(leaveRequestDto.getEndDate());
        newLeave.setLeaveType(leaveRequestDto.getLeaveType());
        newLeave.setStatus(LeaveStatus.PENDING); // Set initial status to PENDING

        return leaveRepository.save(newLeave);
    }

    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    public Leave getLeaveById(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Leave request not found with ID: " + id));
    }

    public Leave updateLeaveRequest(Long id, LeaveRequestDto leaveRequestDto, String authorizationHeader) {
        Leave existingLeave = getLeaveById(id);

        // 1. Validate employee existence via employee-service
        verifyEmployeeExists(leaveRequestDto.getEmployeeId(), authorizationHeader);

        // 2. Validate dates
        if (leaveRequestDto.getEndDate().isBefore(leaveRequestDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before the start date.");
        }

        // 3. Check for existing leave requests that would cause a conflict, excluding the current one being updated
        boolean hasConflict = leaveRepository.findAll().stream()
                .filter(l -> !l.getId().equals(id)) // Exclude the current entity
                .anyMatch(l -> l.getEmployeeId().equals(leaveRequestDto.getEmployeeId()) &&
                        l.getStartDate().isBefore(leaveRequestDto.getEndDate().plusDays(1)) &&
                        l.getEndDate().isAfter(leaveRequestDto.getStartDate().minusDays(1)));

        if (hasConflict) {
            throw new ConflictException("An employee leave request already exists for this date range.");
        }

        // 4. Update the existing leave request
        existingLeave.setEmployeeId(leaveRequestDto.getEmployeeId());
        existingLeave.setStartDate(leaveRequestDto.getStartDate());
        existingLeave.setEndDate(leaveRequestDto.getEndDate());
        existingLeave.setLeaveType(leaveRequestDto.getLeaveType());
        if (leaveRequestDto.getStatus() != null) {
            existingLeave.setStatus(leaveRequestDto.getStatus());
        }

        return leaveRepository.save(existingLeave);
    }

    public Leave updateLeaveStatus(Long id, LeaveStatus newStatus) {
        Leave existingLeave = getLeaveById(id);

        if (existingLeave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only PENDING leave requests can be updated.");
        }

        existingLeave.setStatus(newStatus);
        return leaveRepository.save(existingLeave);
    }

    public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new NotFoundException("Leave request not found with ID: " + id);
        }
        leaveRepository.deleteById(id);
    }

    private void verifyEmployeeExists(Long employeeId, String authorizationHeader) {
        String uri = employeeServiceUri + employeeId;

        HttpStatusCode status = Objects.requireNonNull(webClientBuilder.build().get()
                        .uri(uri)
                        .header("Authorization", authorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new NotFoundException("Employee not found.")))
                        .toBodilessEntity()
                        .block())
                .getStatusCode();
    }
}
