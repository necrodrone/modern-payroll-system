package com.payrollsystem.holiday_service.service;

import com.payrollsystem.holiday_service.dto.SuspensionRequestDto;
import com.payrollsystem.holiday_service.exception.BadRequestException;
import com.payrollsystem.holiday_service.exception.ConflictException;
import com.payrollsystem.holiday_service.exception.NotFoundException;
import com.payrollsystem.holiday_service.model.Suspension;
import com.payrollsystem.holiday_service.repository.SuspensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuspensionService {

    private final SuspensionRepository suspensionRepository;

    @Autowired
    public SuspensionService(SuspensionRepository suspensionRepository) {
        this.suspensionRepository = suspensionRepository;
    }

    public Suspension createSuspension(SuspensionRequestDto suspensionDto) {
        if (suspensionDto.getEndDate().isBefore(suspensionDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before the start date.");
        }

        if (suspensionRepository.findByDates(suspensionDto.getStartDate(), suspensionDto.getEndDate()).isPresent()) {
            throw new ConflictException("A suspension already exists for this date range.");
        }

        Suspension newSuspension = new Suspension();
        newSuspension.setName(suspensionDto.getName());
        newSuspension.setStartDate(suspensionDto.getStartDate());
        newSuspension.setEndDate(suspensionDto.getEndDate());
        newSuspension.setReason(suspensionDto.getReason());

        return suspensionRepository.save(newSuspension);
    }

    public List<Suspension> getAllSuspensions() {
        return suspensionRepository.findAll();
    }

    public Suspension getSuspensionById(Long id) {
        return suspensionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Suspension not found with ID: " + id));
    }

    public Suspension updateSuspension(Long id, SuspensionRequestDto suspensionDto) {
        Suspension existingSuspension = getSuspensionById(id);

        if (suspensionDto.getEndDate().isBefore(suspensionDto.getStartDate())) {
            throw new BadRequestException("End date cannot be before the start date.");
        }

        Optional<Suspension> conflict = suspensionRepository.findByDates(suspensionDto.getStartDate(), suspensionDto.getEndDate());
        if (conflict.isPresent() && !conflict.get().getId().equals(id)) {
            throw new ConflictException("A suspension already exists for this date range.");
        }

        existingSuspension.setName(suspensionDto.getName());
        existingSuspension.setStartDate(suspensionDto.getStartDate());
        existingSuspension.setEndDate(suspensionDto.getEndDate());
        existingSuspension.setReason(suspensionDto.getReason());

        return suspensionRepository.save(existingSuspension);
    }

    public void deleteSuspension(Long id) {
        if (!suspensionRepository.existsById(id)) {
            throw new NotFoundException("Suspension not found with ID: " + id);
        }
        suspensionRepository.deleteById(id);
    }
}
