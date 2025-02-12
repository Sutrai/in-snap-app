package com.oous.authorizationserver.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oous.authorizationserver.domain.api.registration.RegistrationReq;

public interface RegistrationStore {

    void save(RegistrationReq req, String sessionId) throws JsonProcessingException;

    RegistrationReq take(String sessionId) throws JsonProcessingException;
}
