package com.oous.authorizationserver.service;

import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RegistrationService {

    void register(RegistrationReq req, HttpServletResponse response);

    void checkOtp(String otp, HttpServletRequest request);
}
