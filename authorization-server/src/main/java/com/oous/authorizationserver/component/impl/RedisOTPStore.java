package com.oous.authorizationserver.component.impl;

import com.oous.authorizationserver.component.OTPStore;
import com.oous.authorizationserver.util.CryptoUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.codec.Hex;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisOTPStore implements OTPStore {

    private final static String SESSION_ID_TO_OTP = "otp_store:session_id_to_otp:";

    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> store;
    private final Config config;


    public RedisOTPStore(Config config, StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.store = redisTemplate.opsForValue();
        this.config = new Config(config.cookieName(), config.cookieDomain(), config.cookieMaxAge());
    }

    @Override
    public GenerationResult generate(HttpServletResponse response) {

        String otp = RandomStringUtils.randomNumeric(6);

        String sessionId = generateSessionId();
        log.info("Generate OTP - {}. Generated sessionId - {}", otp, sessionId);

        store.set(SESSION_ID_TO_OTP + sessionId, otp, config.cookieMaxAge(), TimeUnit.SECONDS);

        Cookie cookie = new Cookie(config.cookieName(), sessionId);
        cookie.setMaxAge(config.cookieMaxAge());
        cookie.setDomain(config.cookieDomain());
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        log.info("Add cookie to response = " + cookie);
        return new GenerationResult(sessionId, otp);
    }

    private String generateSessionId() {
        UUID uuid = UUID.randomUUID();
        String salt = RandomStringUtils.randomAlphanumeric(8);
        return new String(Hex.encode(CryptoUtils.pbkdf(
                uuid.toString(),
                salt.getBytes(StandardCharsets.UTF_8),
                256,
                2048
        )));
    }
}
