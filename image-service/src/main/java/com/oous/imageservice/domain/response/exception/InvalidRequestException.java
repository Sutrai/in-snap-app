package com.oous.imageservice.domain.response.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends CommonException {
    public InvalidRequestException() {
        super("Unsupported data type",
                "Only MultipartFile or URL strings are allowed as input",
                HttpStatus.BAD_REQUEST);
    }
}
