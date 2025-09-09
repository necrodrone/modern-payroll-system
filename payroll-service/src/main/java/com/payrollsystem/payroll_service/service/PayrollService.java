package com.payrollsystem.payroll_service.service;

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
import java.util.stream.Collectors;

@Service
public class PayrollService {

    private final WebClient webClient;
    private final PayrollRepository payrollRepository;
    private final String employeeServiceUri;
    private final String attendanceServiceUri;
    private final String holidayServiceUri;
    private final String taxServiceUri;

    // Static nested classes for DTOs to improve type safety
    private record EmployeeDetailsDto(
            Long employeeId,
            BigDecimal hourlyRate,
            BigDecimal dailyRate
    ) {}

    private record TaxBracketDto(
            BigDecimal minIncome,
            BigDecimal maxIncome,
            BigDecimal rate,
            BigDecimal taxOnPreviousBracket
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

    @Autowired
    public PayrollService(WebClient.Builder webClientBuilder,
                          PayrollRepository payrollRepository,
                          @Value("${service.employee.uri}") String employeeServiceUri,
                          @Value("${service.attendance.uri}") String attendanceServiceUri,
                          @Value("${service.holiday.uri}") String holidayServiceUri,
                          @Value("${service.tax.uri}") String taxServiceUri) {
        this.webClient = webClientBuilder.build();
        this.payrollRepository = payrollRepository;
        this.employeeServiceUri = employeeServiceUri;
        this.attendanceServiceUri = attendanceServiceUri;
        this.holidayServiceUri = holidayServiceUri;
        this.taxServiceUri = taxServiceUri;
    }

    /**
     * Calculates payroll in a reactive, non-blocking manner.
     * The method chains multiple non-blocking service calls to compute the final payroll.
     *
     * @param payrollRequestDto The DTO containing the employee and pay period information.
     * @param authorizationHeader The authorization header for securing service calls.
     * @return A Mono<Payroll> representing the asynchronous result of the payroll calculation.
     */
    public Mono<Payroll> calculatePayroll(PayrollRequestDto payrollRequestDto, String authorizationHeader) {
        // 1. Validate the pay period
        if (payrollRequestDto.getPayPeriodEndDate().isBefore(payrollRequestDto.getPayPeriodStartDate())) {
            return Mono.error(new BadRequestException("Pay period end date cannot be before start date."));
        }

        // Use Mono.zip to make concurrent calls for employee details and hours worked
        Mono<EmployeeDetailsDto> employeeDetailsMono = getEmployeeDetails(payrollRequestDto.getEmployeeId(), authorizationHeader);
        Mono<Integer> hoursWorkedMono = getTotalHoursWorked(
                payrollRequestDto.getEmployeeId(),
                payrollRequestDto.getPayPeriodStartDate(),
                payrollRequestDto.getPayPeriodEndDate(),
                authorizationHeader
        );

        // Chain the asynchronous calls using flatMap
        return Mono.zip(employeeDetailsMono, hoursWorkedMono)
                .flatMap(tuple -> {
                    EmployeeDetailsDto employeeDetails = tuple.getT1();
                    Integer totalHoursWorked = tuple.getT2();

                    // Now get non-working days and dynamic deductions, also concurrently
                    Mono<List<DeductionDto>> dynamicDeductionsMono = getDynamicDeductions(employeeDetails.employeeId(), authorizationHeader);
                    Mono<Long> nonWorkingDaysMono = getNonWorkingDays(payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate());
                    Mono<List<TaxBracketDto>> taxBracketsMono = getTaxBrackets(authorizationHeader);

                    return Mono.zip(dynamicDeductionsMono, nonWorkingDaysMono, taxBracketsMono)
                            .map(innerTuple -> {
                                List<DeductionDto> deductions = innerTuple.getT1();
                                long nonWorkingDays = innerTuple.getT2();
                                List<TaxBracketDto> taxBrackets = innerTuple.getT3();

                                // Perform all calculations once all data is available
                                BigDecimal grossPay = calculateGrossPay(employeeDetails, totalHoursWorked, nonWorkingDays,
                                        payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate());
                                BigDecimal dynamicDeductions = calculateDynamicDeductions(deductions, payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate());
                                BigDecimal totalTaxes = calculateTaxes(grossPay, payrollRequestDto.getPayPeriodStartDate(), payrollRequestDto.getPayPeriodEndDate(), taxBrackets);
                                BigDecimal totalDeductions = dynamicDeductions.add(totalTaxes);
                                BigDecimal netPay = grossPay.subtract(totalDeductions);

                                // Create the Payroll object
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

    // --- Private helper methods for calculations ---

    private BigDecimal calculateGrossPay(EmployeeDetailsDto employeeDetails, Integer totalHoursWorked, long nonWorkingDays, LocalDate startDate, LocalDate endDate) {
        if (employeeDetails.hourlyRate() != null) {
            return employeeDetails.hourlyRate().multiply(BigDecimal.valueOf(totalHoursWorked));
        } else if (employeeDetails.dailyRate() != null) {
            long totalDaysInPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            long workingDaysInPeriod = totalDaysInPeriod - nonWorkingDays;
            return employeeDetails.dailyRate().multiply(BigDecimal.valueOf(workingDaysInPeriod));
        } else {
            throw new BadRequestException("Employee must have either an hourly or daily rate set.");
        }
    }

    /**
     * Calculates tax based on dynamically fetched tax brackets.
     * @param grossPay The employee's gross pay for the period.
     * @param taxBrackets The list of tax bracket DTOs.
     * @return The total calculated tax.
     */
    private BigDecimal calculateTaxes(BigDecimal grossPay, LocalDate startDate, LocalDate endDate, List<TaxBracketDto> taxBrackets) {
        // Step 1: Project the pay period's gross pay to an annual equivalent
        long daysInPayPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal periodsInYear = new BigDecimal("365").divide(BigDecimal.valueOf(daysInPayPeriod), 2, RoundingMode.HALF_UP);
        BigDecimal annualGrossPay = grossPay.multiply(periodsInYear);

        // Step 2: Calculate the total annual tax
        BigDecimal annualTax = BigDecimal.ZERO;
        // Sort the brackets by minIncome to ensure correct processing
        taxBrackets.sort(Comparator.comparing(TaxBracketDto::minIncome));

        for (TaxBracketDto bracket : taxBrackets) {
            if (annualGrossPay.compareTo(bracket.minIncome()) > 0) {
                // If annualGrossPay is within the current bracket
                if (annualGrossPay.compareTo(bracket.maxIncome()) <= 0) {
                    BigDecimal taxableAmountInBracket = annualGrossPay.subtract(bracket.minIncome());
                    annualTax = bracket.taxOnPreviousBracket().add(taxableAmountInBracket.multiply(bracket.rate()));
                    break;
                } else {
                    // This is the last applicable bracket, add the full tax from it
                    annualTax = bracket.taxOnPreviousBracket().add(bracket.maxIncome().subtract(bracket.minIncome()).multiply(bracket.rate()));
                }
            } else {
                // Annual gross pay is less than the current bracket's minimum, so no more tax applies.
                break;
            }
        }

        // Step 3: Prorate the annual tax back to the current pay period
        BigDecimal payPeriodTax = annualTax.divide(periodsInYear, 2, RoundingMode.HALF_UP);
        return payPeriodTax.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDynamicDeductions(List<DeductionDto> deductions, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalDynamicDeductions = BigDecimal.ZERO;
        if (deductions != null) {
            LocalDate midpoint = startDate.withDayOfMonth(15);
            boolean isFirstHalf = endDate.isBefore(midpoint) || endDate.isEqual(midpoint);

            for (DeductionDto deduction : deductions) {
                boolean isActive = !(startDate.isAfter(deduction.endDate()) || endDate.isBefore(deduction.startDate()));
                if (isActive) {
                    switch (deduction.frequency()) {
                        case "FIRST_HALF":
                            if (isFirstHalf) {
                                totalDynamicDeductions = totalDynamicDeductions.add(deduction.amount());
                            }
                            break;
                        case "SECOND_HALF":
                            if (!isFirstHalf) {
                                totalDynamicDeductions = totalDynamicDeductions.add(deduction.amount());
                            }
                            break;
                        case "BI_MONTHLY":
                            totalDynamicDeductions = totalDynamicDeductions.add(deduction.amount().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
                            break;
                    }
                }
            }
        }
        return totalDynamicDeductions;
    }

    /**
     * Handles WebClient errors by retrieving the error body and wrapping it in a RuntimeException.
     *
     * @param response The ClientResponse with an error status.
     * @param serviceName The name of the service that returned the error.
     * @return A Mono that will emit a RuntimeException.
     */
    private static Mono<? extends Throwable> handleServiceError(ClientResponse response, String serviceName) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new RuntimeException(serviceName + " service error: " + errorBody)));
    }


    // --- Private helper methods for inter-service communication ---

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
        String uri = employeeServiceUri + employeeId + "/deductions";
        return webClient.get()
                .uri(uri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Employee deduction"))
                .bodyToFlux(DeductionDto.class)
                .collectList();
    }

    private Mono<Long> getNonWorkingDays(LocalDate startDate, LocalDate endDate) {
        String uri = String.format("%s/non-working-days?startDate=%s&endDate=%s", holidayServiceUri, startDate, endDate);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.isError(), response -> handleServiceError(response, "Holiday"))
                .bodyToFlux(HolidayDto.class)
                .collectList()
                .map(List::size)
                .cast(Long.class);
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
