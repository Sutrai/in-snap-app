package com.oous.authorizationserver.service;

import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import com.oous.authorizationserver.domain.entity.UserEntity;

import java.util.UUID;

public interface UserService {

    UserEntity saveAndActivate(RegistrationReq req);

    boolean existByEmail(String email);
}
