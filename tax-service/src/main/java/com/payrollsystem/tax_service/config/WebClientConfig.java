package com.payrollsystem.tax_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * Creates a WebClient.Builder bean.
     * This builder can be injected into any service to create a WebClient
     * for making API calls to other microservices.
     * @return a WebClient.Builder instance.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
