package com.payrollsystem.payroll_service.controller;

import com.payrollsystem.payroll_service.dto.PayrollRequestDto;
import com.payrollsystem.payroll_service.exception.NotFoundException;
import com.payrollsystem.payroll_service.model.Payroll;
import com.payrollsystem.payroll_service.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    @Autowired
    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /**
     * Calculates and persists payroll for a given employee and pay period.
     * @param payrollRequestDto The DTO containing the payroll calculation request.
     * @param authorizationHeader The JWT authorization token.
     * @return A Mono<ResponseEntity<Payroll>> indicating the asynchronous result of the operation.
     */
    @PostMapping("/calculate")
    public Mono<ResponseEntity<Payroll>> calculatePayroll(@RequestBody PayrollRequestDto payrollRequestDto,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return payrollService.calculatePayroll(payrollRequestDto, authorizationHeader)
                .map(ResponseEntity::ok)
                .onErrorResume(NotFoundException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Retrieves a single payroll record by its ID in a non-blocking way.
     * @param id The ID of the payroll record.
     * @return A Mono<ResponseEntity<Payroll>> representing the asynchronous result.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Payroll>> getPayrollById(@PathVariable Long id) {
        return payrollService.getPayrollById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all payroll records in a non-blocking way.
     * @return A Flux<Payroll> that will stream payroll records as they are retrieved.
     */
    @GetMapping
    public Flux<Payroll> getAllPayrolls() {
        return payrollService.getAllPayrolls();
    }

    /**
     * Retrieves all payroll records for a specific employee in a non-blocking way.
     * @param employeeId The ID of the employee.
     * @return A Flux<Payroll> that will stream payroll records as they are retrieved.
     */
    @GetMapping("/employee/{employeeId}")
    public Flux<Payroll> getPayrollsByEmployeeId(@PathVariable Long employeeId) {
        return payrollService.getPayrollsByEmployeeId(employeeId);
    }
}
