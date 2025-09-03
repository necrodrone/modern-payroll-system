package com.payrollsystem.attendance_service.repository;

import com.payrollsystem.attendance_service.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    /**
     * Checks if a shift with the given name already exists in the database.
     * @param name The name of the shift to check.
     * @return true if a shift with the given name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Finds a shift by its name.
     * @param name The name of the shift to find.
     * @return An Optional containing the found shift, or an empty Optional if not found.
     */
    Optional<Shift> findByName(String name);
}
