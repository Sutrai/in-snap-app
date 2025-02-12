package com.oous.authorizationserver.domain.response.exception.information;

import com.oous.authorizationserver.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends InformationException {
    public AuthenticationException(String message) {
        super(Code.UNAUTHORIZED, message, HttpStatus.BAD_REQUEST);
    }
}
