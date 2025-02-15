package com.oous.authorizationserver.controller;

import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import com.oous.authorizationserver.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/init")
    public void registerNewUser(@RequestBody RegistrationReq req, HttpServletResponse response){
        registrationService.register(req, response);
    }

    @PostMapping("/confirm")
    public void checkOtp(String otp, HttpServletRequest request){
        registrationService.checkOtp(otp, request);
    }
}
