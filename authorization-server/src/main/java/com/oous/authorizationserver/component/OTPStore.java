package com.oous.authorizationserver.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface OTPStore {

    GenerationResult generate(HttpServletResponse response);

    record GenerationResult(String sessionId, String opt) {}

    record Config(String cookieName, String cookieDomain, int cookieMaxAge) { }

    boolean validate(String otp, HttpServletRequest response);

    String getSessionId(HttpServletRequest response);
}
