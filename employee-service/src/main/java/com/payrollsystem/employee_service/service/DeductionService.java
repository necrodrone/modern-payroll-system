package com.payrollsystem.employee_service.service;

import com.payrollsystem.employee_service.dto.DeductionDto;
import com.payrollsystem.employee_service.dto.DeductionRequestDto;
import com.payrollsystem.employee_service.exception.BadRequestException;
import com.payrollsystem.employee_service.exception.NotFoundException;
import com.payrollsystem.employee_service.model.Deduction;
import com.payrollsystem.employee_service.repository.DeductionRepository;
import com.payrollsystem.employee_service.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeductionService {

    private final DeductionRepository deductionRepository;
    private final EmployeeRepository employeeRepository;

    public DeductionService(DeductionRepository deductionRepository, EmployeeRepository employeeRepository) {
        this.deductionRepository = deductionRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new deduction for an employee.
     * @param deductionRequestDto The DTO containing the deduction details.
     * @return The created deduction as a DTO.
     */
    public DeductionDto createDeduction(DeductionRequestDto deductionRequestDto) {
        // Validate if the employee exists before creating the deduction
        if (!employeeRepository.existsById(deductionRequestDto.getEmployeeId())) {
            throw new NotFoundException("Employee not found with ID: " + deductionRequestDto.getEmployeeId());
        }

        // Validate that the start date is not after the end date
        if (deductionRequestDto.getStartDate().isAfter(deductionRequestDto.getEndDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }

        // Convert DTO to entity
        Deduction deduction = new Deduction();
        deduction.setEmployeeId(deductionRequestDto.getEmployeeId());
        deduction.setName(deductionRequestDto.getName());
        deduction.setAmount(deductionRequestDto.getAmount());
        deduction.setStartDate(deductionRequestDto.getStartDate());
        deduction.setEndDate(deductionRequestDto.getEndDate());
        deduction.setFrequency(deductionRequestDto.getFrequency());

        Deduction savedDeduction = deductionRepository.save(deduction);
        return mapToDto(savedDeduction);
    }

    /**
     * Retrieves all deductions from the database.
     * @return A list of all deductions as DTOs.
     */
    public List<DeductionDto> getAllDeductions() {
        List<Deduction> deductions = deductionRepository.findAll();
        return deductions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Retrieves a single deduction by its ID.
     * @param deductionId The ID of the deduction to retrieve.
     * @return The found deduction as a DTO.
     */
    public DeductionDto getDeductionById(Long deductionId) {
        Deduction deduction = deductionRepository.findById(deductionId)
                .orElseThrow(() -> new NotFoundException("Deduction not found with ID: " + deductionId));
        return mapToDto(deduction);
    }

    /**
     * Retrieves all deductions for a specific employee.
     * @param employeeId The ID of the employee.
     * @return A list of deductions for the specified employee as DTOs.
     */
    public List<DeductionDto> getDeductionsByEmployeeId(Long employeeId) {
        List<Deduction> deductions = deductionRepository.findByEmployeeId(employeeId);
        return deductions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Updates an existing deduction.
     * @param deductionId The ID of the deduction to update.
     * @param updatedDeductionDto The DTO with the updated details.
     * @return The updated deduction as a DTO.
     */
    public DeductionDto updateDeduction(Long deductionId, DeductionRequestDto updatedDeductionDto) {
        Deduction existingDeduction = deductionRepository.findById(deductionId)
                .orElseThrow(() -> new NotFoundException("Deduction not found with ID: " + deductionId));

        // Validate that the start date is not after the end date
        if (updatedDeductionDto.getStartDate().isAfter(updatedDeductionDto.getEndDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }

        // Update fields from DTO
        existingDeduction.setName(updatedDeductionDto.getName());
        existingDeduction.setAmount(updatedDeductionDto.getAmount());
        existingDeduction.setStartDate(updatedDeductionDto.getStartDate());
        existingDeduction.setEndDate(updatedDeductionDto.getEndDate());
        existingDeduction.setFrequency(updatedDeductionDto.getFrequency());

        Deduction savedDeduction = deductionRepository.save(existingDeduction);
        return mapToDto(savedDeduction);
    }

    /**
     * Deletes a deduction by its ID.
     * @param deductionId The ID of the deduction to delete.
     */
    public void deleteDeduction(Long deductionId) {
        if (!deductionRepository.existsById(deductionId)) {
            throw new NotFoundException("Deduction not found with ID: " + deductionId);
        }
        deductionRepository.deleteById(deductionId);
    }

    private DeductionDto mapToDto(Deduction deduction) {
        return new DeductionDto(
                deduction.getId(),
                deduction.getEmployeeId(),
                deduction.getName(),
                deduction.getAmount(),
                deduction.getStartDate(),
                deduction.getEndDate(),
                deduction.getFrequency()
        );
    }
}
