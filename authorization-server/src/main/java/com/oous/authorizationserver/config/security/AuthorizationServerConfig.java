package com.oous.authorizationserver.config.security;

import com.oous.authorizationserver.config.security.granttype.password.OAuth2PasswordAuthenticationConverter;
import com.oous.authorizationserver.config.security.granttype.password.OAuth2PasswordTokenAuthenticationProvider;
import com.oous.authorizationserver.config.security.properties.AuthorizationServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Конфигурация для OAuth2 Authorization Server.
 * Этот класс настраивает сервер авторизации, который отвечает за выдачу токенов
 * и управление OAuth2-процессами (например, авторизация через grant types).
 * Класс не занимается защитой веб-приложения (страниц входа, регистрации и т.д.),
 * а специализируется на OAuth2-процессах и управлении токенами.
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final AuthorizationServerProperties authorizationServerProperties;
    private final AuthenticationManager authenticationManager;
    public static final String LOGIN_PAGE = "/client/auth/login";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        // насстройка OAuth2TokenEndpointConfigurer. Поддержку password grant type
        authorizationServerConfigurer.tokenEndpoint(customizer -> {
            customizer.accessTokenRequestConverter(new OAuth2PasswordAuthenticationConverter());
            customizer.authenticationProvider(new OAuth2PasswordTokenAuthenticationProvider(
                    authenticationManager,
                    authorizationService,
                    tokenGenerator
            ));
        });

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))

                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint(LOGIN_PAGE)
                ))
                .with(authorizationServerConfigurer, Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(authorizationServerProperties.getIssuerUrl())
                .build();
    }
}
