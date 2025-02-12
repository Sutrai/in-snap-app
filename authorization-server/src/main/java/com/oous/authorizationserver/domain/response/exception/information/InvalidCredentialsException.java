package com.oous.authorizationserver.domain.response.exception.information;

import com.oous.authorizationserver.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends InformationException {
    public InvalidCredentialsException(String error) {
        super(Code.INVALID_CREDENTIALS, error, HttpStatus.BAD_REQUEST);
    }
}
