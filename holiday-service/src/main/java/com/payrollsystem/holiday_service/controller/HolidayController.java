package com.payrollsystem.holiday_service.controller;

import com.payrollsystem.holiday_service.dto.HolidayRequestDto;
import com.payrollsystem.holiday_service.model.Holiday;
import com.payrollsystem.holiday_service.service.HolidayService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @PostMapping
    public ResponseEntity<Holiday> createHoliday(@Valid @RequestBody HolidayRequestDto holidayDto) {
        Holiday newHoliday = holidayService.createHoliday(holidayDto);
        return new ResponseEntity<>(newHoliday, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        List<Holiday> holidays = holidayService.getAllHolidays();
        return new ResponseEntity<>(holidays, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable Long id) {
        Holiday holiday = holidayService.getHolidayById(id);
        return new ResponseEntity<>(holiday, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable Long id, @Valid @RequestBody HolidayRequestDto holidayDto) {
        Holiday updatedHoliday = holidayService.updateHoliday(id, holidayDto);
        return new ResponseEntity<>(updatedHoliday, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
