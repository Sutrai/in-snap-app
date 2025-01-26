package com.example.authorizationserver.service.impl;

import com.example.authorizationserver.domain.constant.Code;
import com.example.authorizationserver.domain.entity.User;
import com.example.authorizationserver.domain.response.Exception.CommonException;
import com.example.authorizationserver.repository.UserRepository;
import com.example.authorizationserver.service.UserManager;
import com.example.authorizationserver.util.ValidationUtils;
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

        if (userRepository.existsByNickname(user.getUsername())) {
            log.info("User with nickname '{}' already exists", user.getUsername());
            throw CommonException.builder()
                    .code(Code.USER_ALREADY_EXISTS)
                    .error("A user with this nickname already exists")
                    .techMessage("Nickname uniqueness check failed")
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
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
    public boolean userExists(String username) {
        log.debug("Checking if user exists with username: {}", username);
        return userRepository.existsByNickname(username);
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