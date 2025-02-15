package com.oous.authorizationserver.domain.response.exception.information;

import com.oous.authorizationserver.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class SessionCookieNotFoundException extends InformationException {
    public SessionCookieNotFoundException(String error) {
        super(Code.SESSION_COOKIE_NOT_FOUND, error, HttpStatus.BAD_REQUEST);
    }
}
