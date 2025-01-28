package com.oous.authorizationserver.util;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class KeyUtils {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public Key getSecretKey() {
        return Optional.ofNullable(secretKey)
                .map(this::loadKeyFromFile)
                .orElseThrow(() -> new IllegalStateException("Secret key file path is not configured or invalid"));
    }

    private Key loadKeyFromFile(String filePath) {
        try {
            Path path = Path.of(filePath);
            byte[] keyBytes = Files.readAllBytes(path);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IOException e) {
            log.error("Failed to read secret key file: " + filePath, e);
            throw new IllegalStateException("Unable to load secret key from file", e);
        }
    }
}
