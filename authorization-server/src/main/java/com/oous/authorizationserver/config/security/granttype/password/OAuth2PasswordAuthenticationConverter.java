package com.oous.authorizationserver.config.security.granttype.password;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

/*
 * Класс OAuth2PasswordAuthenticationConverter реализует интерфейс AuthenticationConverter
 * и отвечает за преобразование HTTP-запроса в объект аутентификации для типа grant_type=password.
 */
public class OAuth2PasswordAuthenticationConverter implements AuthenticationConverter {

    private static final String ACCESS_TOKEN_REQUEST_ERROR_URI =
            "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    private static final String USERNAME_PARAM = OAuth2ParameterNames.USERNAME;
    private static final String PASSWORD_PARAM = OAuth2ParameterNames.PASSWORD;
    private static final String SCOPE_PARAM = OAuth2ParameterNames.SCOPE;

    @Override
    public Authentication convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = this.getFormParameters(request);

        String grantType = parameters.getFirst(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
            return null;
        }

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(username) || parameters.get(USERNAME_PARAM).size() != 1){
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, USERNAME_PARAM, ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        String password = parameters.getFirst(PASSWORD_PARAM);
        if (!StringUtils.hasText(password) || parameters.get(password).size() != 1){
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, USERNAME_PARAM, ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        String scope = parameters.getFirst(SCOPE_PARAM);
        if (!StringUtils.hasText(scope) || parameters.get(SCOPE_PARAM).size() != 1){
            throwError(OAuth2ErrorCodes.INVALID_REQUEST, SCOPE_PARAM, ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        Set<String> requestedScopes = new HashSet<>(List.of(scope.split(" ")));

        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(USERNAME_PARAM) &&
                    !key.equals(PASSWORD_PARAM) &&
                    !key.equals(SCOPE_PARAM)
            ) {
                additionalParameters.put(key, (value.size() == 1) ? value.get(0) : value.toArray(new String[0]));
            }
        });

        return new OAuth2PasswordAuthenticationToken(
                username,
                password,
                requestedScopes,
                AuthorizationGrantType.PASSWORD,
                clientPrincipal,
                additionalParameters
        );
    }

    private MultiValueMap<String, String> getFormParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameterMap.forEach((key, values) -> {
            String queryString = StringUtils.hasText(request.getQueryString()) ? request.getQueryString() : "";

            if (!queryString.contains(key)) {
                for (String value : values) {
                    parameters.add(key, value);
                }
            }
        });
        return parameters;
    }

    private void throwError(String errorCode, String parameterName, String errorUri) {
        OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
        throw new OAuth2AuthenticationException(error);
    }
}
