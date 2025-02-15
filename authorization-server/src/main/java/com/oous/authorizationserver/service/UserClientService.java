package com.oous.authorizationserver.service;

import java.util.UUID;

public interface UserClientService {

    void save(UUID userId, String clientId);
}
