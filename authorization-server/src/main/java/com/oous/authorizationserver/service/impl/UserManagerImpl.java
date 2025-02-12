package com.oous.authorizationserver.service.impl;

import com.oous.authorizationserver.domain.constant.Code;
import com.oous.authorizationserver.domain.entity.User;
import com.oous.authorizationserver.domain.response.exception.information.UserAlreadyExistsException;
import com.oous.authorizationserver.repository.UserRepository;
import com.oous.authorizationserver.service.UserManager;
import com.oous.authorizationserver.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserDetailsManager, UserManager {

    private final UserRepository userRepository;
    private final ValidationUtils validator;

    @Override
    public void createUser(UserDetails user) {
        log.info("Attempting to create user with username: {}", user.getUsername());

        if (userRepository.existsByEmail(user.getUsername())) {
            log.info("User with nickname '{}' already exists", user.getUsername());
            throw UserAlreadyExistsException.builder("error.user.already.exists").build();
        }

        log.info("Validating user details for username: {}", user.getUsername());
        validator.validationRequest(user);

        log.info("Saving user with username: {}", user.getUsername());
        userRepository.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        log.debug("Updating user with username: {}", user.getUsername());
    }

    @Override
    public void deleteUser(String username) {
        log.debug("Deleting user with username: {}", username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.debug("Changing password for user. Old password: {}, New password: {}", oldPassword, newPassword);
    }

    @Override
    public boolean userExists(String nickname) {
        log.debug("Checking if user exists with nickname: {}", nickname);
        return userRepository.existsByEmail(nickname);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Loading user by username: {}", username);
        return userRepository.findByNickname(username)
                .orElseThrow(() -> {
                    log.error("User with username '{}' not found", username);
                    return new UsernameNotFoundException("User not Found");
                });
    }

    @Override
    public UserDetails loadUserById(Long id) {
        log.info("Loading user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID '{}' not found", id);
                    return new UsernameNotFoundException("User not Found");
                });
    }
}