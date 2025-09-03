package com.payrollsystem.attendance_service.service;

import com.payrollsystem.attendance_service.exception.ConflictException;
import com.payrollsystem.attendance_service.exception.NotFoundException;
import com.payrollsystem.attendance_service.model.Shift;
import com.payrollsystem.attendance_service.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    public Shift createShift(Shift shift) {
        if (shiftRepository.existsByName(shift.getName())) {
            throw new ConflictException("A shift with the name '" + shift.getName() + "' already exists.");
        }
        LocalTime computedEndTime = calculateComputedShiftEndTime(shift.getFlexStartTime(), shift.getWorkHours(), shift.getBreakMinutes());
        shift.setComputedShiftEndTime(computedEndTime);
        return shiftRepository.save(shift);
    }

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public Shift getShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shift not found with ID: " + id));
    }

    public Shift updateShift(Long id, Shift updatedShift) {
        return shiftRepository.findById(id)
                .map(existingShift -> {
                    // Check if the new name is already in use by another shift
                    if (!existingShift.getName().equals(updatedShift.getName()) && shiftRepository.existsByName(updatedShift.getName())) {
                        throw new ConflictException("A shift with the name '" + updatedShift.getName() + "' already exists.");
                    }
                    existingShift.setName(updatedShift.getName());
                    existingShift.setFlexStartTime(updatedShift.getFlexStartTime());
                    existingShift.setFlexEndTime(updatedShift.getFlexEndTime());
                    existingShift.setWorkHours(updatedShift.getWorkHours());
                    existingShift.setBreakMinutes(updatedShift.getBreakMinutes());
                    LocalTime computedEndTime = calculateComputedShiftEndTime(updatedShift.getFlexStartTime(), updatedShift.getWorkHours(), updatedShift.getBreakMinutes());
                    existingShift.setComputedShiftEndTime(computedEndTime);
                    return shiftRepository.save(existingShift);
                })
                .orElseThrow(() -> new NotFoundException("Shift not found with ID: " + id));
    }

    public void deleteShift(Long id) {
        if (!shiftRepository.existsById(id)) {
            throw new NotFoundException("Shift not found with ID: " + id);
        }
        shiftRepository.deleteById(id);
    }

    private LocalTime calculateComputedShiftEndTime(LocalTime startTime, Integer workHours, Integer breakMinutes) {
        return startTime.plus(workHours, ChronoUnit.HOURS).plus(breakMinutes, ChronoUnit.MINUTES);
    }
}
