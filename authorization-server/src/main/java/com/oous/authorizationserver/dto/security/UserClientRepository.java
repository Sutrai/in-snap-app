package com.oous.authorizationserver.dto.security;

import com.oous.authorizationserver.domain.entity.UserClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserClientRepository extends JpaRepository<UserClient, String> {

    UserClient findByUserIdAndClientId(UUID userId, String clientId);
}
