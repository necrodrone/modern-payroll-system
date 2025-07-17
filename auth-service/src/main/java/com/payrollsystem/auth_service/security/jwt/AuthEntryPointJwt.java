package com.payrollsystem.auth_service.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles unauthorized authentication attempts.
 * It implements {@link AuthenticationEntryPoint} and is responsible for commencing an authentication scheme.
 * In our case, when an unauthenticated user tries to access a protected resource,
 * this entry point will be triggered, sending a 401 Unauthorized response.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Commences an authentication scheme.
     * This method is invoked when an unauthenticated client tries to access a protected resource.
     * It sends a 401 Unauthorized response with a JSON body explaining the error.
     *
     * @param request that resulted in an {@code AuthenticationException}
     * @param response so that the user agent can begin authentication
     * @param authException that caused the authentication to fail
     * @throws IOException if an input or output exception occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Set response content type to JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Set HTTP status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Prepare the error response body
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        // Write the JSON response to the output stream
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}