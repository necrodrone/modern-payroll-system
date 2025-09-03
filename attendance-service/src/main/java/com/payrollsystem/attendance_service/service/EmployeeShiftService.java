package com.payrollsystem.attendance_service.service;

import com.payrollsystem.attendance_service.dto.EmployeeShiftAssignmentDto;
import com.payrollsystem.attendance_service.exception.BadRequestException;
import com.payrollsystem.attendance_service.exception.ConflictException;
import com.payrollsystem.attendance_service.exception.NotFoundException;
import com.payrollsystem.attendance_service.model.EmployeeShift;
import com.payrollsystem.attendance_service.repository.EmployeeShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeShiftService {

    private final WebClient.Builder webClientBuilder;
    private final EmployeeShiftRepository employeeShiftRepository;
    private final ShiftService shiftService;
    private final String employeeServiceUri;

    @Autowired
    public EmployeeShiftService(WebClient.Builder webClientBuilder,
                                EmployeeShiftRepository employeeShiftRepository,
                                ShiftService shiftService,
                                @Value("${service.employee.uri}") String employeeServiceUri) {
        this.webClientBuilder = webClientBuilder;
        this.employeeShiftRepository = employeeShiftRepository;
        this.shiftService = shiftService;
        this.employeeServiceUri = employeeServiceUri;
    }

    public EmployeeShift assignShiftToEmployee(EmployeeShiftAssignmentDto assignmentDto, String authorizationHeader) {
        verifyEmployeeExists(assignmentDto.getEmployeeId(), authorizationHeader);
        shiftService.getShiftById(assignmentDto.getShiftId());

        if (assignmentDto.getEndDate() == null) {
            if (employeeShiftRepository.existsByEmployeeIdAndEndDateIsNull(assignmentDto.getEmployeeId())) {
                throw new ConflictException("A permanent shift assignment already exists for this employee.");
            }
        } else {
            if (employeeShiftRepository.existsByEmployeeIdAndDates(
                    assignmentDto.getEmployeeId(),
                    assignmentDto.getStartDate(),
                    assignmentDto.getEndDate())) {
                throw new ConflictException("An employee shift already exists for this date range.");
            }
        }

        EmployeeShift newAssignment = new EmployeeShift();
        newAssignment.setEmployeeId(assignmentDto.getEmployeeId());
        newAssignment.setShiftId(assignmentDto.getShiftId());
        newAssignment.setStartDate(assignmentDto.getStartDate());
        newAssignment.setEndDate(assignmentDto.getEndDate());

        return employeeShiftRepository.save(newAssignment);
    }

    public List<EmployeeShift> getAllEmployeeShifts() {
        return employeeShiftRepository.findAll();
    }

    public EmployeeShift getEmployeeShiftById(Long id) {
        return employeeShiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee shift not found with ID: " + id));
    }

    public EmployeeShift updateEmployeeShift(Long id, EmployeeShiftAssignmentDto assignmentDto, String authorizationHeader) {
        EmployeeShift existingAssignment = getEmployeeShiftById(id);

        verifyEmployeeExists(assignmentDto.getEmployeeId(), authorizationHeader);
        shiftService.getShiftById(assignmentDto.getShiftId());

        if (assignmentDto.getEndDate() == null) {
            Optional<EmployeeShift> existingPermanent = employeeShiftRepository.findByEmployeeIdAndEndDateIsNull(assignmentDto.getEmployeeId());
            if (existingPermanent.isPresent() && !existingPermanent.get().getId().equals(id)) {
                throw new ConflictException("A permanent shift assignment already exists for this employee.");
            }
        } else {
            Optional<EmployeeShift> conflict = employeeShiftRepository.findByEmployeeIdAndDates(
                    assignmentDto.getEmployeeId(),
                    assignmentDto.getStartDate(),
                    assignmentDto.getEndDate());
            if (conflict.isPresent() && !conflict.get().getId().equals(id)) {
                throw new ConflictException("An employee shift already exists for this date range.");
            }
        }

        existingAssignment.setEmployeeId(assignmentDto.getEmployeeId());
        existingAssignment.setShiftId(assignmentDto.getShiftId());
        existingAssignment.setStartDate(assignmentDto.getStartDate());
        existingAssignment.setEndDate(assignmentDto.getEndDate());

        return employeeShiftRepository.save(existingAssignment);
    }

    public void deleteEmployeeShift(Long id) {
        if (!employeeShiftRepository.existsById(id)) {
            throw new NotFoundException("Employee shift not found with ID: " + id);
        }
        employeeShiftRepository.deleteById(id);
    }

    private void verifyEmployeeExists(Long employeeId, String authorizationHeader) {
        String uri = employeeServiceUri + employeeId;
        HttpStatusCode status = webClientBuilder.build().get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new NotFoundException("Employee not found.")))
                .toBodilessEntity()
                .block()
                .getStatusCode();
        if (!status.is2xxSuccessful()) {
            throw new BadRequestException("Failed to verify employee.");
        }
    }
}
