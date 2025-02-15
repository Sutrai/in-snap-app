package com.oous.authorizationserver.dto.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthorizedUser extends User implements OAuth2User {

    private UUID id;
    private String nickname;
    private String email;
    private Boolean active;
    private Boolean admin;
    private Boolean superuser;
    private LocalDate registrationDate;

    private Map<String, Object> oauthAttributes;

    @Override
    public Map<String, Object> getAttributes() {
        return oauthAttributes;
    }

    public AuthorizedUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AuthorizedUser(
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @Override
    public String getName() {
        return "";
    }
}