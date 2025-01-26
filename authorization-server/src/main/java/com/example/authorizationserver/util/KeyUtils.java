package com.example.authorizationserver.util;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Component
@Slf4j
public class KeyUtils {

    @Value("${JWT_SECRET_KEY}")
    private String secretKeyPath;

    private SecretKey _secretKey;

    public SecretKey getSecretKey() {
        if (Objects.isNull(_secretKey)) {
            _secretKey = loadOrGenerateSecretKey(secretKeyPath);
        }
        return _secretKey;
    }

    private SecretKey loadOrGenerateSecretKey(String secretKeyPath) {
        File keyFile = new File(secretKeyPath);

        if (keyFile.exists()) {
            log.info("Loading secret key from file: {}", secretKeyPath);
            try {
                byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
                return Keys.hmacShaKeyFor(keyBytes);
            } catch (IOException e) {
                throw new RuntimeException("Error loading secret key", e);
            }
        } else {
            log.info("Generating new secret key and saving to file: {}", secretKeyPath);
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
                keyGenerator.init(256);
                SecretKey secretKey = keyGenerator.generateKey();

                Files.write(keyFile.toPath(), secretKey.getEncoded());
                return secretKey;
            } catch (NoSuchAlgorithmException | IOException e) {
                throw new RuntimeException("Error generating secret key", e);
            }
        }
    }
}
