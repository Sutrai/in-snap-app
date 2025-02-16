package com.oous.authorizationserver.repository;

import com.oous.authorizationserver.domain.entity.SystemOAuth2Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class DaoRegisteredClientRepository implements RegisteredClientRepository {

    private final SystemOAuth2ClientRepository systemOauth2ClientRepository;

    @Override
    @Transactional
    public void save(RegisteredClient dto) {
        SystemOAuth2Client entity = new SystemOAuth2Client();
        if (dto.getId() != null) {
            entity = systemOauth2ClientRepository.getReferenceById(dto.getId());
        }
        this.map(dto, entity);
        systemOauth2ClientRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return null;
    }

    private void map(RegisteredClient dto, SystemOAuth2Client entity) {
        entity.setClientId(dto.getClientId());
        entity.setClientSecret(dto.getClientSecret());
        entity.setClientSecretExpiresAt(dto.getClientSecretExpiresAt() != null ?
                LocalDateTime.ofInstant(dto.getClientSecretExpiresAt(), ZoneOffset.UTC) :
                null);
        entity.setClientName(dto.getClientName());
        entity.setClientAuthenticationMethods(dto.getClientAuthenticationMethods());
        entity.setAuthorizationGrantTypes(dto.getAuthorizationGrantTypes());
        entity.setRedirectUris(dto.getRedirectUris());
        entity.setScopes(dto.getScopes());
    }
}
