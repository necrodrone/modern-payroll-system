package com.payrollsystem.tax_service.controller;

import com.payrollsystem.tax_service.model.TaxBracket;
import com.payrollsystem.tax_service.service.TaxBracketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/tax-brackets")
public class TaxBracketController {

    private final TaxBracketService taxBracketService;

    @Autowired
    public TaxBracketController(TaxBracketService taxBracketService) {
        this.taxBracketService = taxBracketService;
    }

    @PostMapping
    public ResponseEntity<TaxBracket> createTaxBracket(@Valid @RequestBody TaxBracket taxBracket) {
        TaxBracket newBracket = taxBracketService.createTaxBracket(taxBracket);
        return new ResponseEntity<>(newBracket, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaxBracket>> getAllTaxBrackets() {
        List<TaxBracket> taxBrackets = taxBracketService.getAllTaxBrackets();
        return new ResponseEntity<>(taxBrackets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxBracket> getTaxBracketById(@PathVariable Long id) {
        TaxBracket taxBracket = taxBracketService.getTaxBracketById(id);
        return new ResponseEntity<>(taxBracket, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxBracket> updateTaxBracket(@PathVariable Long id, @Valid @RequestBody TaxBracket taxBracket) {
        TaxBracket updatedBracket = taxBracketService.updateTaxBracket(id, taxBracket);
        return new ResponseEntity<>(updatedBracket, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaxBracket(@PathVariable Long id) {
        taxBracketService.deleteTaxBracket(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
