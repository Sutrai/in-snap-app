package com.oous.imageservice.domain.response.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
public class InvalidImageException extends CommonException {
    public InvalidImageException(String techMessage) {
        super("Invalid image provided", techMessage, HttpStatus.BAD_REQUEST);
    }
}
