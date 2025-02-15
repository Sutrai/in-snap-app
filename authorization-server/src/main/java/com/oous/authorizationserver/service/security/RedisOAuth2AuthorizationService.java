package com.oous.authorizationserver.service.security;

import com.oous.authorizationserver.dto.security.AuthorizationInfo;
import com.oous.authorizationserver.dto.security.AuthorizedUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    // Префикс ключа у которой уже сгенерирован токен
    private static final String COMPLETE_KEY_PREFIX = "oauth2_authorization_complete:";

    // Префикс ключа для которой процесс авторизации ещё не завершился
    private static final String INIT_KEY_PREFIX = "oauth2_authorization_init:";

    private static final String INFO_KEY_PREFIX = "oauth2_authorization_info:";
    public static final String PRINCIPAL_ATTRIBUTE_KEY = "java.security.Principal";

    private final RedisTemplate<String, OAuth2Authorization> redisTemplate;
    private final RedisTemplate<String, AuthorizationInfo> redisTemplateAuthInfo;
    private final ValueOperations<String, OAuth2Authorization> authorizations;
    private final ValueOperations<String, AuthorizationInfo> authInfoByUser;
    private final Consumer<AuthorizationInfo> onSaveHandler;
    private final Consumer<AuthorizationInfo> onRemoveHandler;
    private final long ttl;

    public RedisOAuth2AuthorizationService(
            RedisTemplate<String, OAuth2Authorization> redisTemplate,
            RedisTemplate<String, AuthorizationInfo> redisTemplateAuthInfo,
            Consumer<AuthorizationInfo> onSaveHandler,
            Consumer<AuthorizationInfo> onRemoveHandler,
            long ttl
    ) {
        this.redisTemplate = redisTemplate;
        this.authorizations = redisTemplate.opsForValue();
        this.redisTemplateAuthInfo = redisTemplateAuthInfo;
        this.authInfoByUser = redisTemplateAuthInfo.opsForValue();
        this.ttl = ttl;
        this.onSaveHandler = onSaveHandler;
        this.onRemoveHandler = onRemoveHandler;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        if (isComplete(authorization)){
            String key = COMPLETE_KEY_PREFIX + authorization.getId();
            AuthorizationInfo info = this.saveAuthInfo(authorization);

            // Удаляем ключ, добавленный при незавершённой авторизации
            String initKey = INIT_KEY_PREFIX + authorization;
            if (Boolean.TRUE.equals(this.redisTemplate.hasKey(initKey))){
                redisTemplate.delete(initKey);
            }
            this.authorizations.set(key, authorization, this.ttl, TimeUnit.SECONDS);

            this.onSaveHandler.accept(info);
        } else {
            String key = INIT_KEY_PREFIX + authorization.getId();
            this.authorizations.set(key, authorization, this.ttl, TimeUnit.SECONDS);
        }
    }

    private AuthorizationInfo saveAuthInfo(OAuth2Authorization authorization) {
        AuthorizedUser authorizedUser = extractPrincipal(authorization);

        String redirectUri = null;
        OAuth2AuthorizationRequest authRequest = authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
        if (authRequest != null) {
            redirectUri = authRequest.getRedirectUri();
        }

        String key = INFO_KEY_PREFIX
                + (authorizedUser != null ? authorizedUser.getId().toString() : authorization.getPrincipalName())
                + ":" + authorization.getId();

        boolean keyAlreadyExists = Boolean.TRUE.equals(redisTemplateAuthInfo.hasKey(key));
        AuthorizationInfo lastAuthInfo = null;
        if (keyAlreadyExists) {
            lastAuthInfo = this.authInfoByUser.get(key);
        }

        AuthorizationInfo tokenDto = AuthorizationInfo.builder()
                .clientId(authorization.getRegisteredClientId())
                .startDate(lastAuthInfo != null ? lastAuthInfo.getStartDate() : LocalDateTime.now())
                .lastRefreshDate(LocalDateTime.now())
                .scopes(authorization.getAuthorizedScopes())
                .authorizationGrantType(authorization.getAuthorizationGrantType())
                .authorizationId(authorization.getId())
                .userId(authorizedUser != null ? authorizedUser.getId() : null)
                .username(authorizedUser != null ? authorizedUser.getUsername() : null)
                .redirectUri(redirectUri)
                .build();
        this.authInfoByUser.set(key, tokenDto, this.ttl, TimeUnit.SECONDS);
        return tokenDto;
    }

    @Override
    public void remove(OAuth2Authorization authorization) {

    }

    @Override
    public OAuth2Authorization findById(String id) {
        return null;
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return null;
    }

    private static boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private static AuthorizedUser extractPrincipal(OAuth2Authorization authorization) {
        AuthorizedUser authorizedUser = null;
        if (authorization.getAttributes().containsKey(PRINCIPAL_ATTRIBUTE_KEY)) {
            Authentication userAuthentication = authorization.getAttribute(PRINCIPAL_ATTRIBUTE_KEY);
            if (userAuthentication.getPrincipal() != null && userAuthentication.getPrincipal() instanceof AuthorizedUser principal) {
                    authorizedUser = principal;
            }
        }
        return authorizedUser;
    }
}
