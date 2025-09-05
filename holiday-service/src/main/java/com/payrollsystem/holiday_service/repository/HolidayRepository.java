package com.payrollsystem.holiday_service.repository;

import com.payrollsystem.holiday_service.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Optional<Holiday> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);
}
