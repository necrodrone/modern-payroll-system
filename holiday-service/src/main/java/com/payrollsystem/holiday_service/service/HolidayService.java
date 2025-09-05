package com.payrollsystem.holiday_service.service;

import com.payrollsystem.holiday_service.dto.HolidayRequestDto;
import com.payrollsystem.holiday_service.dto.NonWorkingDayDto;
import com.payrollsystem.holiday_service.exception.ConflictException;
import com.payrollsystem.holiday_service.exception.NotFoundException;
import com.payrollsystem.holiday_service.model.Holiday;
import com.payrollsystem.holiday_service.model.Suspension;
import com.payrollsystem.holiday_service.repository.HolidayRepository;
import com.payrollsystem.holiday_service.repository.SuspensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final SuspensionRepository suspensionRepository;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository, SuspensionRepository suspensionRepository) {
        this.holidayRepository = holidayRepository;
        this.suspensionRepository = suspensionRepository;
    }

    public List<NonWorkingDayDto> getNonWorkingDays(LocalDate startDate, LocalDate endDate) {
        List<NonWorkingDayDto> nonWorkingDays = new ArrayList<>();

        // 1. Get all holidays in the date range
        List<Holiday> holidays = holidayRepository.findAll().stream()
                .filter(h -> !h.getDate().isBefore(startDate) && !h.getDate().isAfter(endDate))
                .toList();

        for (Holiday holiday : holidays) {
            nonWorkingDays.add(new NonWorkingDayDto(holiday.getDate(), holiday.getName(), "HOLIDAY"));
        }

        // 2. Get all suspensions in the date range
        List<Suspension> suspensions = suspensionRepository.findAll().stream()
                .filter(s -> !s.getStartDate().isAfter(endDate) && !s.getEndDate().isBefore(startDate))
                .toList();

        for (Suspension suspension : suspensions) {
            LocalDate currentDate = suspension.getStartDate();
            while (!currentDate.isAfter(suspension.getEndDate())) {
                LocalDate finalDate = currentDate; // Create a new effectively final variable
                if (!nonWorkingDays.stream().anyMatch(d -> d.getDate().equals(finalDate))) {
                    nonWorkingDays.add(new NonWorkingDayDto(finalDate, suspension.getName(), "SUSPENSION"));
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        // 3. Add weekends (Saturdays and Sundays)
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalDate finalDate = currentDate; // Create a new effectively final variable
            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                if (!nonWorkingDays.stream().anyMatch(d -> d.getDate().equals(finalDate))) {
                    nonWorkingDays.add(new NonWorkingDayDto(finalDate, "Weekend", "WEEKEND"));
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return nonWorkingDays;
    }

    public Holiday createHoliday(HolidayRequestDto holidayDto) {
        // Check for existing holiday on the same date
        if (holidayRepository.existsByDate(holidayDto.getDate())) {
            throw new ConflictException("A holiday for this date already exists.");
        }
        Holiday newHoliday = new Holiday();
        newHoliday.setName(holidayDto.getName());
        newHoliday.setDate(holidayDto.getDate());
        newHoliday.setType(holidayDto.getType());

        return holidayRepository.save(newHoliday);
    }

    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    public Holiday getHolidayById(Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Holiday not found with ID: " + id));
    }

    public Holiday updateHoliday(Long id, HolidayRequestDto holidayDto) {
        Holiday existingHoliday = getHolidayById(id);

        Optional<Holiday> conflict = holidayRepository.findByDate(holidayDto.getDate());
        if (conflict.isPresent() && !conflict.get().getId().equals(id)) {
            throw new ConflictException("A holiday for this date already exists.");
        }
        existingHoliday.setName(holidayDto.getName());
        existingHoliday.setDate(holidayDto.getDate());
        existingHoliday.setType(holidayDto.getType());

        return holidayRepository.save(existingHoliday);
    }

    public void deleteHoliday(Long id) {
        if (!holidayRepository.existsById(id)) {
            throw new NotFoundException("Holiday not found with ID: " + id);
        }
        holidayRepository.deleteById(id);
    }
}
