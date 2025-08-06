package com.payrollsystem.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

@RestController
public class PublicKeyController {

    private final KeyPair keyPair;

    @Autowired
    public PublicKeyController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    /**
     * Exposes the public key as a Base64-encoded string.
     * The employee-service will call this endpoint to get the key.
     */
    @GetMapping("/api/auth/public-key")
    public String getPublicKey() {
        PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}