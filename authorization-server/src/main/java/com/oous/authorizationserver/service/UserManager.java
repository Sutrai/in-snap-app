package com.oous.authorizationserver.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserManager {

    UserDetails loadUserById(Long id);
}
