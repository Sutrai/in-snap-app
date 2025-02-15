package com.oous.authorizationserver.config.security.granttype.password;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

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
