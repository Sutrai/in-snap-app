package com.example.eurekaclient.Util;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;

@Component
public class KeyUtils {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public Key getSecretKey() {
        byte[] keyBytes = null;
        try {
            keyBytes = Files.readAllBytes(new File(secretKey).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
