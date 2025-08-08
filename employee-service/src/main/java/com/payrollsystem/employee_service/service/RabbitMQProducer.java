package com.payrollsystem.employee_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payrollsystem.employee_service.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.employee-events}")
    private String employeeEventsExchangeName;

    @Value("${rabbitmq.routingkey.employee-created}")
    private String employeeCreatedRoutingKey;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends an EmployeeCreatedEvent message to RabbitMQ.
     * The DTO is converted to a JSON string before sending.
     *
     * @param event The EmployeeCreatedEvent to send.
     */
    public void sendEmployeeCreatedEvent(EmployeeCreatedEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(employeeEventsExchangeName, employeeCreatedRoutingKey, jsonMessage);
            logger.info("Sent employee created event to RabbitMQ: {}", jsonMessage);
        } catch (JsonProcessingException e) {
            logger.error("Error converting EmployeeCreatedEvent to JSON: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error sending message to RabbitMQ: {}", e.getMessage(), e);
        }
    }
}