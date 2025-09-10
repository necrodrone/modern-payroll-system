package com.payrollsystem.payroll_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.payrollsystem.payroll_service.dto.PayrollRequestDto;
import com.payrollsystem.payroll_service.exception.BadRequestException;
import com.payrollsystem.payroll_service.exception.NotFoundException;
import com.payrollsystem.payroll_service.model.Payroll;
import com.payrollsystem.payroll_service.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class PayrollService {

    private final WebClient webClient;
    private final PayrollRepository payrollRepository;
    private final String employeeServiceUri;
    private final String attendanceServiceUri;
    private final String holidayServiceUri;
    private final String taxServiceUri;
    private final String deductionsServiceUri;
    private final String leaveServiceUri; // New service URI for leaves

    // Static nested classes for DTOs
    private record EmployeeDetailsDto(
            @JsonProperty("id") Long employeeId,
            BigDecimal hourlyRate,
            BigDecimal dailyRate
    ) {}

    private record TaxBracketDto(
            @JsonProperty("amountFrom") BigDecimal minIncome,
            @JsonProperty("amountTo") BigDecimal maxIncome,
            @JsonProperty("taxPercentage") BigDecimal rate,
            @JsonProperty("flatDeduction") BigDecimal taxOnPreviousBracket
    ) {}

    private record DeductionDto(
            String frequency,
            BigDecimal amount,
            LocalDate startDate,
            LocalDate endDate
    ) {}

    private record HolidayDto(
            String date
    ) {}

    private record LeaveDto(
            Long id,
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate,
            String leaveType,
            String status
    ) {}

    @Autowired
    public PayrollService(WebClient.Builder webClientBuilder,
                          PayrollRepository payrollRepository,
                          @Value("${service.employee.uri}") String employeeServiceUri,
                          @Value("${service.deduction.uri}") String deductionsServiceUri,
                          @Value("${service.attendance.uri}") String attendanceServiceUri,
                          @Value("${service.holiday.uri}") String holidayServiceUri,
                          @Value("${service.tax.uri}") String taxServiceUri,
                          @Value("${service.leave.uri}") String leaveServiceUri) {
        this.webClient = webClientBuilder.build();
        this.payrollRepository = payrollRepository;
        this.employeeServiceUri = employeeServiceUri;
        this.deductionsServiceUri = deductionsServiceUri;
        this.attendanceServiceUri = attendanceServiceUri;
        this.holidayServiceUri = holidayServiceUri;
        this.taxServiceUri = taxServiceUri;
        this.leaveServiceUri = leaveServiceUri;
    }

    public Mono<Payroll> calculatePayroll(PayrollRequestDto payrollRequestDto, String authorizationHeader) {
        if (payrollRequestDto.getPayPeriodEndDate().isBefore(payrollRequestDto.getPayPeriodStartDate())) {
            return Mono.error(new BadRequestException("Pay period end date cannot be before start date."));
        }

        Mono<EmployeeDetailsDto> employeeDetailsMono = getEmployeeDetails(payrollRequestDto.getEmployeeId(), authorizationHeader);
        Mono<Integer> hoursWorkedMono = getTotalHoursWorked(
                payrollRequestDto.getEmployeeId(),
                payrollRequestDto.getPayPeriodStartDate(),
                payrollRequestDto.getPayPeriodEndDate(),
                authorizationHeader
        );

        return Mono.zip(employeeDetailsMono, hoursWorkedMono)
                .flatMap(tuple -> {
                    EmployeeDetailsDto employeeDetails = tuple.getT1();
                    Integer totalHoursWorked = tuple.getT2();

                    Mono<List<DeductionDto>> dynamicDeductionsMono = getDynamicDeductions(employeeDetails.employeeId(), authorizationHeader);
                    Mono<Long> nonWorkingDaysMono = getNonWorkingDays(payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate());
                    Mono<List<TaxBracketDto>> taxBracketsMono = getTaxBrackets(authorizationHeader);
                    Mono<List<LeaveDto>> leavesMono = getLeaves(employeeDetails.employeeId(), payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate(), authorizationHeader);

                    return Mono.zip(dynamicDeductionsMono, nonWorkingDaysMono, taxBracketsMono, leavesMono)
                            .map(innerTuple -> {
                                List<DeductionDto> deductions = innerTuple.getT1();
                                long nonWorkingDays = innerTuple.getT2();
                                List<TaxBracketDto> taxBrackets = innerTuple.getT3();
                                List<LeaveDto> leaves = innerTuple.getT4();

                                BigDecimal grossPay = calculateGrossPay(
                                        employeeDetails,
                                        totalHoursWorked,
                                        nonWorkingDays,
                                        payrollRequestDto.getPayPeriodStartDate(),
                                        payrollRequestDto.getPayPeriodEndDate(),
                                        leaves
                                );
                                BigDecimal dynamicDeductions = calculateDynamicDeductions(deductions, payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate());
                                BigDecimal totalTaxes = calculateTaxes(grossPay, payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate(), taxBrackets);
                                BigDecimal totalDeductions = dynamicDeductions.add(totalTaxes);
                                BigDecimal netPay = grossPay.subtract(totalDeductions);

                                Payroll payroll = new Payroll();
                                payroll.setEmployeeId(payrollRequestDto.getEmployeeId());
                                payroll.setPayPeriodStartDate(payrollRequestDto.getPayPeriodStartDate());
                                payroll.setPayPeriodEndDate(payrollRequestDto.getPayPeriodEndDate());
                                payroll.setGrossPay(grossPay.setScale(2, RoundingMode.HALF_UP));
                                payroll.setNetPay(netPay.setScale(2, RoundingMode.HALF_UP));
                                payroll.setTotalTaxes(totalTaxes.setScale(2, RoundingMode.HALF_UP));
                                payroll.setTotalDeductions(totalDeductions.setScale(2, RoundingMode.HALF_UP));
                                payroll.setTotalHoursWorked(totalHoursWorked);

                                return payroll;
                            });
                })
                .flatMap(payrollRepository::save)
                .switchIfEmpty(Mono.error(new IllegalStateException("Failed to create payroll record.")));
    }

    private BigDecimal calculateGrossPay(EmployeeDetailsDto employeeDetails, Integer totalHoursWorked, long nonWorkingDays, LocalDate startDate, LocalDate endDate, List<LeaveDto> leaves) {
        long paidLeaveDays = leaves.stream()
                .filter(leave -> "PAID".equalsIgnoreCase(leave.status()))
                .filter(leave -> !leave.startDate().isAfter(endDate) && !leave.endDate().isBefore(startDate))
                .mapToLong(leave -> {
                    LocalDate effectiveStartDate = leave.startDate().isBefore(startDate) ? startDate : leave.startDate();
                    LocalDate effectiveEndDate = leave.endDate().isAfter(endDate) ? endDate : leave.endDate();
                    return ChronoUnit.DAYS.between(effectiveStartDate, effectiveEndDate) + 1;
                })
                .sum();

        if (employeeDetails.hourlyRate() != null) {
            // Assume 8 hours per paid leave day for hourly employees
            BigDecimal totalPaidLeaveHours = BigDecimal.valueOf(paidLeaveDays * 8);
            BigDecimal totalHours = BigDecimal.valueOf(totalHoursWorked).add(totalPaidLeaveHours);
            return employeeDetails.hourlyRate().multiply(totalHours);
        } else if (employeeDetails.dailyRate() != null) {
            long totalDaysInPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            long workingDaysInPeriod = totalDaysInPeriod - nonWorkingDays;
            // Paid leave days count as working days for daily rate employees
            long grossPayDays = workingDaysInPeriod;
            return employeeDetails.dailyRate().multiply(BigDecimal.valueOf(grossPayDays));
        } else {
            throw new BadRequestException("Employee must have either an hourly or daily rate set.");
        }
    }

    private BigDecimal calculateTaxes(BigDecimal grossPay, LocalDate startDate, LocalDate endDate, List<TaxBracketDto> taxBrackets) {
        long daysInPayPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal periodsInYear = new BigDecimal("365").divide(BigDecimal.valueOf(daysInPayPeriod), 2, RoundingMode.HALF_UP);
        BigDecimal annualGrossPay = grossPay.multiply(periodsInYear);

        BigDecimal annualTax = BigDecimal.ZERO;

        // Sort safely, treating null minIncome as 0
        taxBrackets.sort(Comparator.comparing(b -> b.minIncome() == null ? BigDecimal.ZERO : b.minIncome()));

        for (TaxBracketDto bracket : taxBrackets) {
            BigDecimal min = bracket.minIncome() == null ? BigDecimal.ZERO : bracket.minIncome();
            BigDecimal max = bracket.maxIncome() == null ? new BigDecimal("999999999") : bracket.maxIncome();
            BigDecimal rate = bracket.rate() == null ? BigDecimal.ZERO : bracket.rate();
            BigDecimal baseTax = bracket.taxOnPreviousBracket() == null ? BigDecimal.ZERO : bracket.taxOnPreviousBracket();

            if (annualGrossPay.compareTo(min) > 0) {
                if (annualGrossPay.compareTo(max) <= 0) {
                    BigDecimal taxableAmountInBracket = annualGrossPay.subtract(min);
                    annualTax = baseTax.add(taxableAmountInBracket.multiply(rate));
                    break;
                } else {
                    annualTax = baseTax.add(max.subtract(min).multiply(rate));
                }
            } else {
                break;
            }
        }

        BigDecimal payPeriodTax = annualTax.divide(periodsInYear, 2, RoundingMode.HALF_UP);
        return payPeriodTax.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDynamicDeductions(List<DeductionDto> deductions, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalDynamicDeductions = BigDecimal.ZERO;
        if (deductions != null) {
            LocalDate midpoint = startDate.withDayOfMonth(15);
            boolean isFirstHalf = endDate.isBefore(midpoint) || endDate.isEqual(midpoint);

            for (DeductionDto deduction : deductions) {
                boolean isActive = !( (deduction.endDate() != null && startDate.isAfter(deduction.endDate()))
                        || endDate.isBefore(deduction.startDate()) );

                if (isActive) {
                    switch (deduction.frequency()) {
                        case "FIRST_HALF" -> {
                            if (isFirstHalf) {
                                totalDynamicDeductions = totalDynamicDeductions.add(deduction.amount());
                            }
                        }
                        case "SECOND_HALF" -> {
                            if (!isFirstHalf) {
                                totalDynamicDeductions = totalDynamicDeductions.add(deduction.amount());
                            }
                        }
                        case "BI_MONTHLY" ->
                                totalDynamicDeductions = totalDynamicDeductions.add(
                                        deduction.amount().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP)
                                );
                    }
                }
            }
        }
        return totalDynamicDeductions;
    }

    private static Mono<? extends Throwable> handleServiceError(ClientResponse response, String serviceName) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new RuntimeException(serviceName + " service error: " + errorBody)));
    }

    private Mono<EmployeeDetailsDto> getEmployeeDetails(Long employeeId, String authorizationHeader) {
        String uri = employeeServiceUri + employeeId;
        return webClient.get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.error(new NotFoundException("Employee not found.")))
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Employee"))
                .bodyToMono(EmployeeDetailsDto.class);
    }

    private Mono<Integer> getTotalHoursWorked(Long employeeId, LocalDate startDate, LocalDate endDate, String authorizationHeader) {
        String uri = String.format("%s/hours-worked?employeeId=%d&startDate=%s&endDate=%s", attendanceServiceUri, employeeId, startDate, endDate);
        return webClient.get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Attendance"))
                .bodyToMono(Integer.class);
    }

    private Mono<List<TaxBracketDto>> getTaxBrackets(String authorizationHeader) {
        String uri = taxServiceUri + "/tax-brackets";
        return webClient.get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Tax"))
                .bodyToFlux(TaxBracketDto.class)
                .collectList();
    }

    private Mono<List<DeductionDto>> getDynamicDeductions(Long employeeId, String authorizationHeader) {
        String uri = deductionsServiceUri + "employee/" + employeeId;
        return webClient.get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Employee deduction"))
                .bodyToFlux(DeductionDto.class)
                .collectList();
    }

    private Mono<List<LeaveDto>> getLeaves(Long employeeId, LocalDate startDate, LocalDate endDate, String authorizationHeader) {
        String uri = String.format("%s/employee/%d/approved?startDate=%s&endDate=%s", leaveServiceUri, employeeId, startDate, endDate);
        return webClient.get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Leave"))
                .bodyToFlux(LeaveDto.class)
                .collectList();
    }

    private Mono<Long> getNonWorkingDays(LocalDate startDate, LocalDate endDate) {
        String uri = String.format("%s/non-working-days?startDate=%s&endDate=%s", holidayServiceUri, startDate, endDate);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Holiday"))
                .bodyToFlux(HolidayDto.class)
                .count();
    }

    public Flux<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    public Mono<Payroll> getPayrollById(Long id) {
        return payrollRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Payroll record not found with ID: " + id)));
    }

    public Flux<Payroll> getPayrollsByEmployeeId(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }
}
