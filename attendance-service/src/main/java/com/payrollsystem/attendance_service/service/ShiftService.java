package com.payrollsystem.attendance_service.service;

import com.payrollsystem.attendance_service.model.Shift;
import com.payrollsystem.attendance_service.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    private LocalTime calculateComputedShiftEndTime(LocalTime flexStartTime, Integer workHours, Integer breakMinutes) {
        if (flexStartTime == null || workHours == null) {
            return null;
        }
        LocalTime shiftEndTime = flexStartTime.plusHours(workHours);
        if (breakMinutes != null) {
            shiftEndTime = shiftEndTime.plusMinutes(breakMinutes);
        }
        return shiftEndTime;
    }

    // Create a new flexi-time shift
    public Shift createShift(Shift shift) {
        shift.setComputedShiftEndTime(calculateComputedShiftEndTime(
                shift.getFlexStartTime(),
                shift.getWorkHours(),
                shift.getBreakMinutes()
        ));
        return shiftRepository.save(shift);
    }

    // Get a shift by its ID
    public Optional<Shift> getShiftById(Long id) {
        return shiftRepository.findById(id);
    }

    // Get all shifts
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    // Update an existing flexi-time shift
    public Shift updateShift(Long id, Shift updatedShift) {
        return shiftRepository.findById(id)
                .map(shift -> {
                    shift.setName(updatedShift.getName());
                    shift.setFlexStartTime(updatedShift.getFlexStartTime());
                    shift.setFlexEndTime(updatedShift.getFlexEndTime());
                    shift.setWorkHours(updatedShift.getWorkHours());
                    shift.setBreakMinutes(updatedShift.getBreakMinutes());

                    shift.setComputedShiftEndTime(calculateComputedShiftEndTime(
                            shift.getFlexStartTime(),
                            shift.getWorkHours(),
                            shift.getBreakMinutes()
                    ));
                    return shiftRepository.save(shift);
                })
                .orElseGet(() -> {
                    updatedShift.setId(id);
                    updatedShift.setComputedShiftEndTime(calculateComputedShiftEndTime(
                            updatedShift.getFlexStartTime(),
                            updatedShift.getWorkHours(),
                            updatedShift.getBreakMinutes()
                    ));
                    return shiftRepository.save(updatedShift);
                });
    }

    // Delete a shift
    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }
}
