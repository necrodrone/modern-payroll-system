package com.payrollsystem.auth_service.security;

import com.payrollsystem.auth_service.security.jwt.AuthEntryPointJwt;
import com.payrollsystem.auth_service.security.jwt.AuthTokenFilter;
import com.payrollsystem.auth_service.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class for the authentication service.
 * This class configures security aspects such as authentication providers,
 * password encoders, JWT filters, and authorization rules for HTTP requests.
 */
@Configuration
@EnableMethodSecurity(
        // securedEnabled = true, // Enables @Secured annotation
        // jsr250Enabled = true, // Enables @RolesAllowed annotation
        prePostEnabled = true // Enables @PreAuthorize and @PostAuthorize annotations
)
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Creates and returns an instance of {@link AuthTokenFilter}.
     * This filter is responsible for intercepting incoming requests,
     * extracting JWT tokens, and authenticating users based on the token.
     *
     * @return An instance of AuthTokenFilter.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Configures the DAO Authentication Provider.
     * This provider uses our custom UserDetailsService to retrieve user details
     * and a PasswordEncoder to verify passwords.
     *
     * @return A configured DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Provides the {@link AuthenticationManager} bean.
     * The AuthenticationManager is responsible for authenticating users.
     *
     * @param authConfig The AuthenticationConfiguration.
     * @return The AuthenticationManager.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the {@link PasswordEncoder} bean.
     * We use BCryptPasswordEncoder for strong password hashing.
     *
     * @return An instance of BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * This method defines authorization rules, session management,
     * CSRF protection, and adds our custom JWT filter.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF as we are using stateless JWT
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Handle unauthorized access
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Permit all requests to authentication endpoints
                                .requestMatchers("/api/test/**").permitAll() // Permit all requests to test endpoints (optional for development)
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Permit access to Swagger UI
                                .anyRequest().authenticated() // Require authentication for all other requests
                );

        // Add our custom authentication provider
        http.authenticationProvider(authenticationProvider());

        // Add our custom JWT token filter before the Spring Security's UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}