package com.payrollsystem.tax_service.repository;

import com.payrollsystem.tax_service.model.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

@Repository
public interface TaxBracketRepository extends JpaRepository<TaxBracket, Long> {

    /**
     * Checks for any existing tax bracket that overlaps with the given income range.
     * @param amountFrom The start of the new income range.
     * @param amountTo The end of the new income range.
     * @return true if an overlap exists, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(tb) > 0 THEN TRUE ELSE FALSE END FROM TaxBracket tb " +
            "WHERE (tb.amountTo IS NULL AND :amountFrom >= tb.amountFrom) " +
            "OR (tb.amountTo IS NOT NULL AND :amountFrom < tb.amountTo AND :amountTo > tb.amountFrom)")
    boolean existsByAmountRange(@Param("amountFrom") BigDecimal amountFrom, @Param("amountTo") BigDecimal amountTo);

    /**
     * Checks for an existing tax bracket that overlaps with the given income range, excluding the one with the given ID.
     * @param amountFrom The start of the new income range.
     * @param amountTo The end of the new income range.
     * @param id The ID of the tax bracket to exclude from the check.
     * @return true if an overlap exists, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(tb) > 0 THEN TRUE ELSE FALSE END FROM TaxBracket tb " +
            "WHERE tb.id != :id " +
            "AND ((tb.amountTo IS NULL AND :amountFrom >= tb.amountFrom) " +
            "OR (tb.amountTo IS NOT NULL AND :amountFrom < tb.amountTo AND :amountTo > tb.amountFrom))")
    boolean existsByAmountRangeExcludeId(@Param("amountFrom") BigDecimal amountFrom, @Param("amountTo") BigDecimal amountTo, @Param("id") Long id);

    /**
     * Finds the tax bracket that applies to a given income amount.
     * @param annualIncome The annual income to check.
     * @return The corresponding TaxBracket object, or an empty Optional if no bracket applies.
     */
    @Query("SELECT tb FROM TaxBracket tb " +
            "WHERE :annualIncome BETWEEN tb.amountFrom AND tb.amountTo OR (tb.amountFrom <= :annualIncome AND tb.amountTo IS NULL)")
    Optional<TaxBracket> findByAnnualIncome(@Param("annualIncome") BigDecimal annualIncome);
}
