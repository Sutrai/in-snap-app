package com.oous.authorizationserver.service.impl;

import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import com.oous.authorizationserver.domain.entity.UserEntity;
import com.oous.authorizationserver.domain.response.exception.information.RegistrationException;
import com.oous.authorizationserver.repository.RoleRepository;
import com.oous.authorizationserver.repository.UserRepository;
import com.oous.authorizationserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserEntity saveAndActivate(RegistrationReq req) {
        UserEntity user = new UserEntity();
        user.setNickname(req.getNickname());
        user.setEmail(req.getEmail());
        user.getRoles().add(roleRepository.getDefaultRole());
        user.setActive(true);
        user.setAdmin(false);
        user.setSuperuser(false);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
