package com.payrollsystem.holiday_service.repository;

import com.payrollsystem.holiday_service.model.Suspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SuspensionRepository extends JpaRepository<Suspension, Long> {

    @Query("SELECT s FROM Suspension s WHERE " +
            "s.startDate <= :endDate AND s.endDate >= :startDate")
    Optional<Suspension> findByDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
