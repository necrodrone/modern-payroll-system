package com.payrollsystem.payroll_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Main security configuration class for the WebFlux application.
 * It enables WebFlux security and sets up JWT-based authentication.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for all incoming requests.
     *
     * @param http The ServerHttpSecurity to configure.
     * @param jwtDecoder The ReactiveJwtDecoder bean to be used for decoding and validating JWTs.
     * @return A configured SecurityWebFilterChain.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, Mono<ReactiveJwtDecoder> jwtDecoder) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder.block())
                        )
                )
                .authorizeExchange(exchanges -> exchanges
                        // Permit access to the API documentation endpoints, including the prefix
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .anyExchange().authenticated()
                );

        return http.build();
    }
}