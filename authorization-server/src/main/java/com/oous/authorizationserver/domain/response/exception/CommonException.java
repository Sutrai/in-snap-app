package com.oous.authorizationserver.domain.response.exception;

import com.oous.authorizationserver.domain.constant.Code;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CommonException extends RuntimeException {
    private final Code code;
    private final String error;
    private final String techMessage;
    private final HttpStatus httpStatus;
}
