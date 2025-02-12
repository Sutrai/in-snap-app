package com.oous.authorizationserver.controller;

import com.oous.authorizationserver.domain.constant.Role;
import com.oous.authorizationserver.domain.entity.User;
import com.oous.authorizationserver.domain.response.Response;
import com.oous.authorizationserver.repository.UserRepository;
import com.oous.authorizationserver.security.TokenGenerator;
import com.oous.authorizationserver.domain.api.login.LoginReq;
import com.oous.authorizationserver.domain.api.refreshToken.RefreshTokenReq;
import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import com.oous.authorizationserver.util.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager userDetailsManager;
    private final ValidationUtils validator;
    private final TokenGenerator tokenGenerator;
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;
    private final UserRepository userRepository;

    public AuthController(DaoAuthenticationProvider daoAuthenticationProvider, PasswordEncoder passwordEncoder, UserDetailsManager userDetailsManager, ValidationUtils validator, TokenGenerator tokenGenerator, UserRepository userRepository) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
        this.validator = validator;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
    }

    @PostMapping("/registration")
    public ResponseEntity<Response> registration(@RequestBody RegistrationReq req){
        validator.validationRequest(req);

        User user = User.builder()
                .nickname(req.getNickname())
                .email(req.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        userDetailsManager.createUser(user);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user,
                req.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginReq req){
        validator.validationRequest(req);
        Authentication authentication = daoAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
                req.getNickname(), req.getPassword()));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity<Response> refresh(@RequestBody RefreshTokenReq req, HttpServletRequest request) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(
                new BearerTokenAuthenticationToken(req.getRefreshToken()));

        User user = (User) authentication.getPrincipal();

        User foundUser = userRepository.findById(Long.valueOf(user.getId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + user.getId()));

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(foundUser.getRole().toString()));

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                foundUser, authentication.getCredentials(), authorities);

        return ResponseEntity.ok(tokenGenerator.createToken(newAuthentication));
    }

}