package com.oous.imageservice.domain.response.exception;

import org.springframework.http.HttpStatus;

public class MediaNotFoundException extends CommonException {
    public MediaNotFoundException() {
        super("media not found", null, HttpStatus.NOT_FOUND);
    }
}
