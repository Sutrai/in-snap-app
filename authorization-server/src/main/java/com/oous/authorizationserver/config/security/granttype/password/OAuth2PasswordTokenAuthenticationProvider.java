package com.oous.authorizationserver.config.security.granttype.password;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ERROR_URI;

@Slf4j
@RequiredArgsConstructor
public class OAuth2PasswordTokenAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2PasswordAuthenticationToken passwordAuthentication = (OAuth2PasswordAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClient(passwordAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (registeredClient == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }

        Authentication userAuthentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    passwordAuthentication.getUsername(),
                    passwordAuthentication.getPassword()
            )
        );

        // Создадим объект OAuth2Authorization который сохраним в Redis
        // На этом этапе объект ещё не является завершенным и сохраняется в спец. список.
        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(UUID.randomUUID().toString())
                .principalName(passwordAuthentication.getUsername())
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizedScopes(passwordAuthentication.getScopes())
                .attribute(Principal.class.getName(), userAuthentication)
                .build();
        authorizationService.save(authorization);

        Authentication principal = authorization.getAttribute(Principal.class.getName());
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorization(authorization)
                .authorizedScopes(authorization.getAuthorizedScopes())
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrant(passwordAuthentication);

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization);

        OAuth2AccessToken accessToken = this.generateAndSetAccessToken(tokenContextBuilder, authorizationBuilder);

        OAuth2RefreshToken refreshToken = null;

        if (registeredClient.getClientAuthenticationMethods().contains(AuthorizationGrantType.REFRESH_TOKEN)){
            refreshToken = this.generateAndSetRefreshToken(tokenContextBuilder, authorizationBuilder);
        }

        // Строим новый объект OAuth2Authorization и сохраняем его.
        // На этом этапе он становится завершенным
        authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient,
                clientPrincipal,
                accessToken,
                refreshToken,
                Collections.emptyMap()
        );
    }

    private OAuth2AccessToken generateAndSetAccessToken(
            DefaultOAuth2TokenContext.Builder tokenContextBuilder,
            OAuth2Authorization.Builder authorizationBuilder
    ){
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(
                    OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.",
                    ERROR_URI
            );
            throw new OAuth2AuthenticationException(error);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(),
                generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(),
                tokenContext.getAuthorizedScopes()
        );
        if (generatedAccessToken instanceof ClaimAccessor claimAccessor) {
            authorizationBuilder.token(accessToken, metadata -> metadata.put(
                    OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                    claimAccessor.getClaims()));
        } else authorizationBuilder.accessToken(accessToken);

        return accessToken;
    }

    private OAuth2RefreshToken generateAndSetRefreshToken(
            DefaultOAuth2TokenContext.Builder tokenContextBuilder,
            OAuth2Authorization.Builder authorizationBuilder
    ){
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
        OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
        if (generatedRefreshToken != null) {
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken refreshToken)) {
                OAuth2Error error = new OAuth2Error(
                        OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate a valid refresh token.",
                        ERROR_URI
                );
                throw new OAuth2AuthenticationException(error);
            }

            authorizationBuilder.refreshToken(refreshToken);
            return refreshToken;
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }
}
