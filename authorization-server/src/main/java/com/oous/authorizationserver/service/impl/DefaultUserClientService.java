package com.oous.authorizationserver.service.impl;

import com.oous.authorizationserver.domain.entity.UserClient;
import com.oous.authorizationserver.dto.security.UserClientRepository;
import com.oous.authorizationserver.service.UserClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserClientService implements UserClientService {

    private final UserClientRepository userClientRepository;

    @Override
    @Transactional
    public void save(UUID userId, String clientId) {
        if (userId == null || clientId == null) {
            return;
        }
        UserClient userClient = userClientRepository.findByUserIdAndClientId(userId, clientId);
        if (userClient == null) {
            userClient = new UserClient();
            userClient.setUserId(userId);
            userClient.setClientId(clientId);
            userClient.setDeleted(false);
        }
        userClientRepository.save(userClient);
    }
}
