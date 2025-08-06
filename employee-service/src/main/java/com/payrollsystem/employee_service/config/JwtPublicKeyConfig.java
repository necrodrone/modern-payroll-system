package com.payrollsystem.employee_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtPublicKeyConfig {

    @Value("${auth.public-key-url}")
    private String publicKeyUrl;

    @Bean
    public PublicKey publicKey(RestTemplate restTemplate) throws Exception {
        // Fetch the Base64 encoded public key from the auth-service
        String base64PublicKey = restTemplate.getForObject(publicKeyUrl, String.class);

        // Decode the Base64 string and create a PublicKey object
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // We need a RestTemplate bean to make the HTTP call
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}