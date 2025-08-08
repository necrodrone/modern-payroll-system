package com.payrollsystem.employee_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.employee-events}")
    private String employeeEventsExchangeName;

    // Define the Topic Exchange
    @Bean
    public TopicExchange employeeEventsExchange() {
        return new TopicExchange(employeeEventsExchangeName);
    }
}