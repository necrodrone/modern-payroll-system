package com.payrollsystem.auth_service.security.jwt;

import com.payrollsystem.auth_service.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for JSON Web Token (JWT) operations.
 * Handles generation, validation, and parsing of JWTs.
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * JWT secret key obtained from application properties.
     * This key is used for signing and verifying JWTs.
     */
    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    /**
     * JWT expiration time in milliseconds, obtained from application properties.
     */
    @Value("${auth.jwt.expirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a secret key from the base64 encoded JWT secret string.
     *
     * @return The signing key.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Generates a JWT token for the authenticated user.
     * The token contains the username as the subject, along with issuance and expiration dates.
     *
     * @param authentication The Spring Security Authentication object containing user details.
     * @return The generated JWT token string.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Set username as subject
                .setIssuedAt(new Date()) // Set token issuance time
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set token expiration time
                .signWith(key(), SignatureAlgorithm.HS256) // Sign the token with the secret key and HS256 algorithm
                .compact(); // Build and compact the JWT into a string
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token string.
     * @return The username (subject) from the token.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates a JWT token.
     * Checks for proper signing, expiration, and other common JWT issues.
     *
     * @param authToken The JWT token string to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}