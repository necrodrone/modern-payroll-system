package com.payrollsystem.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payrollsystem.notification_service.dto.EmployeeCreatedEvent; // We'll create this DTO
import com.payrollsystem.notification_service.service.EmailService;
import com.payrollsystem.notification_service.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeNotificationListener.class);
    private final EmailService emailService;
    private final SmsService smsService;
    private final ObjectMapper objectMapper; // To convert message to DTO

    @Autowired
    public EmployeeNotificationListener(EmailService emailService, SmsService smsService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.objectMapper = objectMapper;
    }

    /**
     * Listens for messages on the 'employee.created.queue' and processes them.
     * The message is expected to be a JSON string representing an EmployeeCreatedEvent.
     *
     * @param message The raw message string received from RabbitMQ.
     */
    @RabbitListener(queues = "${rabbitmq.queue.employee-created}")
    public void handleEmployeeCreatedEvent(String message) {
        logger.info("Received message from RabbitMQ: {}", message);
        try {
            // Convert JSON string message to EmployeeCreatedEvent DTO
            EmployeeCreatedEvent event = objectMapper.readValue(message, EmployeeCreatedEvent.class);
            logger.info("Processing employee created event for: {}", event.getEmployeeName());

            // Send Email
            String emailSubject = "Welcome to Modern Payroll System, " + event.getEmployeeName() + "!";
            String emailBody = "Dear " + event.getEmployeeName() + ",\n\n"
                    + "Welcome to the Modern Payroll System! Your account has been successfully created.\n"
                    + "Your employee ID is: " + event.getEmployeeId() + "\n"
                    + "We are excited to have you on board.\n\n"
                    + "Best regards,\n"
                    + "The Payroll Team";
            emailService.sendEmail(event.getEmployeeEmail(), emailSubject, emailBody);

            // Send SMS
            String smsMessage = "Welcome, " + event.getEmployeeName() + "! Your Modern Payroll account is active. Employee ID: " + event.getEmployeeId();
            smsService.sendSms(event.getEmployeePhoneNumber(), smsMessage);

            logger.info("Notifications sent for employee: {}", event.getEmployeeName());

        } catch (Exception e) {
            logger.error("Error processing employee created event: {}", e.getMessage(), e);
            // In a real-world scenario, you might want to send this message to a dead-letter queue
            // or log it for manual inspection if processing fails.
        }
    }
}