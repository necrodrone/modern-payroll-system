package com.payrollsystem.tax_service.service;

import com.payrollsystem.tax_service.exception.BadRequestException;
import com.payrollsystem.tax_service.exception.ConflictException;
import com.payrollsystem.tax_service.exception.NotFoundException;
import com.payrollsystem.tax_service.model.TaxBracket;
import com.payrollsystem.tax_service.repository.TaxBracketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TaxBracketService {

    private final TaxBracketRepository taxBracketRepository;

    @Autowired
    public TaxBracketService(TaxBracketRepository taxBracketRepository) {
        this.taxBracketRepository = taxBracketRepository;
    }

    public TaxBracket createTaxBracket(TaxBracket taxBracket) {
        // Validate dates
        if (taxBracket.getAmountTo() != null && taxBracket.getAmountTo().compareTo(taxBracket.getAmountFrom()) <= 0) {
            throw new BadRequestException("Amount to must be greater than amount from.");
        }

        // Check for any overlapping brackets
        if (taxBracketRepository.existsByAmountRange(taxBracket.getAmountFrom(), taxBracket.getAmountTo())) {
            throw new ConflictException("The provided tax bracket range overlaps with an existing one.");
        }
        return taxBracketRepository.save(taxBracket);
    }

    public List<TaxBracket> getAllTaxBrackets() {
        return taxBracketRepository.findAll();
    }

    public TaxBracket getTaxBracketById(Long id) {
        return taxBracketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tax bracket not found with ID: " + id));
    }

    public TaxBracket updateTaxBracket(Long id, TaxBracket updatedTaxBracket) {
        TaxBracket existingBracket = getTaxBracketById(id);

        if (updatedTaxBracket.getAmountTo() != null && updatedTaxBracket.getAmountTo().compareTo(updatedTaxBracket.getAmountFrom()) <= 0) {
            throw new BadRequestException("Amount to must be greater than amount from.");
        }

        if (taxBracketRepository.existsByAmountRangeExcludeId(updatedTaxBracket.getAmountFrom(), updatedTaxBracket.getAmountTo(), id)) {
            throw new ConflictException("The updated tax bracket range overlaps with an existing one.");
        }

        existingBracket.setAmountFrom(updatedTaxBracket.getAmountFrom());
        existingBracket.setAmountTo(updatedTaxBracket.getAmountTo());
        existingBracket.setTaxPercentage(updatedTaxBracket.getTaxPercentage());
        existingBracket.setFlatDeduction(updatedTaxBracket.getFlatDeduction());

        return taxBracketRepository.save(existingBracket);
    }

    public void deleteTaxBracket(Long id) {
        if (!taxBracketRepository.existsById(id)) {
            throw new NotFoundException("Tax bracket not found with ID: " + id);
        }
        taxBracketRepository.deleteById(id);
    }
}
