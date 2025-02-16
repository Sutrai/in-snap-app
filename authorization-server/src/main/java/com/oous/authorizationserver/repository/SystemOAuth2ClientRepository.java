package com.oous.authorizationserver.repository;

import com.oous.authorizationserver.domain.entity.SystemOAuth2Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemOAuth2ClientRepository
        extends JpaRepository<SystemOAuth2Client, String>, JpaSpecificationExecutor<SystemOAuth2Client> {

    SystemOAuth2Client getByClientId(String clientId);
}
