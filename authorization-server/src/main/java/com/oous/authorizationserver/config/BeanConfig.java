package com.oous.authorizationserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oous.authorizationserver.component.OTPStore;
import com.oous.authorizationserver.component.RegistrationStore;
import com.oous.authorizationserver.component.impl.RedisOTPStore;
import com.oous.authorizationserver.component.impl.RedisRegistrationStore;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final AppProperties.EmailProperties mailProperties;

    @Bean
    public OTPStore.Config otpStoreConfig(
        @Value("${otp-store.cookie-name}") String cookieName,
        @Value("${otp-store.cookie-domain}") String cookieDomain,
        @Value("${otp-store.cookie-max-age}") int cookieMaxAge
    ) {
        return new OTPStore.Config(cookieName, cookieDomain, cookieMaxAge);
    }

    @Bean
    public OTPStore otpStore(OTPStore.Config otpStoreConfig, StringRedisTemplate redisTemplate) {
        return new RedisOTPStore(otpStoreConfig, redisTemplate);
    }

    @Bean
    public RegistrationStore registrationStore(
        OTPStore.Config otpStoreConfig,
        StringRedisTemplate stringRedisTemplate,
        ObjectMapper objectMapper
    ) {
        return new RedisRegistrationStore(otpStoreConfig.cookieMaxAge(), stringRedisTemplate, objectMapper);
    }

    @Bean
    public Session mailSession() {
        Properties props = new Properties();
        props.putAll(mailProperties.getProperties());
        props.put("mail.smtp.host", mailProperties.getHost());
        props.put("mail.smtp.port", String.valueOf(mailProperties.getPort()));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getUsername(), mailProperties.getPassword());
            }
        });
    }
}
