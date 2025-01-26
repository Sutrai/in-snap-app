package com.example.authorizationserver.controller;

import com.example.authorizationserver.domain.api.common.AuthenticationResp;
import com.example.authorizationserver.domain.api.refreshToken.RefreshTokenResp;
import com.example.authorizationserver.domain.constant.Code;
import com.example.authorizationserver.domain.constant.Role;
import com.example.authorizationserver.domain.entity.User;
import com.example.authorizationserver.domain.response.Exception.CommonException;
import com.example.authorizationserver.domain.response.Response;
import com.example.authorizationserver.repository.UserRepository;
import com.example.authorizationserver.security.TokenGenerator;
import com.example.authorizationserver.domain.api.login.LoginReq;
import com.example.authorizationserver.domain.api.refreshToken.RefreshTokenReq;
import com.example.authorizationserver.domain.api.registration.RegistrationReq;
import com.example.authorizationserver.util.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public AuthController(DaoAuthenticationProvider daoAuthenticationProvider, PasswordEncoder passwordEncoder, UserDetailsManager userDetailsManager, ValidationUtils validator, TokenGenerator tokenGenerator, JwtAuthenticationProvider refreshTokenAuthProvider, UserRepository userRepository) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
        this.validator = validator;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenAuthProvider = refreshTokenAuthProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/registration")
    public ResponseEntity<Response> registration(@RequestBody RegistrationReq req){
        User user = User.builder()
                .nickname(req.getNickname())
                .email(req.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        System.out.println(1);
        userDetailsManager.createUser(user);
        System.out.println(2);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user,
                req.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
        System.out.println(3);
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

        Optional<User> optionalUser = userRepository.findById(Long.valueOf(user.getId()));

        User foundUser = optionalUser.get();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(foundUser.getRole().toString()));

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                foundUser, authentication.getCredentials(), authorities);

        return ResponseEntity.ok(tokenGenerator.createToken(newAuthentication));
    }

}