package com.oous.imageservice.domain.response.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CommonException extends RuntimeException {
    private final String error;
    private final String techMessage;
    private final HttpStatus httpStatus;
}
