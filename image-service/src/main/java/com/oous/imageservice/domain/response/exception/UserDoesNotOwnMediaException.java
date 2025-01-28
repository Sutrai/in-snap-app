package com.oous.imageservice.domain.response.exception;

import org.springframework.http.HttpStatus;

public class UserDoesNotOwnMediaException extends CommonException {
    public UserDoesNotOwnMediaException() {
        super("user does not own the media to delete it", null, HttpStatus.FORBIDDEN);
    }
}
