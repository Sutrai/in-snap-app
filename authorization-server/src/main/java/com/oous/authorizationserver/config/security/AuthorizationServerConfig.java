package com.oous.authorizationserver.config.security;

import com.oous.authorizationserver.config.security.granttype.password.OAuth2PasswordAuthenticationConverter;
import com.oous.authorizationserver.config.security.granttype.password.OAuth2PasswordTokenAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final AuthenticationManager authenticationManager;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        // настроим OAuth2TokenEndpointConfigurer. Добавим поддержку password grant type
        authorizationServerConfigurer.tokenEndpoint(customizer -> {
            customizer.accessTokenRequestConverter(new OAuth2PasswordAuthenticationConverter());
            customizer.authenticationProvider(new OAuth2PasswordTokenAuthenticationProvider(
                    authenticationManager,
                    authorizationService,
                    tokenGenerator
            ));
        });

        return http.build();
    }
}
