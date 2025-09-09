package com.payrollsystem.payroll_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class responsible for creating the ReactiveJwtDecoder bean.
 * This decoder fetches the public key from the authentication server at startup.
 */
@Configuration
public class JwtDecoderConfig {

    private static final Logger LOGGER = Logger.getLogger(JwtDecoderConfig.class.getName());

    // Injects the public key URL from application.properties
    @Value("${auth.public-key-url}")
    private String publicKeyUrl;

    /**
     * Creates a ReactiveJwtDecoder bean that is configured to use an RSA public key
     * fetched from a remote URL. The key is fetched once upon application startup.
     *
     * @return A Mono of ReactiveJwtDecoder.
     */
    @Bean
    public Mono<ReactiveJwtDecoder> reactiveJwtDecoder() {
        return fetchPublicKey()
                .map(this::createJwtDecoder)
                .doOnSuccess(decoder -> LOGGER.info("Successfully created JWT Decoder with remote public key."))
                .doOnError(error -> LOGGER.log(Level.SEVERE, "Failed to create JWT Decoder.", error))
                .cache(); // Cache the result to avoid re-fetching the key on every subscription
    }

    /**
     * Fetches the Base64-encoded public key from the configured URL.
     *
     * @return A Mono emitting the RSAPublicKey.
     */
    private Mono<RSAPublicKey> fetchPublicKey() {
        LOGGER.info("Fetching public key from: " + publicKeyUrl);
        return WebClient.create()
                .get()
                .uri(publicKeyUrl)
                .retrieve()
                .bodyToMono(String.class) // The response is expected to be the Base64 key as a plain string
                .map(this::convertStringToRSAPublicKey);
    }

    /**
     * Converts a Base64 encoded public key string into an RSAPublicKey object.
     *
     * @param keyString The Base64 encoded public key.
     * @return The corresponding RSAPublicKey object.
     */
    private RSAPublicKey convertStringToRSAPublicKey(String keyString) {
        try {
            LOGGER.info("Decoding and parsing public key.");
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error converting string to RSA public key", e);
            throw new RuntimeException("Could not convert public key string", e);
        }
    }

    /**
     * Creates a NimbusReactiveJwtDecoder instance using the provided public key.
     *
     * @param publicKey The RSAPublicKey to use for JWT signature verification.
     * @return A configured ReactiveJwtDecoder.
     */
    private ReactiveJwtDecoder createJwtDecoder(RSAPublicKey publicKey) {
        return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
    }
}
