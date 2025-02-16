package com.oous.authorizationserver.config.security.granttype.password;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * Класс OAuth2PasswordAuthenticationToken представляет собой объект аутентификации
 * для типа grant_type=password в OAuth 2.0. Этот токен используется для передачи
 * учетных данных пользователя (username и password) и других параметров в процессе
 * аутентификации.
  * Наследуется от OAuth2AuthorizationGrantAuthenticationToken, который является базовым
 * классом для всех типов аутентификации, связанных с OAuth 2.0.
 */
@Getter
public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final String password;
    private final Set<String> scopes;

    protected OAuth2PasswordAuthenticationToken(
            String username,
            String password,
            Set<String> scopes,
            AuthorizationGrantType authorizationGrantType,
            Authentication clientPrincipal,
            Map<String, Object> additionalParameters
    ) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
        this.username = username;
        this.password = password;
        this.scopes = scopes;
    }
}
