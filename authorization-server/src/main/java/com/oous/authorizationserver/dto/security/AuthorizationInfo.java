package com.oous.authorizationserver.dto.security;

import lombok.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationInfo implements Serializable {

    private LocalDateTime startDate;
    private LocalDateTime lastRefreshDate;
    private String clientId;
    private Set<String> scopes;
    private AuthorizationGrantType authorizationGrantType;
    private String authorizationId;
    private UUID userId;
    private String username;
    private String redirectUri;

}