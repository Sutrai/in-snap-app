package com.oous.authorizationserver.security;

import com.oous.authorizationserver.domain.constant.Code;
import com.oous.authorizationserver.domain.response.exception.information.AuthenticationException;
import com.oous.authorizationserver.filter.JwtAuthenticationFilter;
import com.oous.authorizationserver.util.KeyUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Key;
import java.time.Instant;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtToUserConverter jwtToUserConverter;
    private final UserDetailsManager userDetailsManager;
    private final KeyUtils keyUtils;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Primary
    JwtDecoder jwtAccessTokenDecoder() {
        Key secretKey = keyUtils.getSecretKey();

        return token -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Instant issuedAt = claims.getIssuedAt().toInstant();
            Instant expiration = claims.getExpiration().toInstant();

            return new Jwt(token, issuedAt, expiration,
                    Collections.singletonMap("alg", "HS256"), claims);

        };
    }

    @Bean(name = "jwtRefreshTokenDecoder")
    JwtDecoder jwtRefreshTokenDecoder() {
        Key secretKey = keyUtils.getSecretKey();

        return token -> {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Instant issuedAt = claims.getIssuedAt().toInstant();
                Instant expiration = claims.getExpiration().toInstant();

                return new Jwt(token, issuedAt, expiration,
                        Collections.singletonMap("alg", "HS256"), claims);

            } catch (io.jsonwebtoken.ExpiredJwtException e) {

                throw AuthenticationException.builder("error.authentication.invalid_token").build();
            }
        };
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsManager);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean(name = "jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider jwtRefreshTokenAuthProvider() {
        log.info("Creating JwtAuthenticationProvider bean for refresh token...");
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
        provider.setJwtAuthenticationConverter(jwtToUserConverter);
        return provider;
    }
}
