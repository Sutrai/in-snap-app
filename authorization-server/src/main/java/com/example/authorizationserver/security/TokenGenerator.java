package com.example.authorizationserver.security;

import com.example.authorizationserver.domain.api.common.AuthenticationResp;
import com.example.authorizationserver.domain.api.refreshToken.RefreshTokenResp;
import com.example.authorizationserver.domain.constant.Code;
import com.example.authorizationserver.domain.entity.User;
import com.example.authorizationserver.domain.response.Exception.CommonException;
import com.example.authorizationserver.util.KeyUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private final KeyUtils keyUtils;

    public String createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Creating access token for user: {}", user.getUsername());

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("scope", Collections.emptyList())
                .signWith(keyUtils.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Access token created for user: {}", user.getUsername());
        return accessToken;
    }

    public String createRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Creating refresh token for user: {}", user.getUsername());

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 3600 * 24 * 7))
                .claim("type", "refresh")
                .signWith(keyUtils.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Refresh token created for user: {}", user.getUsername());
        return refreshToken;
    }

    public AuthenticationResp createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.error("Invalid credentials provided. Principal is not of User type: {}", authentication.getPrincipal().getClass());
            throw CommonException.builder()
                    .code(Code.INVALID_CREDENTIALS)
                    .error("Invalid credentials provided.")
                    .techMessage(String.format("Principal %s is not of User type", authentication.getPrincipal().getClass()))
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        log.info("Creating tokens for user: {}", user.getUsername());

        String refreshToken;
        if (authentication.getCredentials() instanceof Jwt jwt) {
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);
            long daysUntilExpired = duration.toDays();

            log.info("Refresh token expires in {} days.", daysUntilExpired);
            if (daysUntilExpired < 7) {
                log.info("Creating new refresh token due to expiration date.");
                refreshToken = createRefreshToken(authentication);
            } else {
                log.info("Using existing refresh token.");
                refreshToken = jwt.getTokenValue();
            }

        } else {
            log.info("Creating new refresh token as credentials are not JWT.");
            refreshToken = createRefreshToken(authentication);
        }

        AuthenticationResp response = AuthenticationResp.builder()
                .accessToken(createAccessToken(authentication))
                .refreshToken(refreshToken)
                .accountId(String.valueOf(user.getId()))
                .accountNickname(user.getNickname())
                .build();

        log.info("Tokens created successfully for user: {}", user.getUsername());
        return response;
    }
}
