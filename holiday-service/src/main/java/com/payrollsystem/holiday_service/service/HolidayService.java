package com.payrollsystem.holiday_service.service;

import com.payrollsystem.holiday_service.dto.HolidayRequestDto;
import com.payrollsystem.holiday_service.exception.ConflictException;
import com.payrollsystem.holiday_service.exception.NotFoundException;
import com.payrollsystem.holiday_service.model.Holiday;
import com.payrollsystem.holiday_service.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
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

        // Check for conflicts with other holidays, but not with the one being updated
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
