package com.payrollsystem.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.employee-created}")
    private String employeeCreatedQueueName;

    @Value("${rabbitmq.exchange.employee-events}")
    private String employeeEventsExchangeName;

    @Value("${rabbitmq.routingkey.employee-created}")
    private String employeeCreatedRoutingKey;

    // Define the Queue
    @Bean
    public Queue employeeCreatedQueue() {
        return new Queue(employeeCreatedQueueName, false); // 'false' means not durable
    }

    // Define the Topic Exchange
    @Bean
    public TopicExchange employeeEventsExchange() {
        return new TopicExchange(employeeEventsExchangeName);
    }

    // Bind the Queue to the Exchange using a Routing Key
    @Bean
    public Binding employeeCreatedBinding(Queue employeeCreatedQueue, TopicExchange employeeEventsExchange) {
        return BindingBuilder.bind(employeeCreatedQueue).to(employeeEventsExchange).with(employeeCreatedRoutingKey);
    }
}