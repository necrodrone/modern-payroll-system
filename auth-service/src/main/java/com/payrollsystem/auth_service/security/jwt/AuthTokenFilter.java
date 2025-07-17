package com.payrollsystem.auth_service.security.jwt;

import com.payrollsystem.auth_service.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom Spring Security filter for JWT authentication.
 * This filter executes once per request to check for a valid JWT in the request header.
 * If a valid token is found, it extracts user details and sets up Spring Security's authentication context.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private com.payrollsystem.auth_service.security.jwt.JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Performs the filtering logic for each request.
     * It extracts the JWT from the Authorization header, validates it,
     * loads user details, and sets the authentication in the SecurityContext.
     *
     * @param request The HttpServletRequest.
     * @param response The HttpServletResponse.
     * @param filterChain The FilterChain.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Parse JWT from the request header
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 2. Extract username from JWT
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 3. Load UserDetails from the database using the username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 4. Create an Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Set the Authentication object in SecurityContextHolder
                // This informs Spring Security that the current user is authenticated.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     * The token is expected to be in the format "Bearer <token>".
     *
     * @param request The HttpServletRequest.
     * @return The extracted JWT token string, or null if not found or invalid format.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // Check if the Authorization header exists and starts with "Bearer "
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Extract the token part after "Bearer "
            return headerAuth.substring(7);
        }

        return null;
    }
}